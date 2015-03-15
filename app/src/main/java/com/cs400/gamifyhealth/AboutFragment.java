package com.cs400.gamifyhealth;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Erin on 2/15/2015.
 */

//TODO: fix back stack specifically for this fragment
public class AboutFragment extends Fragment {

    // About screen for app

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_about, container, false);
        WebView webview = (WebView) V.findViewById(R.id.about_webView);
        webview.loadUrl("file:///android_asset/About_Text.html");
        NavigationDrawerMain.settingsFragmentLaunched = true;
        return V;
    }

    @Override
    public void onPause() {
        super.onPause();
        NavigationDrawerMain.settingsFragmentLaunched = false;
    }
}

