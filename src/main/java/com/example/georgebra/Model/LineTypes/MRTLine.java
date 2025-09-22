package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.StationTypes.Station;

import java.time.LocalTime;

public class MRTLine extends MetroLine {

    public MRTLine(String lineName, String lineCode, String lineColour) {
        super(lineName, lineCode, lineColour);
    }

    @Override
    public int getDefaultHeadwaySeconds() { //TODO
        return 180;
    } // 3 min
    @Override
    public boolean serviceStopped(Station station, LocalTime t) {
        return true;
    } // all stops

    public String toString() {
        return "MRT " + super.toString();
    }
}
