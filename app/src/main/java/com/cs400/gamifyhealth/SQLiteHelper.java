package com.cs400.gamifyhealth;
import android.content.Context;
import android.database.sqlite.*;
/**
 * Created by Erin on 11/10/2014.
 */
public class SQLiteHelper extends SQLiteOpenHelper{


    public static final String TABLE_1 = "day";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";

    private static final String DATABASE_NAME = "gamify.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_2 = "workout";
    public static final String W_WID = "wid";
    public static final String W_NAME = "name";
    public static final String W_TIME = "time";
    public static final String W_DIST = "distance";
    public static final String W_RAT = "rate";
    public static final String W_REP = "reps";
    public static final String W_TYPE = "type";



    private static final String CREATE_DAY = "create table "
            + TABLE_1 + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_DATE
            + " text not null);";

    private static final String CREATE_WORK = "create table " + TABLE_2 +
            "(" + W_WID + " int not null" +
            W_NAME + " text not null, "
            + W_TIME + " int, "
            + W_DIST + " int, "
            + W_RAT + " int, "
            + W_REP + " int, "
            + W_TYPE + " text not null);" ;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DAY);
        sqLiteDatabase.execSQL(CREATE_WORK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
