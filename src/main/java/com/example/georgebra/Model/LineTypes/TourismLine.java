package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.StationTypes.Station;

import java.time.LocalTime;
import java.util.HashSet;

public class TourismLine extends MetroLine { //such as SentosaLine
    public TourismLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#000000");
    }

    @Override
    public int getDefaultHeadwaySeconds() {  //TODO
        return 180;
    } // 3 min
    @Override
    public boolean serviceStopped(Station station, LocalTime t) { //TODO
        return true;
    } // all stops

    public String toString() {
        return "Tourism " + super.toString();
    }
}
