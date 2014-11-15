package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/13/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class DBConnection{

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

    private static final String CREATE_WORK = "create table " + TABLE_2 +
            "( " + W_DATE  + " text not null, " +
            W_NAME + " text not null, "
            + W_TIME + " int, "
            + W_DIST + " int, "
            + W_RAT + " int, "
            + W_REP + " int, "
            + W_TYPE + " text not null);" ;

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
        this.database.execSQL("drop table workout");
        this.database.execSQL("create table workout( date text not null, name text not null, time int, distance int, rate int, reps int, type text not null);");
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
        if (t == "REP") {
            values.put(W_REP, w.getUnit());
        }
        else if (t == "TIM"){
            values. put(W_TIME, w.getUnit());
        }
        else if (t == "DTA-T"){
            values. put(W_TIME, w.getUnit());
        }
        else if (t == "DTA-R"){
            values. put(W_RAT, w.getUnit());
        }
        else if (t == "DTA-D"){
            values. put(W_DIST, w.getUnit());
        }
        values.put(W_TYPE,w.getType());
        long insertId = database.insert(TABLE_2, null,
                values);
    }

    public void checkDB() {
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
