package com.cs400.gamifyhealth;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_settings, container, false);
        WebView webview = (WebView) V.findViewById(R.id.about_webView);
        webview.loadUrl("file:///android_asset/About_Text.html");
        return V;
    }


}
