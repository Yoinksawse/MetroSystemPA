package com.example.RouteCrafter.Model.TesterClasses;

import com.example.RouteCrafter.Model.GraphTheoryHandler.GraphHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphTester {

    public static void main(String[] args) {
        System.out.println("=== GraphHandler Dijkstra Algorithm Tests ===\n");

        // Test 1: Simple Linear Graph
        testLinearGraph();

        // Test 2: Complex Network with Multiple Paths
        testComplexNetwork();

        // Test 3: Large Scale Graph
        testLargeScaleGraph();

        // Test 4: Disconnected Graph
        testDisconnectedGraph();

        // Test 5: Single GraphNode
        testSingleNode();
    }

    private static void testLinearGraph() {
        System.out.println("Test 1: Simple Linear Graph");
        GraphHandler graph = new GraphHandler();

        // Create a linear path: 0-1-2-3-4 with equal weights
        graph.addEdge(0, 1, 10);
        graph.addEdge(1, 2, 10);
        graph.addEdge(2, 3, 10);
        graph.addEdge(3, 4, 10);

        ArrayList<Integer> path = graph.dijkstra(0, 4);
        int distance = graph.getShortestDistance(4);

        System.out.println("Path from 0 to 4: " + path);
        System.out.println("Expected path: [0, 1, 2, 3, 4]");
        System.out.println("Distance: " + distance + " (Expected: 40)");
        System.out.println("Test passed: " + path.equals(Arrays.asList(0, 1, 2, 3, 4)) + "\n");
    }

    private static void testComplexNetwork() {
        System.out.println("Test 2: Complex Network with Multiple Paths");
        GraphHandler graph = new GraphHandler();

        /* Complex graph structure:
               0
              /|\
             1 2 3
            / \|/ \
           4---5---6
            \     /
             \   /
               7
        */
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(0, 3, 8);
        graph.addEdge(1, 4, 2);
        graph.addEdge(1, 5, 4);
        graph.addEdge(2, 5, 1);
        graph.addEdge(3, 5, 2);
        graph.addEdge(3, 6, 3);
        graph.addEdge(4, 5, 6);
        graph.addEdge(4, 7, 7);
        graph.addEdge(5, 6, 3);
        graph.addEdge(6, 7, 4);

        // Test multiple routes
        ArrayList<Integer> path1 = graph.dijkstra(0, 7);
        int dist1 = graph.getShortestDistance(7);
        System.out.println("Path from 0 to 7: " + path1);
        System.out.println("Expected: [0, 2, 5, 6, 7] (Distance: 11)");
        System.out.println("Actual distance: " + dist1);

        ArrayList<Integer> path2 = graph.dijkstra(4, 6);
        int dist2 = graph.getShortestDistance(6);
        System.out.println("Path from 4 to 6: " + path2);
        System.out.println("Expected: [4, 5, 6] (Distance: 10)");
        System.out.println("Test passed: " + (dist1 == 11 && dist2 == 9) + "\n");
    }

    private static void testLargeScaleGraph() {
        System.out.println("Test 3: Large Scale Graph (20 nodes)");
        GraphHandler graph = new GraphHandler();

        // Create a grid-like structure
        /*
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int node = i * 5 + j;
                if (j < 4) graph.addEdge(node, node + 1, 2); // horizontal
                if (i < 3) graph.addEdge(node, node + 5, 3); // vertical
            }
        }

         */
        // Row 0
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 5, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(1, 6, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(2, 7, 3);
        graph.addEdge(3, 4, 2);
        graph.addEdge(3, 8, 3);
        graph.addEdge(4, 9, 3);

// Row 1
        graph.addEdge(5, 6, 2);
        graph.addEdge(5, 10, 3);
        graph.addEdge(6, 7, 2);
        graph.addEdge(6, 11, 3);
        graph.addEdge(7, 8, 2);
        graph.addEdge(7, 12, 3);
        graph.addEdge(8, 9, 2);
        graph.addEdge(8, 13, 3);
        graph.addEdge(9, 14, 300);

// Row 2
        graph.addEdge(10, 11, 2);
        graph.addEdge(10, 15, 3);
        graph.addEdge(11, 12, 2);
        graph.addEdge(11, 16, 3);
        graph.addEdge(12, 13, 2);
        graph.addEdge(12, 17, 3);
        graph.addEdge(13, 14, 2);
        graph.addEdge(13, 18, 3);
        graph.addEdge(14, 19, 3);

// Row 3
        graph.addEdge(15, 16, 2);
        graph.addEdge(16, 17, 2);
        graph.addEdge(17, 18, 2);
        graph.addEdge(18, 19, 2);

        // Add some diagonal shortcuts
        graph.addEdge(0, 6, 4);
        graph.addEdge(1, 7, 4);
        graph.addEdge(3, 7, 4);
        graph.addEdge(4, 8, 4);

        ArrayList<Integer> path = graph.dijkstra(0, 19);
        int distance = graph.getShortestDistance(19);

        System.out.println("Path from 0 to 19: " + path);
        System.out.println("Distance: " + distance);
        System.out.println("Expected optimal path should use shortcuts\n");
    }

    private static void testDisconnectedGraph() {
        System.out.println("Test 4: Disconnected Graph");
        GraphHandler graph = new GraphHandler();

        // Two separate components
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        graph.addEdge(3, 4, 2); // Separate component

        ArrayList<Integer> path = graph.dijkstra(0, 4);
        int distance = graph.getShortestDistance(4);

        System.out.println("Path from 0 to 4: " + path);
        System.out.println("Distance: " + distance);
        System.out.println("Expected: [] (empty path), Distance: " + Integer.MAX_VALUE);
        System.out.println("Test passed: " + (path.isEmpty() && distance == Integer.MAX_VALUE) + "\n");
    }

    private static void testSingleNode() {
        System.out.println("Test 5: Single GraphNode");
        GraphHandler graph = new GraphHandler();

        graph.addNode(0); // Just add single node

        ArrayList<Integer> path = graph.dijkstra(0, 0);
        int distance = graph.getShortestDistance(0);

        System.out.println("Path from 0 to 0: " + path);
        System.out.println("Distance: " + distance);
        System.out.println("Expected: [0], Distance: 0");
        System.out.println("Test passed: " + (path.equals(Arrays.asList(0)) && distance == 0) + "\n");
    }

    private static void printPathDetails(ArrayList<Integer> path, int expectedDistance, int actualDistance) {
        System.out.println("Path: " + path);
        System.out.println("Expected distance: " + expectedDistance);
        System.out.println("Actual distance: " + actualDistance);
        System.out.println("Test passed: " + (expectedDistance == actualDistance));
    }
}