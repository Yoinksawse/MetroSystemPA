package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.HashSet;

public class TourismLine extends MetroLine { //such as SentosaLine
    Group currentLine = new Group();

    public TourismLine(String lineName, String lineCode, int lineId) {
        super(lineName, lineCode, lineId, "000000");
    }

    /*
    //useless
    public Group draw() {
        //Clean UI
        currentLine.getChildren().clear();

        int stationCnt = stationNameToIndexMap.size();
        HashSet<String> visitedU = new HashSet<>();
        for (int i = 0; i < stationCnt; i++) {
            visitedU.add(indexToStationNameMap.get(i));
            for (Pair<Integer, Integer> entry: adjListLine[i]) {
                //prevent creation of repeats
                if (visitedU.contains(indexToStationNameMap.get(entry.getKey()))) continue;

                //create nodes
                String uName = indexToStationNameMap.get(i);
                Station u = stationNameToStationMap.get(uName);
                String vName = indexToStationNameMap.get(entry.getKey());
                Station v = stationNameToStationMap.get(vName);

                Line edgeLine = new Line(u.getX(), u.getY(), v.getX(), v.getY());
                edgeLine.setStrokeWidth(10);
                Color colour = Color.web(this.lineColour);
                edgeLine.setFill(colour);

                //group nodes
                Group UGroup = u.draw();
                Group VGroup = v.draw();

                Group edge = new Group();
                edge.getChildren().add(edgeLine);

                //add bindings
                edgeLine.startXProperty().bindBidirectional(UGroup.layoutXProperty());
                edgeLine.startYProperty().bindBidirectional(UGroup.layoutYProperty());
                edgeLine.endXProperty().bindBidirectional(VGroup.layoutXProperty());
                edgeLine.endYProperty().bindBidirectional(VGroup.layoutYProperty());

                //package up
                currentLine.getChildren().add(UGroup);
                currentLine.getChildren().add(VGroup);
                currentLine.getChildren().add(edge);
            }
        }
        return currentLine;
    }

    public Group setHighlighted(boolean highlighted) {
        //Clean UI
        currentLine.getChildren().clear();

        if (highlighted) currentLine.setEffect(highlightShadow);
        else currentLine.setEffect(null);
        return currentLine;
    }

     */

    public String toString() {
        return "Tourism " + super.toString();
    }
}
