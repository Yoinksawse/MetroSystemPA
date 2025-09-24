package com.example.RouteCrafter.Model.LineTypes;

import com.example.RouteCrafter.Model.StationTypes.Station;

import java.time.LocalTime;

public class MRTLine extends MetroLine {

    public MRTLine(String lineName, String lineCode, String lineColour) {
        super(lineName, lineCode, lineColour);
    }

    //public boolean serviceStopped(Station station, LocalTime t) { return false; }

    public void setLineColour(String colour) {
        lineColour = colour;
    }

    @Override
    public String getLineColour() {
        return this.lineColour;
    }

    @Override
    public String getLineType() {
        return "MRT";
    }

    public String toString() {
        return "MRT " + super.toString();
    }
}
