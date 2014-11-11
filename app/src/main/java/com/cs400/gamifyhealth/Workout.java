package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/10/2014.
 */
public class Workout {
    private String name;
    private String time;
    private int distance;
    private int rate;
    private int reps;
    private String type;

    public Workout(String name, String time, int distance, int rate, int reps, String type) {
        this.name = name;
        this.time = time;
        this.distance = distance;
        this.rate = rate;
        this.reps = reps;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getTime() {
        return this.time;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getRate() {
        return this.rate;
    }

    public String getType() {
        return this.type;
    }
}
