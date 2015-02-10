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
        System.out.println("Attack engine doing something");
        randomGen = new Random();
        this.activity=a;
        datasource = new DBConnection(this.activity);

    }

    public void printObjectsOwned(){
        System.out.println(Arrays.toString(objectsOwned));
    }


    public int calculateAttackStrength() {

        return randomGen.nextInt(21) + 11;

    }



    public int generateAttackType() {
        return randomGen.nextInt(4);

    }

    public double generateSeverity(){
        return randomGen.nextInt(4);
    }

    //called post-attack
    public void updateDB(int farmsDestroyed, int fortsDestroyed, int housesDestroyed, int newPopulation, double severity){

        /*** Gets a string based on attack type to  be used for the dialog ***/
        String attackType = "";
        System.out.println("I'm attempting damage");
        System.out.println(farmsDestroyed + " " + fortsDestroyed + " " + housesDestroyed);
        datasource.open();
        ArrayList<Building> objectLocs = datasource.getObjectsOwned();
        boolean farmsDone = true;
        if (farmsDestroyed > 0){
            farmsDone = false;
            attackType = "Farms Removed";
        }
        boolean fortsDone = true;
        if (fortsDestroyed > 0){
            fortsDone = false;
            attackType = "Forts Removed";
        }
        boolean housesDone = true;
        if (housesDestroyed > 0){
            housesDone = false;
            attackType = "Houses Removed";
        }

        //If none of the above are attacked, must be people
        int farm = 0;
        int fort = 0;
        int house = 0;
        System.out.println(farmsDone);
        System.out.println(fortsDone);
        System.out.println(housesDone);
        ArrayList<Building> toRemove = new ArrayList<Building>();
        for (Building b: objectLocs){
            if (farmsDone == false){
                if (b.type.equals("farm")) {
                    toRemove.add(b);
                    farm++;
                    if (farm == farmsDestroyed) {
                        farmsDone = true;
                    }
                }

            }
            if (fortsDone == false){
                if (b.type.equals("fort")) {
                    toRemove.add(b);
                    fort++;
                    if (fort == fortsDestroyed) {
                        fortsDone = true;
                    }
                }

            }
            if (housesDone == false){
                if (b.type.equals("house")) {
                    toRemove.add(b);
                    house++;
                    if (house == housesDestroyed) {
                        housesDone = true;
                    }
                }

            }
            if (farmsDone && fortsDone && housesDone){
                break;
            }
        }
        System.out.println("I'm removing these buildings");
        System.out.println(toRemove);
        for (Building b: toRemove){
            datasource.removeObject(b.xcoord, b.ycoord);
        }
        System.out.println("new object counts");
        int[] test = datasource.getObjectCounts();
        System.out.println(Arrays.toString(test));
        datasource.printObjectDB();
        datasource.close();
        String pref_file_key = activity.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = activity.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        mEditor.putInt("POPULATION", newPopulation);
        int attacks = sharedPrefs.getInt("ATTACKS",0)-1;
        mEditor.putInt("ATTACKS",attacks);
        mEditor.commit();

        //If we remove no buildings, we must have removed some number of people
        if (toRemove.size()==0){
            if (population != newPopulation) {
                showAttackDialog(population-newPopulation,"People Removed", (int) severity);
            }else{
                showAttackDialog(0,"You survived the attack with no casualties", (int)  severity);
            }
        }else{
            showAttackDialog(toRemove.size(),attackType, (int) severity);
        }

    }

    //Displays a dialog with a simple message anytime a user's city is attacked
    private void showAttackDialog(int size, String attackType, int severity) {
        if (size==0){
            //If we attack people, but don't have any to remove, just display a notice
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Oh No!");
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
            if (attackType.contains("Farms")){
                if (severity == 1) {
                    message = activity.getString(R.string.farm1);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 2){
                    message = activity.getString(R.string.farm2);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 3){
                    message = activity.getString(R.string.farm3);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 4){
                    message = activity.getString(R.string.farm4);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 5){
                    message = activity.getString(R.string.farm5);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
            }else if (attackType.contains("Houses")){
                if (severity == 1) {
                    message = activity.getString(R.string.houses1);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 2){
                    message = activity.getString(R.string.houses2);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 3){
                    message = activity.getString(R.string.houses3);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 4){
                    message = activity.getString(R.string.houses4);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 5){
                    message = activity.getString(R.string.houses5);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
            }else{
                if (severity == 1) {
                    message = activity.getString(R.string.fort1);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 2){
                    message = activity.getString(R.string.fort2);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 3){
                    message = activity.getString(R.string.fort3);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 4){
                    message = activity.getString(R.string.fort4);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
                }
                if (severity == 5){
                    message = activity.getString(R.string.fort5);
                    builder.setMessage(message + " You had: " + size + " " + attackType);
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
        String pref_file_key = this.activity.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = this.activity.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        //ask Andy, how do we access sharedpreferences, store 1 int, help
        population = sharedPrefs.getInt("POPULATION", 1);
        datasource.open();
        objectsOwned = datasource.getObjectCounts();
        objectsOwned[3] = population;
        datasource.close();
        System.out.println(Arrays.toString(objectsOwned));
        int[] postAttack = new int[4];
        for (int i = 0; i<4; i++){
            postAttack[i] = objectsOwned[i];
        }
        double severity = this.generateSeverity();

        double percentage = (11 + (6.0 * severity))/100.0;

        int type = generateAttackType();

        double fortsOwned = (double)objectsOwned[1];

        //forts reduce damage done by all types of attacks except those that specifically target forts

        if (type == 1) {

            fortsOwned = fortsOwned - (fortsOwned * percentage);

            //rounding down ensures that you always lose at least one fort per fort attack

            postAttack[1] = (int)fortsOwned;

        }

        else {

            System.out.println("Attacking non fort to do " + percentage);

            percentage = percentage - (percentage * (fortsOwned / 10));

            System.out.println("Reduced percentage " + percentage);


            double itemDamaged = (double)objectsOwned[type];
            System.out.println("Item damaged" + itemDamaged);

            itemDamaged = itemDamaged - (itemDamaged * percentage);
            System.out.println("Item damaged after attack " + itemDamaged);

            //population must be at least 1
            if (type == 3){
                if ((int)itemDamaged == 0){
                    itemDamaged = 1;
                }
            }
            //it's not possible to have negative farms, forts, or houses
            else{
                if ((int)itemDamaged < 0){
                    itemDamaged = 0;
                }
            }

            postAttack[type] = (int)itemDamaged;
            int farmsDamaged = objectsOwned[0] - postAttack[0];
            int fortsDamaged = objectsOwned[1] - postAttack[1];
            int housesDamaged = objectsOwned[2] - postAttack[2];
            int newPopulation = postAttack[3];
            severity = severity + 1;
            this.updateDB(farmsDamaged, fortsDamaged, housesDamaged, newPopulation, severity);
            objectsOwned[type] = postAttack[type];
        }

    }
}
