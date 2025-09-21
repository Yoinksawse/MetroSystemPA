package com.example.georgebra.Model.LineTypes;

public class MRTLine extends MetroLine {

    public MRTLine(String lineName, String lineCode, String lineColour) {
        super(lineName, lineCode, lineColour);
    }

    public String toString() {
        return "MRT " + super.toString();
    }
}
