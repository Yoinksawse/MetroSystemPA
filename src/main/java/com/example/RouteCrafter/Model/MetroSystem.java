package com.example.RouteCrafter.Model;

import com.example.RouteCrafter.Model.GraphTheoryHandler.GraphHandler;
import com.example.RouteCrafter.Model.Interfaces.Graphable;
import com.example.RouteCrafter.Model.LineTypes.MetroLine;
import com.example.RouteCrafter.Model.StationTypes.Interchange;
import com.example.RouteCrafter.Model.StationTypes.Station;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetroSystem implements Graphable {
    private final int MAXN = 505;
    public final static int HEAVY_EDGE_WEIGHT = 10000;
    private String cityName;
    private String systemID;
    public int exchangeTime;
    private GraphHandler graph;

    private HashSet<Station> stationList = new HashSet<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<MetroLine> metroLineList = new ArrayList<>();
    private HashMap<String, Station> stationNameToStationMap = new HashMap<>();
    private HashMap<String, HashMap<String,Integer>> stationNameToIndexesMap = new HashMap<>();
    //stationName, <LineName, nodeindexesfortheline>>
    private HashMap<Integer, String> indexToStationNameMap = new HashMap<>(); //indexes for adjlist
    private HashMap<String, String> idToStationNameMap = new HashMap<>();
    //ID REFERS TO "NS13", index REFERS TO AN INTEGER FOR THE UTILITY OF THIS CLASS
    //private HashMap<String,ArrayList<Integer>> interchangeGraphReferences = new HashMap<>();
    private HashMap<String, ArrayList<Pair<Integer, String>>> interchangeGraphReferences = new HashMap<>();
    //<interchangeName, <<theGraphReference, lineOfThisIndex>>
    public final static Pattern systemIDPattern = Pattern.compile("^[a-zA-Z]{1,10}$");

    public MetroSystem(String cityName, String systemID) {
        this(cityName, 3, systemID);
    }
    public MetroSystem(String cityName, int averageExchangeTime, String systemID) {
        this.systemID = systemID;
        this.cityName = cityName;
        this.exchangeTime = averageExchangeTime;
        this.graph = new GraphHandler();
    }

    public MetroSystem(MetroSystem other) {
        this.systemID = other.getSystemID();
        this.cityName = other.getCityName();
        this.exchangeTime = other.getExchangeTime();
        this.graph = other.getGraphHandler();

        this.stationList = other.getStationList();
        this.interchanges = other.getInterchanges();
        this.metroLineList = other.getLineList();

        this.stationNameToStationMap = other.getStationNameToStationMap();
        this.stationNameToIndexesMap = other.getStationNameToIndexesMap();
        this.indexToStationNameMap = other.getIndexToStationNameMap();
        this.idToStationNameMap = other.getIdToStationNameMap();
        this.interchangeGraphReferences = other.getInterchangeGraphReferences();
    }

    public void addStation(Station newStation) { //this method avoids handling graphs
        for (Station s : stationList) {
            if (s.getName().equalsIgnoreCase(newStation.getName()) && !(s instanceof Interchange)) {
                return; //containStation = true
            }
        }

        if (newStation instanceof Interchange) {
            //Interchange.mergeInterchangesLineData((Interchange) newStation);
            for (Map.Entry<String,String> entry: ((Interchange) newStation).getAllLinesInfo().entrySet()) {
                idToStationNameMap.put(entry.getValue(), newStation.getName());
            }
        }
        else {
            idToStationNameMap.put(newStation.getStationID(), newStation.getName());
        }
        stationList.add(newStation);
        stationNameToStationMap.put(newStation.getName(), newStation);
    }

    public void addStation(Station newStation, HashMap<String,Integer> newGraphIndexes) {
        this.addStation(newStation);
        //handling graphing
        for (Map.Entry<String,Integer> lineInfoEntry: newGraphIndexes.entrySet()) {
            if (lineInfoEntry.getValue() < graph.getNodeCnt()) return; //invalid operation, node will overlap
        }

        HashMap<String, Integer> existingIndexes = stationNameToIndexesMap.get(newStation.getName());
        newGraphIndexes.putAll(existingIndexes);
        stationNameToIndexesMap.put(newStation.getName(), newGraphIndexes);
        for (Map.Entry<String,Integer> lineInfoEntry: newGraphIndexes.entrySet()) {
            indexToStationNameMap.put(lineInfoEntry.getValue(), newStation.getName());
        }
    }

    public void addEdge(Station u, Station v, int weight) {
        if (u == null || v == null) return;
        Station newU = u, newV = v; //newU, newV will eventually refer to an existing station/a newly added station for edge adding
        addStation(u); addStation(v);

        if (u instanceof Interchange) {
            for (Map.Entry<String, HashMap<String,Integer>> stationNameToIndexEntry: stationNameToIndexesMap.entrySet()) {
                String sName = stationNameToIndexEntry.getKey();
                if (u.getName().equalsIgnoreCase(sName)) {
                    //u is existing interchange, edge vertex needs to refer to existing station/interchange obj
                    newU = stationNameToStationMap.get(sName); break;
                }
            }
        }
        if (v instanceof Interchange) {
            for (Map.Entry<String, HashMap<String,Integer>> stationNameToIndexEntry: stationNameToIndexesMap.entrySet()) {
                String sName = stationNameToIndexEntry.getKey();
                if (v.getName().equalsIgnoreCase(sName)) {
                    //v is existing interchange, edge vertex needs to refer to existing station/interchange obj
                    newV = stationNameToStationMap.get(sName); break;
                }
            }
        }

        HashMap<String,Integer> lineIndexesU = stationNameToIndexesMap.get(newU.getName());
        HashMap<String,Integer> lineIndexesV = stationNameToIndexesMap.get(newV.getName());

        for (Map.Entry<String,Integer> lineIndexEntryU: lineIndexesU.entrySet()) {
            for (Map.Entry<String,Integer> lineIndexEntryV: lineIndexesV.entrySet()) {
                graph.addEdge(lineIndexEntryU.getValue(), lineIndexEntryV.getValue(), weight);
            }
        }
    }

    public void addLine(MetroLine newMetroLine) {
        metroLineList.add(newMetroLine);
        int newNodeOffset = this.graph.getNodeCnt();
        HashSet<Station> lineStations = newMetroLine.getStationList();
        HashMap<String, String> idToStationNameMapLine = newMetroLine.getIdToStationNameMap();
        HashMap<Integer, String> indexToStationNameMapLine = newMetroLine.getIndexToStationNameMap();
        HashMap<String, Station> stationNameToStationMapLine = newMetroLine.getStationNameToStationMap();
        HashMap<String, Integer> stationNameToIndexMapLine = newMetroLine.getStationNameToIndexMap();

        //1. merge index-stationname (ok)
        for (Map.Entry<Integer, String> entry : indexToStationNameMapLine.entrySet()) {
            int otherIndex = entry.getKey();
            //Station s = stationNameToStationMapLine.get(entry.getValue());
            this.indexToStationNameMap.put(otherIndex + newNodeOffset, entry.getValue());
        }

        //2. merge stationName-Index (ok)
        //there are no duplicates, so it is safe to add
        for (Map.Entry<String, Integer> entry : stationNameToIndexMapLine.entrySet()) {
            String newStationName = entry.getKey();
            Station newStation = stationNameToStationMapLine.get(newStationName);
            int oldIndex = entry.getValue();
            //newStations have not been added to this map yet
            if (newStation instanceof Interchange && stationNameToStationMapLine.containsKey(newStationName)) {
                HashMap<String, String> newAllLinesInfo = ((Interchange) newStation).getAllLinesInfo();
                HashMap<String, Integer> mp = stationNameToIndexesMap.get(newStationName);
                if (mp == null) mp = new HashMap<>();
                boolean interchangeFirstOccurrenceAlreadyRecoreded = false;
                for (Map.Entry<String, String> lineInfo : newAllLinesInfo.entrySet()) {
                    if (mp.containsKey(lineInfo.getKey().trim())) {
                        interchangeFirstOccurrenceAlreadyRecoreded = true;
                    }
                }

                if (interchangeFirstOccurrenceAlreadyRecoreded) {
                    //System.out.println(oldIndex + newNodeOffset);

                    stationNameToIndexesMap.get(newStationName).put(newMetroLine.getLineName(), oldIndex + newNodeOffset);
                } else {
                    HashMap<String, Integer> newEntry = new HashMap<>();
                    newEntry.put(newMetroLine.getLineName(), oldIndex + newNodeOffset);
                    stationNameToIndexesMap.put(newStationName, newEntry);
                }
            }
            else { //merging singleStation
                HashMap<String, Integer> mp = stationNameToIndexesMap.get(newStationName);
                if (mp == null) mp = new HashMap<>();
                mp.put(newMetroLine.getLineName(), oldIndex + newNodeOffset);
                stationNameToIndexesMap.put(newStationName, mp);
                //System.out.println(stationNameToIndexesMap);
            }
        }

        //3. merge graph
        //get all nodes (ok?)
        GraphHandler graphLine = newMetroLine.getGraphHandler();
        ArrayList<Integer> graphNodesLine = new ArrayList<>(graphLine.getNodes());
        ArrayList<Integer> graphNodesSystem = new ArrayList<>(this.graph.getNodes());

        //create interchangeReferences map
        for (Integer otherNd : graphNodesLine) {
            String stationName = indexToStationNameMapLine.get(otherNd);
            Station newStation = stationNameToStationMapLine.get(stationName);

            if (newStation instanceof Interchange) {
                ArrayList<Pair<Integer, String>> interchangeReferences = interchangeGraphReferences.computeIfAbsent(newStation.getName(), k -> new ArrayList<>());
                interchangeReferences.add(new Pair<>(otherNd + newNodeOffset, newMetroLine.getLineName()));// + newNodeOffset);
                //gives resultant interchange node reference after graphs be merged //TODO
            }
        }

        for (Integer thisNd : graphNodesSystem) {
            String stationName = indexToStationNameMap.get(thisNd);
            Station newStation = stationNameToStationMap.get(stationName);
            if (newStation instanceof Interchange) { //TODO
                ArrayList<Pair<Integer, String>> interchangeReferences = interchangeGraphReferences.get((newStation.getName()));
                if (interchangeReferences == null) {
                    interchangeReferences = new ArrayList<>();
                    interchangeGraphReferences.put(newStation.getName(), interchangeReferences);
                }
                interchangeReferences.add(new Pair<>(thisNd, newMetroLine.getLineName()));// + newNodeOffset);? //TODO
                //gives resultant interchange node reference after graphs be merged
                // (equal to before, since this.graph remain unchanged)
            }
        }

        //officially merge graph (after creating interchangeGraphReferences to preserve past state)
        this.graph.mergeGraphHandler(newMetroLine.getGraphHandler(), newNodeOffset);

        //4. web interchange transfers
        //interchangeGraphReferences: <interchangeName, <<theGraphReference, lineOfThisIndex>>

        ArrayList<Pair<Integer, Integer>> interchangeInnerEdgesLocal = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Pair<Integer,String>>> entry: interchangeGraphReferences.entrySet()) {
            //entry is an interchange's info
            ArrayList<Pair<Integer, String>> curInterchangeNodesStationName = new ArrayList<>(entry.getValue());
            //generate complete graph to represent free exchange within interchange
            for (int i = 0; i < curInterchangeNodesStationName.size(); i++) {
                for (int j = i + 1; j < curInterchangeNodesStationName.size(); j++) {
                    //hiddenedge is within station: 3min transfer
                    int nodeIndexI = curInterchangeNodesStationName.get(i).getKey(),
                            nodeIndexJ = curInterchangeNodesStationName.get(j).getKey();
                    if (nodeIndexI != nodeIndexJ) {
                        interchangeInnerEdgesLocal.add(new Pair<>(nodeIndexI, nodeIndexJ));
                    }
                }
            }
        }

        //System.out.println(newHiddenEdges);
        for (Pair<Integer, Integer> pii: interchangeInnerEdgesLocal) { //TODO
            graph.addEdge(pii.getKey(), pii.getValue(), exchangeTime); //add edges for both
            graph.addHeavyEdge(pii.getKey(), pii.getValue()); //heavy edge adj list need to override previous
        }

        //5. merge stations
        //System.out.println(lineStations);
        for (Station lineStation : lineStations) {
            //System.out.println(lineStation.getName());
            this.addStation(lineStation);
        }
    }

    @Override
    public GraphHandler getGraphHandler() {
        return this.graph;
    }

    private HashMap<String,ArrayList<Pair<Integer, String>>> getInterchangeGraphReferences() {
        return new HashMap<>(this.interchangeGraphReferences);
    }

    private HashMap<String, HashMap<String, Integer>> getStationNameToIndexesMap() {
        return new HashMap<>(this.stationNameToIndexesMap);
    }

    public HashSet<Station> getStationList() {
        return new HashSet<>(this.stationList);
    }

    private HashSet<Interchange> getInterchanges() {
        return this.interchanges;
    }

    private int getExchangeTime() {
        return this.exchangeTime;
    }

    public ArrayList<MetroLine> getLineList() {
        return new ArrayList<>(this.metroLineList);
    }

    public String getSystemID() {
        return this.systemID;
    }

    public String getCityName() {
        return cityName;
    }

    public HashMap<Integer,String> getIndexToStationNameMap() {
        return this.indexToStationNameMap;
    }

    @Override
    public HashMap<String, String> getIdToStationNameMap() {
        return this.idToStationNameMap;
    }

    @Override
    public Station getStationByName(String name) {
        return Graphable.super.getStationByName(name);
    }

    public HashMap<String, Station> getStationNameToStationMap() {
        return this.stationNameToStationMap;
    }

    public HashMap<String, String> getMetroLineNameToColourMap() {
        return MetroLine.getLineNameToColourMap();
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setSystemID(String systemID) {
        Matcher matcher = systemIDPattern.matcher(systemID);
        if (!matcher.find()) throw new InputMismatchException("System ID must have 1-10 word characters.");
        if (!systemID.endsWith("MTR")) this.systemID += "MTR";

        this.systemID = systemID;
    }

    //station of the path, time, exchanges
    public Pair<ArrayList<Station>, Pair<Integer,Integer>> genLeastExchangePath(String UStr, String VStr) throws IllegalArgumentException {
        Pair<ArrayList<Station>,Integer> leastExchangePath;
        try { leastExchangePath = genShortestPath(UStr, VStr, true); }
        catch (IllegalArgumentException e) { throw e; }
        //I don't have the GUI components here to show error in alertbox; so sadly have throw

        int time = leastExchangePath.getValue(), exchanges = time / HEAVY_EDGE_WEIGHT;
        time = (time % HEAVY_EDGE_WEIGHT) + exchanges * this.exchangeTime;

        ArrayList<Station> shortestPathStations = leastExchangePath.getKey();

        return new Pair<>(shortestPathStations, new Pair<>(time, exchanges));
    }

    //utils
    public Pair<ArrayList<Station>, Integer> genShortestPath(String UStr, String VStr) throws IllegalArgumentException {
        return this.genShortestPath(UStr, VStr, false);
    }

    public Pair<ArrayList<Station>, Integer> genShortestPath(String UStr, String VStr, boolean generatingLeastExchangePath) throws IllegalArgumentException {
        UStr = UStr.trim(); VStr = VStr.trim();
        Station u, v;
        Pattern containDigit = Pattern.compile("[0-9]+");
        Matcher matcherU = containDigit.matcher(UStr);
        Matcher matcherV = containDigit.matcher(VStr);

        if (!matcherU.find() && !matcherV.find()) { //Station names do not contain digits
            u = stationNameToStationMap.get(UStr.toLowerCase()); //Standard Format);
            v = stationNameToStationMap.get(VStr.toLowerCase()); //Standard Format);
        }
        else return null; //handle this silently...

        ArrayList<Integer> pathGraphNodes = new ArrayList<>();
        int time = Integer.MAX_VALUE;
        if (u != null && v != null) {
            HashMap<String,Integer> uIndexesData = stationNameToIndexesMap.get(u.getName().toLowerCase()); //Standard Format
            HashMap<String,Integer> vIndexesData = stationNameToIndexesMap.get(v.getName().toLowerCase()); //Standard Format

            int uIndex = 0;
            HashSet<Integer> vIndices = new HashSet<>(vIndexesData.values());

            if (generatingLeastExchangePath) { //minimum exchange computation
                for (Object uIndexObj: uIndexesData.values().toArray()) {
                    uIndex = (Integer) uIndexObj;
                    ArrayList<Integer> curShortestPathGraphNodes = graph.dijkstra(uIndex, vIndices, true);
                    int curTime = graph.getShortestDistance(graph.MOST_RECENT_END_NODE); //valid shortly after running the algo

                    if (curTime < time) {
                        time = curTime;
                        pathGraphNodes = curShortestPathGraphNodes;
                        System.out.println(pathGraphNodes + ", Time: " + time);
                    }
                }
            }
            else { //shortest Path computation
                for (Object uIndexObj: uIndexesData.values().toArray()) {
                    uIndex = (Integer) uIndexObj;
                    ArrayList<Integer> curShortestPathGraphNodes = graph.dijkstra(uIndex, vIndices);
                    int curTime = graph.getShortestDistance(graph.MOST_RECENT_END_NODE); //valid shortly after running the algo

                    if (curTime < time) {
                        time = curTime;
                        pathGraphNodes = curShortestPathGraphNodes;
                        System.out.println(pathGraphNodes + ", Time: " + time);
                    }
                }
            }
        }
        else throw new IllegalArgumentException("Invalid station names: " + ((u == null) ? UStr : "") + " " + ((v == null) ? VStr : ""));

        ArrayList<Station> shortestPath = new ArrayList<>();
        String prevStnName = "";
        for (Integer stationGraphNode : pathGraphNodes) {
            String stationName = indexToStationNameMap.get(stationGraphNode);
            if (stationName.equalsIgnoreCase(prevStnName)) continue;
            shortestPath.add(stationNameToStationMap.get(stationName));
            prevStnName = stationName;
        }
        return new Pair<>(shortestPath, time);
    }

    @Override
    public String toString() {
        String systemString = "=== " + this.cityName + " Metro ===\n";
        for (Station s: stationList) {
            systemString += (s + "\n");
        }

        for (MetroLine line: this.metroLineList) {
            systemString += (line.toString());
        }

        return systemString;
    }
}