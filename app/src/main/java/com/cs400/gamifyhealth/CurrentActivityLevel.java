package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CurrentActivityLevel extends Activity {
    private ListView mListView;
    private SeekBar slider;
    private SharedPreferences sharedPrefs;
    private ArrayList<String> activityList;
    private SeekBarAdapter mAdapter;
    private Map<String,Integer> currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_activity_level);
        mListView = (ListView) findViewById(R.id.seekBarListView);
        currentLevel = new HashMap<String, Integer>();
        activityList = new ArrayList<String>();
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        for(int i=0;i<activities.length;i++){
           activityList.add(activities[i]);

        }
        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row,activityList);
        mListView.setAdapter(mAdapter);
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
            title.setText(activityList.get(position));
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
                        if(activityList.get(position).toString().contains("_DTA_T")){
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        }else if(activityList.get(position).toString().contains("_DTA_D")){
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

            if (title.getText().toString().contains("_REP")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            }else if (title.getText().toString().contains("_TIM")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            }else{
                if (title.getText().toString().contains("_DTA_T")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                }else{
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }



}
