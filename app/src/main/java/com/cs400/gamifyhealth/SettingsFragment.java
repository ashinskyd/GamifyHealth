package com.cs400.gamifyhealth;



import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;


public class SettingsFragment extends Fragment {
    private Button resetButton;
    private Switch safeSwitch;
    private Button testButton;
    private DBConnection dataSource;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_settings, container, false);
        resetButton = (Button) V.findViewById(R.id.reset_button);
         testButton = (Button) V.findViewById(R.id.testbutton);
        safeSwitch = (Switch) V.findViewById(R.id.safe_switch);
        TextView desc = (TextView) V.findViewById(R.id.description1_textView);
        TextView resetDesc = (TextView) V.findViewById(R.id.reset_description);
        String resetText = this.getActivity().getString(R.string.reset_description);
        String descText = this.getActivity().getString(R.string.safe_mode_description);
        desc.setText(descText);
        resetDesc.setText(resetText);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().clear().commit();
                dataSource = new DBConnection(getActivity());
                dataSource.open();
                dataSource.createTables();
                dataSource.close();
                Intent i = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(i);
            }
        });
        return V;
    }


}
