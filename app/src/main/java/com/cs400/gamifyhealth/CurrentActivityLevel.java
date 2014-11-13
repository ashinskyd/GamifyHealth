package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class CurrentActivityLevel extends Activity {
    private LinearLayout container;
    private SeekBar slider;
    private SharedPreferences sharedPrefs;
    private Map<Integer,String> idMap;
    private Map<Integer,TextView> idMap2;
    private TextView title;
    private TextView progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_activity_level);
        idMap = new HashMap<Integer,String>();
        idMap2 = new HashMap<Integer,TextView>();
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        container = (LinearLayout) findViewById(R.id.container);

        for(int i=0;i<activities.length;i++){
            slider = new SeekBar(getApplicationContext());
            title = new TextView(getApplicationContext());

            progress = new TextView(getApplicationContext());
            progress.setGravity(Gravity.CENTER);
            progress.setText(Integer.toString(slider.getProgress()));

            title.setText(activities[i]);
            idMap.put(i, activities[i]);
            idMap2.put(i,progress);
            slider.setId(i);
            slider.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable));
            slider.setThumb(getResources().getDrawable(R.drawable.thumb_drawable));

            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    Log.d("TAG","ID: "+idMap.get(seekBar.getId()));
                    Log.d("TAG","Value Set: "+seekBar.getProgress());
                    TextView v = idMap2.get(seekBar.getId());//.setText(seekBar.getProgress());
                    v.setText(Integer.toString(seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

            });
            container.addView(title);
            container.addView(slider);
            container.addView(progress);
        }


    }




}
