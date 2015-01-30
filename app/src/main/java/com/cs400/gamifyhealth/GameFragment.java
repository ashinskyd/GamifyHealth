package com.cs400.gamifyhealth;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


//TODO: how do we curtail calls to the database??

//NOTE: FOR NOW WE ARE TESTING THE ATTACK ENGINE CALLS IN THIS CLASS
//WE ALSO INITIALIZE THE POPULATION VALUE IN SHAREDPREFERENCES HERE

public class GameFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private Button houseStore;
    private SharedPreferences sharedPrefs;
    private Set<Integer> occupiedIndices = new HashSet<Integer>();
    private GridLayout mGrid;
    private OnFragmentInteractionListener mListener;
    private DBConnection dataSource;

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DBConnection(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_game, container, false);
        getActivity().getActionBar().setTitle("Game Page");
        //Set the population counter
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int attacks = sharedPrefs.getInt("ATTACKS",0);
        AttackEngine a = new AttackEngine(getActivity());
        for (int i=0;i<attacks;i++){
            Log.d("TAG","ATTACKED GEN");
            a.attack();
        }
        int population = sharedPrefs.getInt("POPULATION",1);
        TextView peopleCounter =(TextView) V.findViewById(R.id.people_counter);
        peopleCounter.setText(population+" People");
        //Set Credit Counter
        int credits = sharedPrefs.getInt("CREDITS",1);
        TextView creditCounter = (TextView) V.findViewById(R.id.credit_counter);
        creditCounter.setText(credits+" Credits");

        //If we click the houseStore icon, we launch the store
        houseStore = (Button) V.findViewById(R.id.cottage_button);
        houseStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction;
                HouseStoreFragment houseStoreFragment = new HouseStoreFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, houseStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mGrid = (GridLayout) V.findViewById(R.id.map);
        getOccupiedIndices(mGrid);
        //tester code: shared preferences isn't quite working as intended yet
        //ideally, should store a population in sharedpreferences with key "population" and value 1 to represent initial population IF

/*        AttackEngine a = new AttackEngine(this.getActivity());
        a.printObjectsOwned();
        System.out.println("attack 1");
        a.attack();
        System.out.println("objects according to attack engine");
        a.printObjectsOwned();
        System.out.println("objects according to db");
  */
        inflateMap(V);

        Boolean store; //Used to determine if (upon inflating) we are in the process of buying a store
        Bundle b = getArguments();
        if (b!=null && b.getBoolean("HOUSE_STORE")){
            //Register the listeners if we come from the store
            RegisterListeners(mGrid);
        }
        return V;
    }

    private void RegisterListeners(GridLayout mGrid) {
        int c=-1;
        for (int i = 0; i < (10 * 13); i++) {
            c += 1;
            final Button tileIcon = (Button) mGrid.findViewWithTag("space_"+c);
            if (!occupiedIndices.contains(c)) {
                final int d = c;
                tileIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int xCoord = d / 13;
                        int yCoord = d % 13;
                        tileIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.sample));
                        dataSource.open();
                        dataSource.insertObject("farm", xCoord, yCoord, "Default");
                        dataSource.printObjectDB();
                        dataSource.close();
                        UnregisterListeners();
                    }
                });
            }
        }
    }

    private void UnregisterListeners(){
        int c=-1;
        for (int i = 0; i < (10 * 13); i++) {
            c += 1;
            final Button tileIcon = (Button) mGrid.findViewWithTag("space_"+c);
            tileIcon.setOnClickListener(null);
        }

    }

    private void inflateMap(View v) {
        Boolean store; //Used to determine if (upon inflating) we are in the process of buying a store
        Bundle b = getArguments();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        final GridLayout mGrid = (GridLayout) v.findViewById(R.id.map);

        //Here is where we will change the tile size based on zoom level. Current is hardcoded to 40
        int h = (int)(40 * scale);
        int c = -1;

        for (int i=0;i<(10*13);i++) {
            c+=1;
            final Button tileIcon = new Button(getActivity());
            tileIcon.setTag("space_" +c);
            if (occupiedIndices.contains(c)){
                tileIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.sample));
            }else{
                tileIcon.setBackgroundColor(Color.TRANSPARENT);
            }
            tileIcon.setLayoutParams(new ViewGroup.LayoutParams(h,h));
            //upon a tile being clicked, if we came from the store, we just se the
            mGrid.addView(tileIcon,c);
        }

    }


    private void getOccupiedIndices(GridLayout mGrid){
        //Method gets the users purchases and updates the collection of occupied indices
        dataSource.open();
        ArrayList<Building> buildings = dataSource.getObjectsOwned();
        dataSource.close();
        for (Building building: buildings){
            int index = (building.xcoord * mGrid.getRowCount()) + building.ycoord;
            occupiedIndices.add(index);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(URI i);
    }

}
