package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/13/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.Pair;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

//population and credits are stored in sharedpreferences
//objects like
public class DBConnection{
    //TODO: Implement rate so that rate is in Min/Mile so that rate goal is getting smaller
    //done
    private SQLiteHelper helper;
    private SQLiteDatabase database;

    public static final String TABLE_2 = "workout";
    public static final String W_DATE = "date";
    public static final String W_NAME = "name";
    public static final String W_TIME = "time";
    public static final String W_DIST = "distance";
    public static final String W_RAT = "rate";
    public static final String W_REP = "reps";
    public static final String W_TYPE = "type";
    private int[] houseCapacities = {4,8,12,16,20};

    public DBConnection(Context c) {
        this.helper = new SQLiteHelper(c);
    }

    public void open() throws SQLException {
        this.database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }


    //redo later: change all to plural
    public void createTables(){
        this.database.execSQL("drop table if exists workout");
        this.database.execSQL("drop table if exists goals");
        this.database.execSQL("drop table if exists objects");
        this.database.execSQL("create table workout( date text not null, name text not null, time int, distance int, rate int, reps int, type text not null);");
        this.database.execSQL("create table objects( type text not null, xposition int, yposition int, name text not null);");
        this.database.execSQL("create table goals( startDate text not null, name text not null, type text not null, startUnit int, goalUnit int, currentWeek int, currentWeekGoal int, duration int);");
    }
    //goal table schema: startDate, name, type, startUnit, goalUnit, currentWeek, currentWeekGoal, duration (IN THAT ORDER!!)
    public void insertGoal(Goal g) throws ParseException{
        ContentValues values = new ContentValues();
        values.put("startDate", g.startDate);
        values.put("name", g.name);
        values.put("type", g.type);
        values.put("startUnit", g.startUnit);
        values.put("goalUnit", g.goalUnit);
        values.put("currentWeek", g.currentWeek);
        values.put("currentWeekGoal", g.currentWeekGoal);
        values.put("duration", g.duration);
        long insertId = database.insert("goals", null,
                values);
    }

    //object table scheme: type, xposition, yposition
    //valid types = "farm, fort, house" USE THESE WORDS EXACTLY
    public void insertObject(String type, int x, int y, String name){
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("xposition", x);
        values.put("yposition", y);
        values.put("name", name);
        database.insert("objects", null,
                values);
    }

    public int getPopulationCap(){
        String[] allColumns = {"type","xposition", "yposition", "name"};
        int capacity =0;
        Cursor cursor = database.query("objects",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(0);
            if (type.equals("house")){
                capacity += houseCapacities[Integer.parseInt(cursor.getString(3))];
            }
            cursor.moveToNext();
        }
        return capacity;
    }


    //in order for this to work, we must prevent the user from adding to goals of the same name and type
    //ie no two goals of the same thing measuring the same thing at the same time
    public void removeGoal(Goal g){
        String command = "delete from goals where type = '" + g.type + "' and name = '" + g.name + "' ;";
        this.database.execSQL(command);
    }

    //given an object, inserts a workout into the database
    public void insertWorkout(Workout w) {
        ContentValues values = new ContentValues();
        GregorianCalendar c = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(c.getTime());
        System.out.println(s);
        values.put(W_DATE, s);
        values.put(W_NAME, w.getName());
        String t = w.getType();
        if (t.equals("REP")) {
            values.put(W_REP, w.getUnit());
        }
        else if (t.equals("TIM")){
            values. put(W_TIME, w.getUnit());
        }
        else if (t.equals("DTA-T")){
            values. put(W_TIME, w.getUnit());
        }
        else if (t.equals("DTA-R")){
            values. put(W_RAT, w.getUnit());
        }
        else if (t.equals("DTA-D")){
            values. put(W_DIST, w.getUnit());
        }
        values.put(W_TYPE,w.getType());
        //if a line with the given type, name, and date values is already in the db
        //return that line, add the amount in workout to other workout to create total
        //raise flag (on ui end?) so user knows
        long insertId = database.insert(TABLE_2, null,
                values);
    }

