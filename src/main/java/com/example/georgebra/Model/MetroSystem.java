package com.example.georgebra.Model;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetroSystem{
    private String cityName;
    int exchangeTime;
    private final int MAXN = 505;
    GraphHandler graph;

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
    private HashMap<String,HashMap<Integer, String>> interchangeGraphReferences = new HashMap<>();
    //<interchangeName, <<theGraphReference, lineOfThisIndex>>

    public MetroSystem(String cityName) {
        this(cityName, 3);
    }
    public MetroSystem(String cityName, int averageExchangeTime) {
        this.cityName = cityName;
        this.exchangeTime = averageExchangeTime;
        graph = new GraphHandler();
    }

    private void addStation(Station newStation) { //this method avoids handling graphs
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
        newGraphIndexes.putAll(newGraphIndexes);
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
                mp.put(newStationName, oldIndex + newNodeOffset);
                stationNameToIndexesMap.put(newStationName, mp);
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
                HashMap<Integer, String> interchangeReferences = interchangeGraphReferences.computeIfAbsent(newStation.getName(), k -> new HashMap<>());
                interchangeReferences.put(otherNd + newNodeOffset, newMetroLine.getLineName());// + newNodeOffset);
                //gives resultant interchange node reference after graphs be merged
            }
        }

        for (Integer thisNd : graphNodesSystem) {
            String stationName = indexToStationNameMap.get(thisNd);
            Station newStation = stationNameToStationMap.get(stationName);
            if (newStation instanceof Interchange) {
                HashMap<Integer, String> interchangeReferences = interchangeGraphReferences.get((newStation.getName()));
                if (interchangeReferences == null) {
                    interchangeReferences = new HashMap<>();
                    interchangeGraphReferences.put(newStation.getName(), interchangeReferences);
                }
                interchangeReferences.put(thisNd, newMetroLine.getLineName());// + newNodeOffset);?
                //gives resultant interchange node reference after graphs be merged
                // (equal to before, since this.graph remain unchanged)
            }
        }

        //officially merge graph (after creating interchangeGraphReferences to preserve past state)
        this.graph.mergeGraphHandler(newMetroLine.getGraphHandler(), newNodeOffset);

        //4. web interchange transfers
        //interchangeGraphReferences: <interchangeName, <<theGraphReference, lineOfThisIndex>>

        HashSet<Pair<Integer, Integer>> newHiddenEdges = new HashSet<>();
        for (Map.Entry<String, HashMap<Integer,String>> entry: interchangeGraphReferences.entrySet()) {
            //entry is an interchange's info
            ArrayList<Integer> curInterchangeNodes = new ArrayList<>(entry.getValue().keySet());
            //generate complete graph to represent free exchange within interchange
            for (int i = 0; i < curInterchangeNodes.size(); i++) {
                for (int j = i + 1; j < curInterchangeNodes.size(); j++) {
                    //hiddenedge is within station: 3min transfer
                    int nodeIndexI = curInterchangeNodes.get(i), nodeIndexJ = curInterchangeNodes.get(j);
                    if (nodeIndexI != nodeIndexJ) {
                        newHiddenEdges.add(new Pair<>(nodeIndexI, nodeIndexJ));
                    }
                }
            }
        }

        //System.out.println(newHiddenEdges);
        for (Pair<Integer, Integer> pii: newHiddenEdges) {
            graph.addEdge(pii.getKey(), pii.getValue(), exchangeTime);
        }

        //5. merge stations
        //System.out.println(lineStations);
        for (Station lineStation : lineStations) {
            //System.out.println(lineStation.getName());
            this.addStation(lineStation);
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

    /*
    public HashMap<String, HashMap<String, Integer>> getStationNameToIndexesMap() {
        return this.stationNameToIndexesMap;
    }
     */

    public HashMap<Integer,String> getIndexToStationNameMap() {
        return this.indexToStationNameMap;
    }

    public HashMap<String, Station> stationNameToStationMap() { //TODO
        return this.stationNameToStationMap;
    }

    public HashMap<String, String> getMetroLineNameToColourMap() {
        return MetroLine.getLineNameToColourMap();
    }

    public GraphHandler getGraph() {
        return graph;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    //TODO
    public Pair<ArrayList<Station>, Integer> genLeastExchangePath(String UStr, String VStr, int originalExchangeTime) {
        Pair<ArrayList<Station>,Integer> shortestPath = this.genShortestPath(UStr, VStr);
        int time = shortestPath.getValue(), exchanges = time / 10000;
        time = (time % 10000) + exchanges * originalExchangeTime;

        return new Pair<>(shortestPath.getKey(), time);
    }

    //utils
    public Pair<ArrayList<Station>, Integer> genShortestPath(String UStr, String VStr) {
        UStr = UStr.trim(); VStr = VStr.trim();
        Station u = null, v = null;
        Pattern containDigit = Pattern.compile("[0-9]+");
        Matcher matcherU = containDigit.matcher(UStr);
        Matcher matcherV = containDigit.matcher(VStr);

        if (!matcherU.find() && !matcherV.find()) { //Station names do not contain digits
            u = stationNameToStationMap.get(UStr.toLowerCase()); //Standard Format);
            v = stationNameToStationMap.get(VStr.toLowerCase()); //Standard Format);
            //System.out.println(stationNameToIndexesMap);
            //System.out.println(indexToStationNameMap);
        }
        else { //If digits found in both, input is for station IDs; try to find them.
            String uName = idToStationNameMap.get(UStr);
            String vName = idToStationNameMap.get(VStr);
            //System.out.println(uName);
            //System.out.println(vName);
            u = stationNameToStationMap.get(uName.toLowerCase()); //Standard Format
            v = stationNameToStationMap.get(vName.toLowerCase()); //Standard Format
        } //null ret by get() if not found

        ArrayList<Integer> shortestPathGraphNodes;
        int time = 0, firstStationIndex = 0;
        if (u != null && v != null) {
            HashMap<String,Integer> uIndexes = stationNameToIndexesMap.get(u.getName().toLowerCase()); //Standard Format
            HashMap<String,Integer> vIndexes = stationNameToIndexesMap.get(v.getName().toLowerCase()); //Standard Format
            int uIndex = (Integer) uIndexes.values().toArray()[0]; //get any index
            int vIndex = (Integer) vIndexes.values().toArray()[0];
            //System.out.println(uIndex + " " + vIndex + ", " + u.getName() + " " + v.getName());
            shortestPathGraphNodes = graph.dijkstra(uIndex, vIndex);
            firstStationIndex = uIndex;
            //System.out.println(shortestPathGraphNodes);
            //System.out.println(stationNameToIndexesMap);
            time = graph.getShortestDistance(vIndex); //valid shortly after running the algo
        }
        else throw new IllegalArgumentException("Invalid station names:" + ((u == null) ? UStr : "") + " " + ((v == null) ? VStr : ""));

        ArrayList<Station> shortestPath = new ArrayList<>();
        String prevStnName = "";
        String firstStationName = indexToStationNameMap.get(firstStationIndex);
        for (Integer stationGraphNode : shortestPathGraphNodes) {
            String stationName = indexToStationNameMap.get(stationGraphNode);
            if (stationName.equalsIgnoreCase(prevStnName)) {
                if (stationName.equalsIgnoreCase(firstStationName)) time -= 3;
                continue;
            }
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