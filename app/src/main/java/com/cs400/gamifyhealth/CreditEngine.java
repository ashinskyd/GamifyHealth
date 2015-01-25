package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Erin on 1/24/2015.
 */


//todo: put credits in sharedprefs in such that a way that the value is only re-inserted for first time players

public class CreditEngine {

    public int coins;
    public Activity activity;
    public SharedPreferences sp;

    public CreditEngine(Activity a){
        //get credits/coins from sharedprefs
        this.activity = a;
        String pref_file_key = a.getString(R.string.preference_file_key);
        sp = a.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        coins = sp.getInt("CREDITS", 1);
    }


    //called assuming a goal is met
    public void updateCredits(Goal g){
        int creditsEarned = g.duration * 10;
        coins = coins + creditsEarned;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", coins);
        editor.commit();

    }

    public void postWorkout(){
        coins = coins + 10;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", coins);
    }



}
