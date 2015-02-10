package com.cs400.gamifyhealth;



import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;


public class GoalDisplayFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DBConnection dataSource;
    private ArrayList<Goal> goalSet;
    private ListView mListView;
    private GoalProgressListAdapter mAdapter;

    private String mParam1;
    private String mParam2;
    private String []types = {"(Distance)", "(Time)", "(Rate)" , "Reps"};
    private String []units = {"Miles", "Hours", "Min/Mi" , "Reps"};


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
        EarningsEngine earningsEngine = new EarningsEngine(getActivity());
        earningsEngine.weeklyGoalCheck();
        dataSource.open();
        goalSet = dataSource.getGoals();
        dataSource.close();



        mListView = (ListView) V.findViewById(R.id.goalSetListView);
        mAdapter = new GoalProgressListAdapter(getActivity().getApplicationContext(),R.layout.goal_display_row,goalSet);
        mListView.setAdapter(mAdapter);
        return V;
    }

    private class GoalProgressListAdapter extends ArrayAdapter<Goal>{
        private ArrayList<Goal> goalSet;
        private Context context;

        public GoalProgressListAdapter(Context Context, int resource, ArrayList<Goal> goals) {
            super(Context, resource, goals);
            goalSet = goals;
            context = Context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.goal_display_row, parent, false);
            TextView nameTextview =  (TextView) convertView.findViewById(R.id.nameTextView);
            TextView weeklyGoalTextView = (TextView) convertView.findViewById(R.id.weeklygoalTextView);
            TextView weekDateTextView = (TextView) convertView.findViewById(R.id.weekdateTextView);
            TextView finalGoalTextView = (TextView) convertView.findViewById(R.id.finalgoalTextView);
            TextView goalDurationTextView = (TextView) convertView.findViewById(R.id.durationTextView);

            Goal g = goalSet.get(position);
            String name = g.name;

            String t = g.type;
            String type;
            String unit;
            if (t.equals("DTA-T")|t.equals("TIM")){
                 type = types[1];
                unit = units[1];
            }
            else if( t.equals("DTA-R")){
                type = types[2];
                unit = units[2];
            }
            else if( t.equals("DTA-D")){
                type = types[0];
                unit = units[0];
            }
            else{
                type = types[3];
                unit = units[3];
            }
            name=name+" "+type;
            nameTextview.setText(name);
            //TODO: Calculate the current week date based on calender
            double weeklyGoal = g.calculateCurrentGoal();
            //weekDateTextView.setText(g.currentWeek);
            weeklyGoalTextView.setText(Double.toString(weeklyGoal)+" "+unit);
            finalGoalTextView.setText(g.goalUnit+" "+unit);
            goalDurationTextView.setText(g.duration+" Weeks");
            return convertView;
        }
    }
}
