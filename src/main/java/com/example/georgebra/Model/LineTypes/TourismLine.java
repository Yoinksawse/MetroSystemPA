package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.Drawable;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.HashSet;

public class TourismLine extends MetroLine implements Drawable { //such as SentosaLine
    Group currentLine = new Group();

    public TourismLine(String lineName, String lineID, int lineNo) {
        super(lineName, lineID, lineNo, "000000");
    }

    //useless
    public Group draw() {
        //Clean UI
        currentLine.getChildren().clear();

        int stationCnt = stationIndexMap.size();
        HashSet<String> visitedU = new HashSet<>();
        for (int i = 0; i < stationCnt; i++) {
            visitedU.add(indexStationMap.get(i).getName());
            for (Pair<Station, Integer> entry: adjListLine[i]) {
                //prevent creation of repeats
                if (visitedU.contains(entry.getKey().getName())) continue;

                //create nodes
                Station u = indexStationMap.get(i);
                Station v = entry.getKey();

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

    public String toString() {
        return "+Tourism " + super.toString();
    }
}
