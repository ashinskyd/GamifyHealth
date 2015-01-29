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
import android.widget.CheckBox;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditActivitySetFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        continueButton = (Button) V.findViewById(R.id.continueButton2);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                StringBuilder sb = new StringBuilder();
                List<String> repArray;
                List<String> dtaArray;
                repArray = Arrays.asList(getString(R.string.activity_types_REP).split(","));
                dtaArray = Arrays.asList(getString(R.string.activity_types_DTA).split(","));
                for (int j = 0; j < activityItems.size(); j++) {
                    if (activityItems.get(j).getIsChecked()) {
                        if(repArray.contains(activityItems.get(j).getName())){
                            sb.append(activityItems.get(j).getName()).append("_REP").append(",");

                        }else if(dtaArray.contains(activityItems.get(j).getName())){
                            if(activityItems.get(j).getName().contains("Time")){
                                sb.append(activityItems.get(j).getName()).append("_DTA-T").append(",");
                            }else{
                                sb.append(activityItems.get(j).getName()).append("_DTA-D").append(",");
                            }
                        }else{
                            sb.append(activityItems.get(j).getName()).append("_TIM").append(",");
                        }
                    }
                }
                for (int i=0;i<removeSet.size();i++){
                    Log.d("TAG","ADDING: "+removeSet.get(i));
                }
                //editor.putString("ACTIVITIES",sb.toString());
                //editor.commit();
            }
        });
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("ACTIVITIES", null);

        String[] temp = s.split(",");
        for (int i=0;i<temp.length;i++){
            String temp2 = temp[i].split("_")[0];
            orignalSet.add(temp2);
            Log.d("TAG","SET: "+temp2);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
