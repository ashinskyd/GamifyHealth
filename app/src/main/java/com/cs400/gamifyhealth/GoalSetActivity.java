package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GoalSetActivity extends Activity {
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private SeekBarAdapter mAdapter;
    private Button continueButton;
    private ArrayList<String> activitySet;
    private ArrayList<Integer> activitySetLevels;
    private Map<String,Integer> activityLevelMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        continueButton = (Button) findViewById(R.id.continueButton2);
        mListView = (ListView) findViewById(R.id.seekBarListView);
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String[] activitySetString = sharedPrefs.getString("ACTIVITIES",null).split(",");
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");
        activitySet = new ArrayList<String>();
        activitySetLevels = new ArrayList<Integer>();
        activityLevelMap = new HashMap<String, Integer>();
        for (int i=0; i<activitySetString.length; i++){
                activitySet.add(i, activitySetString[i]);
                activitySetLevels.add(i,Integer.parseInt(activityStartValString[i]));
                activityLevelMap.put(activitySetString[i],Integer.parseInt(activityStartValString[i]));
        }
        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row,activitySet);
        mListView.setAdapter(mAdapter);
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }




    private class SeekBarAdapter extends ArrayAdapter<String> {
        private Context context;
        public SeekBarAdapter(Context context, int textViewResourceId, ArrayList<String> activityList) {
            super(context, textViewResourceId, activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.seekbar_row, parent, false);
            final SeekBar sb = (SeekBar) convertView.findViewById(R.id.seekBar);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);
            title.setText(activitySet.get(position).split("_")[0]);
            if (activityLevelMap.get(activitySet.get(position))!= null) {
                sb.setProgress(activityLevelMap.get(activitySet.get(position)));
            }
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    activityLevelMap.put(activitySet.get(position), seekBar.getProgress());
                    if (activitySet.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    } else if (activitySet.get(position).contains("_TIM")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                    } else {
                        if (activitySet.get(position).contains("_DTA_T")) {
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        } else if (activitySet.get(position).contains("_DTA_D")) {
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Miles"));
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

            if (title.getText().toString().contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (title.getText().toString().contains("_TIM")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            } else {
                if (title.getText().toString().contains("_DTA_T")) {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                } else {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }
}