    //used for the goal display screen, for a given goal, shows a user's relative progress
    public double checkGoalProgress(Goal g){
        String[] allColumns = {W_DATE,W_NAME, W_TIME, W_DIST,
                W_RAT, W_REP, W_TYPE};
        int cursorChecks = 0;
        String t = g.type;
        Double goal = g.calculateCurrentGoal();
        if (t.equals("REP")){
            cursorChecks = 5;
        }
        else if (t.equals("TIM")){
            cursorChecks = 2;
        }
        else if (t.equals("DTA-T")){
            cursorChecks = 2;
        }
        else if (t.equals("DTA-R")){
            cursorChecks = 4;
        }
        else if (t.equals("DTA-D")){
            cursorChecks  = 3;
        }
        double sum = 0;
        int count = 0;
        String startdate =  g.startDate;
        System.out.println("goal date" + g.startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(startdate);
        }
        catch(ParseException e){
            System.out.println("bad date");
        }
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        start.setTime(d);
        endDate.setTime(d);
        Cursor cur = database.query(TABLE_2,
                allColumns, null, null, null, null, null);
        cur.moveToFirst();
        if (g.currentWeek > 1){
            start.add(Calendar.DAY_OF_MONTH, 7 * (g.currentWeek));
        }
        endDate.add(Calendar.DAY_OF_MONTH, 7 * g.currentWeek);

        while (cur.isAfterLast() == false) {
            String tdate = cur.getString(0);
            Date td = null;
            try {
                td = sdf.parse(tdate);
            }
            catch(ParseException e){
                System.out.println("bad date");
            }
            GregorianCalendar temp = new GregorianCalendar();
            temp.setTime(td);
            System.out.println("temp" + temp.toString());
            if (temp.before(endDate) == true|temp.compareTo(endDate) == 0){
                //adjust for when they have the same date
                if (temp.after(start) == true| temp.compareTo(start) == 0){
                    System.out.println(g.name);
                    System.out.println(cur.getString(1));
                    if (cur.getString(1).equals(g.name)){
                        sum = sum + cur.getInt(cursorChecks);
                        count++;
                    }
                }
            }
            cur.moveToNext();
        }
        cur.close();
        if (t.equals("DTA-R") == false){
            return sum;
        }
        else{
            return sum/count;
        }
    }



