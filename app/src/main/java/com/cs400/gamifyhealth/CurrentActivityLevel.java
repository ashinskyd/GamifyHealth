package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CurrentActivityLevel extends Activity {
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private ArrayList<String> activityList;
    private SeekBarAdapter mAdapter;
    private Button coninueButton;
    private Map<String,Integer> currentLevel;

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
                Intent i = new Intent(getBaseContext(),GoalSetActivity.class);
                final AlphaAnimation buttonClick = new AlphaAnimation(1F,0.8F);
                view.startAnimation(buttonClick);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        currentLevel = new HashMap<String, Integer>();
        activityList = new ArrayList<String>();
        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        for(int i=0;i<activities.length;i++){
            activityList.add(i,activities[i]);
            currentLevel.put(activities[i],0);
        }
        //TODO: Should really be looking at the shared prefs to populate any existing values
        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row,activityList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        StringBuilder temp = new StringBuilder();
        StringBuilder S = new StringBuilder();
        Iterator entryIter = currentLevel.entrySet().iterator();
        while (entryIter.hasNext()){
            Map.Entry entry = (Map.Entry) entryIter.next();
            temp.append(((String) entry.getKey()).concat(","));
            S.append(entry.getValue().toString().concat(","));
        }
        SharedPreferences.Editor mEditor = sharedPrefs.edit();
        mEditor.putString("ACTIVITIES",temp.toString());
        mEditor.putString("Activity_Prelim_Levels", S.toString());
        mEditor.commit();
    }


    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }


    private class SeekBarAdapter extends ArrayAdapter<String> {
        private Context context;
        public SeekBarAdapter(Context context, int textViewResourceId, ArrayList<String> activityList){
            super(context, textViewResourceId,activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.seekbar_row, parent, false);
            final SeekBar sb = (SeekBar) convertView.findViewById(R.id.seekBar);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);
            String temp = activityList.get(position).split("_")[0];
            title.setText(temp);
            progress.setText("HI");
            if(currentLevel.get(activityList.get(position))!=null){
                sb.setProgress(currentLevel.get(activityList.get(position)));
            }
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    currentLevel.put(activityList.get(position),seekBar.getProgress());
                    if (activityList.get(position).toString().contains("_REP")){
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    }else if (activityList.get(position).toString().contains("_TIM")){
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                    }else{
                        if(activityList.get(position).toString().contains("_DTA-T")){
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        }else if(activityList.get(position).toString().contains("_DTA-D")){
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Miles"));
                        }
                    }
                    //TODO: Keep track of the progress for all the types somehow, then save it somewhere...
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            if (activityList.get(position).contains("_REP")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            }else if (activityList.get(position).contains("_TIM")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            }else{
                if (activityList.get(position).contains("_DTA-T")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                }else{
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }



}
