package com.example.RouteCrafter.Model.LineTypes;

import com.example.RouteCrafter.Model.StationTypes.Station;

import java.time.LocalTime;

public class TourismLine extends MetroLine { //such as SentosaLine
    public TourismLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#000000");
    }

    //public boolean serviceStopped(Station station, LocalTime t) { return true; }

    @Override
    public String getLineColour() {
        return "#000000";
    }

    @Override
    public String getLineType() {
        return "Tourism";
    }

    public String toString() {
        return "Tourism " + super.toString();
    }
}
