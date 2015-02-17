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


    private String mParam1;
    private String mParam2;

    //use 2 arrays to keep track of activities to add/remove
    private ArrayList<String> addSet;
    private ArrayList<String> activitySet;

    //Same scheme as GoalSetActivity for storing new goals
    private Map<String,Integer> goalMap;
    private Map<String,EditText> goalTimeEditTextMap;
    private ArrayList<Integer> activitySetLevels;
    private Button continueButton;
    private ListView mListView;
    private SharedPreferences sharedPrefs;
    private Map<String,Integer> goalLevelMap;
    private SeekBarAdapter mAdapter;
    private UnitConverter converter;

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
        converter = new UnitConverter();
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
                Boolean proceed = false;
                proceed = checkEntries();
                if (proceed){
                    addNewGoals();
                }else{
                    //If theres an input error, we don't proceed but throw a Toast
                    Toast toast = Toast.makeText(getActivity(),"Please Enter a Duration of at least 4 weeks for each goal",Toast.LENGTH_SHORT);
                    toast.show();
                }


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
            Button minusButton = (Button) convertView.findViewById(R.id.minus_button);
            Button plusButton = (Button) convertView.findViewById(R.id.plus_button);
            //Android Recycle Problem: If the user scrolls up/down, we need to re-populate the editText based on what they previously enter
            if (goalTimeEditTextMap.get(activitySet.get(position))!=null){
                eT.setText(goalTimeEditTextMap.get(activitySet.get(position)).getText().toString());
            }
            goalTimeEditTextMap.put(activitySet.get(position),eT);
            TextView oldValue = (TextView) convertView.findViewById(R.id.oldValuetextView);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView delta = (TextView) convertView.findViewById(R.id.deltatextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);

            //Set the user's current level in the layout and set the text of the layout to correspond

            if (activitySet.get(position).toString().contains("_REP")){
                sb.setMax(500);
            }else if (activitySet.get(position).toString().contains("_TIM")){
                sb.setMax(100);
            }else{
                if(activitySet.get(position).toString().contains("_DTA-T")){
                    sb.setMax(100);
                }else if(activitySet.get(position).toString().contains("_DTA-D")){
                    if (activitySet.get(position).toString().contains("Swimming")) {
                        sb.setMax(1000);
                    } else if(activitySet.get(position).toString().contains("Running")) {
                        sb.setMax(200);
                    } else {
                        sb.setMax(200);
                    }
                }
            }

            title.setText(activitySet.get(position).split("_")[0]);
            if (goalLevelMap.get(activitySet.get(position))!= null) {
                sb.setProgress(goalLevelMap.get(activitySet.get(position)));
            }
            int delt = sb.getProgress()-activitySetLevels.get(position);

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb.setProgress(sb.getProgress()+1);
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb.setProgress(sb.getProgress()-1);
                }
            });

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
                    if (activitySet.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                        delta.setText("+".concat(Integer.toString(delt)));
                        delta.setTextColor(Color.parseColor("#A4C739"));
                    } else if (activitySet.get(position).contains("_TIM")) {
                        String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                        progress.setText(displayString);
                        String deltaString = "+ "+converter.convertUnit(delt,"TIM");
                        delta.setText(deltaString);
                        delta.setTextColor(Color.parseColor("#A4C739"));
                    } else {
                        if (activitySet.get(position).contains("_DTA-T")) {
                            String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                            progress.setText(displayString);
                            String deltaString = "+ "+converter.convertUnit(delt,"DTA-T");
                            delta.setText(deltaString);
                            delta.setTextColor(Color.parseColor("#A4C739"));
                        }else if(activitySet.get(position).contains("Swimming")){
                            progress.setText(Integer.toString(sb.getProgress()).concat(" Laps"));
                            delta.setText("+".concat(Integer.toString(delt)));
                            delta.setTextColor(Color.parseColor("#A4C739"));
                        } else{
                            String displayString = converter.convertUnit(sb.getProgress(),"DTA-D");
                            progress.setText(displayString);
                            String deltaString = "+ "+converter.convertUnit(delt,"DTA-D");
                            delta.setText(deltaString);
                            delta.setTextColor(Color.parseColor("#A4C739"));
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
                delta.setText("+".concat(Integer.toString(delt)));
                delta.setTextColor(Color.parseColor("#A4C739"));
                oldValue.setText("Cur: "+Integer.toString(activitySetLevels.get(position))+ " Reps");
            } else if (activitySet.get(position).contains("_TIM")) {
                String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                progress.setText(displayString);
                String deltaString = "+ "+converter.convertUnit(delt,"TIM");
                delta.setText(deltaString);
                delta.setTextColor(Color.parseColor("#A4C739"));
                String currentLevel = converter.convertUnit(activitySetLevels.get(position),"TIM");
                oldValue.setText("Cur: "+currentLevel);
            } else {
                if (activitySet.get(position).contains("_DTA-T")) {
                    String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                    progress.setText(displayString);
                    String deltaString = "+ "+converter.convertUnit(delt,"DTA-T");
                    delta.setText(deltaString);
                    delta.setTextColor(Color.parseColor("#A4C739"));
                    String currentLevel = converter.convertUnit(activitySetLevels.get(position),"DTA-T");
                    oldValue.setText("Cur: "+currentLevel);
                }else if(activitySet.get(position).contains("Swimming")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Laps"));
                    delta.setText("+".concat(Integer.toString(delt)));
                    delta.setTextColor(Color.parseColor("#A4C739"));
                    oldValue.setText("Cur: "+Integer.toString(activitySetLevels.get(position)).concat(" Laps"));
                } else{
                    String displayString = converter.convertUnit(sb.getProgress(),"DTA-D");
                    progress.setText(displayString);
                    String deltaString = "+ "+converter.convertUnit(delt,"DTA-D");
                    delta.setText(deltaString);
                    delta.setTextColor(Color.parseColor("#A4C739"));
                    String currentLevel = converter.convertUnit(activitySetLevels.get(position),"DTA-D");
                    oldValue.setText("Cur: "+currentLevel);
                }
            }

            return convertView;
        }

    }

}
