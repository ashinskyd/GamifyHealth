package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//Activity is used to gather the current fitness level of a user
public class CurrentActivityLevel extends Activity {
    //Declares the data structures to hold the information in addition to the layout values
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private ArrayList<String> activityList;
    private SeekBarAdapter mAdapter;
    private Button coninueButton;
    private Map<String,Integer> currentLevel;
    private UnitConverter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_activity_level);
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mListView = (ListView) findViewById(R.id.seekBarListView);
        coninueButton = (Button) findViewById(R.id.continueButton2);
        coninueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On button click, launch the next activity
                Intent i = new Intent(getBaseContext(),GoalSetActivity.class);
                final AlphaAnimation buttonClick = new AlphaAnimation(1F,0.8F);
                view.startAnimation(buttonClick);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        currentLevel = new HashMap<String, Integer>();
        //Map used to store the key,value of activty-name,Value for every activity
        activityList = new ArrayList<String>();
        //Arraylist is gatherd from sharedprefs and holds a master list of all activiteis
        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        for(int i=0;i<activities.length;i++){
            //Loop puts all activities and a default value of 0 into the loop
            activityList.add(i,activities[i]);
            currentLevel.put(activities[i],0);
        }
        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row,activityList);
        mListView.setAdapter(mAdapter);
        converter = new UnitConverter();
    }

    @Override
    public void onPause(){
        super.onPause();
        /*Upon pausing we store in shared prefs the following:
        *@names is the string of all the activities
        *@levels is the current fitness level for all activities
        *NOTE: In shared prefs, we store 2 strings, 1 for the names and 1 for the values. They correspond 1 to 1 just as in a Map
        */
        StringBuilder names = new StringBuilder();
        StringBuilder levels = new StringBuilder();
        Iterator entryIter = currentLevel.entrySet().iterator();
        while (entryIter.hasNext()){
            Map.Entry entry = (Map.Entry) entryIter.next();
            names.append(((String) entry.getKey()).concat(","));
            levels.append(entry.getValue().toString().concat(","));
        }
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        /*
        *@ACTIVITIES: the sharedprefs string of all the activites: "Running (Time)_DTA-T,Ping-Pong_REP..."
        *@Activity_Prelim_Levels: the sharedprefs string of the preliminary fitness level
         */
        mEditor.putString("ACTIVITIES",names.toString());
        mEditor.putString("Activity_Prelim_Levels", levels.toString());
        mEditor.commit();
    }

    //Just sets the animation for back button press
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    //Inner class is a custom adapter for our listview. It uses a custom layout to inflate
    private class SeekBarAdapter extends ArrayAdapter<String> {
        private Context context;
        public SeekBarAdapter(Context context, int textViewResourceId, ArrayList<String> activityList){
            super(context, textViewResourceId,activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.seekbar_row, parent, false);
            final SeekBar mSeekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);
            String temp = activityList.get(position).split("_")[0];
            Button minusButton = (Button) convertView.findViewById(R.id.minus_button);
            Button plusButon = (Button) convertView.findViewById(R.id.plus_button);
            //Sets the scale of the seekbar based on the specific type of activity
            if (activityList.get(position).toString().contains("_REP")){
                mSeekBar.setMax(500);
            }else if (activityList.get(position).toString().contains("_TIM")){
                mSeekBar.setMax(100);
            }else{
                if(activityList.get(position).toString().contains("_DTA-T")){
                    mSeekBar.setMax(100);
                }else if(activityList.get(position).toString().contains("_DTA-D")){
                    if (activityList.get(position).toString().contains("Swimming")) {
                        mSeekBar.setMax(1000);
                    } else if(activityList.get(position).toString().contains("Running")) {
                        mSeekBar.setMax(200);
                    } else {
                        mSeekBar.setMax(200);
                    }
                }
            }
            title.setText(temp);
            //Required to set some text before proceding to avoid a null ptr
            progress.setText("HI");
            if(currentLevel.get(activityList.get(position))!=null){
                mSeekBar.setProgress(currentLevel.get(activityList.get(position)));
            }

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSeekBar.setProgress(mSeekBar.getProgress()-1);
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                }
            });

            plusButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSeekBar.setProgress(mSeekBar.getProgress()+1);
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                }
            });
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //Listens for seekbar sliding
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //When progress is changed, update our currentlevel map and adjust the textview to display the value
                    currentLevel.put(activityList.get(position), seekBar.getProgress());
                    if (activityList.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(mSeekBar.getProgress()).concat(" Reps"));
                    } else if (activityList.get(position).contains("_TIM")) {
                        String displayString = converter.convertUnit(mSeekBar.getProgress(), "TIM");
                        progress.setText(displayString);
                    } else {
                        if (activityList.get(position).contains("_DTA-T")) {
                            String displayString = converter.convertUnit(mSeekBar.getProgress(), "DTA-T");
                            progress.setText(displayString);
                        }else if(activityList.get(position).contains("Swimming")){
                            progress.setText(Integer.toString(mSeekBar.getProgress()).concat(" Laps"));
                        } else{
                            String displayString = converter.convertUnit(mSeekBar.getProgress(), "DTA-D");
                            progress.setText(displayString);
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
            if (activityList.get(position).contains("_REP")) {
                progress.setText(Integer.toString(mSeekBar.getProgress()).concat(" Reps"));
            } else if (activityList.get(position).contains("_TIM")) {
                String displayString = converter.convertUnit(mSeekBar.getProgress(), "TIM");
                progress.setText(displayString);
            } else {
                if (activityList.get(position).contains("_DTA-T")) {
                    String displayString = converter.convertUnit(mSeekBar.getProgress(), "DTA-T");
                    progress.setText(displayString);
                }else if(activityList.get(position).contains("Swimming")){
                    progress.setText(Integer.toString(mSeekBar.getProgress()).concat(" Laps"));
                } else{
                    String displayString = converter.convertUnit(mSeekBar.getProgress(), "DTA-D");
                    progress.setText(displayString);
                }
            }
            return convertView;
        }
    }



}
