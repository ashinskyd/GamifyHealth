package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DataEntryFragment extends Fragment implements WorkoutDialogFragment.NoticeDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> activityList;
    private Map<String,Integer> currentLevel;
    private ArrayList<Integer> activitySetLevels;
    private ListView mListView;
    private SeekBarAdapter mAdapter;
    private SharedPreferences sharedPrefs;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataEntryFragment newInstance(String param1, String param2) {
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
        getActivity().setTitle("Workout Entry");
        currentLevel = new HashMap<String, Integer>();
        activityList = new ArrayList<String>();
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String[] activities = sharedPrefs.getString("ACTIVITIES","").split(",");
        String[] activityStartValString = sharedPrefs.getString("Activity_Prelim_Levels",null).split(",");
        activitySetLevels = new ArrayList<Integer>();
        for(int i=0;i<activities.length;i++){
            activityList.add(i,activities[i]);
            activitySetLevels.add(i,Integer.parseInt(activityStartValString[i]));
            currentLevel.put(activities[i],Integer.parseInt(activityStartValString[i]));
        }
        mListView = (ListView) V.findViewById(R.id.seekBarListView);
        mAdapter = new SeekBarAdapter(getActivity().getApplicationContext(),R.layout.seekbar_row,activityList);
        mListView.setAdapter(mAdapter);
        Button confirm = (Button) V.findViewById(R.id.continueButton2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutDialogFragment dialog = new WorkoutDialogFragment();
                dialog.setTargetFragment(DataEntryFragment.this,0);
                dialog.show(getActivity().getFragmentManager(),"DialogFragment");
            }
        });
        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDialogPositiveClick() {
        Log.d("TAG","Positive Callback");
        for(String s: currentLevel.keySet()){
            Log.d("TAG","Activity: "+s+"VALUE: "+currentLevel.get(s));
        }
    }

    @Override
    public void onDialogNegativeClick() {
        Log.d("TAG","Positive Callback");
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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

            String temp = activityList.get(position).split("_")[0];
            title.setText(temp);
            progress.setText("HI");
              sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    currentLevel.put(activityList.get(position),seekBar.getProgress());
                    if (activityList.get(position).toString().contains("_REP")){
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
                    }else if (activityList.get(position).toString().contains("_TIM")){
                        progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                    }else{
                        if(activityList.get(position).toString().contains("_DTA_T")){
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Hours"));
                        }else if(activityList.get(position).toString().contains("_DTA_D")){
                            progress.setText(Integer.toString(seekBar.getProgress()).concat(" Miles"));
                        }
                    }
                    //TODO: Keep track of the progress for all the types somehow, then save it somewhere...
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            if (activityList.get(position).contains("_REP")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Reps"));
            }else if (activityList.get(position).contains("_TIM")){
                progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
            }else{
                if (activityList.get(position).contains("_DTA_T")){
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Hours"));
                }else{
                    progress.setText(Integer.toString(sb.getProgress()).concat(" Miles"));
                }
            }

            return convertView;
        }
    }





}
