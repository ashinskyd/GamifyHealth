package com.cs400.gamifyhealth;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class NavigationDrawerMain extends FragmentActivity {
    private String[] itemTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;
    private FrameLayout mFrame;
    private AttackService attackService;
    public static boolean settingsFragmentLaunched=false;
    private BitmapFactory.Options opts = new BitmapFactory.Options();
    private Bitmap bitmap;
    public static Drawable d;
    public static Drawable d2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationdrawerlayout);
        checkAttackService();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("SERVICE_STARTED", true);
        editor.commit();

        //Items populated in our navDrawer's listview
        itemTitles = new String[5];
        itemTitles[0] = "Game Page";
        itemTitles[1] = "Workout Entry";
        itemTitles[2] = "Edit Activities";
        itemTitles[3] = "Current Goals";
        itemTitles[4] = "Settings";

        setTitle("Gamify your Health");
        ArrayList<String> itemTitles2 = new ArrayList<String>(Arrays.asList(itemTitles));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mFrame = (FrameLayout) findViewById(R.id.content_frame);
        NavDrawerAdapter mAdapter = new NavDrawerAdapter(this,R.layout.simple_list_item,itemTitles2);
        mDrawerListView.setAdapter(mAdapter);
        //Some navDrawerSetup that we needed to override
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map, opts);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.map2, opts);
        d = new BitmapDrawable(getResources(),bitmap);
        d2 =new BitmapDrawable(getResources(),bitmap2);

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
                    case 4:
                        SettingsFragment settingsFragment = new SettingsFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, settingsFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                }
                mDrawerLayout.closeDrawer(mDrawerListView);
                settingsFragmentLaunched = false;
            }
        });

    }

    private void checkAttackService() {
        //Check that the service is started
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_drawer_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.action_help:
                HelpFragment HelpFragment = new HelpFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, HelpFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        FragmentTransaction defaultTransaction = getFragmentManager().beginTransaction();
        if (!settingsFragmentLaunched){
            GameFragment gameFragment = new GameFragment();
            defaultTransaction.replace(R.id.content_frame,gameFragment);
            defaultTransaction.addToBackStack(null);
            defaultTransaction.commit();
        }else{
            SettingsFragment settingsFragment = new SettingsFragment();
            defaultTransaction = getFragmentManager().beginTransaction();
            defaultTransaction.replace(R.id.content_frame, settingsFragment);
            defaultTransaction.addToBackStack(null);
            defaultTransaction.commit();
        }
        return;
    }

    //Custom adapter to inflate the NavDrawer with an imageView
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
            mImage = (ImageView) convertView.findViewById(R.id.imageView);
            switch (position){
                case 0:
                    mImage.setBackground(getResources().getDrawable(R.drawable.game_icon));
                    break;
                case 1:
                    mImage.setBackground(getResources().getDrawable(R.drawable.runner_icon));
                    break;
                case 2:
                    mImage.setBackground(getResources().getDrawable(R.drawable.menus_icon));
                    break;
                case 3:
                    mImage.setBackground(getResources().getDrawable(R.drawable.goal_progress_icon));
                    break;
                case 4:
                    mImage.setBackground(getResources().getDrawable(R.drawable.settings_icon));
                    break;
            }
            mText.setText(mList.get(position));
            return convertView;
        }
    }

}
