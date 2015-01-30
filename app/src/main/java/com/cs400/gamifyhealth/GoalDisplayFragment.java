package com.cs400.gamifyhealth;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class GoalDisplayFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DBConnection dataSource;
    private ArrayList<Goal> goalSet;

    private String mParam1;
    private String mParam2;



    public static GoalDisplayFragment newInstance(String param1, String param2) {
        GoalDisplayFragment fragment = new GoalDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public GoalDisplayFragment() {
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
        getActivity().getActionBar().setTitle("Goal Display");
        View V = inflater.inflate(R.layout.fragment_goal_display, container, false);
        dataSource = new DBConnection(getActivity());
        dataSource.open();
        goalSet = dataSource.getGoals();
        dataSource.close();
        for (Goal g: goalSet){
            Log.d("TAG", "ITEM1: "+g.name);
            Log.d("TAG", "ITEM2: "+g.startDate);
            Log.d("TAG", "ITEM3: "+g.currentWeek);
            Log.d("TAG", "ITEM4: "+g.calculateCurrentGoal());
            Log.d("TAG", "ITEM5: "+g.duration);
        }
        return V;
    }


}
