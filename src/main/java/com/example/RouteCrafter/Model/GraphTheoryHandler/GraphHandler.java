package com.example.RouteCrafter.Model.GraphTheoryHandler;

import javafx.util.Pair;

import java.util.*;

public class GraphHandler {
    private final int MAXN = 505;
    private HashMap<Integer, Integer> adjList[];
    private HashSet<Integer> graphNodes = new HashSet<>();
    //private HashMap<Integer, GraphNode> indexToNode = new HashMap<>();
    private int duplicateInterchangesCnt; //specially created class field to assist with interchange; TODO: find a way to remove/rename
    public GraphHandler() {
        //initialisation
        this.adjList = new HashMap[MAXN];
        for (int i = 0; i < adjList.length; i++) {
            adjList[i] = new HashMap<>();
        }

        duplicateInterchangesCnt = 0;
    }

    public void addNode(int node) {
        /*
        if (graphNodes.size() - duplicateInterchangesCnt + 1 > MAXN || node >= MAXN){
            throw new IllegalStateException("Graph capacity exceeded (no existing metroSystem with more than 500 stations)");
        }
         */
        graphNodes.add(node);
    }

    public void addEdge(int u, int v, int weight) {
        if (u >= MAXN || v >= MAXN || u < 0 || v < 0) return;
        boolean uAlreadyExistsBeforeAddingNode = graphNodes.contains(u);
        boolean vAlreadyExistsBeforeAddingNode = graphNodes.contains(v);
        addNode(u); addNode(v);
        adjList[u].put(v, weight);
        adjList[v].put(u, weight);
        if (uAlreadyExistsBeforeAddingNode) duplicateInterchangesCnt++;
        if (vAlreadyExistsBeforeAddingNode) duplicateInterchangesCnt++;
    }

    public HashMap<Integer,Integer>[] getAdjList() {
        return this.adjList;
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
        HashMap<Integer,Integer> otherAdjList[] = other.getAdjList();
        HashSet<Integer> otherGraphNodes = other.getNodes();

        for (Integer node : otherGraphNodes) {
            this.addNode(newNodeOffset + node);
            for (Map.Entry<Integer,Integer> entry: otherAdjList[node].entrySet()) {
                int toNode = entry.getKey();
                int weight = entry.getValue();

                this.addEdge(newNodeOffset + node, newNodeOffset + toNode, weight);
            }
        }
    }

    private int[] dist;
    public ArrayList<Integer> dijkstra(int u, int v) {
        if (!graphNodes.contains(u)) throw new IllegalArgumentException("Node u (" + u + ") doesn't exist");
        if (!graphNodes.contains(v)) throw new IllegalArgumentException("Node v (" + v + ") doesn't exist");

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

            if (currentDist != dist[currentNode]) continue; //skip outdated node

            visited[currentNode] = true;
            if (currentNode == v) break;

            for (Map.Entry<Integer,Integer> edge : adjList[currentNode].entrySet()) {
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

        //System.out.println(path);
        return path;
    }

    public int getShortestDistance(int v) {
        if (v < 0 || v >= MAXN || dist == null) return Integer.MAX_VALUE;
        return dist[v];
    }

    public String toString() {
        String finalString = "";
        for (int i = 0; i < graphNodes.size(); i++) {
            for (Map.Entry<Integer, Integer> pii: adjList[i].entrySet()) {
                finalString += (i + " " + pii.getKey() + " " + pii.getValue() + "\n");
            }
        }
        return finalString;
    }
}