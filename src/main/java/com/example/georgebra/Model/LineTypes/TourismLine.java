package com.example.georgebra.Model.LineTypes;

import java.util.HashSet;

public class TourismLine extends MetroLine { //such as SentosaLine
    public TourismLine(String lineName, String lineCode) {
        super(lineName, lineCode, "#000000");
    }

    public String toString() {
        return "Tourism " + super.toString();
    }
}
