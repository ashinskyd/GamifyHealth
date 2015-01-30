package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
                Log.d("TAG", "BUTTON CLICKED");
            }
        });
        Bundle b = getArguments();
        addSet = b.getStringArrayList("AddSet");
        addSetCopy = new ArrayList<String>();
        List<String> repArray;
        List<String> dtaArray;
        repArray = Arrays.asList(getString(R.string.activity_types_REP).split(","));
        dtaArray = Arrays.asList(getString(R.string.activity_types_DTA).split(","));
        for (String i : addSet) {
            if (repArray.contains(i)) {
                currentLevel.put(i.concat("_REP,"), 0);
                addSetCopy.add(i.concat("_REP,"));
            } else if (dtaArray.contains(i)) {
                if (i.contains("Time")) {
                    currentLevel.put(i.concat("_DTA-T,"), 0);
                    addSetCopy.add(i.concat("_DTA-T,"));
                } else {
                    currentLevel.put(i.concat("_DTA-D,"), 0);
                    addSetCopy.add(i.concat("_DTA-D,"));
                }
            } else {
                currentLevel.put(i.concat("_TIM,"), 0);
                addSetCopy.add(i.concat("_TIM,"));
            }
        }
        mAdapter = new SeekBarAdapter(getActivity(), R.layout.seekbar_row, addSetCopy);
        mListView.setAdapter(mAdapter);
        return V;
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
            String temp = addSet.get(position);
            title.setText(temp);
            //Required to set some text before proceding to avoid a null ptr
            progress.setText("HI");
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //Listens for seekbar sliding
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //When progress is changed, update our currentlevel map and adjust the textview to display the value
                    currentLevel.put(addSet.get(position), seekBar.getProgress());
                    if (addSetCopy.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    } else if (addSetCopy.get(position).contains("_TIM")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                    } else {
                        if (addSetCopy.get(position).contains("_DTA-T")) {
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        } else if (addSetCopy.get(position).toString().contains("_DTA-D")) {
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

            //Adds the proper unit to the progress view
            if (addSetCopy.get(position).contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (addSetCopy.get(position).contains("_TIM")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            } else {
                if (addSetCopy.get(position).contains("_DTA-T")) {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                } else {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }


    }
}