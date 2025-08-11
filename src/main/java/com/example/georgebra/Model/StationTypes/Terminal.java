package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;

public class Terminal extends Station implements Drawable {
    Group currentStation = new Group();

    public Terminal(double x, double y, String id, String name, String lineName, int stationNo) {
        super(x, y, id, name, lineName, stationNo);

    }

    /*
    public Terminal(Station other) {
        super(other.getX(), other.getY(), other.getStationID(), other.getName(), other.getLineName(), other.getStationNo());

    }
     */

    public Group draw() {
        currentStation.getChildren().clear();

        javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);
        currentStation.getChildren().add(stn);
        return currentStation;
    }

    public Group setHighlighted() {
        //Clean UI
        currentStation.getChildren().clear();

        return currentStation;
    }

    public String toString() {
        return super.toString() + "[Terminal]";
    }
}
