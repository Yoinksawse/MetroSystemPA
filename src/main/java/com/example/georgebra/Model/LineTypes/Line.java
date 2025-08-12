package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.util.Pair;

import java.util.*;

public abstract class Line {
    protected final int lineNo;
    protected final String lineID;
    protected final String lineName;
    protected String lineColour; //in hex, TODO: convert pls!!!!!!!!
    protected ArrayList<Station> stationList = new ArrayList<>();
    protected HashMap<Station, Integer> stationIndexMap = new HashMap<>();
    protected HashMap<Integer, Station> indexStationMap = new HashMap<>();
    protected ArrayList<Pair<Station, Integer>> adjListLine[];

    public Line(String lineName, String lineID, int lineNo, String colour) throws MissingFormatArgumentException{
        if (lineName.isEmpty() || lineID.isEmpty()) throw new MissingFormatArgumentException("Provide a Line name.");
        if (lineNo < 0) throw new IllegalArgumentException("Invalid Line number.");

        this.lineNo = lineNo;
        this.lineID = lineID;
        this.lineName = lineName;
        this.lineColour = colour;

        //initialisation
        this.adjListLine = new ArrayList[500];
        for (int i = 0; i < adjListLine.length; i++) {
            adjListLine[i] = new ArrayList<>();
        }
    }
    public Line(Line other) {
        this(other.getLineName(), other.getLineID(), other.getLineNo(), other.getLineColour());
        this.stationList = other.getStationList();
        this.stationIndexMap = other.getStationIndexMap();
        this.indexStationMap = other.getIndexStationMap();
        this.adjListLine = other.getAdjListLine();
    }

    private void addStation(Station newStation) {
        stationIndexMap.put(newStation, stationList.size());
        indexStationMap.put(stationList.size(), newStation);
        stationList.add(newStation);



    }

    /*
    public void removeStation(Station x) throws IllegalArgumentException {
        if (stationList.contains(x)) {
            stationIndexMap.remove(x);
            stationList.remove(x);
        }
        else throw new IllegalArgumentException(x + " does not exist.");
    }
     */

    public void addEdge(Station u, Station v, int weight) {
        boolean containU = false;
        boolean containV = false;
        for (Station s: stationList)
            if (u.getName().equals(s.getName())) containU = true;
        for (Station s: stationList)
            if (v.getName().equals(s.getName())) containV = true;
        if (!containU) addStation(u);
        if (!containV) addStation(v);
        adjListLine[stationIndexMap.get(u)].add(new Pair<>(v, weight));
        adjListLine[stationIndexMap.get(v)].add(new Pair<>(u, weight));
    }

    /*
    public void removeEdge(Station u, Station v) throws IllegalArgumentException {
        if (!stationList.contains(u)) throw new IllegalArgumentException(u + " does not exist.");
        if (!stationList.contains(v)) throw new IllegalArgumentException(v + " does not exist.");
        adjListLine[stationIndexMap.get(u)].remove(v);
        adjListLine[stationIndexMap.get(v)].remove(u);
    }
    */

    public void setLineColour(String colour) {
        this.lineColour = colour; // HEX
    }

    public int getLineNo() {
        return this.lineNo;
    }
    public String getLineID() {
        return this.lineID;
    }
    public String getLineName() {
        return this.lineName;
    }
    public String getLineColour() {
        return this.lineColour;
    }
    public ArrayList<Station> getStationList() {
        return new ArrayList<Station>(this.stationList);
    }

    public HashMap<Station, Integer> getStationIndexMap() {
        return new HashMap<Station, Integer>(stationIndexMap);
    }

    public HashMap<Integer, Station> getIndexStationMap() {
        return new HashMap<Integer, Station>(indexStationMap);
    }
    public ArrayList<Pair<Station, Integer>>[] getAdjListLine() {
        return this.adjListLine;
    }


    //TODO: draws
    public abstract Group draw();
    public abstract Group setHighlighted();

    //utils
    //DO NOT USE
    public static HashMap<Integer,Station> reverseStationIndexMap(Line line) {
        HashMap<Station, Integer> stationIndexMap = line.getStationIndexMap();
        HashMap<Integer, Station> indexStationMap = new HashMap<>();
        for (HashMap.Entry<Station, Integer> e: stationIndexMap.entrySet()) {
            indexStationMap.put(e.getValue(), e.getKey());
        }
        return indexStationMap;
    }

    public String findCurrentInterchangeID(Interchange x) throws IllegalArgumentException{
        ArrayList<Pair<String, String>> tempDifferentLinesInfo = ((Interchange) x).getDifferentLinesInfo();
        for (Pair<String, String> pss: tempDifferentLinesInfo) {
            String curLineName = pss.getKey();
            String curLineInterchangeID = pss.getValue();
            if (curLineName.equals(this.lineName)) {
                return curLineInterchangeID;
            }
        }

        throw new IllegalArgumentException("Interchange " + x.getName() + " is not in Line" + this.lineID);
    }

    public String toString() { //the adj list will only require station IDs
        //adds beginning root
        String root = "Line " + this.lineNo + ": " + this.lineName + " (" + this.lineID + ")\n";

        //the adjacency list
        //HashMap<Integer,Station> indexStationMap = reverseStationIndexMap(this);
        HashSet<Station> recorded = new HashSet<>();
        for (int i = 0; i < indexStationMap.size(); i++) {
            Station u = indexStationMap.get(i);
            String uLineID = (u instanceof Interchange) ? findCurrentInterchangeID((Interchange) u) : u.getStationID();
            recorded.add(u);

            for (Pair<Station, Integer> x: adjListLine[i]) {
                Station v = x.getKey();
                int time = x.getValue();
                if (!recorded.contains(v)) {
                    String vLineID = (v instanceof Interchange) ? findCurrentInterchangeID((Interchange) v) : v.getStationID();
                    root += (uLineID + " " + vLineID + " " + time + "\n");
                }
            }
        }
        return root;
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(lineID, line.lineID);
    }
    public int hashCode() {
        return Objects.hash(lineID);
    }
    */
}
