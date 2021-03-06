package com.cs400.gamifyhealth;

/**
 * Created by Erin on 2/6/2015.
 */

    import android.app.Activity;
    import android.app.FragmentTransaction;
    import android.content.Context;
    import android.content.SharedPreferences;
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


public class FortStoreFragment extends Fragment {

    // Fragment for fort purchasing screen

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int credits;
    private int population;
    private boolean [] isSelectable;
    private SharedPreferences sharedPrefs;
    private int[] prices = {40, 77, 111, 142, 170};
    private int[] thresholds = {1, 20, 40, 60, 80};

    public static FortStoreFragment newInstance(String param1, String param2) {
        FortStoreFragment fragment = new FortStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FortStoreFragment() {
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
        View V = inflater.inflate(R.layout.fragment_fort_store, container, false);
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        credits = sharedPrefs.getInt("CREDITS",0);
        population = sharedPrefs.getInt("POPULATION",0);
        getActivity().getActionBar().setTitle("Purchase Fortifications");
        ArrayList<String> fortArray = new ArrayList<String>();
        isSelectable = new boolean[5];
        fortArray.add("Balista");
        fortArray.add("Broken Wall");
        fortArray.add("Guard Tower");
        fortArray.add("Weapon Stores");
        fortArray.add("Fortress");
        Button houseStore = (Button) V.findViewById(R.id.cottage_button);
        Button farmStore = (Button) V.findViewById(R.id.wheat_button);
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
        fortStore.setBackground(getResources().getDrawable(R.drawable.sword_button2));
        fortStore.setOnClickListener(new View.OnClickListener() {
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


        ListView fortListView = (ListView) V.findViewById(R.id.store_listView);
        FortArrayAdapter mAdapter = new FortArrayAdapter(getActivity(),R.layout.store_custom_row_item, fortArray);
        fortListView.setAdapter(mAdapter);
        fortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSelectable[i]){

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("FORT_STORE",true);
                    bundle.putInt("FORT_VALUE",i);
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

    private class FortArrayAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> fortList;

        public FortArrayAdapter(Context context, int textViewResourceId, ArrayList<String> fortList){
            super(context, textViewResourceId, fortList);
            this.context = context;
            this.fortList = fortList;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.store_custom_row_item, parent, false);
            convertView.setEnabled(false);
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
            switch (position){
                case 0:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.fort1_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.fort1));
                    }
                    description.setText(getResources().getString(R.string.fortDescription1));
                    break;
                case 1:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.fort2_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.fort2));
                    }
                    description.setText(getResources().getString(R.string.fortDescription2));
                    break;
                case 2:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.fort3_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.fort3));
                    }
                    description.setText(getResources().getString(R.string.fortDescription3));
                    break;
                case 3:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.fort4_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.fort4));
                    }
                    description.setText(getResources().getString(R.string.fortDescription4));
                    break;
                case 4:
                    if (credits < price || population < threshold){
                        image.setBackground(getResources().getDrawable(R.drawable.fort5_bw));
                        isSelectable[position] = false;
                        creditImageView.setBackground(getResources().getDrawable(R.drawable.money_bag_bw));
                        minPeopleImageView.setBackground(getResources().getDrawable(R.drawable.walking_man_bw));
                    }else{
                        isSelectable[position] = true;
                        image.setBackground(getResources().getDrawable(R.drawable.fort5));
                    }
                    description.setText(getResources().getString(R.string.fortDescription5));
                    break;

            }
            return convertView;
        }
    }

}
