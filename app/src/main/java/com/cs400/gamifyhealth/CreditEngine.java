package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Erin on 1/24/2015.
 */


//todo: put credits in sharedprefs in such that a way that the value is only re-inserted for first time players

public class CreditEngine {
    public Activity activity;
    public SharedPreferences sp;

    public CreditEngine(Activity a){
        //get credits/coins from sharedprefs
        this.activity = a;
        String pref_file_key = a.getString(R.string.preference_file_key);
        sp = a.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
    }


    //called assuming a goal is completed for good
    public void updateCredits(Goal g){
        int creditsEarned = g.duration * 10;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + creditsEarned);
        editor.commit();

    }

    //weekly bonus
    public void postWeekly(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + 50);
        editor.commit();
    }

    public void postWorkout(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + 10);
        editor.commit();
    }

    //return credits to add
    //Post messages to the user when they meet weekly goals
    //2. Create new fragment window to prompt to user to add a new goal level when
    //a goal's duration is completed. Then, add a goal with startlevel = endunit, and an endunit supplied by the user
    public void weeklyGoalCheck(){
        DBConnection datasource = new DBConnection(activity);
        datasource.open();
        ArrayList<Goal> g = datasource.getGoals();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        GregorianCalendar curDate = new GregorianCalendar(Locale.US);
        ArrayList<Goal> candidateGoals = new ArrayList<Goal>();
        for (Goal goal : g) {
            String startdate = goal.startDate;
            System.out.println("goal date" + goal.startDate);
            Date d = null;
            try {
                d = sdf.parse(startdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GregorianCalendar start = new GregorianCalendar();
            start.setTime(d);
            int daysElapsed = 0;
            while (true) {
                if (start.after(curDate)) {
                    break;
                }
                daysElapsed++;
                start.add(Calendar.DAY_OF_MONTH, 1);
            }
            if ((daysElapsed - 1) % 7 == 0){
                if ((daysElapsed - 1) != 0) {
                    candidateGoals.add(goal);
                }
            }
        }
        for (Goal h: candidateGoals){
            try {
                boolean[] b = datasource.checkGoal(h);
                if (b[0] == true){

                    if (b[1] == true){
                        //IMPLEMENT GOAL REMOVAL FROM GUI LATER
                    }
                    else if (b[1] == false){
                        h.goalMet();
                        datasource.removeGoal(h); //removes the copy based on name, type
                        datasource.insertGoal(h);
                        this.postWeekly();
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        datasource.close();
    }



}
