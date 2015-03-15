package com.cs400.gamifyhealth;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HelpFragment extends Fragment {

    // Simple fragment to display help screen when "?" button is tapped


    public HelpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_help, container, false);
        WebView webview = (WebView) V.findViewById(R.id.webView);
        webview.loadUrl("file:///android_asset/helpscreen.html");
        getActivity().getActionBar().setTitle("Help");
        return V;
    }


}
