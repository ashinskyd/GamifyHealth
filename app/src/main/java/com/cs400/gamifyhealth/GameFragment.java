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

import org.xmlpull.v1.XmlPullParser;


//TODO: how do we curtail calls to the database??

//NOTE: FOR NOW WE ARE TESTING THE ATTACK ENGINE CALLS IN THIS CLASS
//WE ALSO INITIALIZE THE POPULATION VALUE IN SHAREDPREFERENCES HERE

public class GameFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button houseStore;
    private SharedPreferences sharedPrefs;

    private OnFragmentInteractionListener mListener;

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
        Boolean store;
        Bundle b = getArguments();
        if (b!=null){
            store = true;
        }else{
            store = false;
        }
        houseStore = (Button) V.findViewById(R.id.cottage_store_button);
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
        //tester code: shared preferences isn't quite working as intended yet
        //ideally, should store a population in sharedpreferences with key "population" and value 1 to represent initial population IF

        AttackEngine a = new AttackEngine(this.getActivity());
        a.printObjectsOwned();
        System.out.println("attack 1");
        a.attack();
        System.out.println("objects according to attack engine");
        a.printObjectsOwned();
        System.out.println("objects according to db");
        //DBConnection datasource = new DBConnection(this.getActivity());
        //datasource.getObjectCounts();
        GridLayout mGrid = (GridLayout) V.findViewById(R.id.map);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int h = (int)(40 * scale);
        int c = -1;
        for (int i=0;i<(10*13);i++) {
                c+=1;
                final Button space = new Button(getActivity());
                //final Space space2 = new Space(getActivity());
                space.setTag("space_" +c);
                space.setBackgroundColor(Color.TRANSPARENT);
                //space2.setLayoutParams(new ViewGroup.LayoutParams(h,h));
                space.setLayoutParams(new ViewGroup.LayoutParams(h,h));
                if (store == true){
                    space.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Log.d("TAG", "SPACED CLICKED! " + space.getTag().toString());
                            space.setBackground(getActivity().getResources().getDrawable(R.drawable.crown));
                            //TODO: Add the touched coordinates to the DB
                            //REdraw/relaunch fragment from navdrawer
                            mListener.onFragmentInteraction(0);
                            return false;
                        }
                    });
                }
                 mGrid.addView(space,c);
        }
        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int i);
    }

}
