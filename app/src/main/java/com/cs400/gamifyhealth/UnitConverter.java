package com.cs400.gamifyhealth;

import android.util.Log;

/**
 * Created by Andy Nelson on 2/16/15.
 */
public class UnitConverter {

    public UnitConverter() {
    }

    public String convertUnit(int quantity, String type) {
        String displayString = "";
        if (type.equals("DTA-T") || type.equals("TIM")) {
            int hours = quantity / 4;
            int minutes = (quantity % 4) * 15;
            displayString = hours + " h" + " " + minutes;
            if (minutes == 0) {
                displayString += " ";
            }
            displayString += " m";
        }
        else if (type.equals("DTA-D")) {
            float miles = quantity / 4.0f;

            displayString = Float.toString(miles);

            displayString += " mi";
        }
        else {
            Log.d("TAG", "broke");
        }
        return displayString;
    }
}
