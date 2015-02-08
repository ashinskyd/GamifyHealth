package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GoalSetActivity extends Activity {

    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private SeekBarAdapter mAdapter;
    private Button continueButton;

    //Array of all activities user selected from sharedPrefs
    private ArrayList<String> activitySet;
    //Array of all the starting levels for each activity in activitySet
    private ArrayList<Integer> activitySetLevels;
    //Map to hold the goal unit for each activity
    private Map<String,Integer> goalLevelMap;
    //Holds each activities duration EditText so we have access later
    private Map<String,EditText> goalTimeEditTextMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        continueButton = (Button) findViewById(R.id.continueButton2);
        mListView = (ListView) findViewById(R.id.seekBarListView);
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        //Gets the string of all the users current activities
        String[] activitySetString = sharedPrefs.getString("ACTIVITIES",null).split(",");
        //Gets the starting unit for each activity
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");

        goalTimeEditTextMap = new HashMap<String, EditText>();
        activitySet = new ArrayList<String>();
        activitySetLevels = new ArrayList<Integer>();
        goalLevelMap = new HashMap<String, Integer>();

        //Puts the above information into the corresponding arraylists
        for (int i=0; i<activitySetString.length; i++){
            activitySet.add(i, activitySetString[i]);
            activitySetLevels.add(i,Integer.parseInt(activityStartValString[i]));
            //We record the users current level in the goalMap so they cannot chose a goal less than their current level
            goalLevelMap.put(activitySetString[i],Integer.parseInt(activityStartValString[i]));
        }

        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row2,activitySet);
        mListView.setAdapter(mAdapter);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Goal> goalList = new ArrayList<Goal>();
                ContentValues values = new ContentValues();
                GregorianCalendar c = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(c.getTime());

                //Check that all entries are valid before proceding
                Boolean proceed = false;
                proceed = checkEntries();

                //If the input is all valid, proceed to the game
                if (proceed){
                    for (int i=0;i<activitySet.size();i++){
                        //Iterates through all the goals, adds to the DB
                        int goalTarget = goalLevelMap.get(activitySet.get(i));
                        int goalDuration = Integer.parseInt(goalTimeEditTextMap.get(activitySet.get(i)).getText().toString());
                        int startValue = activitySetLevels.get(i);
                        String activity = activitySet.get(i);
                        int indexOfSpace = activity.indexOf(" ");
                        String activityName = activity;
                        if (indexOfSpace!=-1 && activity.charAt(indexOfSpace+1)=='('){
                            activityName = activity.substring(0,indexOfSpace);
                        }else if(indexOfSpace!=-1 && activity.charAt(indexOfSpace+1)!='('){
                            activityName = activity.split("_")[0];
                        }else{
                            activityName = activity.split("_")[0];
                        }
                        String activityType = activity.split("_")[1];
                        Goal g = new Goal(date,activityName,activityType,startValue,goalTarget,goalDuration);
                        goalList.add(g);
                    }
                    DBConnection datasource = new DBConnection(GoalSetActivity.this);
                    datasource.open();
                    try{
                        for (Goal g: goalList){
                            datasource.insertGoal(g);
                        }
                    }catch (ParseException e){
                        Log.d("TAG","Exception Caught");
                    }
                    datasource.close();
                    Intent i = new Intent(getApplicationContext(),NavigationDrawerMain.class);
                    startActivity(i);
                }else{
                    //If theres an input error, we don't proceed but throw a Toast
                    Toast toast = Toast.makeText(getApplicationContext(),"Please Enter a Duration of at least 4 weeks for each goal",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    //Checks that all entries are integers of at least 4 weeks for the Duraiton
    private Boolean checkEntries() {
        boolean flag = true;
        for (int i=0;i<activitySet.size();i++){
            if (!goalTimeEditTextMap.get(activitySet.get(i)).getText().toString().matches("[0-9]+")){
                flag = false;
                break;
            }else{
                if( Integer.parseInt(goalTimeEditTextMap.get(activitySet.get(i)).getText().toString())<4){
                    flag = false;
                    break;
                }
            }

        }
        return flag;
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    //If we pause mid-entry, we save the intermediete data in sharedPrefs so the user won't have to reenter it
    public void onPause(){
        super.onPause();
        StringBuilder temp = new StringBuilder();
        StringBuilder S = new StringBuilder();
        String key;
        Iterator entryIter = goalLevelMap.entrySet().iterator();
        while (entryIter.hasNext()){
            Map.Entry entry = (Map.Entry) entryIter.next();
            temp.append(( entry.getValue().toString()).concat(","));
            key = entry.getKey().toString();
            S.append(goalTimeEditTextMap.get(key).getText().toString().concat(","));

        }
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        mEditor.putString("Activity_Goal_Levels", temp.toString());
        mEditor.putString("Goal_Time_Levels", S.toString());
        mEditor.commit();
    }

    //Custom arrayadapter so we can have a seekbar and editText for each goal
    private class SeekBarAdapter extends ArrayAdapter<String> {
        private Context context;
        public SeekBarAdapter(Context context, int textViewResourceId, ArrayList<String> activityList) {
            super(context, textViewResourceId, activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.seekbar_row2, parent, false);
            final SeekBar sb = (SeekBar) convertView.findViewById(R.id.seekBar);
            EditText eT = (EditText) convertView.findViewById(R.id.editText2);

            //Android Recycle Problem: If the user scrolls up/down ,we need to re-populated the editText based on what they previously enter
            if (goalTimeEditTextMap.get(activitySet.get(position))!=null){
                eT.setText(goalTimeEditTextMap.get(activitySet.get(position)).getText().toString());
            }
            goalTimeEditTextMap.put(activitySet.get(position),eT);
            TextView oldValue = (TextView) convertView.findViewById(R.id.oldValuetextView);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView delta = (TextView) convertView.findViewById(R.id.deltatextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);

            //Set the users currentLevel in the layout and set the text of the layout to correspond
            oldValue.setText("Cur: "+Integer.toString(activitySetLevels.get(position)));
            if (activitySet.get(position).toString().contains("_REP")){
                sb.setMax(500);
            }else if (activitySet.get(position).toString().contains("_TIM")){
                sb.setMax(25);
            }else{
                if(activitySet.get(position).toString().contains("_DTA-T")){
                    sb.setMax(25);
                }else if(activitySet.get(position).toString().contains("_DTA-D")){
                    if (activitySet.get(position).toString().contains("Swimming")) {
                        sb.setMax(1000);
                    } else if(activitySet.get(position).toString().contains("Running")) {
                        sb.setMax(50);
                    } else {
                        sb.setMax(200);
                    }
                }
            }
            if (activitySet.get(position).contains("_REP")) {
                oldValue.setText(oldValue.getText().toString().concat(" Reps"));
            } else if (activitySet.get(position).contains("_TIM")) {
                oldValue.setText(oldValue.getText().toString().concat(" Hours"));
            } else {
                if (activitySet.get(position).contains("_DTA-T")) {
                    oldValue.setText(oldValue.getText().toString().concat(" Hours"));
                } else if (activitySet.get(position).contains("_DTA-D")) {
                    if (activitySet.get(position).contains("Swimming")){
                        oldValue.setText(oldValue.getText().toString().concat(" Laps"));
                    }else{
                        oldValue.setText(oldValue.getText().toString().concat(" Miles"));
                    }
                }
            }
            title.setText(activitySet.get(position).split("_")[0]);
            if (goalLevelMap.get(activitySet.get(position))!= null) {
                sb.setProgress(goalLevelMap.get(activitySet.get(position)));
            }
            int delt = sb.getProgress()-activitySetLevels.get(position);
            if (delt>=0){
                delta.setText("+".concat(Integer.toString(delt)));
                delta.setTextColor(Color.parseColor("#A4C739"));

            }else{
                delta.setText("-".concat(Integer.toString(delt)));
                delta.setTextColor(Color.parseColor("#ff0000"));
            }

            //When the seekbar is changed, we update the UI Accordingly
            //We also update our Map<> so that we have a record of each activity and its goal
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    //Prohibits goal from being less than the starting value
                    if (seekBar.getProgress()<activitySetLevels.get(position)){
                        sb.setProgress(activitySetLevels.get(position));
                    }
                    //Puts the data in our map
                    goalLevelMap.put(activitySet.get(position), seekBar.getProgress());
                    int delt = sb.getProgress()-activitySetLevels.get(position);
                    if (delt>=0){
                        delta.setText("+".concat(Integer.toString(delt)));
                        delta.setTextColor(Color.parseColor("#A4C739"));

                    }else{
                        delta.setText("-".concat(Integer.toString(delt)));
                        delta.setTextColor(Color.parseColor("#ff0000"));
                    }

                    if (activitySet.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    } else if (activitySet.get(position).contains("_TIM")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                    } else {
                        if (activitySet.get(position).contains("_DTA-T")) {
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        } else if (activitySet.get(position).contains("_DTA-D")) {
                           if (activitySet.get(position).contains("Swimming")){
                               progress.setText(Integer.toString(seekBar.getProgress()).concat(" Laps"));
                           }else{
                               progress.setText(Integer.toString(seekBar.getProgress()).concat(" Miles"));
                           }

                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            //String parsing for our layout
            if (activitySet.get(position).contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (activitySet.get(position).contains("_TIM")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            } else {
                if (activitySet.get(position).contains("_DTA-T")) {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                }else if(activitySet.get(position).contains("Swimming")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Laps"));
                } else{
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }
}
