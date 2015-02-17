package com.cs400.gamifyhealth;



import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


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
    private String []units = {" miles per week ", " hours per week ", " min/mile average " , " reps per week "};
    private String []curUnits = {" miles", " hours", " min/mile average" , " reps"};


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

        //TODO: ADD SWIMMING FIX SO IT DOESN'T SHOW MILES WHEN IT SHOULD LAPS
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.goal_display_row, parent, false);
            TextView nameTextview =  (TextView) convertView.findViewById(R.id.nameTextView);
            TextView weeklyGoalTextView = (TextView) convertView.findViewById(R.id.weeklygoalTextView);
            TextView finalGoalTextView = (TextView) convertView.findViewById(R.id.finalgoalTextView);
            TextView weeklyProgressTextView = (TextView) convertView.findViewById(R.id.progressText);
            TextView progressTextView = (TextView) convertView.findViewById(R.id.progress_textview);


            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            Goal g = goalSet.get(position);
            String name = g.name;

            String t = g.type;
            String type;
            String unit;
            String curU;
            if (t.equals("DTA-T")|t.equals("TIM")){
                 type = types[1];
                unit = units[1];
                curU = curUnits[1];
            }
            else if( t.equals("DTA-R")){
                type = types[2];
                unit = units[2];
                curU = curUnits[2];
            }
            else if( t.equals("DTA-D")){
                if (g.name.equals("Swimming")){
                    type = types[0];
                    unit = " laps per week ";
                    curU = " laps ";
                }
                else {
                    type = types[0];
                    unit = units[0];
                    curU = curUnits[0];
                }

            }
            else{
                type = types[3];
                unit = units[3];
                curU = curUnits[3];
            }
            name=name+" "+type;
            nameTextview.setText(name);
            String f = this.getContext().getString(R.string.finalgoalstring);
            String w = this.getContext().getString(R.string.weeklygoalstring);
            double finalGoal = g.goalUnit;
            double weeklyGoal = g.calculateCurrentGoal();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = null;
            try {
                d = sdf.parse(g.startDate);
            }
            catch(ParseException e){
                System.out.println("You broke the date");
            }
            GregorianCalendar weeklyDate = new GregorianCalendar();
            GregorianCalendar finalDate = new GregorianCalendar();
            weeklyDate.setTime(d);
            finalDate.setTime(d);
            weeklyDate.add(Calendar.DAY_OF_MONTH, 7 * g.currentWeek);
            finalDate.add(Calendar.DAY_OF_MONTH, 7 * g.duration);
            String wg = w + " " +  Double.toString(weeklyGoal) + unit + "by " + sdf.format(weeklyDate.getTime());
            String fg = f + " " +  Double.toString(finalGoal) + unit + "by " + sdf.format(finalDate.getTime());
            dataSource.open();
            double progress = dataSource.checkGoalProgress(g);
            dataSource.close();
            String ps = "Progress This Week: " + Double.toString(progress) + curU;
            weeklyProgressTextView.setText(ps);
            weeklyGoalTextView.setText(wg);
            finalGoalTextView.setText(fg);
            int percentage = (int) ((progress/weeklyGoal)*100);
            if (percentage>100){
                percentage=100;
            }
            progressTextView.setText(Double.toString(percentage) + "% complete");
            progressBar.setMax(100);
            progressBar.setProgress((int) percentage);
            Log.d("TAG", "PROGRESS: " + percentage);
            LinearLayout row = (LinearLayout) convertView.findViewById(R.id.goalRow);
            if (percentage == 100) {
                progressBar.getProgressDrawable().setColorFilter(0xFFC9FF88, PorterDuff.Mode.SRC_IN);
            }
            return convertView;
        }
    }
}
