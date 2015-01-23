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
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;


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
        getActivity().getActionBar().setTitle("Edit Activities");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("ACTIVITIES", null);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
