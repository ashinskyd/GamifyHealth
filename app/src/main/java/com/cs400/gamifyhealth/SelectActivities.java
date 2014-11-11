package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;


public class SelectActivities extends Activity {
    private ListView activitiesListView;
    private CheckBoxAdapter checkBoxAdapter;
    private ArrayList<ActivityModel> activityItems;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activities);
        activitiesListView = (ListView) findViewById(R.id.activityListView);
        continueButton = (Button) findViewById(R.id.continueButton2);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CurrentActivityLevel.class );
                //TODO: Send to arraylist to shared prefs
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        activityItems = new ArrayList<ActivityModel>();
        activityItems.add(0,new ActivityModel("Running",false));
        activityItems.add(1,new ActivityModel("Swimming",false));
        activityItems.add(2,new ActivityModel("Crunches",false));
        CheckBoxAdapter adapter = new CheckBoxAdapter(getApplicationContext(),R.layout.checkbox_layout,activityItems);
        activitiesListView.setAdapter(adapter);
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
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityItems.get(position).setChecked(cb.isChecked());
                    for(int i=0;i<activityItems.size();i++){
                        Log.d("TAG","Item: "+activityItems.get(i).getName());
                        Log.d("TAG","Value: "+activityItems.get(i).isChecked());
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

        public boolean isChecked(){
            return this.isChecked;
        }

        public void setChecked(boolean b){
            this.isChecked = b;
        }


    }
}
