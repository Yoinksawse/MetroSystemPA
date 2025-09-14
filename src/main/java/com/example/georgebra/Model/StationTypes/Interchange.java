package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import javafx.scene.Group;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Interchange extends Station {
    Group currentStation = new Group();
    private int lineCnt = 0;
    public static ArrayList<Interchange> interchanges = new ArrayList<>();
    private static HashSet<String> interchangeNames = new HashSet<>();
    private HashSet<Pair<String, String>> allLinesInfo = new HashSet<>();
    GraphHandler exchangeGraph = new GraphHandler();
    private HashMap<String,Integer> lineCodeToIndexMiniMap = new HashMap<>();
    private HashMap<Integer,String> indexToLineCodeMiniMap = new HashMap<>();
    //^ above hashset contains entries: (name of one line the interchange is in, id of interchange in that line)

    //primary constructor
    public Interchange(double x, double y, ArrayList<Pair<String, String>> otherallLinesInfo, String name) throws IllegalArgumentException {
        //Pass  first line name to super, but store all lines
        super(x, y, name);

        for (Pair<String, String> lineInfo: otherallLinesInfo) {
            String id = lineInfo.getValue();
            if (id.isEmpty()) { // || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
                throw new IllegalArgumentException("Invalid Station ID: " + id);
            }
        }

        this.allLinesInfo = new HashSet<>(otherallLinesInfo);
        //this.updateExchangeGraph();

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

    /*
    //TODO: if a new line is added, expand the complete exchange graph
    public void updateExchangeGraph() {
        final int EXCHANGE_TIME = 3;
        ArrayList<Pair<String,String>> allLinesInfoArray = new ArrayList<>(allLinesInfo);
        for (int i = 0; i < allLinesInfoArray.size(); i++) {
            lineCodeToIndexMiniMap.put(allLinesInfoArray.get(i).getValue(), i);
            indexToLineCodeMiniMap.put(i, allLinesInfoArray.get(i).getValue());
            for (int j = 0; j < allLinesInfoArray.size(); j++) {
                exchangeGraph.addEdge(i, j, EXCHANGE_TIME);
            }
        }
    }
     */

    //check if the interchange already exists, if it does, add the lines to existing interchange
    public static void mergeInterchangesLineData(Interchange other) {
        //find existing
        for (Interchange existing: interchanges) {
            if (existing.getName().equalsIgnoreCase(other.getName())) { //it already has an "instance" somewhere
                ArrayList<Pair<String, String>> xAllLinesInfo = other.getAllLinesInfo();
                for (Pair<String, String> stringStringPair : xAllLinesInfo) {
                    existing.addLine(stringStringPair.getKey(), stringStringPair.getValue());
                    //existing.updateExchangeGraph();
                }
            }
        }


        throw new NoSuchElementException("HELLO! Your interchange does not have a match.");
    }

    public static ArrayList<Interchange> getInterchanges() {
        return new ArrayList<>(interchanges);
    }

    public ArrayList<Pair<String, String>> getAllLinesInfo() {
        return new ArrayList<>(allLinesInfo);
    }

    public void addLine(String lineName, String interchangeID) {
        if (lineName == null || lineName.isEmpty()) {
            throw new IllegalArgumentException("MetroLine name cannot be null or empty");
        }
        if (interchangeID == null || interchangeID.isEmpty()) {
            throw new IllegalArgumentException("Interchange ID cannot be null or empty");
        }

        boolean alreadyContains = false;
        for (Pair<String, String> pss: this.allLinesInfo) {
            if (pss.getKey().equalsIgnoreCase(lineName)) alreadyContains = true;
        }

        if (!alreadyContains) {
            this.allLinesInfo.add(new Pair<>(lineName, interchangeID));
            this.lineCnt++;
        }

        //updateExchangeGraph();
    }

    public String toString() {
        String ids = "";
        for (Pair<String, String> id: allLinesInfo) {
            ids += (id.getValue() + "/");
        }
        if (ids.length() >= 2) ids = ids.substring(0, ids.length() - 1);
        return "*" + ids + ": " + this.name + " " + this.x + " " + this.y;
    }
}


    /*
    public Group draw() {
        //clean UI
        currentStation.getChildren().clear();

        javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);

        String ids = "";
        for (Pair<String, String> id: allLinesInfo) ids += (id.getValue() + "/");
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
     */
