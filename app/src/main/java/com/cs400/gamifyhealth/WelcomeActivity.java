package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import java.text.ParseException;
import java.util.ArrayList;


public class WelcomeActivity extends Activity {
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean started = sharedPrefs.getBoolean("SERVICE_STARTED",false);
        DBConnection datasource = new DBConnection(this);
        datasource.open();
        //how do we tell this is the first time the app has been opened
        //we only want to create tables once
        datasource.createTables();
        datasource.insertObject("farm", 0, 0, "default");
        datasource.insertObject("farm", 1, 0, "default");
        datasource.insertObject("house", 1, 1, "mansion");
        datasource.insertObject("house", 0, 1, "mansion");
        //datasource.insertObject("house", 2, 2, "mansion");
        /*datasource.insertObject("farm", 0, 1, "default");
        datasource.insertObject("house", 1, 2, "hut");
        datasource.insertObject("house", 1, 4, "hut");
        datasource.insertObject("farm", 2, 1, "default");
        datasource.insertObject("farm", 2, 4, "default");
        datasource.insertObject("fort", 1, 3, "default");
        datasource.insertObject("farm", 2, 2, "default");
       */
        ArrayList<Building> b = datasource.getObjectsOwned();
        for (Building k : b){
            System.out.println("type " + k.type + " xpos " + k.xcoord + " y pos " + k.ycoord + " name " + k.name);
        }
        datasource.close();
        if (started){
            Intent intent = new Intent(this, NavigationDrawerMain.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_welcome);
            continueButton = (Button) findViewById(R.id.continueButton);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F,0.8F);
                    view.startAnimation(buttonClick);
                    Intent i = new Intent(getBaseContext(),SelectActivities.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            });
            int iPop = 2;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("POPULATION", iPop);
            editor.putInt("CREDITS", 100);
            editor.commit();
        }

        /*Workout w = new Workout("Running", 5, "DTA-D");
        Workout d = new Workout("Walking", 10, "DTA-T");
        */



        /*datasource.insertWorkout(w);

        datasource.insertWorkout(d);
        datasource.checkWorkoutDB();
        Goal g = new Goal("2015-01-10", "Running","DTA-D", 0, 10, 4 );
        Goal g2 = new Goal("2015-01-10", "Walking","DTA-D", 0, 5, 10 );
        try {
            datasource.insertGoal(g);
            datasource.insertGoal(g2);
        }
        catch (ParseException e){
            System.out.println("parse error");
        }
        datasource.printGoalDB();
        //write try catch for thing
        try {
            datasource.checkGoal(g);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }
}
