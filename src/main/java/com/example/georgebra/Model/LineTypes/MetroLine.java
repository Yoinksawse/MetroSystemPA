package com.example.georgebra.Model.LineTypes;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.Interfaces.Graphable;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MetroLine implements Graphable {
    protected final String lineCode; //e.g. DTL
    protected final String lineName; //DOWNTOWN LINE!!!!!!
    protected String lineColour; //in hex
    private GraphHandler graph;
    protected HashSet<Station> stationList;
    private HashSet<Interchange> interchanges;

    protected HashMap<String,Integer> stationNameToIndexMap = new HashMap<>();
    protected HashMap<Integer,String> indexToStationNameMap = new HashMap<>();
    protected HashMap<String,Station> stationNameToStationMap = new HashMap<>();
    private HashMap<String, String> idToStationNameMap = new HashMap<>();

    public final static Pattern lineCodePattern = Pattern.compile("^[a-zA-Z]{1,10}$");
    public static HashMap<String, String> lineNameToColourMap = new HashMap<>();

    public final DropShadow highlightShadow =
            new DropShadow(20, Color.rgb(255, 255, 150, 0.8));

    public MetroLine(String lineName, String lineCode, String colour) throws MissingFormatArgumentException{
        if (lineName.isEmpty() || lineCode.isEmpty()) throw new MissingFormatArgumentException("Provide a MetroLine name.");
        //if (lineId < 0) throw new IllegalArgumentException("Invalid MetroLine number.");

        if (!colour.matches("^#?[a-fA-F0-9]{6}$")) {
            throw new IllegalArgumentException("Invalid colour code: Use RGB HEX");
        }

        //Pattern colourRGBPattern = Pattern.compile("#[a-fA-F0-9]{6}");
        //Matcher colourRGBMatcher = colourRGBPattern.matcher(colour);
        //if (colour.charAt(0) != '#') colour = "#" + colour;
        //System.out.println(colour); //TODO
        //if (!colourRGBMatcher.matches()) throw new IllegalArgumentException("Invalid colour code: Use RGB HEX");

        //this.lineId = lineId; //doesnt need form verification; can be any unsigned int (preparedness for large scale future metro expansion)
        this.lineCode = lineCode; //doesnt need form verification; can be of any form, even chinese; if you input rubbish that's your issue
        this.lineName = lineName.toLowerCase(); //Standard Format; //doesnt need form verification; can be in chinese/japanese/american/african/british so no need verification
        this.lineColour = colour; //verified!
        //System.out.println(this.lineColour);

        lineNameToColourMap.put(this.lineName.toLowerCase(), this.lineColour); //lowercase is standard input

        graph = new GraphHandler();
        stationList = new HashSet<>();
        interchanges = new HashSet<>();
    }
    public MetroLine(MetroLine other) {
        this(other.getLineName(), other.getLineCode(), other.getLineColour());
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
                //System.out.println(s + " " + newStation);
                //System.out.println("NIQUSIBA");
                return; //containStation = true
            }
        }

        if (newStation instanceof Interchange) {
            //merging interchange if necessary

            if (indexToStationNameMap.containsValue(newStation.getName())){
                Interchange.mergeInterchangesLineData((Interchange) newStation);
            }
            else { //newstation hasnt been added yet
                stationList.add(newStation);
                int newIndex = graph.getNodeCnt();

                graph.addNode(newIndex);

                stationNameToIndexMap.put(newStation.getName(), newIndex);
                indexToStationNameMap.put(newIndex, newStation.getName());
                //System.out.println(newIndex + " " + newStation.getName());
                stationNameToStationMap.put(newStation.getName(), newStation);

                //add all ids, including repeats (its map anyway)
                for (Map.Entry<String, String> pss : ((Interchange) newStation).getAllLinesInfo().entrySet()) {
                    String id = pss.getValue(); //possible id of interchange
                    idToStationNameMap.put(id, newStation.getName());
                }
                interchanges.add((Interchange) newStation);
            }
        }
        else {
            //merging normal station
            stationList.add(newStation);

            int newIndex = graph.getNodeCnt();
            graph.addNode(newIndex);

            stationNameToIndexMap.put(newStation.getName(), newIndex);
            indexToStationNameMap.put(newIndex, newStation.getName());
            stationNameToStationMap.put(newStation.getName(), newStation);
            //System.out.println(newIndex + " " + newStation.getName());
            idToStationNameMap.put(newStation.getStationID(), newStation.getName());
        }
    }
    /*
    public void removeStation(Station x) throws IllegalArgumentException {
        if (stationList.contains(x)) {
            stationNameToIndexMap.remove(x.getName());
            getStationNameToStationMap.remove(x.getName());
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

    public String getLineCode() {
        return this.lineCode;
    }
    public String getLineName() {
        return this.lineName;
    }
    public String getLineColour() {
        return this.lineColour;
    }
    public HashSet<Station> getStationList() {
        return new HashSet<Station>(this.stationList);
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

    public static HashMap<String, String> getLineNameToColourMap() {
        return new HashMap<>(MetroLine.lineNameToColourMap);
    }

    public HashMap<Integer, Integer>[] getAdjList() {
        return this.graph.getAdjList();
    }

    //utils
    public String findCurrentInterchangeID(Interchange x) throws IllegalArgumentException{
        HashMap<String, String> tempDifferentLinesInfo = ((Interchange) x).getAllLinesInfo();
        for (Map.Entry<String, String> pss: tempDifferentLinesInfo.entrySet()) {
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
        String root = "Line: " + this.lineName + ", " + this.lineCode + "\n";

        //the adjacency list
        //HashMap<Integer,Station> indexStationMap = reverseStationIndexMap(this);
        HashSet<Station> recorded = new HashSet<>();
        //System.out.println("indexToStationNameMap: " + indexToStationNameMap);
        for (int i = 0; i < indexToStationNameMap.size(); i++) {
            String uName = indexToStationNameMap.get(i);
            Station u = stationNameToStationMap.get(uName);
            //System.out.println(uName);
            String uLineID = (u instanceof Interchange) ? findCurrentInterchangeID((Interchange) u) : u.getStationID();
            recorded.add(u);

            HashMap<Integer, Integer>[] adjListLine = graph.getAdjList();
            for (Map.Entry<Integer, Integer> x: adjListLine[i].entrySet()) {
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
