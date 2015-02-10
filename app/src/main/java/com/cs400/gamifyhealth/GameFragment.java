package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




//Activity is our main game fragment, hosted by navigationDrawerMain
public class GameFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View gameFrameView;
    private String mParam1;
    private String mParam2;
    private Button houseStore;
    private Button fortStore;
    private SharedPreferences sharedPrefs;
    private Set<Integer> occupiedIndices = new HashSet<Integer>();
    private ArrayList<Building> buildingArrayList = new ArrayList<Building>();
    private Map<Integer,Building> buildingMap = new HashMap<Integer, Building>();
    private GridLayout mGrid;
    private DBConnection dataSource;
    private TextView peopleCounter;
    private TextView creditCounter;
    //These are arrays of item prices so we know how much to deduct from a user's credit when they purchase something
    private int[] housePrices = {10, 20, 30, 40, 50};
    private int[] farmPrices = {10, 20, 30, 40, 50};
    private int[] fortPrices = {10, 20, 30, 40, 50};
    //These arrays keep track of all the icons we use to inflate our map
    private int[] houseIcons = {R.drawable.house1, R.drawable.house2, R.drawable.house3, R.drawable.house4, R.drawable.house5};
    private int[] farmIcons = {R.drawable.farm1, R.drawable.farm2, R.drawable.farm3, R.drawable.farm4, R.drawable.farm5};
    private int[] fortIcons = {R.drawable.fort1, R.drawable.fort2, R.drawable.fort3, R.drawable.fort4, R.drawable.fort5};
    private int[] gridSize;
    private Button farmStore;
    private int credits;
    private int zoomCounter;

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
        gameFrameView = inflater.inflate(R.layout.fragment_game, container, false);
        //Set the population counter
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        gridSize = new int[2];
        return gameFrameView;
    }

    @Override
    public void onResume(){
        super.onResume();
        //String gridSizeString is the x,y size of the grid
        String gridSizeString = sharedPrefs.getString("GRID_SIZE","4,5");
        gridSize[0] = Integer.parseInt(gridSizeString.split(",")[0]);
        gridSize[1] = Integer.parseInt(gridSizeString.split(",")[1]);

        //Onresuming, update the credits and population and attack if necessary
        credits = sharedPrefs.getInt("CREDITS",1);
        int attacks = sharedPrefs.getInt("ATTACKS",0);
        zoomCounter = sharedPrefs.getInt("ZOOM_COUNTER",0);
        AttackEngine a = new AttackEngine(getActivity());
        //Attack and remove the notification
        for (int i=0;i<attacks;i++){
            a.attack();
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll();
        }
        dataSource.open();
        //arraylist of all the users buildings
        buildingArrayList = dataSource.getObjectsOwned();
        dataSource.close();
        //Check if we need to resize the map before initializing the Ui
        calculateMapSize();
        initUi(gameFrameView);
        getOccupiedIndices(mGrid);
        inflateMap(gameFrameView);
        //If we are passed a bundle, retrive it
        //NOTE: This is used to determine if we come from a store or we're just showing the map
        Bundle b = getArguments();
        //Register the listeners if we come from the store
        if (b!=null && b.getBoolean("HOUSE_STORE")){
            getActivity().getActionBar().setTitle("Select a position to place house");
            RegisterListeners(mGrid, "House_Store" , b.getInt("HOUSE_VALUE"));
        }
        else if (b!=null && b.getBoolean("FARM_STORE")){
            getActivity().getActionBar().setTitle("Select a position to place farm");
            RegisterListeners(mGrid, "Farm_Store", b.getInt("FARM_VALUE"));
        }
        else if (b!=null && b.getBoolean("FORT_STORE")){
            getActivity().getActionBar().setTitle("Select a position to place farm");
            RegisterListeners(mGrid, "Fort_Store" , b.getInt("FORT_VALUE"));
        }
        else{
            getActivity().getActionBar().setTitle("Game Page");
        }

    }


    //Method deterimes if there is not enough space in current grid and resizes to the next level if it needs to
    private void calculateMapSize() {
        if (buildingArrayList.size()>=(gridSize[0]*gridSize[1]-1)){
            gridSize[0]+=4;
            gridSize[1]+=5;
            zoomCounter = zoomCounter +1;
            SharedPreferences.Editor mEditor = sharedPrefs.edit();
            mEditor.putString("GRID_SIZE",Integer.toString(gridSize[0])+","+gridSize[1]).commit();
            mEditor.putInt("ZOOM_COUNTER",zoomCounter);
            mEditor.commit();
            //Call to update the x,y position of every building in our database
            dataSource.open();
            dataSource.expandObjectTable();
            buildingArrayList = dataSource.getObjectsOwned();
            dataSource.close();
        }
    }

    //Sets up basic Ui buttons and textviews
    private void initUi(View V) {
        int population = sharedPrefs.getInt("POPULATION",1);
        peopleCounter = (TextView) V.findViewById(R.id.people_counter);
        peopleCounter.setText(population + " People");
        //Set Credit Counter
        creditCounter = (TextView) V.findViewById(R.id.credit_counter);
        creditCounter.setText(credits+ " Gold");
        houseStore = (Button) V.findViewById(R.id.cottage_button);
        farmStore = (Button) V.findViewById(R.id.wheat_button);
        fortStore = (Button) V.findViewById(R.id.sword_button);
        //Listeners for the store buttons: launch the appropriate stores on clicking
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
        farmStore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction transaction;
                FarmStoreFragment farmStoreFragment = new FarmStoreFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, farmStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });
        fortStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction;
                FortStoreFragment fortStoreFragment = new FortStoreFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fortStoreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mGrid = (GridLayout) V.findViewById(R.id.map);
        //Set the gridsize based on the zoom level
        mGrid.setRowCount(gridSize[1]);
        Log.d("TAG", "GRID X: " + gridSize[0] + " Y: " + gridSize[1]);
        mGrid.setColumnCount(gridSize[0]);
    }

    /**
     * @param mGrid The gridlayout of our game
     * @param storeValue the type of store ie: farm/house/forts
     * @param iconValue the specific level of item ie: house_1,house_2...
     */
    private void RegisterListeners(GridLayout mGrid, final String storeValue, final int iconValue) {
        int indices = gridSize[0]*gridSize[1];
        int index=-1;
        for (int i = 0; i < indices; i++) {
            index += 1;
            //finds the specific clicked button based on it's tag
            final Button tileIcon = (Button) mGrid.findViewWithTag("space_"+index);
            if (!occupiedIndices.contains(index)) {
                final int d = index;
                //uppon clicking a tile, we change the background, add the item to the database, update sharedPrefs credits, and change the textview
                tileIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int xCoord = d / gridSize[1];
                        int yCoord = d % gridSize[1];
                        if (storeValue.equals("House_Store")){
                            tileIcon.setBackground(getActivity().getResources().getDrawable(houseIcons[iconValue]));
                            dataSource.open();
                            dataSource.insertObject("house", xCoord, yCoord, Integer.toString(iconValue));
                            dataSource.printObjectDB();
                            dataSource.close();
                            int price = housePrices[iconValue];
                            sharedPrefs.edit().putInt("CREDITS", credits-price).commit();
                            getActivity().getActionBar().setTitle("Game Page");
                            creditCounter.setText(sharedPrefs.getInt("CREDITS",1)+" Gold");
                        }else if (storeValue.equals("Farm_Store")){
                            tileIcon.setBackground(getActivity().getResources().getDrawable(farmIcons[iconValue]));
                            dataSource.open();
                            dataSource.insertObject("farm", xCoord, yCoord, Integer.toString(iconValue));
                            dataSource.printObjectDB();
                            dataSource.close();
                            int price = farmPrices[iconValue];
                            sharedPrefs.edit().putInt("CREDITS", credits-price).commit();
                            getActivity().getActionBar().setTitle("Game Page");
                            creditCounter.setText(sharedPrefs.getInt("CREDITS",1)+" Gold");
                        } else if (storeValue.equals("Fort_Store")){
                            tileIcon.setBackground(getActivity().getResources().getDrawable(fortIcons[iconValue]));
                            dataSource.open();
                            dataSource.insertObject("fort", xCoord, yCoord, Integer.toString(iconValue));
                            dataSource.printObjectDB();
                            dataSource.close();
                            int price = fortPrices[iconValue];
                            sharedPrefs.edit().putInt("CREDITS", credits-price).commit();
                            getActivity().getActionBar().setTitle("Game Page");
                            creditCounter.setText(sharedPrefs.getInt("CREDITS",1)+" Gold");
                        }
                        UnregisterListeners();
                    }
                });
            }
        }
    }

    //Method turns off all the onClickListeners in our grid
    private void UnregisterListeners(){
        int indices = gridSize[0]*gridSize[1];
        int c=-1;
        for (int i = 0; i < indices; i++) {
            c += 1;
            final Button tileIcon = (Button) mGrid.findViewWithTag("space_"+c);
            tileIcon.setOnClickListener(null);
        }

    }

    //Populates the map with all of the icons based on the items in a user's object DB
    private void inflateMap(View v) {
        final GridLayout mGrid = (GridLayout) v.findViewById(R.id.map);
        mGrid.setRowCount(gridSize[1]);
        mGrid.setColumnCount(gridSize[0]);
        //Here is where we will change the tile size based on zoom level. We calculate the px based on the user's display and the number of rows/columns
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int x = size.x;
        int index = -1;
        int indices = gridSize[0]*gridSize[1];
        for (int i=0;i<indices;i++) {
            index+=1;
            final Button tileIcon = new Button(getActivity());
            tileIcon.setTag("space_" +index);
            if (occupiedIndices.contains(index)){
                if (buildingMap.get(index).type.equals("house")){
                    int drawable = houseIcons[Integer.parseInt(buildingMap.get(index).name)];
                    tileIcon.setBackground(getActivity().getResources().getDrawable(drawable));
                }else if(buildingMap.get(index).type.equals("farm")){
                    int drawable = farmIcons[Integer.parseInt(buildingMap.get(index).name)];
                    tileIcon.setBackground(getActivity().getResources().getDrawable(drawable));
                }else if(buildingMap.get(index).type.equals("fort")){
                    int drawable = fortIcons[Integer.parseInt(buildingMap.get(index).name)];
                    tileIcon.setBackground(getActivity().getResources().getDrawable(drawable));
                }
            }else{
                tileIcon.setBackgroundColor(Color.TRANSPARENT);
            }
            tileIcon.setLayoutParams(new ViewGroup.LayoutParams(x/gridSize[0],x/gridSize[0]));
            mGrid.addView(tileIcon,index);
        }

    }


    private void getOccupiedIndices(GridLayout mGrid){
        //Method gets the users purchases and updates the collection of occupied indices
        for (Building building: buildingArrayList){
            int index = (building.xcoord * gridSize[1]) + building.ycoord;
            occupiedIndices.add(index);
            buildingMap.put(index,building);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
