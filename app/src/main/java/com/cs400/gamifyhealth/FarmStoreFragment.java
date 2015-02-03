package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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
        ArrayList<String> farmsArray = new ArrayList<String>();
        farmsArray.add("Bad Farm");
        farmsArray.add("OK Farm");
        farmsArray.add("Decent Farm");
        farmsArray.add("Amazing Farm");
        farmsArray.add("Best Farm");
        Button farmStore = (Button) V.findViewById(R.id.wheat_button);
        Button houseStore = (Button) V.findViewById(R.id.cottage_button);
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
        ListView farmListView = (ListView) V.findViewById(R.id.store_listView);
        FarmArrayAdapter mAdapter = new FarmArrayAdapter(getActivity(),R.layout.store_custom_row_item, farmsArray);
        farmListView.setAdapter(mAdapter);
        farmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            switch (position){
                case 0:
                    image.setBackground(getResources().getDrawable(R.drawable.house1));
                    break;
                case 1:
                    image.setBackground(getResources().getDrawable(R.drawable.house2));
                    break;
                case 2:
                    image.setBackground(getResources().getDrawable(R.drawable.house3));
                    break;
                case 3:
                    image.setBackground(getResources().getDrawable(R.drawable.house4));
                    break;
                case 4:
                    image.setBackground(getResources().getDrawable(R.drawable.house5));
                    break;

            }
            description.setText("This is a placeholder for a farm description: " + farmList.get(position));
            return convertView;
        }
    }

}
