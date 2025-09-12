package com.example.georgebra.Model.StationTypes;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;

public class Terminal extends Station {
    Group currentStation = new Group();

    public Terminal(double x, double y, String id, String lineName, String name, int stationNo) {
        super(x, y, id, lineName, name);
    }

    /*
    public Terminal(Station other) {
        super(other.getX(), other.getY(), other.getStationID(), other.getName(), other.getLineName(), other.getStationNo());

    }
     */

    /*
    public Group draw() {
        currentStation.getChildren().clear();

        javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);
        currentStation.getChildren().add(stn);

        //mouse hover
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(currentStation, tooltip);

        //mouse drag
        final double[] offsetX = {0};
        final double[] offsetY = {0};
        currentStation.setOnMousePressed(e -> {
            offsetX[0] = e.getSceneX() - currentStation.getLayoutX();
            offsetY[0] = e.getSceneY() - currentStation.getLayoutY();
        });
        currentStation.setOnMouseDragged(e -> {
            currentStation.setLayoutX(e.getSceneX() - offsetX[0]);
            currentStation.setLayoutY(e.getSceneY() - offsetY[0]);
        });
        return currentStation;
    }

    public Group setHighlighted(boolean highlighted) {
        //Clean UI
        currentStation.getChildren().clear();

        if (highlighted) currentStation.setEffect(highlightShadow);
        else currentStation.setEffect(null);
        return currentStation;
    }
     */

    public String toString() {
        return super.toString() + "[Terminal]";
    }
}
