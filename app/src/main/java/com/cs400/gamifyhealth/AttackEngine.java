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
        SharedPreferences sharedPrefs = a.getPreferences(Context.MODE_PRIVATE);
        //ask Andy, how do we access sharedpreferences, store 1 int, help
        //int population = sharedPrefs.getInt("population", 1);

        int population = 1;
        objectsOwned[3] = population;
        System.out.println(Arrays.toString(objectsOwned));


    }



    public int postAttack() {

        return randomGen.nextInt(2880) + 2880;

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

        double fortsOwned = (double)objectsOwned[3];

        if (type == 3) {

            fortsOwned = fortsOwned - (fortsOwned * percentage);

            objectsOwned[3] = (int)fortsOwned;

        }

        else {

            System.out.println(percentage);

            percentage = percentage - (percentage * (fortsOwned / 10));

            System.out.println(percentage + "here");

            double shitOwned = (double)objectsOwned[type];

            shitOwned = shitOwned - (shitOwned * percentage);

            objectsOwned[type] = (int)shitOwned;

        }

    }
}
