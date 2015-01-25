package com.cs400.gamifyhealth;

/**
 * Created by Erin on 1/24/2015.
 */
public class Building {

    public int xcoord;
    public int ycoord;
    public String type;
    public String name;

    public Building(String t, int x, int y, String name){
        this.type = t;
        this.xcoord = x;
        this.ycoord = y;
        this.name = name;
    }



}
