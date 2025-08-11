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
    private int stationCnt;
    private String cityName;
    private ArrayList<Station> stationList = new ArrayList<>();
    private HashSet<Interchange> interchanges = new HashSet<>();
    private ArrayList<Line> lineList = new ArrayList<>();
    private HashMap<Station, Integer> stationIndexMap = new HashMap<>();
    private ArrayList<Pair<Station, Integer>> adjListSystem[];
    Group currentSystem = new Group();

    public MetroSystem(String cityName) {
        this.stationCnt = 0;
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

    public void addStation(Station x) throws IllegalArgumentException{
        if (x instanceof Interchange) { //if it is an interchange
            Interchange existing;
            existing = Interchange.checkExistenceAndMergeLines((Interchange) x);

            if (existing != null) x = existing;
            else interchanges.add((Interchange) x);
        }

        //check if the station exists
        for (Station s: stationList) {
            if (x.getName().equals(s.getName())) {
                throw new IllegalArgumentException("Station: " + x.getName() + " already exists");
            }
        }
        //if not interchange
        stationList.add(x);
        stationIndexMap.put(x, stationCnt);
        stationCnt++;
    }

    public void addEdge(Station u, Station v, int weight) {
        if (u == null || v == null) return;
        try {
            addStation(u);
            addStation(v);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.toString());
        }
        adjListSystem[stationIndexMap.get(u)].add(new Pair<Station, Integer>(v, weight));
        //adjListSystem[stationIndexMap.get(v)].add(u);
    }

    public void addLine(Line newLine) {
        lineList.add(newLine);

        ArrayList<Pair<Station,Integer>>[] adjListLine = newLine.getAdjListLine();
        HashMap<Integer,Station> indexStationMap = Line.reverseStationIndexMap(newLine);
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

                if (actualU == null) {
                    this.addStation(u);
                    actualU = (Interchange) u;
                }

                recorded.add(actualU);

                //for that interchange object, create edges between it and the vs of this one!!
                for (Pair<Station, Integer> x: adjListLine[i]) {
                    Station v = x.getKey();
                    int time = x.getValue();
                    if (!recorded.contains(v) && !(v instanceof Interchange)) {
                        addEdge(actualU, v,time);
                    }
                }
            }
            else {
                //copy all non-interchange stations
                this.addStation(u);

                //copy all non-interchange edges
                recorded.add(u);
                for (Pair<Station, Integer> x: adjListLine[i]) {
                    Station v = x.getKey();
                    int time = x.getValue();
                    if (!recorded.contains(v) && !(v instanceof Interchange)) {
                        addEdge(u, v, time);
                    }
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
    ArrayList<Station> shortestPath = new ArrayList<>();    //recording the answer
    HashMap<Station, Station> prevMap = new HashMap<>();
    boolean visited[] = new boolean[1005];
    HashMap<Station, Integer> stationToIndexMap = this.stationIndexMap; //mapping indexes to stations
    HashMap<Integer, Station> indexToStationMap = new HashMap<>();      //& vice versa
    PriorityQueue<Station> unmarked = new PriorityQueue<>();

    //initialisation
    public ArrayList<Station> dijkstra(Station u, Station v) {
        //clear prev data
        shortestPath.clear();
        prevMap.clear();

        //set reversed values for second map
        for (HashMap.Entry<Station, Integer> e: stationIndexMap.entrySet())
            indexToStationMap.put(e.getValue(), e.getKey());

        Arrays.fill(visited, false);  //none are visited
        visited[stationToIndexMap.get(u)] = true; //except starting node
        u.setEstimate(0);           //starting node has time 0
        shortestPath.add(u);  //shortest path begins with starting node

        for (Station s: stationList) {
            if (!s.getName().equals(u.getName())) {  //for all stations except the starting station,
                s.setEstimate(Integer.MAX_VALUE); //set estimates
                unmarked.add(s);  //not yet considered
            }
        }

        runDijkstra(u, v);
        return shortestPath;
    }

    private void runDijkstra(Station u, Station v) {
        //update estimate of all adj nodes
        for (Pair<Station, Integer> edge: adjListSystem[stationToIndexMap.get(u)]) {//visit them
            //and add edge weight to current node
            edge.getKey().setEstimate(u.getEstimate() + edge.getValue());
        }

        //choose next node
        while (!unmarked.isEmpty()){
            Station nxt = unmarked.peek();
            unmarked.remove();
            if (!visited[stationToIndexMap.get(nxt)]) {
                visited[stationToIndexMap.get(nxt)] = true;
                runDijkstra(nxt, v);
            }
        }
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