package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.StationTypes.Station;

import java.time.LocalTime;

public class LRTLine extends MetroLine {

    public LRTLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#A9A9A9");
    }

    @Override
    public int getDefaultHeadwaySeconds() {  //TODO
        return 180;
    } // 3 min
    @Override
    public boolean serviceStopped(Station station, LocalTime t) {
        return true;
    } // all stops

    public String toString() {
        return "LRT " + super.toString();
    }
}
