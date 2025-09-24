package com.example.RouteCrafter.Model.LineTypes;

import com.example.RouteCrafter.Model.StationTypes.Station;

import java.time.LocalTime;

public class LRTLine extends MetroLine {

    public LRTLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#A9A9A9");
    }

    @Override
    public String getLineColour() {
        return "#A9A9A9";
    }

    //public boolean serviceStopped(Station station, LocalTime t) return false; } // all stops

    @Override
    public String getLineType() {
        return "LRT";
    }

    public String toString() {
        return "LRT " + super.toString();
    }
}
