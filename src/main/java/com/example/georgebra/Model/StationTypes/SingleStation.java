package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import java.util.MissingFormatArgumentException;

public class SingleStation extends Station implements Drawable {
    Group currentStation = new Group();

    public SingleStation(double x, double y, String id, String lineName, String name) {
        super(x, y, id, lineName, name);
    }

    /*
    public SingleStation(Station other) {
        super(other.getX(), other.getY(), other.getStationID(), other.getName(), other.getLineName(), other.getStationNo());

    }
     */

    public Group draw() {
        currentStation.getChildren().clear();

        javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);

        Text stationID = new javafx.scene.text.Text(this.id);
        stationID.setX(x);
        stationID.setY(y);

        currentStation.getChildren().add(stn);
        currentStation.getChildren().add(stationID);

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

    public String toString() {
        return super.toString();
    }
}