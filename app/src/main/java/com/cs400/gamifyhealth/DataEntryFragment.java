package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


//This fragment is our way for the user to enter a workout. It is inflated in NavigationDrawerMainActivity
public class DataEntryFragment extends Fragment implements WorkoutDialogFragment.NoticeDialogListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ArrayList<String> activityList;
    //Gets an array of all of the users relevant activities
    private Map<String,Integer> currentLevel;
    //Map records the activty,value for all activities and their values
    private ArrayList<Integer> activitySetLevels;
    //Used to store the levels for intermediate processing

    //Our layout objects
    private ListView mListView;
    private SeekBarAdapter mAdapter;
    private SharedPreferences sharedPrefs;
    private UnitConverter converter;

    public static DataEntryFragment newInstance(String param1, String param2) {
        //Factory Method to create teh fragment
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public DataEntryFragment() {
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
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_data_entry, container, false);
        getActivity().getActionBar().setTitle("Workout Entry");
        currentLevel = new HashMap<String, Integer>();
        activityList = new ArrayList<String>();
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        //Gets the relevant activity set from sharedprefs

        converter = new UnitConverter();
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");
        //Gets the prelim level generated at setup

        activitySetLevels = new ArrayList<Integer>();
        for(int i=0;i<activities.length;i++){
            activityList.add(i,activities[i]);
            activitySetLevels.add(i,Integer.parseInt(activityStartValString[i]));
            //Adds the activity and the given level to our arrays
            currentLevel.put(activities[i],0);
        }
        mListView = (ListView) V.findViewById(R.id.seekBarListView);
        mAdapter = new SeekBarAdapter(getActivity().getApplicationContext(),R.layout.seekbar_row,activityList);
        mListView.setAdapter(mAdapter);
        Button confirm = (Button) V.findViewById(R.id.continueButton2);
        //When user enters a workout, shows a dialog to confirm their entries
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On buttonclick, inflate a dialog asking user to confirm their selection or cancel
                WorkoutDialogFragment dialog = new WorkoutDialogFragment();
                dialog.setTargetFragment(DataEntryFragment.this, 0);
                dialog.show(getActivity().getFragmentManager(), "DialogFragment");
            }
        });
        return V;
    }




    @Override
    public void onAttach(Activity activity) {
        //makes sure our activity listens for fragment changes (unused)
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDialogPositiveClick() {
        //Listener implementation-Upon confirmation we do some processing
        ArrayList<Workout> workoutArray = new ArrayList<Workout>();
        //Array of workout objects

        for(Map.Entry<String, Integer> entry: currentLevel.entrySet()) {
            //Loop iterates through the users activities and creates a set of workout objects based on their selections
            String activity = entry.getKey();
            int indexOfSpace = activity.indexOf(" ");
            String activityName = activity;

            //This is string processing to have appropriate interaction with the database
            if (indexOfSpace != -1 && activity.charAt(indexOfSpace + 1) == '(') {
                activityName = activity.substring(0, indexOfSpace);
            } else if (indexOfSpace != -1 && activity.charAt(indexOfSpace + 1) != '(') {
                activityName = activity.split("_")[0];
            } else {
                activityName = activity.split("_")[0];
            }
            String activityType = activity.split("_")[1];
            int unit = entry.getValue();
            Workout w = new Workout(activityName,unit,activityType);
            workoutArray.add(w);
        }

        //Opens the database and inserts all of the workouts
        DBConnection datasource = new DBConnection(getActivity());
        datasource.open();
        for (Workout w: workoutArray){
            datasource.insertWorkout(w);
        }
        datasource.close();

        //If credits/population need to be updated, we update accordingly
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String currentDate = dateFormat.format(cal.getTime());
        currentDate = currentDate.substring(currentDate.indexOf("-")+1);
        String oldDate = sharedPrefs.getString("LAST_WORKOUT"," ");
        EarningsEngine earningsEngine = new EarningsEngine(getActivity());
        if (!currentDate.equals(oldDate)){
            SharedPreferences.Editor mEditor = sharedPrefs.edit();
            mEditor.putString("LAST_WORKOUT", currentDate);
            mEditor.commit();
            earningsEngine.weeklyGoalCheck();
            earningsEngine.postWorkout();
            earningsEngine.updatePop();

        }
        FragmentTransaction transaction;
        GameFragment gameFragment = new GameFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, gameFragment);
        transaction.commit();
    }

    @Override
    public void onDialogNegativeClick() {
        Log.d("TAG","Positive Callback");
    }
    //If the user cancels from the dialog, we do nothing



    //Same custom adapter from currentActivityLevel. Used to populate listview
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
            final SeekBar sb = (SeekBar) convertView.findViewById(R.id.seekBar);
            TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
            final TextView progress = (TextView) convertView.findViewById(R.id.progressTextView);
            Button minusButton = (Button) convertView.findViewById(R.id.minus_button);
            Button plusButton = (Button) convertView.findViewById(R.id.plus_button);

            String temp = activityList.get(position).split("_")[0];
            if (activityList.get(position).toString().contains("_REP")){
                sb.setMax(125);
            }else if (activityList.get(position).toString().contains("_TIM")){
                sb.setMax(25);
            }else{
                if(activityList.get(position).toString().contains("_DTA-T")){
                    sb.setMax(25);
                }else if(activityList.get(position).toString().contains("_DTA-D")){
                    if (activityList.get(position).toString().contains("Swimming")) {
                        sb.setMax(250);
                    } else if(activityList.get(position).toString().contains("Running")) {
                        sb.setMax(75);
                    } else {
                        sb.setMax(50);
                    }
                }
            }
            title.setText(temp);
            //Required to set some text before proceeding to avoid a null ptr
            progress.setText("HI");
            if(currentLevel.get(activityList.get(position))!=null){
                sb.setProgress(currentLevel.get(activityList.get(position)));
            }

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb.setProgress(sb.getProgress()-1);
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                    sb.setProgress(sb.getProgress()+1);
                }
            });
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //Listens for seekbar sliding
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //When progress is changed, update our currentlevel map and adjust the textview to display the value
                    currentLevel.put(activityList.get(position),seekBar.getProgress());
                    if (activityList.get(position).contains("_REP")) {
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    } else if (activityList.get(position).contains("_TIM")) {
                        String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                        progress.setText(displayString);
                    } else {
                        if (activityList.get(position).contains("_DTA-T")) {
                            String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                            progress.setText(displayString);
                        }else if(activityList.get(position).contains("Swimming")){
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
            if (activityList.get(position).contains("_REP")) {
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            } else if (activityList.get(position).contains("_TIM")) {
                String displayString = converter.convertUnit(sb.getProgress(),"TIM");
                progress.setText(displayString);
            } else {
                if (activityList.get(position).contains("_DTA-T")) {
                    String displayString = converter.convertUnit(sb.getProgress(),"DTA-T");
                    progress.setText(displayString);
                }else if(activityList.get(position).contains("Swimming")){
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
