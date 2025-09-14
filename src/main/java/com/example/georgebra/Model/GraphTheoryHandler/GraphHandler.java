package com.example.georgebra.Model.GraphTheoryHandler;

import javafx.util.Pair;

import java.util.*;

public class GraphHandler {
    private final int MAXN = 505;
    private HashSet<Pair<Integer, Integer>> adjList[];
    private HashSet<Integer> graphNodes = new HashSet<>();
    //private HashMap<Integer, GraphNode> indexToNode = new HashMap<>();
    private int duplicateInterchangesCnt; //specially created class field to assist with interchange; TODO: find a way to remove/rename
    public GraphHandler() {
        //initialisation
        this.adjList = new HashSet[MAXN];
        for (int i = 0; i < adjList.length; i++) {
            adjList[i] = new HashSet<>();
        }

        duplicateInterchangesCnt = 0;
    }

    public void addNode(int nodeNo) {
        if (graphNodes.size() - duplicateInterchangesCnt + 1 > MAXN){
            throw new IllegalStateException("Graph capacity exceeded (no existing metroSystem with more than 500 stations)");
        }
        graphNodes.add(nodeNo);
    }

    public void addEdge(int u, int v, int weight) {
        if (u >= MAXN || v >= MAXN || u < 0 || v < 0) return;
        boolean uIsNew = !graphNodes.contains(u);
        boolean vIsNew = !graphNodes.contains(v);

        addNode(u); addNode(v);
        adjList[u].add(new Pair<>(v, weight));
        adjList[v].add(new Pair<>(u, weight));
        if (uIsNew) duplicateInterchangesCnt++;
        if (vIsNew) duplicateInterchangesCnt++;
    }

    //for transfer within interchanges
    // TODO: deprecated; no need for labelling hidden edges; just strike out repeat stations in final path of stations (in msys)
    /*
    public void addHiddenEdge(int u, int v, int weight) {
    }
     */

    public ArrayList<Pair<Integer, Integer>>[] getAdjList() {
        ArrayList<Pair<Integer, Integer>>[] deepCopyAdjList;
        deepCopyAdjList = new ArrayList[MAXN];
        for (int i = 0; i < adjList.length; i++) {
            deepCopyAdjList[i] = new ArrayList<>(this.adjList[i]);
        }
        return deepCopyAdjList;
    }

    public int getNodeCnt() {
        return this.graphNodes.size();
    }
    public int getDuplicateInterchangesCnt() {
        return this.duplicateInterchangesCnt;
    }

    public HashSet<Integer> getNodes() {
        //System.out.println("HAHA" + graphNodes.size());
        return new HashSet<>(this.graphNodes);
    }

    public void mergeGraphHandler(GraphHandler other) {
        this.mergeGraphHandler(other, this.graphNodes.size());
    }

    public void mergeGraphHandler(GraphHandler other, int newNodeOffset) {
        ArrayList<Pair<Integer, Integer>> otherAdjList[] = other.getAdjList();
        //HashSet<GraphNode> otherGraphNodes = other.getNodes();
        HashSet<Integer> otherGraphNodes = other.getNodes();


        //for (GraphNode otherNode : other.getNodes()) {
        for (Integer otherNode : other.getNodes()) {
            int newIndex = newNodeOffset + otherNode;
            this.addNode(newIndex);
        }

        //for (GraphNode fromGraphNode : otherGraphNodes) {
        for (Integer fromNode : otherGraphNodes) {
            for (Pair<Integer,Integer> entry: otherAdjList[fromNode]) {
                int toNode = entry.getKey();
                int weight = entry.getValue();

                this.addEdge(newNodeOffset + fromNode, newNodeOffset + toNode, weight);
            }
        }
    }


    private int[] dist;
    public ArrayList<Integer> dijkstra(int u, int v) {
        if (graphNodes.contains(u) || graphNodes.contains(v)) throw new IllegalArgumentException("Nodes u and v don't exist");

        //prevmap for path reconstruction
        HashMap<Integer, Integer> prevMap = new HashMap<>();
        prevMap.put(u, null);

        boolean[] visited = new boolean[MAXN];
        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
        pq.add(new Pair<>(0, u));
        dist = new int[MAXN];
        Arrays.fill(dist, Integer.MAX_VALUE); dist[u] = 0;

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> current = pq.poll();
            int currentDist = current.getKey();
            int currentNode = current.getValue();

            // Skip if this is an outdated entry (we found a better path already)
            if (currentDist != dist[currentNode]) {
                continue;
            }

            visited[currentNode] = true;

            // Early termination if we reached the target
            if (currentNode == v) break;

            for (Pair<Integer, Integer> edge : adjList[currentNode]) {
                int neighbor = edge.getKey();
                int weight = edge.getValue();

                int newDist = dist[currentNode] + weight;

                if (newDist < dist[neighbor]) {
                    dist[neighbor] = newDist;
                    prevMap.put(neighbor, currentNode);

                    pq.add(new Pair<>(newDist, neighbor));
                }
            }
        }

        //reconstruction from end node
        ArrayList<Integer> path = new ArrayList<>();

        Integer cur = v;
        if (dist[v] == Integer.MAX_VALUE) return path;

        while (cur != null) {
            path.add(cur);
            cur = prevMap.get(cur);
        }

        Collections.reverse(path);
        return path;
    }

    public int getShortestDistance(int v) {
        if (v < 0 || v >= MAXN || dist == null) return Integer.MAX_VALUE;
        return dist[v];
    }

    public String toString() {
        String finalString = "";
        for (int i = 0; i < graphNodes.size(); i++) {
            for (Pair<Integer, Integer> pii: adjList[i]) {
                finalString += (i + " " + pii.getKey() + " " + pii.getValue() + "\n");
            }
        }
        return finalString;
    }
}