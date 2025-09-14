package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MetroLine {
    protected final int lineId; //a single number
    protected final String lineCode; //e.g. DTL
    protected final String lineName; //DOWNTOWN LINE!!!!!!
    protected String lineColour; //in hex
    private GraphHandler graph;
    protected ArrayList<Station> stationList;
    private HashSet<Interchange> interchanges;

    protected HashMap<String,Integer> stationNameToIndexMap = new HashMap<>();
    protected HashMap<Integer,String> indexToStationNameMap = new HashMap<>();
    protected HashMap<String,Station> stationNameToStationMap = new HashMap<>();
    private HashMap<String, String> idToStationNameMap = new HashMap<>();

    public static HashMap<String, String> lineNameToColourMap = new HashMap<>();

    public final DropShadow highlightShadow =
            new DropShadow(20, Color.rgb(255, 255, 150, 0.8));

    public MetroLine(String lineName, String lineCode, int lineId, String colour) throws MissingFormatArgumentException{
        if (lineName.isEmpty() || lineCode.isEmpty()) throw new MissingFormatArgumentException("Provide a MetroLine name.");
        if (lineId < 0) throw new IllegalArgumentException("Invalid MetroLine number.");

        Pattern colourRGBPattern = Pattern.compile("^[a-fA-F0-9]{6}$");
        Matcher colourRGBMatcher = colourRGBPattern.matcher(colour);
        if (!colourRGBMatcher.find()) throw new IllegalArgumentException("Invalid colour code: Use RGB HEX");

        this.lineId = lineId; //doesnt need form verification; can be any unsigned int (preparedness for large scale future metro expansion)
        this.lineCode = lineCode; //doesnt need form verification; can be of any form
        this.lineName = lineName; //doesnt need form verification; can be in chinese/japanese/american/african/british so no need verification
        this.lineColour = colour; //verified!

        lineNameToColourMap.put(this.lineName, this.lineColour);

        graph = new GraphHandler();
        stationList = new ArrayList<>();
        interchanges = new HashSet<>();
    }
    public MetroLine(MetroLine other) {
        this(other.getLineName(), other.getLineCode(), other.getLineId(), other.getLineColour());
        this.stationList = other.getStationList();
        this.stationNameToStationMap = other.getStationNameToStationMap();
        this.stationNameToIndexMap = other.getStationNameToIndexMap();
        this.idToStationNameMap = other.getIdToStationNameMap();
        this.indexToStationNameMap = other.getIndexToStationNameMap();
        this.graph = other.getGraphHandler();
    }

    //i not supposed to do dijkstra here. so no getShortestPath created.
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
                for (Pair<String, String> pss : ((Interchange) newStation).getAllLinesInfo()) {
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
    /*
    public void removeStation(Station x) throws IllegalArgumentException {
        if (stationList.contains(x)) {
            stationNameToIndexMap.remove(x.getName());
            stationNameToStationMap.remove(x.getName());
            stationList.remove(x);
        }
        else throw new IllegalArgumentException(x + " does not exist.");
    }
     */

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

    public int getLineId() {
        return this.lineId;
    }
    public String getLineCode() {
        return this.lineCode;
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
    public GraphHandler getGraphHandler() {
        return this.graph;
    }

    public HashMap<String, Station> getStationNameToStationMap() {
        return new HashMap<String,Station>(this.stationNameToStationMap);
    }

    public HashMap<String, Integer> getStationNameToIndexMap() {
        return new HashMap<String,Integer>(this.stationNameToIndexMap);
    }

    public HashMap<Integer, String> getIndexToStationNameMap() {
        return new HashMap<Integer,String>(this.indexToStationNameMap);
    }

    public HashMap<String, String> getIdToStationNameMap() {
        return new HashMap<String,String>(this.idToStationNameMap);
    }

    public ArrayList<Pair<Integer, Integer>>[] getAdjListLine() {
        return this.graph.getAdjList();
    }


    /*
    //TODO: draws
    public abstract Group draw();
    public abstract Group setHighlighted(boolean highlighted);
     */

    //utils
    public String findCurrentInterchangeID(Interchange x) throws IllegalArgumentException{
        ArrayList<Pair<String, String>> tempDifferentLinesInfo = ((Interchange) x).getAllLinesInfo();
        for (Pair<String, String> pss: tempDifferentLinesInfo) {
            String curLineName = pss.getKey();
            String curLineInterchangeID = pss.getValue();
            if (curLineName.equalsIgnoreCase(this.lineName)) {
                return curLineInterchangeID;
            }
        }

        return null;
        //throw new IllegalArgumentException("Interchange " + x.getName() + " is not in MetroLine" + this.lineCode);
    }

    public String toString() { //the adj list will only require station IDs
        //adds beginning root
        String root = "Line" + this.lineId + ": " + this.lineName + ", " + this.lineCode + "\n";



        //the adjacency list
        //HashMap<Integer,Station> indexStationMap = reverseStationIndexMap(this);
        HashSet<Station> recorded = new HashSet<>();
        for (int i = 0; i < indexToStationNameMap.size(); i++) {
            String uName = indexToStationNameMap.get(i);
            Station u = stationNameToStationMap.get(uName);
            String uLineID = (u instanceof Interchange) ? findCurrentInterchangeID((Interchange) u) : u.getStationID();
            recorded.add(u);

            ArrayList<Pair<Integer, Integer>>[] adjListLine = graph.getAdjList();
            for (Pair<Integer, Integer> x: adjListLine[i]) {
                int destinationIndex = x.getKey();
                String destinationName = indexToStationNameMap.get(destinationIndex);
                Station v = stationNameToStationMap.get(destinationName);
                int time = x.getValue();
                if (!recorded.contains(v)) {
                    String vLineID = (v instanceof Interchange) ? findCurrentInterchangeID((Interchange) v) : v.getStationID();
                    root += (uLineID + " " + vLineID + " " + time +"\n");
                }
            }
        }
        return root;
    }
}
