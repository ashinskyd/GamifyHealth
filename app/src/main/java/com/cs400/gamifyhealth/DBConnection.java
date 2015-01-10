package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/13/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;


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

    public DBConnection(Context c) {
        this.helper = new SQLiteHelper(c);
    }

    public void open() throws SQLException {
        this.database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }



    public void createTables(){
        this.database.execSQL("drop table if exists workout");
        this.database.execSQL("drop table if exists goals");
        this.database.execSQL("create table workout( date text not null, name text not null, time int, distance int, rate int, reps int, type text not null);");
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

    public boolean checkGoal(Goal g)throws ParseException{
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
        endDate.add(Calendar.DAY_OF_MONTH, 7);
        System.out.println("endDate" + endDate.toString());
        System.out.println("startDate" + startdate.toString());
        //start date  = d

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

        //if rate, get the average rate
        if (t.equals("DTA-R")){
            sum = sum/count;
        }
        //if the goal is not measured in minutes per mile, the goal is met when the weekly total >= the goal
        if ((sum >= goal)&& (!t.equals("DTA-R"))){
            System.out.println("goal met");
            return true;
        }
        //if the goal is a rate goal, measured in minutes per mile, then the goal is met when minute/mile <= goal
        if ((sum <= goal)&& (t.equals("DTA-R"))){
            System.out.println("goal met");
            return true;
        }
        System.out.println("GOAL NOT MET");
        return false;
    }

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

    public void checkWorkoutDB() {
        List<String> s = new ArrayList<String>();
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



}
