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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditActivitySetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditActivitySetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EditActivitySetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ActivityModel> activityItems;
    private OnFragmentInteractionListener mListener;
    private Button continueButton;
    private ArrayList<String> orignalSet;
    private ArrayList<String> addSet;
    private ArrayList<String> removeSet;
    private ArrayList<String> removeSetCopy;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor mEditor;




    public static EditActivitySetFragment newInstance(String param1, String param2) {
        EditActivitySetFragment fragment = new EditActivitySetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public EditActivitySetFragment() {
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
        activityItems = new ArrayList<ActivityModel>();
        View V = inflater.inflate(R.layout.fragment_edit_activity_set, container, false);

        //Pulls the original activity set stored in sharedprefs
        orignalSet = new ArrayList<String>();
        removeSet = new ArrayList<String>();
        addSet = new ArrayList<String>();

        getActivity().getActionBar().setTitle("Edit Activities");
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mEditor = sharedPrefs.edit();

        continueButton = (Button) V.findViewById(R.id.continueButton2);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeSet.size()!=0) {
                    processRemoveSet();
                }
                if (addSet.size()!=0){
                    //Launch the fragment to add new goal and give it the new goal Array as a bundle
                    Bundle b = new Bundle();
                    b.putStringArrayList("AddSet",addSet);
                    FragmentTransaction transaction;
                    NewCurrentLevelActivity NewCurrentLevelActivity = new NewCurrentLevelActivity();
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, NewCurrentLevelActivity);
                    NewCurrentLevelActivity.setArguments(b);
                    transaction.commit();
                }else{
                    FragmentTransaction transaction;
                    GameFragment gameFragment = new GameFragment();
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, gameFragment);
                    transaction.commit();
                }
            }
        });
        String s = sharedPrefs.getString("ACTIVITIES", null);

        String[] temp = s.split(",");
        for (int i=0;i<temp.length;i++){
            String temp2 = temp[i].split("_")[0];
            orignalSet.add(temp2);
        }

        activityItems.add(new ActivityModel("Running (Time)", s.contains("Running (Time)")));
        activityItems.add(new ActivityModel("Running (Distance)", s.contains("Running (Distance)")));
        activityItems.add(new ActivityModel("Swimming (Time)",s.contains("Swimming (Time)")));
        activityItems.add(new ActivityModel("Swimming (Distance)",s.contains("Swimming (Distance)")));
        activityItems.add(new ActivityModel("Crunches",s.contains("Crunches")));
        activityItems.add(new ActivityModel("Cycling", s.contains("Cycling")));
        activityItems.add(new ActivityModel("Pull-Ups",s.contains("Pull-Ups")));
        activityItems.add(new ActivityModel("Dips",s.contains("Dips")));
        activityItems.add(new ActivityModel("Push-Ups",s.contains("Push-Ups")));
        activityItems.add(new ActivityModel("Walking",s.contains("Walking")));
        activityItems.add(new ActivityModel("Squats",s.contains("Squats")));
        activityItems.add(new ActivityModel("Soccer",s.contains("Soccer")));
        activityItems.add(new ActivityModel("Squash", s.contains("Squash")));
        activityItems.add(new ActivityModel("Cycling (Time)",s.contains("Cycling (Time)")));
        activityItems.add(new ActivityModel("Cycling (Distance)",s.contains("Cycling (Distance)")));
        CheckBoxAdapter adapter = new CheckBoxAdapter(getActivity().getApplicationContext(),R.layout.checkbox_layout,activityItems);
        ListView activitiesListView = (ListView) V.findViewById(R.id.activityListView);
        activitiesListView.setAdapter(adapter);

        return V;
    }

    private void processRemoveSet() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        //Get the remove set activities, add only activities which aren't in removeset to sharedprefs
        List<String> activities = Arrays.asList(sharedPrefs.getString("ACTIVITIES", "").split(","));
        List<String> currentLevel = Arrays.asList(sharedPrefs.getString("Activity_Prelim_Levels", "").split(","));
        ArrayList<String> currentLevelSet = new ArrayList<String>();
        ArrayList<String> activitySet = new ArrayList<String>();
        List<String> repArray;
        List<String> dtaArray;
        repArray = Arrays.asList(getString(R.string.activity_types_REP).split(","));
        dtaArray = Arrays.asList(getString(R.string.activity_types_DTA).split(","));
        removeSetCopy = new ArrayList<String>();
        //need to make a copy of the remove set and add in the type for string parsing later on
        for (int i=0;i<activities.size();i++) {
            String temp = activities.get(i).split("_")[0];
            String temp2 = activities.get(i);
            if (!removeSet.contains(temp)){
                activitySet.add(temp);
                currentLevelSet.add(currentLevel.get(i));


            }else{
                removeSetCopy.add(temp2);
            }
        }

        for (int j = 0; j < activitySet.size(); j++) {
            sb2.append(currentLevelSet.get(j).concat(","));
            if(repArray.contains(activitySet.get(j))){
                sb.append(activitySet.get(j).concat("_REP").concat(","));
            }else if(dtaArray.contains(activitySet.get(j))){
                if(activitySet.get(j).contains("Time")){
                    sb.append(activitySet.get(j).concat("_DTA-T").concat(","));
                }else{
                    sb.append(activitySet.get(j).concat("_DTA-D").concat(","));
                }
            }else{
                sb.append(activityItems.get(j).getName()).append("_TIM").append(",");
            }

        }
        mEditor.putString("ACTIVITIES", sb.toString());
        mEditor.putString("Activity_Prelim_Levels", sb2.toString());
        mEditor.commit();
        removeGoals();
    }

    private void removeGoals() {
        Goal G;
        DBConnection dataSource = new DBConnection(getActivity());
        dataSource.open();
        for (String s: removeSetCopy){
            GregorianCalendar c = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(c.getTime());
            int indexOfSpace = s.indexOf(" ");
            String activityName = s;
            if (indexOfSpace!=-1 && s.charAt(indexOfSpace+1)=='('){
                activityName = s.substring(0,indexOfSpace);
            }else if(indexOfSpace!=-1 && s.charAt(indexOfSpace+1)!='('){
                activityName = s.split("_")[0];
            }else{
                activityName = s.split("_")[0];
            }
            String activityType = s.split("_")[1];
            G = new Goal(date,activityName,activityType,0,10,10);
            Log.d("TAG","STRING: "+s);
            dataSource.removeGoal(G);
            CharSequence text = "REMOVED ACTIVITY: "+activityName.split("_")[0];
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
        }
        dataSource.close();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class CheckBoxAdapter extends ArrayAdapter<ActivityModel> {
        private Context context;
        public CheckBoxAdapter(Context context, int textViewResourceId, ArrayList<ActivityModel> activityList){
            super(context, textViewResourceId,activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.checkbox_layout, parent, false);
            final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            cb.setText(activityItems.get(position).getName());
            cb.setChecked(activityItems.get(position).getIsChecked());
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItems.get(position).setChecked(cb.isChecked());
                    if (cb.isChecked()&& !orignalSet.contains(activityItems.get(position).getName())){
                        addSet.add(activityItems.get(position).getName());
                    }else if (!cb.isChecked() && orignalSet.contains(activityItems.get(position).getName())){
                        removeSet.add(activityItems.get(position).getName());
                    }else if (!cb.isChecked()&& !orignalSet.contains(activityItems.get(position).getName())){
                        addSet.remove(activityItems.get(position).getName());
                    }else if (cb.isChecked()&& orignalSet.contains(activityItems.get(position).getName())){
                        removeSet.remove(activityItems.get(position).getName());
                    }
                }
            });
            return convertView;
        }
    }

    private class ActivityModel{
        private String name;
        private boolean isChecked;

        public ActivityModel(String name, boolean isChecked){
            this.isChecked = isChecked;
            this.name = name;
        }

        public String getName(){
            return this.name;
        }

        public boolean getIsChecked(){
            return this.isChecked;
        }

        public void setChecked(boolean b){
            this.isChecked = b;
        }


    }



}
