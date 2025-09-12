package com.example.georgebra.Model;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetroSystem{
    private String cityName;
    GraphHandler graph;
    Group currentSystem = new Group();

    private ArrayList<Station> stationList = new ArrayList<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<MetroLine> metroLineList = new ArrayList<>();
    private HashMap<String, Station> stationNameToStationMap = new HashMap<>();
    private HashMap<String, Integer> stationNameToIndexMap = new HashMap<>();
    private HashMap<Integer, String> indexToStationNameMap = new HashMap<>(); //indexes for adjlist
    private HashMap<String, String> idToStationNameMap = new HashMap<>();
    //TODO: DONT FORGET TO READ THIS IMPORTANT NOTE: ID REFERS TO "NS13", index REFERS TO AN INTEGER FOR THE UTILITY OF THIS CLASS

    public MetroSystem(String cityName) {
        this.cityName = cityName;
        graph = new GraphHandler();
    }

    /*
    public Group draw() {
        //Clean UI
        currentSystem.getChildren().clear();

        int stationCnt = stationNameToIndexMap.size();
        HashSet<String> visitedU = new HashSet<>();
        for (int i = 0; i < stationCnt; i++) {
            visitedU.add(indexToStationNameMap.get(i));
            for (Pair<Integer, Integer> destinationIdAndTime: adjListSystem[i]) {
                //prevent creation of repeats
                String entryName = indexToStationNameMap.get(destinationIdAndTime.getKey());
                if (visitedU.contains(entryName)) continue;

                //create nodes
                Station u = stationNameToStationMap.get(indexToStationNameMap.get(i));
                Station v = stationNameToStationMap.get(entryName);

                Line edgeLine = new Line(u.getX(), u.getY(), v.getX(), v.getY());
                edgeLine.setStrokeWidth(10);
                Color colour = Color.web(u.getStationLineColour());
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
                currentSystem.getChildren().add(UGroup);
                currentSystem.getChildren().add(VGroup);
                currentSystem.getChildren().add(edge);
            }
        }
        return currentSystem;
    }

    //TODO
    public Group setHighlighted(boolean highlighted) {
       return currentSystem;
    }
     */

    public void addStation(Station newStation) {
        //boolean containStation = false;
        //singling out repeat non-interchange stations (no need to add)
        for (Station s : stationList) {
            if (s.getName().equalsIgnoreCase(newStation.getName()) && !(s instanceof Interchange)) {
                return; //containStation = true
            }
        }

        if (newStation instanceof Interchange) {
            //merging interchange if necessary
            boolean targetAdded = false;
            for (Interchange I: interchanges) {
                if (I.getName().equalsIgnoreCase(newStation.getName())) {
                    targetAdded = true;
                    break;
                }
            }

            if (targetAdded) Interchange.mergeInterchangesLineData((Interchange) newStation);
            else { //newstation hasnt been added yet
                int newIndex = stationList.size();
                graph.addNode(newIndex);

                stationNameToIndexMap.put(newStation.getName(), newIndex);
                indexToStationNameMap.put(newIndex, newStation.getName());
                stationNameToStationMap.put(newStation.getName(), newStation);

                //add all ids, including repeats (its map anyway)
                for (Pair<String, String> pss : ((Interchange) newStation).getDifferentLinesInfo()) {
                    String id = pss.getValue(); //possible id of interchange
                    idToStationNameMap.put(id, newStation.getName());
                }
            }
        }
        else {
            //merging normal station
            stationList.add(newStation);

            int newIndex = stationList.size();
            graph.addNode(newIndex);

            stationNameToIndexMap.put(newStation.getName(), newIndex);
            indexToStationNameMap.put(newIndex, newStation.getName());
            stationNameToStationMap.put(newStation.getName(), newStation);
            idToStationNameMap.put(newStation.getStationID(), newStation.getName());
        }
    }

    public void addEdge(Station u, Station v, int weight) {
        Station newU = u, newV = v; //newU, newV will eventually refer to an existing station/a newly added station for edge adding
        addStation(u); addStation(v);

        if (u instanceof Interchange) {
            for (Map.Entry<String, Integer> stationNameToIndexEntry: stationNameToIndexMap.entrySet()) {
                String sName = stationNameToIndexEntry.getKey();
                if (u.getName().equalsIgnoreCase(sName)) {
                    //u is existing interchange, edge vertex needs to refer to existing station/interchange obj
                    newU = stationNameToStationMap.get(sName); break;
                }
            }
        }
        if (v instanceof Interchange) {
            for (Map.Entry<String, Integer> stationNameToIndexEntry: stationNameToIndexMap.entrySet()) {
                String sName = stationNameToIndexEntry.getKey();
                if (v.getName().equalsIgnoreCase(sName)) {
                    //v is existing interchange, edge vertex needs to refer to existing station/interchange obj
                    newV = stationNameToStationMap.get(sName); break;
                }
            }
        }
        graph.addEdge(stationNameToIndexMap.get(newU.getName()), stationNameToIndexMap.get(newV.getName()), weight);
        graph.addEdge(stationNameToIndexMap.get(newV.getName()), stationNameToIndexMap.get(newU.getName()), weight);
    }

    public void addLine(MetroLine newMetroLine) {
        metroLineList.add(newMetroLine);

        //there are already this many stations here, so when i add more stations, the index must be built upon this number.
        int baseStationCnt = this.stationList.size();
        HashMap<String,String> idToStationNameMapLine = newMetroLine.getIdToStationNameMap();
        HashMap<Integer,String> indexToStationNameMapLine = newMetroLine.getIndexToStationNameMap();
        HashMap<String,Integer> stationNameToIndexMapLine = newMetroLine.getStationNameToIndexMap();
        HashMap<String,Station> stationNameToStationMapLine = newMetroLine.getStationNameToStationMap();

        //merge graphs
        this.graph.mergeGraphHandler(newMetroLine.getGraphHandler());
        //merge index-stationname
        for (Map.Entry<Integer,String> entry: indexToStationNameMapLine.entrySet()) {
            int otherIndex = entry.getKey();
            this.indexToStationNameMap.put(otherIndex + baseStationCnt, entry.getValue());
        }
        //merge stationName-Index
        for (Map.Entry<String,Integer> entry: stationNameToIndexMapLine.entrySet()) {
            int otherIndex = entry.getValue();
            this.stationNameToIndexMap.put(entry.getKey(), otherIndex + baseStationCnt);
        }
        //merge id-StationName
        this.idToStationNameMap.putAll(idToStationNameMapLine);

        //merge stations & stationname-station
        HashSet<Station> newStations = new HashSet<>();
        for (Map.Entry<String,Station> entry: stationNameToStationMapLine.entrySet()) {
            Station s = entry.getValue();
            this.addStation(s);
            this.stationNameToStationMap.put(s.getName(), s);
        }
    }

    public ArrayList<Station> getStationList() {
        return new ArrayList<>(this.stationList);
    }

    public ArrayList<MetroLine> getLineList() {
        return new ArrayList<>(this.metroLineList);
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    //utils
    public ArrayList<Station> genShortestPath(String UStr, String VStr) {
        UStr = UStr.trim(); VStr = VStr.trim();
        Station u = null, v = null;
        Pattern containDigit = Pattern.compile("[0-9]+");
        Matcher matcherU = containDigit.matcher(UStr);
        Matcher matcherV = containDigit.matcher(VStr);

        if (!matcherU.find() && !matcherV.find()) { //Station names do not contain digits
            u = stationNameToStationMap.get(UStr);
            v = stationNameToStationMap.get(VStr);
        }
        else { //If digits found in both, input is for station IDs; try to find them.
            String uName = idToStationNameMap.get(UStr);
            String vName = idToStationNameMap.get(VStr);
            u = stationNameToStationMap.get(uName);
            v = stationNameToStationMap.get(vName);
        } //null ret by get() if not found

        ArrayList<Integer> shortestPathIndexes;
        if (u != null && v != null) {
            int uIndex = stationNameToIndexMap.get(u.getName());
            int vIndex = stationNameToIndexMap.get(v.getName());
            shortestPathIndexes = graph.dijkstra(uIndex, vIndex);
        }
        else throw new IllegalArgumentException("Invalid station names:" + ((u == null) ? UStr : "") + " " + ((v == null) ? VStr : ""));

        //TODO: verify if this does not mess up the generated path
        ArrayList<Station> shortestPath = new ArrayList<>();
        for (int stationIndex: shortestPathIndexes) {
            String stationName = indexToStationNameMap.get(stationIndex);
            shortestPath.add(stationNameToStationMap.get(stationName));
        }
        return shortestPath;
    }
    /*
    public ArrayList<Station> genShortestPath(Station u, Station v) {
        return this.genShortestPath(u.getName(), v.getName());
    }
     */

    @Override
    public String toString() {
        String systemString = ""; //=== " + this.cityName + " Metro ===\n";
        for (Station s: stationList) {
            systemString += (s + "\n");
        }

        for (MetroLine line: this.metroLineList) {
            systemString += (line.toString());
        }

        return systemString;
    }

    //useless
    /*
    public void removeEdge(Station u, Station v) {

    }
    public void removeStation(Station x) {

    }
     */
}