package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;


public class NavigationDrawerMain extends FragmentActivity implements DataEntryFragment.OnFragmentInteractionListener, EditActivitySetFragment.OnFragmentInteractionListener {
    private String[] itemTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private FrameLayout mFrame;
    private AttackService attackService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationdrawerlayout);
        checkAttackService();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("SERVICE_STARTED", true);
        editor.commit();

        itemTitles = new String[4];
        itemTitles[0] = "Main Game Page";
        itemTitles[1] = "Workout Entry";
        itemTitles[2] = "Edit Activities";
        itemTitles[3] = "Check Current Goals";
        setTitle("Gamify your Health");
        ArrayList<String> itemTitles2 = new ArrayList<String>(Arrays.asList(itemTitles));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mFrame = (FrameLayout) findViewById(R.id.content_frame);
        //ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,R.layout.simple_list_item, itemTitles);
        NavDrawerAdapter mAdapter = new NavDrawerAdapter(this,R.layout.simple_list_item,itemTitles2);
        mDrawerListView.setAdapter(mAdapter);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Launch Game fragment by default
        FragmentTransaction defaultTransaction = getFragmentManager().beginTransaction();
        GameFragment gameFragment = new GameFragment();
        defaultTransaction.replace(R.id.content_frame,gameFragment);
        defaultTransaction.addToBackStack(null);
        defaultTransaction.commit();

        //Otherwise, listen for navdrawer clicks
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentTransaction transaction;
                switch (i){
                    case 0:
                        GameFragment gameFragment = new GameFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, gameFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 1:
                        DataEntryFragment mFragment = new DataEntryFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, mFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 2:
                        EditActivitySetFragment mFragment2 = new EditActivitySetFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, mFragment2);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 3:
                        GoalDisplayFragment goalDisplayFragment = new GoalDisplayFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, goalDisplayFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
                mDrawerLayout.closeDrawer(mDrawerListView);
            }
        });

    }

    private void checkAttackService() {
        boolean serviceStarted = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean("SERVICE_STARTED",false);
        if (!serviceStarted){
            Intent intent = new Intent(this, AttackService.class);
            startService(intent);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
       Log.d("TAG","Fragment Changed!");
    }
    @Override
    public void onBackPressed() {
        return;
    }

    private class NavDrawerAdapter extends BaseAdapter {
        private ArrayList<String> mList;
        private ImageView mImage;
        private TextView mText;
        private Context context;

        public NavDrawerAdapter(Context context, int textViewResourceId, ArrayList<String> titles) {
            context = context;
            mList = titles;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
                convertView = mInflater.inflate(R.layout.simple_list_item, null);
            }
            mText = (TextView) convertView.findViewById(R.id.navdrawer_item);
            mText.setText(mList.get(position));
            return convertView;
        }
    }

}
