package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class HouseStoreFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int credits;
    private int population;
    private boolean [] isSelectable;
    private static int[] prices = {25, 48, 69, 88, 105};
    private static int[] thresholds = {1, 20, 40, 60, 80, 100};
    private SharedPreferences sharedPrefs;

    public static HouseStoreFragment newInstance(String param1, String param2) {
        HouseStoreFragment fragment = new HouseStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public HouseStoreFragment() {
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
        View V = inflater.inflate(R.layout.fragment_house_store, container, false);
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        credits = sharedPrefs.getInt("CREDITS",0);
        population = sharedPrefs.getInt("POPULATION",0);
        getActivity().getActionBar().setTitle("Purchase Houses");
        ArrayList<String> housesArray = new ArrayList<String>();
        isSelectable = new boolean[5];
        housesArray.add("Hut 1234567889");
        housesArray.add("Yurt 123456789");
        housesArray.add("Townhouse");
        housesArray.add("Yuen Hsi's Mansion");
        housesArray.add("Yuen Hsi's Mansion");
        Button houseStore = (Button) V.findViewById(R.id.cottage_button);
        Button farmStore = (Button) V.findViewById(R.id.wheat_button);
        Button fortStore = (Button) V.findViewById(R.id.sword_button);

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

        houseStore.setBackground(getResources().getDrawable(R.drawable.cottage_button2));
        houseStore.setOnClickListener(new View.OnClickListener() {
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
        ListView houseListView = (ListView) V.findViewById(R.id.store_listView);
        HouseArrayAdapter mAdapter = new HouseArrayAdapter(getActivity(),R.layout.store_custom_row_item, housesArray);
        houseListView.setAdapter(mAdapter);
        houseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSelectable[i]){

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("HOUSE_STORE",true);
                    bundle.putInt("HOUSE_VALUE",i);
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class HouseArrayAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> houseList;

        public HouseArrayAdapter(Context context, int textViewResourceId, ArrayList<String> houseList){
            super(context, textViewResourceId, houseList);
            this.context = context;
            this.houseList = houseList;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.store_custom_row_item, parent, false);
            convertView.setEnabled(false);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
            TextView priceText = (TextView) convertView.findViewById(R.id.priceText);
            int price = prices[position];
            int threshold = thresholds[position];
            priceText.setText(Integer.toString(price));
            switch (position){
                case 0:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.house1_bw));
                        isSelectable[position] = false;
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.house1));
                    }
                    description.setText(getResources().getString(R.string.houseDescription1));
                    break;
                case 1:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.house2_bw));
                        isSelectable[position] = false;
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.house2));
                    }
                    description.setText(getResources().getString(R.string.houseDescription2));
                    break;
                case 2:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.house3_bw));
                        isSelectable[position] = false;
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.house3));
                    }
                    description.setText(getResources().getString(R.string.houseDescription3));
                    break;
                case 3:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.house4_bw));
                        isSelectable[position] = false;
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.house4));
                    }
                    description.setText(getResources().getString(R.string.houseDescription4));
                    break;
                case 4:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.house5_bw));
                        isSelectable[position] = false;
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.house5));
                    }
                    description.setText(getResources().getString(R.string.houseDescription5));
                    break;

            }
            return convertView;
        }
    }

}
