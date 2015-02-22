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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Setup Activity where user selects all their relevant activities
public class SelectActivities extends Activity {
    //Listview contains all the activities for the user to selects
    private ListView activitiesListView;
    //Checkboxadapter is a custom array adapter to populate the listview
    private CheckBoxAdapter checkBoxAdapter;
    //Arraylist of models is a helper class used to determine if an activity is selected or not for easier processing
    private ArrayList<ActivityModel> activityItems;
    private Button continueButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getActionBar().setTitle("Select Activities");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activities);
        activitiesListView = (ListView) findViewById(R.id.activityListView);
        continueButton = (Button) findViewById(R.id.continueButton2);
        context = getApplicationContext();
        //When user clicks continue, we store their selected activities in sharedPrefs
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canProceed()){
                    Intent i = new Intent(getApplicationContext(),CurrentActivityLevel.class );
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F,0.8F);
                    view.startAnimation(buttonClick);
                    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    StringBuilder sb = new StringBuilder();
                    //rep/dta Array are string arrays of specific activity types
                    //We use their to append the specific type directly to sharedPrefs
                    List<String> repArray;
                    List<String> dtaArray;
                    repArray = Arrays.asList(getString(R.string.activity_types_REP).split(","));
                    dtaArray = Arrays.asList(getString(R.string.activity_types_DTA).split(","));
                    //Iterates through every activity and if the item is checked, we add the activity+type to our sharedPrefs String
                    for (int j = 0; j < activityItems.size(); j++) {
                        if (activityItems.get(j).getIsChecked()) {
                            if(repArray.contains(activityItems.get(j).getName())){
                                sb.append(activityItems.get(j).getName()).append("_REP").append(",");
                            }else if(dtaArray.contains(activityItems.get(j).getName())){
                                if(activityItems.get(j).getName().contains("Time")){
                                    sb.append(activityItems.get(j).getName()).append("_DTA-T").append(",");
                                }else{
                                    Log.d("TAG","NAME: "+activityItems.get(j).getName());
                                    sb.append(activityItems.get(j).getName()).append("_DTA-D").append(",");
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
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select at least 1 activity",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //initialize the array of activity types to every possible activty
        activityItems = new ArrayList<ActivityModel>();
        activityItems.add(new ActivityModel("Running (Time)", false));
        activityItems.add(new ActivityModel("Running (Distance)", false));
        activityItems.add(new ActivityModel("Swimming (Time)",false));
        activityItems.add(new ActivityModel("Swimming (Distance)",false));
        activityItems.add(new ActivityModel("Cycling (Time)",false));
        activityItems.add(new ActivityModel("Cycling (Distance)",false));
        activityItems.add(new ActivityModel("Crunches",false));
        activityItems.add(new ActivityModel("Pull-Ups",false));
        activityItems.add(new ActivityModel("Dips",false));
        activityItems.add(new ActivityModel("Push-Ups",false));
        activityItems.add(new ActivityModel("Walking",false));
        activityItems.add(new ActivityModel("Squats",false));
        activityItems.add(new ActivityModel("Soccer",false));
        activityItems.add(new ActivityModel("Football",false));
        activityItems.add(new ActivityModel("Squash",false));
        activityItems.add(new ActivityModel("Frisbee",false));
        activityItems.add(new ActivityModel("Generic Cardio",false));

        activityItems.add(new ActivityModel("Ping Pong",false));
        checkBoxAdapter = new CheckBoxAdapter(getApplicationContext(),R.layout.checkbox_layout,activityItems);
        activitiesListView.setAdapter(checkBoxAdapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    //Custom adapter used to help inflate a custom layout for each row in our listview
    private class CheckBoxAdapter extends ArrayAdapter<ActivityModel>{
        private Context context;
        public CheckBoxAdapter(Context context, int textViewResourceId, ArrayList<ActivityModel> activityList){
            super(context, textViewResourceId,activityList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.checkbox_layout, parent, false);
            final CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            mCheckBox.setText(activityItems.get(position).getName());
            mCheckBox.setChecked(activityItems.get(position).getIsChecked());
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //When a checkbox is chosen, we update our activity model
                    activityItems.get(position).setChecked(mCheckBox.isChecked());

                }
            });
            return convertView;
        }
    }

    //Checks that the user has selected at least 1 activty before proceeding
    private boolean canProceed() {
        for (ActivityModel activityModel: activityItems){
            if (activityModel.isChecked){
                return true;
            }
        }
        Log.d("TAG","ERE");
        return false;
    }

    //Custom object which just keeps track if an activity is chosen or not based on the checkbox
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
