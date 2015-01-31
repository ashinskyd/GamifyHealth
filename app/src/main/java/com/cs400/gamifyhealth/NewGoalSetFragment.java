package com.cs400.gamifyhealth;



import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class NewGoalSetFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> addSet;
    private ArrayList<String> activitySet;
    private Map<String,Integer> goalMap;
    private Map<String,EditText> goalTimeEditTextMap;
    private ArrayList<Integer> activitySetLevels;
    private Button continueButton;
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private Map<String,Integer> goalLevelMap;
    private SeekBarAdapter mAdapter;

    public static NewGoalSetFragment newInstance(String param1, String param2) {
        NewGoalSetFragment fragment = new NewGoalSetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public NewGoalSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_new_goal_set, container, false);
        Bundle b = getArguments();
        addSet = b.getStringArrayList("ADDED_ACTIVITIES");

        activitySetLevels = new ArrayList<Integer>();
        goalTimeEditTextMap = new HashMap<String, EditText>();
        goalLevelMap = new HashMap<String, Integer>();
        activitySet = new ArrayList<String>();

        continueButton = (Button) V.findViewById(R.id.continueButton2);
        mListView = (ListView) V.findViewById(R.id.seekBarListView);
        mListView.setItemsCanFocus(true);
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewGoals();

            }
        });
        initActivitySets();
        mAdapter = new SeekBarAdapter(getActivity(),R.layout.seekbar_row2,activitySet);
        mListView.setAdapter(mAdapter);
        return V;
    }

    private void initActivitySets() {
        String[] activitySetString = sharedPrefs.getString("ACTIVITIES",null).split(",");
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");
        int j = 0;
        for (int i=0; i<activitySetString.length; i++){
            if (addSet.contains(activitySetString[i])){
                activitySet.add(j, activitySetString[i]);
                activitySetLevels.add(j,Integer.parseInt(activityStartValString[i]));
                goalLevelMap.put(activitySetString[i],Integer.parseInt(activityStartValString[i]));
                j++;
            }

        }
    }

    private void addNewGoals() {
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        ContentValues values = new ContentValues();
        GregorianCalendar c = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(c.getTime());
        for (int i=0;i<activitySet.size();i++){
            int goalTarget = goalLevelMap.get(activitySet.get(i));
            int goalDuration = Integer.parseInt(goalTimeEditTextMap.get(activitySet.get(i)).getText().toString());
            int startValue = activitySetLevels.get(i);
            Log.d("TAG","START: "+startValue);
            Log.d("TAG","TARGET: "+goalTarget);
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
            CharSequence text = "ADDED ACTIVITY: "+activityName.split("_")[0];
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
        }
        DBConnection datasource = new DBConnection(getActivity());
        datasource.open();
        try{
            for (Goal g: goalList){
                datasource.insertGoal(g);
            }
        }catch (ParseException e){
            Log.d("TAG","Exception Caught");
        }
        datasource.close();
        FragmentTransaction transaction;
        GameFragment gameFragment = new GameFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, gameFragment);
        transaction.commit();

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
                if (activitySet.get(position).contains("_DTA-T")) {
                    oldValue.setText(oldValue.getText().toString().concat(" Hours"));
                } else if (activitySet.get(position).contains("_DTA-D")) {
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
                        if (activitySet.get(position).contains("_DTA-T")) {
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        } else if (activitySet.get(position).contains("_DTA-D")) {
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
                if (activitySet.get(position).contains("_DTA-T")) {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                } else {
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }

}