package com.cs400.gamifyhealth;

import android.app.Activity;
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



import java.util.ArrayList;
import java.util.Arrays;
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
    private ArrayList<String> activitySet;
    private ArrayList<Integer> activitySetLevels;
    private Map<String,Integer> goalLevelMap;
    private Map<String,EditText> goalTimeEditTextMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        continueButton = (Button) findViewById(R.id.continueButton2);
        mListView = (ListView) findViewById(R.id.seekBarListView);
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String[] activitySetString = sharedPrefs.getString("ACTIVITIES",null).split(",");
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");
        goalTimeEditTextMap = new HashMap<String, EditText>();
        activitySet = new ArrayList<String>();
        activitySetLevels = new ArrayList<Integer>();
        goalLevelMap = new HashMap<String, Integer>();
        for (int i=0; i<activitySetString.length; i++){
                activitySet.add(i, activitySetString[i]);
                activitySetLevels.add(i,Integer.parseInt(activityStartValString[i]));
                goalLevelMap.put(activitySetString[i],Integer.parseInt(activityStartValString[i]));
        }
        mAdapter = new SeekBarAdapter(getApplicationContext(),R.layout.seekbar_row2,activitySet);
        mListView.setAdapter(mAdapter);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),NavigationDrawerMain.class);
                startActivity(i);
            }
        });
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

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

      // / Log.d("TAG", "GOALS: " + temp.toString());
      //  Log.d("TAG", "time: " + S.toString());
        mEditor.commit();
    }


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
            if (goalTimeEditTextMap.get(activitySet.get(position))!=null){
                eT.setText(goalTimeEditTextMap.get(activitySet.get(position)).getText().toString());
            }
            goalTimeEditTextMap.put(activitySet.get(position),eT);
            TextView oldValue = (TextView) convertView.findViewById(R.id.oldValuetextView);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView delta = (TextView) convertView.findViewById(R.id.deltatextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);
            oldValue.setText("Cur: "+Integer.toString(activitySetLevels.get(position)));

            if (activitySet.get(position).contains("_REP")) {
                oldValue.setText(oldValue.getText().toString().concat(" Reps"));
            } else if (activitySet.get(position).contains("_TIM")) {
                oldValue.setText(oldValue.getText().toString().concat(" Hours"));
            } else {
                if (activitySet.get(position).contains("_DTA_T")) {
                    oldValue.setText(oldValue.getText().toString().concat(" Hours"));
                } else if (activitySet.get(position).contains("_DTA_D")) {
                    oldValue.setText(oldValue.getText().toString().concat(" Miles"));
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
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //TODO: Verfiy this
                    if (seekBar.getProgress()<activitySetLevels.get(position)){
                        sb.setProgress(activitySetLevels.get(position));
                    }
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

            if (activitySet.get(position).contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (activitySet.get(position).contains("_TIM")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            } else {
                if (activitySet.get(position).contains("_DTA_T")) {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                } else {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }
}
