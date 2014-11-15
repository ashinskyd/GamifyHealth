package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SelectActivities extends Activity {
    private ListView activitiesListView;
    private CheckBoxAdapter checkBoxAdapter;
    private ArrayList<ActivityModel> activityItems;
    private Button continueButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activities);
        activitiesListView = (ListView) findViewById(R.id.activityListView);
        continueButton = (Button) findViewById(R.id.continueButton2);
        context = getApplicationContext();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CurrentActivityLevel.class );
                final AlphaAnimation buttonClick = new AlphaAnimation(1F,0.8F);
                view.startAnimation(buttonClick);
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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
                                sb.append(activityItems.get(j).getName()).append("_DTA_T").append(",");
                            }else{
                                sb.append(activityItems.get(j).getName()).append("_DTA_D").append(",");
                            }
                        }else{
                            sb.append(activityItems.get(j).getName()).append("_TIM").append(",");
                        }
                    }
                }
                editor.putString("ACTIVITIES",sb.toString());
                editor.commit();
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        activityItems = new ArrayList<ActivityModel>();
        activityItems.add(new ActivityModel("Running (Time)", false));
        activityItems.add(new ActivityModel("Running (Distance)", false));
        activityItems.add(new ActivityModel("Swimming (Time)",false));
        activityItems.add(new ActivityModel("Swimming (Distance)",false));
        activityItems.add(new ActivityModel("Crunches",false));
        activityItems.add(new ActivityModel("Cycling", false));
        activityItems.add(new ActivityModel("Pull-Ups",false));
        activityItems.add(new ActivityModel("Dips",false));
        activityItems.add(new ActivityModel("Push-Ups",false));
        activityItems.add(new ActivityModel("Walking",false));
        activityItems.add(new ActivityModel("Squats",false));
        activityItems.add(new ActivityModel("Soccer",false));
        activityItems.add(new ActivityModel("Football",false));
        activityItems.add(new ActivityModel("Squash",false));
        activityItems.add(new ActivityModel("Cycling (Time)",false));
        activityItems.add(new ActivityModel("Cycling (Distance)",false));
        CheckBoxAdapter adapter = new CheckBoxAdapter(getApplicationContext(),R.layout.checkbox_layout,activityItems);
        activitiesListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private class CheckBoxAdapter extends ArrayAdapter<ActivityModel>{
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
