package com.cs400.gamifyhealth;



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
import android.widget.Switch;


public class SettingsFragment extends Fragment {
    private Button resetButton;
    private Switch safeSwitch;
    private DBConnection dataSource;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_settings, container, false);
        WebView webview = (WebView) V.findViewById(R.id.about_webView);
        webview.loadUrl("file:///android_asset/About_Text.html");
        resetButton = (Button) V.findViewById(R.id.reset_button);
        safeSwitch = (Switch) V.findViewById(R.id.safe_switch);
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
