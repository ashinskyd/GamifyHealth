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

        /*ArrayList<Building> b = datasource.getObjectsOwned();
        for (Building k : b){
            System.out.println("type " + k.type + " xpos " + k.xcoord + " y pos " + k.ycoord + " name " + k.name);
        }*/

        if (started){
            Intent intent = new Intent(this, NavigationDrawerMain.class);
            startActivity(intent);
        }else{
            //how do we tell this is the first time the app has been opened
            //we only want to create tables once
            DBConnection datasource = new DBConnection(this);
            datasource.open();
            datasource.createTables();
            for (int i=0;i<4;i++){
                for (int j=0;j<4;j++){
                    datasource.insertObject("house",i,j,"1");
                }

            }
            datasource.close();
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
            editor.putInt("CREDITS", 1000);
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
