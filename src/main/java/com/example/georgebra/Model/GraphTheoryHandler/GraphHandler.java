package com.example.georgebra.Model.GraphTheoryHandler;

import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.util.Pair;

import java.util.*;

public class GraphHandler {
    private ArrayList<Pair<Integer, Integer>> adjList[];
    private HashSet<Node> nodes = new HashSet<>();


    public GraphHandler() {
        //initialisation
        this.adjList = new ArrayList[505];
        for (int i = 0; i < adjList.length; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    public void mergeGraphHandler(GraphHandler other) {
        int baseStationCnt = this.nodes.size();
        ArrayList<Pair<Integer, Integer>> otherAdjList[] = other.getAdjList();
        HashSet<Node> otherNodes = other.getNodes();

        this.nodes.addAll(otherNodes);

        for (int fromNode = 0; fromNode < otherNodes.size(); fromNode++) {
            for (Pair<Integer,Integer> entry: otherAdjList[fromNode]) {
                int toNode = entry.getKey();
                int weight = entry.getValue();
                this.addEdge(baseStationCnt + fromNode, baseStationCnt + toNode, weight);
            }
        }
    }

    public void addNode(int nodeNo) { //manual addition
        nodes.add(new Node(nodeNo));
    }

    public void addEdge(int u, int v, int weight) {
        addNode(u); addNode(v);
        adjList[u].add(new Pair<>(v, weight));
        adjList[v].add(new Pair<>(u, weight));
    }

    public ArrayList<Pair<Integer, Integer>>[] getAdjList() {
        ArrayList<Pair<Integer, Integer>>[] deepCopyAdjList;
        deepCopyAdjList = new ArrayList[505];
        for (int i = 0; i < adjList.length; i++) {
            adjList[i] = new ArrayList<>(this.adjList[i]);
        }
        return deepCopyAdjList;
    }

    public HashSet<Node> getNodes() {
        return new HashSet<>(this.nodes);
    }

    //init data structures
    boolean visited[] = new boolean[1005];
    ArrayList<Integer> estimates = new ArrayList<>();
    PriorityQueue<Node> unmarked = new PriorityQueue<>();
    HashMap<Integer, Integer> prevMap = new HashMap<>();


    public ArrayList<Integer> dijkstra(int u, int v) {
        //reconstruction from end node
        ArrayList<Integer> path = new ArrayList<>();
        if (v.getEstimate() == Integer.MAX_VALUE) return path; //no path

        Station cur = v;
        while (cur != null) {
            path.add(cur);
            cur = prevMap.get(cur);
        }
        Collections.reverse(path);
        return path;
    }

    /*
    public ArrayList<Integer> dijkstra(Station u, Station v) {
        //clear prev data
        prevMap.clear();

        Arrays.fill(visited, false);  //none are visited
        unmarked.add(u);    //search begins at u
        u.setEstimate(0);           //starting node has time 0

        for (Station s: stationList) {
            if (!s.getName().equalsIgnoreCase(u.getName())) {  //for all stations except the starting station,
                s.setEstimate(Integer.MAX_VALUE); //set estimates
            }
        }

        Station prev = null;
        //pseudo bfs
        while (!unmarked.isEmpty()) {
            //System.out.println(Arrays.toString(unmarked.toArray()));
            Station cur = unmarked.poll();

            System.out.print(cur.getName() + ", ");
            //System.out.println(stationNameToIndexMap.get(cur));
            //System.out.println(stationNameToIndexMap);

            if (visited[stationNameToIndexMap.get(cur.getName())]) continue;
            visited[stationNameToIndexMap.get(cur)] = true;
            if (cur.getName().equalsIgnoreCase(v.getName())) break;

            //update estimate of all adj nodes
            for (Pair<Integer, Integer> edge: adjList[stationNameToIndexMap.get(cur.getName())]) { //visit them
                int nxtIndex = edge.getKey();
                String nxtName = indexToStationNameMap.get(nxtIndex);
                Station nxt = stationNameToStationMap.get(nxtName);
                int weight = edge.getValue();
                int newTime = cur.getEstimate() + weight;

                //there is possible line change
                if (prev != null) {
                    //prev stn lines
                    HashSet<String> prevLineNames = new HashSet<>();
                    if (prev instanceof Interchange) {
                        ArrayList<Pair<String, String>> interchangeLinesInfo = ((Interchange) prev).getDifferentLinesInfo();
                        for (Pair<String, String> pss: interchangeLinesInfo) prevLineNames.add(pss.getKey().toLowerCase());
                    }
                    else prevLineNames.add(prev.getLineName().toLowerCase());

                    //nxt stn lines
                    HashSet<String> nxtLineNames = new HashSet<>();
                    if (nxt instanceof Interchange) {
                        ArrayList<Pair<String, String>> interchangeLinesInfo = ((Interchange) nxt).getDifferentLinesInfo();
                        for (Pair<String, String> pss: interchangeLinesInfo) nxtLineNames.add(pss.getKey().toLowerCase());
                    }
                    else nxtLineNames.add(nxt.getLineName().toLowerCase());

                    //prev and nxt encounter ABSOLUTE line change; estimate waiting time + transfer time = 5min haha
                    if (Collections.disjoint(prevLineNames, nxtLineNames)) newTime += 5;

                    //System.out.println(cur);
                    //System.out.println(prevLineNames);
                    //System.out.println(nxtLineNames);
                    //System.out.println();
                }

                if (newTime < nxt.getEstimate()) {
                    //System.out.println(cur + " -> " + nxt + ": " + newTime + " " + nxt.getEstimate());
                    nxt.setEstimate(newTime);
                    prevMap.put(nxt, cur);
                    unmarked.add(nxt);
                }
            }
            prev = cur;
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
     */

}
