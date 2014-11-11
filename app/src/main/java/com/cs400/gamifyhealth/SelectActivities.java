package com.cs400.gamifyhealth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private CheckBoxAdapter checkBoxAdapter = null;
    public Map<String,Boolean> checkMap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activities);
        activitiesListView = (ListView) findViewById(R.id.activityListView);
        activitiesListView.setAdapter();
        checkMap = new Hashtable<String, Boolean>();
        checkMap.put("Running",false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CheckBoxAdapter extends ArrayAdapter<String>{
        private ArrayList<String> activityList;
        private Context context;

        public CheckBoxAdapter(Context context, int textViewResourceId, ArrayList<String> activityList){
            super(context, textViewResourceId,activityList);
            this.activityList = new ArrayList<String>();
            this.activityList.addAll(activityList);
            this.context = context;
        }

        private class ViewHolder{
            CheckBox checkBox;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder = null;
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.checkbox_layout, parent, false);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            return convertView;
        }
        }
    public class ActivityModel{
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