    //returns a pair of booleans
    //first is the weekly goal met?
    //second is the goal completed?
    public boolean[] checkGoal(Goal g)throws ParseException{
        String[] allColumns = {W_DATE,W_NAME, W_TIME, W_DIST,
                W_RAT, W_REP, W_TYPE};
        int cursorChecks = 0;
        String t = g.type;
        Double goal = g.calculateCurrentGoal();
        if (t.equals("REP")){
            cursorChecks = 5;
        }
        else if (t.equals("TIM")){
            cursorChecks = 2;
        }
        else if (t.equals("DTA-T")){
            cursorChecks = 2;
        }
        else if (t.equals("DTA-R")){
            cursorChecks = 4;
        }
        else if (t.equals("DTA-D")){
            cursorChecks  = 3;
        }
        double sum = 0;
        int count = 0;
        String startdate =  g.startDate;
        System.out.println("goal date" + g.startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d = sdf.parse(startdate);
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        start.setTime(d);
        endDate.setTime(d);
        Cursor cur = database.query(TABLE_2,
                allColumns, null, null, null, null, null);
        cur.moveToFirst();
        if (g.currentWeek > 1){
            start.add(Calendar.DAY_OF_MONTH, 7 * (g.currentWeek));
        }
        endDate.add(Calendar.DAY_OF_MONTH, 7 * g.currentWeek);
        while (cur.isAfterLast() == false) {
            String tdate = cur.getString(0);
            System.out.println("we're parsing " + cur.getString(0));
            Date td = sdf.parse(tdate);
            GregorianCalendar temp = new GregorianCalendar();
            temp.setTime(td);
            System.out.println("temp" + temp.toString());
            if (temp.before(endDate) == true|temp.compareTo(endDate) == 0){
                //adjust for when they have the same date
                if (temp.after(start) == true| temp.compareTo(start) == 0){
                    System.out.println(g.name);
                    System.out.println(cur.getString(1));
                    if (cur.getString(1).equals(g.name)){
                        sum = sum + cur.getInt(cursorChecks);
                        count++;
                    }
                }
            }
            cur.moveToNext();
        }
        cur.close();
        System.out.println("sum " + sum);
        System.out.println(" count" + count);
        boolean weeklygoalmet = false;
        //if rate, get the average rate
        if (t.equals("DTA-R")){
            sum = sum/count;
        }
        //if the goal is not measured in minutes per mile, the goal is met when the weekly total >= the goal
        if ((sum >= goal)&& (!t.equals("DTA-R"))){
            System.out.println("goal met");
            weeklygoalmet = true;
        }
        //if the goal is a rate goal, measured in minutes per mile, then the goal is met when minute/mile <= goal
        if ((sum <= goal)&& (t.equals("DTA-R"))){
            System.out.println("goal met");
            weeklygoalmet = true;
        }
        boolean goalmet = false;
        if (weeklygoalmet){
            if (g.duration == g.currentWeek){
                goalmet = true;
            }
        }
        boolean[] b = new boolean[2];
        b[0] = weeklygoalmet;
        b[1] = goalmet;
        return b;
    }


    //returns a list of objects to owned to the game screen
    //so the player's environment can be reproduced after the game is closed
    public ArrayList<Building> getObjectsOwned(){
        ArrayList<Building> blist = new ArrayList<Building>();
        String[] allColumns = {"type","xposition", "yposition", "name"};
        Cursor cursor = database.query("objects",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(0);
            int xpos = cursor.getInt(1);
            int ypos = cursor.getInt(2);
            String name = cursor.getString(3);
            Building b = new Building(type, xpos, ypos, name);
            blist.add(b);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return blist;
    }

    //object table scheme: type, xposition, yposition
    public void printObjectDB(){
        String[] allColumns = {"type","xposition", "yposition", "name"};
        Cursor cursor = database.query("objects",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            System.out.println(
                    "type: " +  cursor.getString(0) +
                            " xpos: " + cursor.getInt(1) +
                            " ypos: " + cursor.getInt(2) +
                            " name " + cursor.getString(3));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

    }

    //called by AttackEngine to update DB after an attack
    public void removeObject(int x, int y){
        String command = "delete from objects where xposition = " + x + " and yposition = " + y + " ;";
        database.execSQL(command);
    }
    //make a call to the DB that returns player objects as an array of ints representing counts the order farms, forts, houses
    //then the 4th spot in the array becomes the population stored in shared prefs
    //TODO: adjust this to compensate for different types of items of the same class
    //for example yuen-hsi's mansion and a hut are both of type house

    public int[] getObjectCounts(){
        int farmCount = 0;
        int fortCount = 0;
        int houseCount = 0;
        String[] allColumns = {"type","xposition", "yposition", "name"};
        Cursor cursor = database.query("objects",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(0);
            if (type.equals("farm")){
                farmCount++;
            }
            if (type.equals("fort")){
                fortCount++;
            }
            if (type.equals("house")){
                houseCount++;
            }


            cursor.moveToNext();
        }
        int[] objects = new int[4];
        objects[0] = farmCount;
        objects[1] = fortCount;
        objects[2] = houseCount;
        // make sure to close the cursor
        System.out.println("Printing object counts: ");
        for (int i = 0; i<3; i++){
            System.out.println(objects[i]);
        }
        cursor.close();
        return objects;
    }

    //REMNANT TESTING CODE, REMOVE?
    //goal table schema: startDate, name, type, startUnit, goalUnit, currentWeek, currentWeekGoal, duration (IN THAT ORDER!!)
    public void printGoalDB(){
        List<String> s = new ArrayList<String>();
        String[] allColumns = {"startDate","name", "type", "startUnit",
                "goalUnit","currentWeek", "currentWeekGoal", "duration"};
        Cursor cursor = database.query("goals",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            System.out.println(
                    "startDate: " +  cursor.getString(0) +
                            " name: " + cursor.getString(1) +
                            " type: " + cursor.getString(2) +
                            " startUnit: " + cursor.getInt(3) +
                            " goalUnit: " + cursor.getInt(4) +
                            " current Week: " + cursor.getInt(5) +
                            " currentWeekGoal: " + cursor.getInt(6) +
                            " duration: " + cursor.getInt(7) );
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

    }
    //method to get goals
    //decide where this is called later
    public ArrayList<Goal> getGoals(){
        ArrayList<Goal> goalArrayList = new ArrayList<Goal>();
        String[] allColumns = {"startDate","name", "type", "startUnit",
                "goalUnit","currentWeek", "currentWeekGoal", "duration"};
        Cursor cursor = database.query("goals",
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String startDate =   cursor.getString(0);
            String  name =  cursor.getString(1);
            String type =  cursor.getString(2);
            int startUnit = cursor.getInt(3);
            int GoalUnit = cursor.getInt(4);
            int curWeek =  cursor.getInt(5);
            int curWeekGoal = cursor.getInt(6);
            int duration = cursor.getInt(7);
            Goal g = new Goal(startDate, name, type, startUnit, GoalUnit, duration);
            g.currentWeek = curWeek;
            g.currentWeekGoal = curWeekGoal;
            goalArrayList.add(g);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return goalArrayList;

    }

    public void checkWorkoutDB() {
        String[] allColumns = {W_DATE,W_NAME, W_TIME, W_DIST,
                W_RAT, W_REP, W_TYPE};
        Cursor cursor = database.query(TABLE_2,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String date = cursor.getString(0);
            String name = cursor.getString(1);
            int time = cursor.getInt(2);
            int dist = cursor.getInt(3);
            int rate = cursor.getInt(4);
            int rep = cursor.getInt(5);
            String type = cursor.getString(6);
            System.out.println(
                    "Date: " +  date +
                            " Name: " + name +
                            " Time: " + time +
                            " Dist: " + dist +
                            " Rate: " + rate +
                            " Reps: " + rep +
                            " Type: "  +  type);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
    }

    //repositions all existing building objects once the table is expanded
    public void expandObjectTable(){
        String query = "update objects set xposition = xposition + 2,  yposition = yposition + 3 ;";
        this.database.execSQL(query);
    }

}