package com.cs400.gamifyhealth;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;


public class SettingsFragment extends Fragment {
    private Button resetButton;
    private Switch safeSwitch;
    private Button aboutButton;
    private DBConnection dataSource;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor mEditor;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().getActionBar().setTitle("Settings");
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        mEditor = sharedPrefs.edit();
        boolean safeMode = sharedPrefs.getBoolean("Safe_Mode",false);
        resetButton = (Button) V.findViewById(R.id.reset_button);
        aboutButton = (Button) V.findViewById(R.id.aboutButton);
        safeSwitch = (Switch) V.findViewById(R.id.safe_switch);
        safeSwitch.setChecked(safeMode);
        TextView desc = (TextView) V.findViewById(R.id.description1_textView);
        TextView resetDesc = (TextView) V.findViewById(R.id.reset_description);
        String resetText = this.getActivity().getString(R.string.reset_description);
        String descText = this.getActivity().getString(R.string.safe_mode_description);
        desc.setText(descText);
        resetDesc.setText(resetText);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                view.startAnimation(buttonClick);
                FragmentTransaction transaction;
                AboutFragment aboutFragment = new AboutFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, aboutFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.show(getActivity().getFragmentManager(),"Confirm Dialog");
            }
        });
        safeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mEditor.putBoolean("Safe_Mode",isChecked);
                mEditor.commit();
            }
        });
        return V;
    }


    public class ConfirmDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to clear all your data?")
                    .setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                            resetButton.startAnimation(buttonClick);
                            getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().clear().commit();
                            Intent service = new Intent(getActivity(), AttackService.class);
                            getActivity().stopService(service);
                            dataSource = new DBConnection(getActivity());
                            dataSource.open();
                            dataSource.createTables();
                            dataSource.close();
                            Intent j = new Intent(getActivity(), WelcomeActivity.class);
                            j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(j);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            return builder.create();
        }
    }
}
