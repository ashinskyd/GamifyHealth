package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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


public class EarningsEngine {
    public Activity activity;
    public SharedPreferences sp;

    public EarningsEngine(Activity a){
        //get credits/coins from sharedprefs
        this.activity = a;
        String pref_file_key = a.getString(R.string.preference_file_key);
        sp = a.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
    }


    //called assuming a goal is completed for good
    public void updateFinalGoal(Goal g) {
        int creditsEarned = g.duration * 10;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + creditsEarned);
        editor.commit();

    }

    public void updatePop() {

        // Use database to determine population growth rate and capacity
        int[] houseCapacities = {4,8,12,16,20};
        int[] farmGrowths = {1,2,3,4,5};
        int pop = sp.getInt("POPULATION", 1);
        int popCap = 0;
        int growth = 0;

        System.out.println("Before population: "+pop);

        DBConnection dataSource = new DBConnection(activity);
        dataSource.open();
        ArrayList<Building> buildings = dataSource.getObjectsOwned();
        dataSource.close();
        for (Building b: buildings) {
            if (b.type.equals("house")) {
                popCap += houseCapacities[Integer.parseInt(b.name)];
            } else if (b.type.equals("farm")) {
                growth += farmGrowths[Integer.parseInt(b.name)];
            }
        }
        System.out.println("Popcap, growth = "+popCap+" "+growth);

        // Update the population with new citizens based on farms, if it doesn't exceed capacity
        if (pop < popCap) {
            pop = Math.min(pop + growth, popCap);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("POPULATION", pop);
        editor.commit();
        showInfoDialog(growth,"People");
        System.out.println("After population: " + pop);
    }

    //weekly bonus
    public void postWeekly(String goalType){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + 50);
        editor.commit();
        showGoalDialog(goalType);
    }

    public void postWorkout(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("CREDITS", sp.getInt("CREDITS", 0) + 10);
        editor.commit();
        showInfoDialog(10,"Credits");
    }

    private void showInfoDialog(int amount,String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Nice Work!");
        builder.setMessage("You had an increase of: "+amount+" "+type);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.show();
    }

    private void showGoalDialog(String goalName){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("You met your weekly " + goalName + " goal!");
        builder.setMessage("You earned 50 bonus credits!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    //amount = 10 * duration
    private void showGoalCompletedDialog(String goalName, int amount){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("You met your final " + goalName + " goal!");
        builder.setMessage("You earned " + amount + " credits!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
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
            int daystoAdd = (goal.currentWeek * 7);
            start.setTime(d);
            start.add(Calendar.DAY_OF_MONTH, daystoAdd);
            if (curDate.after(start)){
                candidateGoals.add(goal);
            }
        }
        for (Goal h: candidateGoals){
            try {
                boolean[] b = datasource.checkGoal(h);
                System.out.println("BOOLEANS" + b[0] + b[1]);
                if (b[0] == true){

                    if (b[1] == true){
                        //IMPLEMENT GOAL REMOVAL FROM GUI LATER
                    }
                    else if (b[1] == false){
                        h.goalMet();
                        datasource.removeGoal(h); //removes the copy based on name, type
                        datasource.insertGoal(h);
                        this.postWeekly(h.name);
                    }

                }
                else if (b[1] == false){
                    h.goalNotMet();
                    datasource.removeGoal(h); //removes the copy based on name, type
                    datasource.insertGoal(h);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        datasource.printGoalDB();
        datasource.close();
    }



}
