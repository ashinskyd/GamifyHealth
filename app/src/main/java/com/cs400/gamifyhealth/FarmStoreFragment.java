package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class FarmStoreFragment extends Fragment {

    // Fragment for farm purchasing screen

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedPrefs;
    private boolean[] isSelectable;
    private int credits;
    private int population;
    private int[] prices = {20, 38, 54, 68, 80};
    private int[] thresholds = {1, 10, 20, 30, 40};


    public static FarmStoreFragment newInstance(String param1, String param2) {
        FarmStoreFragment fragment = new FarmStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FarmStoreFragment() {
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
        View V = inflater.inflate(R.layout.fragment_farm_store, container, false);
        getActivity().getActionBar().setTitle("Purchase Food Items");
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        credits = sharedPrefs.getInt("CREDITS", 0);
        population = sharedPrefs.getInt("POPULATION",0);
        getActivity().getActionBar().setTitle("Purchase Farms");
        ArrayList<String> farmsArray = new ArrayList<String>();
        isSelectable = new boolean[5];
        farmsArray.add("Wheat Field");
        farmsArray.add("Vegetable Patch");
        farmsArray.add("Pasture");
        farmsArray.add("Corn");
        farmsArray.add("Orchard");
        Button farmStore = (Button) V.findViewById(R.id.wheat_button);
        Button houseStore = (Button) V.findViewById(R.id.cottage_button);
        Button fortStore = (Button) V.findViewById(R.id.sword_button);
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
        farmStore.setBackground(getResources().getDrawable(R.drawable.wheat_button2));
        farmStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction;
                GameFragment gameFragment = new GameFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, gameFragment);
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

        ListView farmListView = (ListView) V.findViewById(R.id.store_listView);
        FarmArrayAdapter mAdapter = new FarmArrayAdapter(getActivity(),R.layout.store_custom_row_item, farmsArray);
        farmListView.setAdapter(mAdapter);
        farmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSelectable[i]){
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("FARM_STORE", true);
                    bundle.putInt("FARM_VALUE", i);
                    FragmentTransaction transaction;
                    GameFragment gameFragment = new GameFragment();
                    transaction = getFragmentManager().beginTransaction();
                    gameFragment.setArguments(bundle);
                    transaction.replace(R.id.content_frame, gameFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }

        });
        return V;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class FarmArrayAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> farmList;

        public FarmArrayAdapter(Context context, int textViewResourceId, ArrayList<String> farmList){
            super(context, textViewResourceId, farmList);
            this.context = context;
            this.farmList = farmList;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.store_custom_row_item, parent, false);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
            TextView priceText = (TextView) convertView.findViewById(R.id.priceText);
            TextView minpeopleText = (TextView) convertView.findViewById(R.id.min_people_textview);
            ImageView minPeopleImageView = (ImageView) convertView.findViewById(R.id.people_cost_imageview);
            ImageView creditImageView = (ImageView) convertView.findViewById(R.id.coin_imageView);
            int price = prices[position];
            int threshold = thresholds[position];
            priceText.setText(Integer.toString(price));
            minpeopleText.setText(Integer.toString(threshold));
            switch (position) {
                case 0:
                    if (credits < price || population < threshold) {
                        image.setBackground(getResources().getDrawable(R.drawable.farm1_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    } else {
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.farm1));
                    }
                    description.setText(getResources().getString(R.string.farmDescription1));
                    break;
                case 1:
                    if (credits < price || population < threshold) {
                        image.setBackground(getResources().getDrawable(R.drawable.farm2_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    } else {
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.farm2));
                    }
                    description.setText(getResources().getString(R.string.farmDescription2));
                    break;
                case 2:
                    if (credits < price || population < threshold) {
                        image.setBackground(getResources().getDrawable(R.drawable.farm3_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    } else {
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.farm3));
                    }
                    description.setText(getResources().getString(R.string.farmDescription3));
                    break;
                case 3:
                    if (credits < price || population < threshold) {
                        image.setBackground(getResources().getDrawable(R.drawable.farm4_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    } else {
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.farm4));
                    }
                    description.setText(getResources().getString(R.string.farmDescription4));
                    break;
                case 4:
                    if (credits < price || population < threshold) {
                        image.setBackground(getResources().getDrawable(R.drawable.farm5_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    } else {
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.farm5));
                    }
                    description.setText(getResources().getString(R.string.farmDescription5));
                    break;
            }
            return convertView;
        }


    }

}
