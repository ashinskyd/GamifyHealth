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

    public CreditEngine(Activity a){
        //get credits/coins from sharedprefs
        this.activity = a;
        String pref_file_key = a.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = a.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        coins = sharedPrefs.getInt("CREDITS", 1);
    }


    //called assuming a goal is met
    public void updateCredits(Goal g){
        int creditsEarned = g.duration * 10;
        coins = coins + creditsEarned;
    }



}
