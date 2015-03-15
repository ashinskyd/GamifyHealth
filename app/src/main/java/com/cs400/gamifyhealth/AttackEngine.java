package com.cs400.gamifyhealth;

/**
 * Created by Erin on 1/18/2015.
 */


//population is in shared prefs
//database stores farms, fortifications, and houses

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


//make a call to the DB that returns player objects as an array of ints representing counts the order farms, forts, houses
//then the 4th spot in the array becomes the population stored in shared prefs
public class AttackEngine {
    public Random randomGen;
    public String[] attackTypeArray;
    public int[] objectsOwned;
    public Activity activity;
    private DBConnection datasource;
    private int population;


    public AttackEngine(Activity a){
        randomGen = new Random();
        this.activity=a;
        datasource = new DBConnection(this.activity);

    }

    public int generateAttackType() {
        int a = randomGen.nextInt(4);
        if (a == 1){
            a = 0;
        }
        return a;
    }

    public double generateSeverity(){
        return randomGen.nextInt(4) + 1;
    }

    //called post-attack
    public void updateDB(int toRemove, int type,  double severity) {

        String pref_file_key = activity.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = activity.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        int attacks = sharedPrefs.getInt("ATTACKS", 0) - 1;
        mEditor.putInt("ATTACKS", attacks);
        mEditor.commit();
        if (toRemove == 0) {
            showAttackDialog(0, "You survived the attack with no casualties.", (int) severity);
            return;

        }
        String typeString = "";
        //attack on people, no database connection required
        if (type == 3) {
            typeString = "people";
            int population = sharedPrefs.getInt("POPULATION", 0);
            showAttackDialog(toRemove, typeString, (int) severity);
            mEditor.putInt("POPULATION", population - toRemove);
            mEditor.commit();
            return;
        }
        //attack on buildings, database connection required
        else {
            boolean removalDone = false;

            int removed = 0;
            if (type == 0) {
                typeString = "farm";
            } else if (type == 1) {
                typeString = "fort";
            } else if (type == 2) {
                typeString = "house";
            }
            datasource.open();
            ArrayList<Building> objectLocs = datasource.getBuildingsOwned();
            ArrayList<Building> removeList = new ArrayList<Building>();
            for (Building b : objectLocs) {
                if (removalDone == false) {
                    if (b.type.equals(typeString)) {
                        removeList.add(b);
                        removed++;
                        if (removed == toRemove) {
                            removalDone = true;
                        }
                    }
                }
                if (removalDone) {
                    break;
                }
            }
            for (Building b : removeList) {
                datasource.removeBuilding(b.xcoord, b.ycoord);
            }
            System.out.println("new object counts");
            int[] test = datasource.getObjectCounts();
            System.out.println(Arrays.toString(test));
            datasource.printBuildingDB();
            datasource.close();
            showAttackDialog(toRemove, typeString, (int) severity);
        }


    }

    //Displays a dialog with a simple message anytime a user's city is attacked
    private void showAttackDialog(int size, String attackType, int severity) {

        if (size==0){
            //If we attack people, but don't have any to remove, just display a notice
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Congratulations!");
            builder.setMessage(attackType);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();

        }else{
            //Sets the dialog's message to be some preconstructed string we made
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Oh No!");
            String message;
            String suffix = "";
            if (attackType.contains("farm")){
                if (size == 1) {
                    suffix = "farm removed.";
                }
                else {
                    suffix = "farms removed.";
                }
                if (severity == 1) {
                    message = activity.getString(R.string.farm1);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 2){
                    message = activity.getString(R.string.farm2);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 3){
                    message = activity.getString(R.string.farm3);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 4){
                    message = activity.getString(R.string.farm4);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 5){
                    message = activity.getString(R.string.farm5);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
            }else if(attackType.contains("people")){
                if (size == 1) {
                    suffix = "person removed.";
                }
                else {
                    suffix = "people removed.";
                }
                if (severity == 1) {
                    message = activity.getString(R.string.people1);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 2){
                    message = activity.getString(R.string.people2);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 3){
                    message = activity.getString(R.string.people3);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 4){
                    message = activity.getString(R.string.people4);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 5){
                    message = activity.getString(R.string.people5);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
            }else if (attackType.contains("house")){
                if (size == 1) {
                    suffix = "house removed.";
                }
                else {
                    suffix = "people removed.";
                }
                if (severity == 1) {
                    message = activity.getString(R.string.houses1);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 2){
                    message = activity.getString(R.string.houses2);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 3){
                    message = activity.getString(R.string.houses3);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 4){
                    message = activity.getString(R.string.houses4);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 5){
                    message = activity.getString(R.string.houses5);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
            }else{
                if (size == 1) {
                    suffix = "fort removed.";
                }
                else {
                    suffix = "forts removed.";
                }
                if (severity == 1) {
                    message = activity.getString(R.string.fort1);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 2){
                    message = activity.getString(R.string.fort2);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 3){
                    message = activity.getString(R.string.fort3);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 4){
                    message = activity.getString(R.string.fort4);
                    builder.setMessage(message + " You had " + size + " " + suffix);
                }
                if (severity == 5){
                    message = activity.getString(R.string.fort5);
                    builder.setMessage(message + " You had" + size + " " + suffix);
                }
            }
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                //When they click the Ok button, relaunch the fragment so it draws the map properly
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FragmentTransaction transaction;
                    GameFragment gameFragment = new GameFragment();
                    transaction = activity.getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, gameFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            builder.show();
        }


    }


    public void attack() {

        // Calculate results of attack
        String pref_file_key = this.activity.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = this.activity.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        population = sharedPrefs.getInt("POPULATION", 1);
        datasource.open();
        objectsOwned = datasource.getObjectCounts();
        objectsOwned[3] = population;
        datasource.close();
        int[] postAttack = new int[4];
        for (int i = 0; i < 4; i++) {
            postAttack[i] = objectsOwned[i];
        }
        double severity = this.generateSeverity();

        double percentage = (11 + (6.0 * severity)) / 100.0;

        int type = generateAttackType();

        double fortsOwned = (double) objectsOwned[1];

        //forts reduce damage done by all types of attacks except those that specifically target forts

        if (type == 1) {
            fortsOwned = fortsOwned - (fortsOwned * percentage);

            //rounding down ensures that you always lose at least one fort per fort attack

            postAttack[1] = (int) fortsOwned;
            int toRemove = objectsOwned[type] - postAttack[type];
            this.updateDB(toRemove, type, severity);
        }
        else {

            //get the specific forts to find damage reduction factor
            datasource.open();
            int fortFactor = datasource.getFortFactor();
            datasource.close();
            System.out.println("Attacking non fort to do " + percentage);

            percentage = percentage - (fortFactor);
            if (percentage < 0.10) {
                percentage = 0.10;
            }
            System.out.println("Reduced percentage " + percentage);


            double itemDamaged = (double) objectsOwned[type];
            System.out.println("Item damaged" + itemDamaged);

            itemDamaged = itemDamaged - (itemDamaged * percentage);
            System.out.println("Item damaged after attack " + itemDamaged);

            //population, farms, and houses must be at least 1
            if ((int) itemDamaged <= 0 ) {
                itemDamaged = 1;
            }
            postAttack[type] = (int) itemDamaged;
            int toRemove = objectsOwned[type] - postAttack[type];
            this.updateDB(toRemove, type, severity);
        }

    }
}
