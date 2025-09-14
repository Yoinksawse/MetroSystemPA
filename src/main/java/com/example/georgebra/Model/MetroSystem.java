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
    int EXCHANGE_TIME;
    private final int MAXN = 505;
    GraphHandler graph;
    //Group currentSystem = new Group();

    private ArrayList<Station> stationList = new ArrayList<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<MetroLine> metroLineList = new ArrayList<>();
    private HashMap<String, Station> stationNameToStationMap = new HashMap<>();
    private HashMap<String, Integer> stationNameToIndexMap = new HashMap<>();
    private HashMap<Integer, String> indexToStationNameMap = new HashMap<>(); //indexes for adjlist
    private HashMap<String, String> idToStationNameMap = new HashMap<>();
    //TODO: DONT FORGET TO READ THIS IMPORTANT NOTE: ID REFERS TO "NS13", index REFERS TO AN INTEGER FOR THE UTILITY OF THIS CLASS
    //private HashMap<String,ArrayList<Integer>> interchangeGraphReferences = new HashMap<>();
    private HashMap<String,HashSet<Integer>> interchangeGraphReferences = new HashMap<>();

    public MetroSystem(String cityName) {
        this(cityName, 3);
    }
    public MetroSystem(String cityName, int averageExchangeTime) {
        this.cityName = cityName;
        this.EXCHANGE_TIME = averageExchangeTime;
        graph = new GraphHandler();
    }

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
        int newNodeOffset = this.graph.getNodeCnt();
        HashMap<String,String> idToStationNameMapLine = newMetroLine.getIdToStationNameMap();
        HashMap<Integer,String> indexToStationNameMapLine = newMetroLine.getIndexToStationNameMap();
        HashMap<String,Station> stationNameToStationMapLine = newMetroLine.getStationNameToStationMap();
        HashMap<String, Integer> stationNameToIndexMapLine = newMetroLine.getStationNameToIndexMap();

        //1. merge index-stationname (ok)
        for (Map.Entry<Integer,String> entry: indexToStationNameMapLine.entrySet()) {
            int otherIndex = entry.getKey();
            //Station s = stationNameToStationMapLine.get(entry.getValue());
            this.indexToStationNameMap.put(otherIndex + newNodeOffset, entry.getValue());

            //alpis.put(otherIndex + newNodeOffset, entry.getValue());
            //System.out.println("newNodeOffset: " + newNodeOffset);
            //System.out.println("otherIndex: " + otherIndex);
            //System.out.println("newIndex: " + (otherIndex + newNodeOffset));
        }
        //System.out.println(newNodeOffset);
        //System.out.println(this.graph);
        //System.out.println(this.indexToStationNameMap);
        //System.out.println("Alpis:" + alpis);

        HashMap<String, Integer> alpsi = new HashMap<>(); //TODO:testing

        //2. merge stationName-Index (ok)
        for (Map.Entry<Integer,String> entry: indexToStationNameMapLine.entrySet()) {
            String stationName = entry.getValue();

            if (stationNameToIndexMap.get(stationName) == null) {
                alpsi.put(stationName, newNodeOffset + entry.getKey());
                this.stationNameToIndexMap.put(stationName, entry.getKey() + newNodeOffset);
            }
        }

        //System.out.println(this.stationNameToIndexMap);
        //System.out.println("Alpsi:" + alpsi);

        //TODO: CONTINUE CONTINUE CONTINUE
        //3. merge graph
        //get all nodes (ok?)
        GraphHandler graphLine = newMetroLine.getGraphHandler();
        ArrayList<Integer> graphNodesLine = new ArrayList<>(graphLine.getNodes());
        ArrayList<Integer> graphNodesSystem = new ArrayList<>(this.graph.getNodes());
        //System.out.println(graphNodesLine);
        //System.out.println(graphNodesSystem);

        //System.out.println("stationNameToIndexMapLine: " + stationNameToIndexMapLine); //TODO
        //create interchangeReferences map
        for (Integer otherNd: graphNodesLine) {
            String stationName = indexToStationNameMapLine.get(otherNd);
            //System.out.println(otherNd.INDEX + " " + stationName); //TODO
            Station newStation = stationNameToStationMapLine.get(stationName);
            if (newStation instanceof Interchange) {
                HashSet<Integer> interchangeReferences = interchangeGraphReferences.get((newStation.getName()));
                if (interchangeReferences == null) {
                    interchangeReferences = new HashSet<>();
                    interchangeGraphReferences.put(newStation.getName(), interchangeReferences);
                }
                interchangeReferences.add(otherNd + newNodeOffset);// + newNodeOffset);
                //gives resultant interchange node reference after graphs be merged
            }
        }

        //System.out.println(interchangeGraphReferences); //TODO

        for (Integer thisNd: graphNodesSystem) {
            String stationName = indexToStationNameMap.get(thisNd);
            //System.out.println(thisNd.INDEX + " " + stationName); //TODO
            Station newStation = stationNameToStationMap.get(stationName);
            if (newStation instanceof Interchange) {
                HashSet<Integer> interchangeReferences = interchangeGraphReferences.get((newStation.getName()));
                if (interchangeReferences == null) {
                    interchangeReferences = new HashSet<>();
                    interchangeGraphReferences.put(newStation.getName(), interchangeReferences);
                }
                if (stationName.equalsIgnoreCase("Botanic Gardens")) {
                    System.out.println("Botanic gardens in this current system index: " + thisNd);
                }
                interchangeReferences.add(thisNd);// + newNodeOffset);?
                //gives resultant interchange node reference after graphs be merged
                // (equal to before, since this.graph remain unchanged)
            }
        }

        //System.out.println(interchangeGraphReferences); //TODO

        //officially merge graph
        this.graph.mergeGraphHandler(newMetroLine.getGraphHandler(), newNodeOffset);

        /*
        //web interchange transfers
        ArrayList<Pair<String, Pair<Integer,Integer>>> apspii = new ArrayList<>(); //TODO

        for (Map.Entry<String,HashSet<Integer>> entry: interchangeGraphReferences.entrySet()) {
            ArrayList<Integer> curInterchangeNodes = new ArrayList<>(entry.getValue());
            //generate complete graph to represent free exchange within interchange
            HashSet<Pair<Integer, Integer>> newHiddenEdges = new HashSet<>();
            for (int i = 0; i < curInterchangeNodes.size(); i++) {
                for (int j = i + 1; j < curInterchangeNodes.size(); j++) {
                    //hiddenedge is within station: 3min transfer
                    int nodeIndexI = curInterchangeNodes.get(i), nodeIndexJ = curInterchangeNodes.get(j);
                    if (nodeIndexI != nodeIndexJ) {
                        newHiddenEdges.add(new Pair<>(nodeIndexI, nodeIndexJ));

                        //testing
                        apspii.add(new Pair<>(entry.getKey(), new Pair<>(nodeIndexI, nodeIndexJ)));
                        //System.out.println(entry.getKey() + " " + i + " " + j);
                        //System.out.println(indexToStationNameMapLine.get(i));
                        //Interchange s = (Interchange) stationNameToStationMapLine.get(indexToStationNameMapLine.get(i));
                        //TODO System.out.println(entry.getKey() + " " + newMetroLine.findCurrentInterchangeID(s) + " " + i + " " + j + " " + EXCHANGE_TIME);
                    }
                }
            }
            //System.out.println(newHiddenEdges); //TODO

            for (Pair<Integer, Integer> pii: newHiddenEdges) {
                graph.addHiddenEdge(pii.getKey(), pii.getValue(), EXCHANGE_TIME);
            }
            //TODO System.out.println(curInterchangeNodes);
        }

        System.out.println("stationNameToIndexMap: " + stationNameToIndexMap); //TODO
        System.out.println("stationNameToIndexMapLine: " + stationNameToIndexMapLine); //TODO
        //System.out.println(graph);
        //System.out.println();
        System.out.println(interchangeGraphReferences); //TODO
        System.out.println("aspspii: " + apspii);
        //System.out.println(graph);
        System.out.println(); System.out.println(); //TODO TODO

        */

        //4. merge id-StationName (ok)
        this.idToStationNameMap.putAll(idToStationNameMapLine);

        //5. merge stations & stationname-station (in addStation)
        for (Map.Entry<String,Station> entry: stationNameToStationMapLine.entrySet()) {
            this.addStation(entry.getValue());
        }

        //TODO
        //System.out.println(interchangeGraphReferences);
        //System.out.println(indexToStationNameMap);
        //System.out.println(stationNameToIndexMap);

        //System.out.println(graphLine);
        //System.out.println();
        //System.out.println(graph);

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
    public Pair<ArrayList<Station>, Integer> genShortestPath(String UStr, String VStr) {
        UStr = UStr.trim(); VStr = VStr.trim();
        Station u = null, v = null;
        Pattern containDigit = Pattern.compile("[0-9]+");
        Matcher matcherU = containDigit.matcher(UStr);
        Matcher matcherV = containDigit.matcher(VStr);

        if (!matcherU.find() && !matcherV.find()) { //Station names do not contain digits
            u = stationNameToStationMap.get(UStr);
            v = stationNameToStationMap.get(VStr);
            //System.out.println(stationNameToIndexMap);
            //System.out.println(indexToStationNameMap);
        }
        else { //If digits found in both, input is for station IDs; try to find them.
            String uName = idToStationNameMap.get(UStr);
            String vName = idToStationNameMap.get(VStr);
            u = stationNameToStationMap.get(uName);
            v = stationNameToStationMap.get(vName);
        } //null ret by get() if not found

        ArrayList<Integer> shortestPathGraphNodes;
        int time = 0;
        if (u != null && v != null) {
            int uIndex = stationNameToIndexMap.get(u.getName());
            int vIndex = stationNameToIndexMap.get(v.getName());
            shortestPathGraphNodes = graph.dijkstra(uIndex, vIndex);
            time = graph.getShortestDistance(vIndex); //valid shortly after running the algo
        }
        else throw new IllegalArgumentException("Invalid station names:" + ((u == null) ? UStr : "") + " " + ((v == null) ? VStr : ""));

        //TODO: verify if this does not mess up the generated path
        ArrayList<Station> shortestPath = new ArrayList<>();
        String prevStnName = "";
        for (Integer stationGraphNode : shortestPathGraphNodes) {
            String stationName = indexToStationNameMap.get(stationGraphNode);
            if (stationName.equalsIgnoreCase(prevStnName)) continue;
            shortestPath.add(stationNameToStationMap.get(stationName));
            prevStnName = stationName;
        }
        return new Pair<>(shortestPath, time);
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

    public Group setHighlighted(boolean highlighted) {
       return currentSystem;
    }
     */