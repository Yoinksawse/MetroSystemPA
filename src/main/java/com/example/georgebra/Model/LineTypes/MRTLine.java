package com.example.georgebra.Model.LineTypes;

public class MRTLine extends MetroLine {

    public MRTLine(String lineName, String lineCode, int lineId, String lineColour) {
        super(lineName, lineCode, lineId, lineColour);
    }

    public String toString() {
        return "MRT " + super.toString();
    }
}
