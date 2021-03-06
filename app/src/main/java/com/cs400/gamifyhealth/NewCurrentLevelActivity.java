package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import java.util.List;
import java.util.Map;


public class NewCurrentLevelActivity extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private ArrayList<String> addSet;
    private ArrayList<String> addSetCopy;
    private Map<String, Integer> currentLevel;
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private Button continueButton;
    private SeekBarAdapter mAdapter;
    private List<String> repArray;
    private List<String> dtaArray;
    private UnitConverter converter;

    public static NewCurrentLevelActivity newInstance(String param1, String param2) {
        NewCurrentLevelActivity fragment = new NewCurrentLevelActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewCurrentLevelActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSet = new ArrayList<String>();
        currentLevel = new HashMap<String, Integer>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_new_current_level, container, false);
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mListView = (ListView) V.findViewById(R.id.seekBarListView);
        continueButton = (Button) V.findViewById(R.id.continueButton2);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCurrentLevels();

            }
        });
        converter = new UnitConverter();
        Bundle b = getArguments();
        addSet = b.getStringArrayList("AddSet");
        addSetCopy = new ArrayList<String>();

        repArray = Arrays.asList(getString(R.string.activity_types_REP).split(","));
        dtaArray = Arrays.asList(getString(R.string.activity_types_DTA).split(","));
        initAddSet();
        mAdapter = new SeekBarAdapter(getActivity(), R.layout.seekbar_row, addSetCopy);
        mListView.setAdapter(mAdapter);
        return V;
    }

    private void initAddSet() {
        for (String i : addSet) {
            if (repArray.contains(i)) {
                currentLevel.put(i.concat("_REP"), 0);
                addSetCopy.add(i.concat("_REP"));
            } else if (dtaArray.contains(i)) {
                if (i.contains("Time")) {
                    currentLevel.put(i.concat("_DTA-T"), 0);
                    addSetCopy.add(i.concat("_DTA-T"));
                } else {
                    currentLevel.put(i.concat("_DTA-D"), 0);
                    addSetCopy.add(i.concat("_DTA-D"));
                }
            } else {
                currentLevel.put(i.concat("_TIM"), 0);
                addSetCopy.add(i.concat("_TIM"));
            }
        }
    }

    private void addCurrentLevels() {
        StringBuilder activities = new StringBuilder();
        activities.append(sharedPrefs.getString("ACTIVITIES", ""));
        StringBuilder preselectedCurrentLevels = new StringBuilder();
        preselectedCurrentLevels.append(sharedPrefs.getString("Activity_Prelim_Levels",""));

        for (String s: addSetCopy){
            activities.append(s).append(",");
            preselectedCurrentLevels.append(currentLevel.get(s)).append(",");
        }
        //Pass the goal set fragment our new activity set
        Bundle b = new Bundle();
        b.putStringArrayList("ADDED_ACTIVITIES",addSetCopy);
        b.putString("ACTIVITIES",activities.toString());
        b.putString("Activity_Prelim_Levels",preselectedCurrentLevels.toString());
        FragmentTransaction transaction;
        NewGoalSetFragment goalSetFragment = new NewGoalSetFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, goalSetFragment);
        goalSetFragment.setArguments(b);
        transaction.commit();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            String temp = addSetCopy.get(position).split("_")[0];
            Button plusButton = (Button) convertView.findViewById(R.id.plus_button);
            Button minusButton = (Button) convertView.findViewById(R.id.minus_button);


            if (addSetCopy.get(position).toString().contains("_REP")){
                sb.setMax(500);
            }else if (addSetCopy.get(position).toString().contains("_TIM")){
                sb.setMax(100);
            }else{
                if(addSetCopy.get(position).toString().contains("_DTA-T")){
                    sb.setMax(100);
                }else if(addSetCopy.get(position).toString().contains("_DTA-D")){
                    if (addSetCopy.get(position).toString().contains("Swimming")) {
                        sb.setMax(1000);
                    } else if(addSetCopy.get(position).toString().contains("Running")) {
                        sb.setMax(200);
                    } else {
                        sb.setMax(200);
                    }
                }
            }
            title.setText(temp);
            //Required to set some text before proceding to avoid a null ptr
            progress.setText("HI");
            if(currentLevel.get(addSetCopy.get(position))!=null){
                sb.setProgress(currentLevel.get(addSetCopy.get(position)));
            }
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb.setProgress(sb.getProgress()+1);
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb.setProgress(sb.getProgress()-1);
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                }
            });
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //Listens for seekbar sliding
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //When progress is changed, update our currentlevel map and adjust the textview to display the value
                    currentLevel.put(addSetCopy.get(position),seekBar.getProgress());
                    if (addSetCopy.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    } else if (addSetCopy.get(position).contains("_TIM")) {
                        String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                        progress.setText(displayString);
                    } else {
                        if (addSetCopy.get(position).contains("_DTA-T")) {
                            String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                            progress.setText(displayString);
                        }else if(addSetCopy.get(position).contains("Swimming")){
                            progress.setText(Integer.toString(sb.getProgress()).concat(" Laps"));
                        } else{
                            String displayString = converter.convertUnit(sb.getProgress(),"DTA-D");
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

            //Adds the proper unit to the progress view
            if (addSetCopy.get(position).contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (addSetCopy.get(position).contains("_TIM")) {
                String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                progress.setText(displayString);
            } else {
                if (addSetCopy.get(position).contains("_DTA-T")) {
                    String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                    progress.setText(displayString);
                }else if(addSetCopy.get(position).contains("Swimming")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Laps"));
                } else{
                    String displayString = converter.convertUnit(sb.getProgress(),"DTA-D");
                    progress.setText(displayString);
                }
            }

            return convertView;
        }
    }
}