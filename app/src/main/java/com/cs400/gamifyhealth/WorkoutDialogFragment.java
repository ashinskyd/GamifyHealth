package com.cs400.gamifyhealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ashinskyd on 1/7/2015.
 */
 public class  WorkoutDialogFragment extends DialogFragment {

    // Popup for workout entry confirmation

        public interface NoticeDialogListener {
            public void onDialogPositiveClick();
            public void onDialogNegativeClick();
        }

        // Use this instance of the interface to deliver action events
        NoticeDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to enter this workout?")
                    .setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("Confirmed", "AAA");
                    mListener.onDialogPositiveClick();
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("Canceled","AAA");
                            mListener.onDialogNegativeClick();
                        }
                    });
            return builder.create();
        }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }

}
