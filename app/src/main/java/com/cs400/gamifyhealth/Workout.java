package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/10/2014.
 */
public class Workout {
    private String name;
    private int unit;
    private String type;

    public Workout(String name, int unit, String type) {
        this.name = name;
        this.unit = unit;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public int getUnit() {return this.unit;}
}
