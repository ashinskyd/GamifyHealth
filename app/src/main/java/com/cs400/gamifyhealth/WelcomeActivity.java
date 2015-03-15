package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;



//This is our app's entry point and starts the main setup process
public class WelcomeActivity extends Activity {
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if the game has already been started, if it has been setup then skip startup
        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean started = sharedPrefs.getBoolean("SERVICE_STARTED", false);

        if (started) {
            Intent intent = new Intent(this, NavigationDrawerMain.class);
            startActivity(intent);
        } else {
            //If it's the first time a user is starting our app, we need to init the database for storage
            DBConnection datasource = new DBConnection(this);
            datasource.open();
            datasource.createTables();
            datasource.insertObject("house", 0, 0, "0");
            datasource.insertObject("farm", 0, 1, "0");
            datasource.close();
            setContentView(R.layout.activity_welcome);
            getActionBar().setTitle("FitFrontier");
            continueButton = (Button) findViewById(R.id.continueButton);
            //Sets the continue button to proceed to next step
            //Also changes default animation scheme
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                    Intent i = new Intent(getBaseContext(), SelectActivities.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            WebView webview = (WebView) findViewById(R.id.welcome_webView);
            webview.loadUrl("file:///android_asset/intro.html");
            //Gives the user a default population and credit amount


            int initialPopulation = 2;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("POPULATION", initialPopulation);
            editor.putInt("CREDITS", 100);
            editor.commit();
        }
    }
}
