package com.example.georgebra.Model;

import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;
import java.util.PriorityQueue;

import java.util.*;

public class MetroSystem implements Drawable{
    //private ArrayList<Pair<Station, ArrayList<Station>>> adjList; //keeping the pair with station just in case
    private String cityName;
    private ArrayList<Station> stationList = new ArrayList<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<MetroLine> metroLineList = new ArrayList<>();
    private HashMap<Station, Integer> stationIndexMap = new HashMap<>();
    private HashMap<Integer, Station> indexStationMap = new HashMap<>();
    private ArrayList<Pair<Station, Integer>> adjListSystem[];
    Group currentSystem = new Group();

    public MetroSystem(String cityName) {
        this.cityName = cityName;

        //initialisation
        this.adjListSystem = new ArrayList[500];
        for (int i = 0; i < adjListSystem.length; i++) {
            adjListSystem[i] = new ArrayList<>();
        }
    }

    public Group draw() {
        //Clean UI
        currentSystem.getChildren().clear();

        int stationCnt = stationIndexMap.size();
        HashSet<String> visitedU = new HashSet<>();
        for (int i = 0; i < stationCnt; i++) {
            visitedU.add(indexStationMap.get(i).getName());
            for (Pair<Station, Integer> entry: adjListSystem[i]) {
                //prevent creation of repeats
                if (visitedU.contains(entry.getKey().getName())) continue;

                //create nodes
                Station u = indexStationMap.get(i);
                Station v = entry.getKey();

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

    public Station addStation(Station station) {
        for (Station s : stationList) {
            if (s.getName().equals(station.getName())) {
                return null;
            }
        }

        if (station instanceof Interchange) {
            Interchange existing = Interchange.checkExistenceAndMergeLines((Interchange) station);
            if (existing != null) {
                station = existing;
            } else {
                interchanges.add((Interchange) station);
            }
        }

        boolean containStation = false;
        for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
            Station s = entry.getKey();
            if (s.getName().equals(station.getName())) {
                containStation = true;
                break;
            }
        }
        if (!containStation) {
            stationIndexMap.put(station, stationList.size());
            indexStationMap.put(stationList.size(), station);
            stationList.add(station);
        }

        if (station instanceof Interchange) return station;
        return null;
    }

    public void addEdge(Station u, Station v, int weight) {
        boolean containU = false;
        boolean containV = false;
        for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
            Station s = entry.getKey();
            if (u.getName().equals(s.getName())) {
                containU = true;
                break;
            }
        }
        for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
            Station s = entry.getKey();
            if (v.getName().equals(s.getName())) {
                containV = true;
                break;
            }
        }

        Station newU = u, newV = v;
        if (!containU) {
            Station tempU = addStation(u);
            if (tempU != null) newU = tempU;
        }
        if (!containV) {
            Station tempV = addStation(v);
            if (tempV != null) newV = tempV;
        }

        if (u instanceof Interchange) {
            for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
                Station s = entry.getKey();
                if (u.getName().equals(s.getName())) newU = s;
            }
        }
        if (v instanceof Interchange) {
            for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
                Station s = entry.getKey();
                if (v.getName().equals(s.getName())) newV = s;
            }
        }
        adjListSystem[stationIndexMap.get(newU)].add(new Pair<Station, Integer>(newV, weight));
        adjListSystem[stationIndexMap.get(newV)].add(new Pair<Station, Integer>(newU, weight));
    }

    public void addLine(MetroLine newMetroLine) {
        metroLineList.add(newMetroLine);

        ArrayList<Pair<Station,Integer>> adjListLine[] = newMetroLine.getAdjListLine();
        HashMap<Integer,Station> indexStationMap = newMetroLine.getIndexStationMap();
        HashSet<Station> recorded = new HashSet<>();
        for (int i = 0; i < indexStationMap.size(); i++) {
            Station u = indexStationMap.get(i);
            //handle interchanges u: if there is an interchange,
            //the interchange will surely be part of an existing line. if not, that line is created too.
            if (u instanceof Interchange) {
                //find the instance of the same interchange currently in system!
                Interchange actualU = null;
                for (Station s: this.stationList) {
                    if (s instanceof Interchange && s.getName().equals(u.getName())) {
                        actualU = (Interchange) s;
                    }
                }

                if (actualU == null) actualU = (Interchange) u;
                recorded.add(actualU);

                //for that interchange object, create edges between it and the vs of this one!!
                for (Pair<Station, Integer> edge: adjListLine[i]) {
                    Station v = edge.getKey();
                    int time = edge.getValue();

                    boolean recordedContains = false;
                    for (Station s: recorded) {
                        if (s.getName().equals(v.getName())) {
                            recordedContains = true;
                            break;
                        }
                    }
                    if (!recordedContains) addEdge(actualU, v, time);
                }
            }
            else {
                //copy all non-interchange stations
                //this.addStation(u);

                //copy all non-interchange edges
                recorded.add(u);
                for (Pair<Station, Integer> edge: adjListLine[i]) {
                    Station v = edge.getKey();
                    int time = edge.getValue();

                    boolean recordedContains = false;
                    for (Station s: recorded) {
                        if (s.getName().equals(v.getName())) {
                            recordedContains = true;
                            break;
                        }
                    }

                    if (!recordedContains) addEdge(u, v, time);
                }
            }
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

    //init data structures
    boolean visited[] = new boolean[1005];
    PriorityQueue<Station> unmarked = new PriorityQueue<>(
            Comparator.comparingInt(Station::getEstimate)
    );
    HashMap<Station, Station> prevMap = new HashMap<>();

    //initialisation
    /*
    public ArrayList<Station> dijkstra(String Uname, String Vname) {
        boolean Uclear = false;
        boolean Vclear = false;
        for (Station s: stationList) {
            if (s.getName().equals(Uname)) Uclear = true;
            if (s.getName().equals(Vname)) Vclear = true;
        }

        return dijkstra(u, v);
    }
     */

    public ArrayList<Station> dijkstra(Station u, Station v) {
        //clear prev data
        prevMap.clear();

        Arrays.fill(visited, false);  //none are visited
        unmarked.add(u);    //search begins at u
        u.setEstimate(0);           //starting node has time 0

        for (Station s: stationList) {
            if (!s.getName().equals(u.getName())) {  //for all stations except the starting station,
                s.setEstimate(Integer.MAX_VALUE); //set estimates
            }
        }

        //TODO BEGIN TEST BEGIN TEST BEGIN TEST BEGIN TEST BEGIN TEST BEGIN TEST BEGIN TEST
        /*
        for (Map.Entry<Integer, Station> entry: indexStationMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();
        */
        /*
        for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();
        */
        //System.out.println(v);
        //System.out.println(stationIndexMap.get(v));
        /*
        for (int i = 0; i < stationList.size(); i++) {
            for (Pair<Station, Integer> edge: adjListSystem[i]) {
                System.out.println(i + " " + indexStationMap.get(i) + " -> " + edge.getKey() + " " + edge.getValue());
            }
        }
         */
        //TODO END TEST END TEST END TEST END TEST END TEST END TEST END TEST END TEST

        //pseudo bfs
        while (!unmarked.isEmpty()){
            //System.out.println(Arrays.toString(unmarked.toArray()));
            Station cur = unmarked.poll();

            System.out.println(cur);
            System.out.println(stationIndexMap.get(cur));
            System.out.println(stationIndexMap);

            if (visited[stationIndexMap.get(cur)]) continue;
            visited[stationIndexMap.get(cur)] = true;
            if (cur.getName().equals(v.getName())) break;

            //update estimate of all adj nodes
            for (Pair<Station, Integer> edge: adjListSystem[stationIndexMap.get(cur)]) { //visit them
                Station nxt = edge.getKey();
                int weight = edge.getValue();

                int newTime = cur.getEstimate() + weight + ((nxt instanceof Interchange) ? 5 : 0);
                if (newTime < nxt.getEstimate()) {
                    //System.out.println(cur + " -> " + nxt + ": " + newTime + " " + nxt.getEstimate());
                    nxt.setEstimate(newTime);
                    prevMap.put(nxt, cur);
                    unmarked.add(nxt);
                }
            }
        }

        //reconstruction from end node
        ArrayList<Station> path = new ArrayList<>();
        if (v.getEstimate() == Integer.MAX_VALUE) return path; //no path

        Station cur = v;
        while (cur != null) {
            path.add(cur);
            cur = prevMap.get(cur);
        }
        Collections.reverse(path);
        return path;
    }

    //utils
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