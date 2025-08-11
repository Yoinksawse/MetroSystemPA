package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;

public class LRTLine extends Line implements Drawable {
    Group currentLine = new Group();

    public LRTLine(String lineName, String lineID, int lineNo) {
        super(lineName, lineID, lineNo, "A9A9A9");
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
        return "+LRT " + super.toString();
    }
}
