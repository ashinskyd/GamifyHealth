package com.cs400.gamifyhealth;

/**
 * Created by Yuen Hsi on 11/15/2014.
 */
public class Goal {
    public String startDate;
    public String name;
    public String type;
    public int startUnit;
    public int goalUnit;
    public int currentWeek;
    // current week goal is kept the same as the current week, but it does not get incremented when the weekly goal is not met
    public int currentWeekGoal;
    public int duration;

    public Goal(String startDate, String name, String type, int startUnit, int goalUnit, int duration) {
        this.startDate = startDate;
        this.name = name;
        this.type = type;
        this.startUnit = startUnit;
        this.goalUnit = goalUnit;
        this.currentWeek = 1;
        this.currentWeekGoal = 1;
        this.duration = duration;
    }

    public double calculateCurrentGoal() {
        int weekDifference = currentWeek - currentWeekGoal;
        double slope = (double) goalUnit/ ((double) duration - weekDifference);
        return (double) ((this.currentWeekGoal * slope) + startUnit);

    }

    // this method is called when the user does not meet the weekly goal
    public void goalNotMet() {
        this.duration++;
        this.currentWeek++;
    }

    // this method is called when the user meets the weekly goal
    public void goalMet() {
        this.currentWeek++;
        this.currentWeekGoal++;
    }
}
