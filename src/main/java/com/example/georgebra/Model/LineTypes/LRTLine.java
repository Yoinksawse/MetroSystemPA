package com.example.georgebra.Model.LineTypes;

public class LRTLine extends MetroLine {

    public LRTLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#A9A9A9");
    }

    public String toString() {
        return "LRT " + super.toString();
    }
}
