package com.cs400.gamifyhealth;

/**
 * Created by Erin on 1/18/2015.
 */


//population is in shared prefs
//database stores farms, fortifications, and houses

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;

import java.util.Arrays;
import java.util.Random;


//make a call to the DB that returns player objects as an array of ints representing counts the order farms, forts, houses
//then the 4th spot in the array becomes the population stored in shared prefs
public class AttackEngine {
    public Random randomGen;
    public String[] attackTypeArray;
    public int[] objectsOwned;


    public AttackEngine(Activity a){
        System.out.println("Attack engine doing something");
        randomGen = new Random();
        attackTypeArray = new String[] {"farms", "forts", "houses",  "people" };
        DBConnection datasource = new DBConnection(a);
        datasource.open();
        objectsOwned = datasource.getObjectCounts();
        datasource.close();
        String pref_file_key = a.getString(R.string.preference_file_key);
        SharedPreferences sharedPrefs = a.getSharedPreferences(pref_file_key, Context.MODE_PRIVATE);
        //ask Andy, how do we access sharedpreferences, store 1 int, help
        int population = sharedPrefs.getInt("POPULATION", 1);
        objectsOwned[3] = population;
        System.out.println(Arrays.toString(objectsOwned));
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



    public void attack() {

        double percentage = (double)calculateAttackStrength() / 100;

        int type = generateAttackType();

        System.out.println("Doing " + percentage + " damage to " + attackTypeArray[type]);

        double fortsOwned = (double)objectsOwned[1];

        //forts reduce damage done by all types of attacks except those that specifically target forts

        if (type == 1) {

            fortsOwned = fortsOwned - (fortsOwned * percentage);

            //rounding down ensures that you always lose at least one fort per fort attack

            objectsOwned[1] = (int)fortsOwned;

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

            objectsOwned[type] = (int)itemDamaged;

        }

    }
}
