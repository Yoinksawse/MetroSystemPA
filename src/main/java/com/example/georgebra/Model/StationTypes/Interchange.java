package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;

public class Interchange extends Station implements Drawable {
    Group currentStation = new Group();
    private int lineCnt = 0;
    private static ArrayList<Interchange> interchanges = new ArrayList<>();
    private static HashSet<String> interchangeNames = new HashSet<>();
    private HashSet<Pair<String, String>> differentLinesInfo = new HashSet<>();
    //^ above hashset contains entries: (name of one line the interchange is in, id of interchange in that line)

    //primary constructor
    public Interchange(double x, double y, ArrayList<Pair<String, String>> otherDifferentLinesInfo, String name, int stationNo) throws IllegalArgumentException {
        //Pass  first line name to super, but store all lines
        super(x, y, name, stationNo);

        for (Pair<String, String> lineInfo: otherDifferentLinesInfo) {
            String id = lineInfo.getValue();
            if (id.isEmpty() || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
                throw new IllegalArgumentException("Invalid Station ID.");
            }
        }

        this.differentLinesInfo = new HashSet<>(otherDifferentLinesInfo);
        interchanges.add(this);
        interchangeNames.add(this.name);
    }

    /*
    public Interchange(Station other) {
        super(other.getX(), other.getY(), other.getStationID(), other.getLineName(), other.getName(), other.getStationNo());
        interchanges.add(this);
        interchangeNames.add(this.name);
    }
    */

    public Group draw() {
        //clean UI
        currentStation.getChildren().clear();

        javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);

        String ids = "";
        for (Pair<String, String> id: differentLinesInfo) ids += (id.getValue() + "/");
        if (ids.length() >= 2) ids = ids.substring(0, ids.length() - 1);

        Text stationID = new javafx.scene.text.Text(ids);
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
        //clean UI
        currentStation.getChildren().clear();

        if (highlighted) currentStation.setEffect(highlightShadow);
        else currentStation.setEffect(null);
        return currentStation;
    }

    //check if the interchange already exists, if it does, add the lines to existing interchange
    public static Interchange checkExistenceAndMergeLines(Interchange other) {
        //find existing
        for (Interchange existing: interchanges) {
            if (existing.getName().equals(other.getName())) { //it already has an "instance" somewhere
                ArrayList<Pair<String, String>> xDifferentLinesInfo = other.getDifferentLinesInfo();

                for (int j = 0; j < xDifferentLinesInfo.size(); j++) {
                    existing.addLine(xDifferentLinesInfo.get(j).getKey(), xDifferentLinesInfo.get(j).getValue());
                }
                return existing; //the existing one
            }
        }

        //it does not have an "instance" anywhere: record it i guess?
        Interchange.interchanges.add(other);
        Interchange.interchangeNames.add(other.getName());
        return null; //not a duplicate
    }

    public static ArrayList<Interchange> getInterchanges() {
        return new ArrayList<>(interchanges);
    }

    public ArrayList<Pair<String, String>> getDifferentLinesInfo() {
        return new ArrayList<>(differentLinesInfo);
    }

    public void addLine(String lineName, String interchangeID) {
        if (lineName == null || lineName.isEmpty()) {
            throw new IllegalArgumentException("MetroLine name cannot be null or empty");
        }
        if (interchangeID == null || interchangeID.isEmpty()) {
            throw new IllegalArgumentException("Interchange ID cannot be null or empty");
        }

        this.differentLinesInfo.add(new Pair<String, String>(lineName, interchangeID));
        this.lineCnt++;
    }

    //I ASSERT STRONGLY THAT A DEVELOPER'S TROLLING WILL HAVE NO IMPACT ON THE APPLICATION'S INPUT HANDLING
    public boolean equals (Interchange other) {
        return this.name.equals(other.name);
    }

    public String toString() {
        String ids = "";
        for (Pair<String, String> id: differentLinesInfo) {
            ids += (id.getValue() + "/");
        }
        if (ids.length() >= 2) ids = ids.substring(0, ids.length() - 1);
        return "*" + ids + " (" + this.name + ")";
    }
}
