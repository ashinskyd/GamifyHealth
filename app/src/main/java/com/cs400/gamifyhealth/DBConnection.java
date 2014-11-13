package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/13/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class DBConnection{

    private SQLiteHelper helper;
    private SQLiteDatabase database;

    public DBConnection(Context c) {
        this.helper = new SQLiteHelper(c);
    }

    public void open() throws SQLException {
        this.database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void insertWorkout(Workout w) {
        ContentValues values = new ContentValues();
        //hard coded, fix later with service magic
        values.put(helper.W_WID, 0);
        values.put(helper.W_NAME, w.getName());
        String t = w.getType();
        if (t == "REP") {
            values.put(helper.W_REP, w.getUnit());
        }
        else if (t == "TIM"){
            values. put(helper.W_TIME, w.getUnit());
        }
        else if (t == "DTA-T"){
            values. put(helper.W_TIME, w.getUnit());
        }
        else if (t == "DTA-R"){
            values. put(helper.W_RAT, w.getUnit());
        }
        else if (t == "DTA-D"){
            values. put(helper.W_DIST, w.getUnit());
        }
        values.put(helper.W_TYPE,w.getType());
        long insertId = database.insert(helper.TABLE_2, null,
                values);
    }

    public void checkDB() {
        List<String> s = new ArrayList<String>();
        String[] allColumns = {helper.W_WID, helper.W_NAME,helper.W_NAME, helper.W_TIME, helper.W_DIST,
                helper.W_RAT, helper.W_REP, helper.W_TYPE};
        Cursor cursor = database.query(helper.TABLE_2,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int time = cursor.getInt(2);
            int dist = cursor.getInt(3);
            int rate = cursor.getInt(4);
            int rep = cursor.getInt(5);
            String type = cursor.getString(6);
            System.out.println(id +  name + time +  dist +  rate +  rep +  type);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
    }



}
