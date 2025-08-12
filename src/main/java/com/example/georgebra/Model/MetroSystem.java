package com.example.georgebra.Model;

import com.example.georgebra.Model.LineTypes.Line;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.scene.Group;
import javafx.util.Pair;
import java.util.PriorityQueue;

import java.util.*;

public class MetroSystem implements Drawable{
    //private ArrayList<Pair<Station, ArrayList<Station>>> adjList; //keeping the pair with station just in case
    private String cityName;
    private ArrayList<Station> stationList = new ArrayList<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<Line> lineList = new ArrayList<>();
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
        currentSystem.getChildren().clear();

        //TODO: implement drawing a system with a full DFS
        //currentSystem.getChildren().add(stn);

        return currentSystem;
    }

    //TODO
    public Group setHighlighted() {
       return currentSystem;
    }

    private Station addStation(Station station) {
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

    public void addLine(Line newLine) {
        lineList.add(newLine);

        ArrayList<Pair<Station,Integer>> adjListLine[] = newLine.getAdjListLine();
        HashMap<Integer,Station> indexStationMap = newLine.getIndexStationMap();
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

    public ArrayList<Line> getLineList() {
        return new ArrayList<>(this.lineList);
    }

    //init data structures
    boolean visited[] = new boolean[1005];
    PriorityQueue<Station> unmarked = new PriorityQueue<>();
    HashMap<Station, Station> prevMap = new HashMap<>();

    //initialisation
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
        ///*
        for (Map.Entry<Station, Integer> entry: stationIndexMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();
        //*/
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


            if (visited[stationIndexMap.get(cur)]) continue;
            visited[stationIndexMap.get(cur)] = true;
            if (cur.getName().equals(v.getName())) break;

            //update estimate of all adj nodes
            for (Pair<Station, Integer> edge: adjListSystem[stationIndexMap.get(cur)]) { //visit them
                Station nxt = edge.getKey();
                int weight = edge.getValue();

                int newTime = cur.getEstimate() + weight;
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
        String systemString = "=== " + this.cityName + " Metro ===\n";
        for (Station s: stationList) {
            systemString += (s + "\n");
        }

        for (Line line: lineList) {
            systemString += (line.toString());
        }

        for (Interchange i: interchanges) {
            systemString += (i.toString() + "\n");
        }

        return systemString;
    }

    private Interchange findInterchangeDuplicate(Interchange other) {
        for (Interchange i: interchanges) {
            if (i.getStationID().equals(other.getStationID())) {
                return i;
            }
        }
        return null;
    }

    //useless crap
    public void removeEdge(Station u, Station v) {

    }
    public void removeStation(Station x) {

    }
}