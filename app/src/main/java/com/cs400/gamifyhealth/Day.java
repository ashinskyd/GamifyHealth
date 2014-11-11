package com.cs400.gamifyhealth;

/**
 * Created by Erin on 11/10/2014.
 */
public class Day {
    private long id;
    private String date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date;
    }
}