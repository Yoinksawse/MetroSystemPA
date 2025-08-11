package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;

public class TourismLine extends Line implements Drawable { //such as SentosaLine
    Group currentLine = new Group();

    public TourismLine(String lineName, String lineID, int lineNo) {
        super(lineName, lineID, lineNo, "000000");
    }

    public Group draw() {
        //Clean UI
        currentLine.getChildren().clear();

        return currentLine;
    }

    public Group setHighlighted() {
        //Clean UI
        currentLine.getChildren().clear();

        return currentLine;
    }

    public String toString() {
        return "+Tourism " + super.toString();
    }
}
