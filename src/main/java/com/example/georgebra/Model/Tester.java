package com.example.georgebra.Model;

import com.example.georgebra.Model.*;
import com.example.georgebra.Model.LineTypes.*;
import com.example.georgebra.Model.StationTypes.*;
import javafx.util.Pair;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) {
        //testStationClasses();
        //testLineClasses();
        //testMetroSystem();
        //testEdgeCases();
        testDijkstraAlgorithm();
    }

    public static void testStationClasses() {
        System.out.println("=== Testing Station Classes ===");

        // Test SingleStation
        SingleStation jurongEast = new SingleStation(100, 100, "NS1", "Jurong East", "North South Line", 1);
        System.out.println("Created SingleStation: " + jurongEast);

        // Test Interchange creation
        ArrayList<Pair<String, String>> rafflesLines = new ArrayList<>();
        rafflesLines.add(new Pair<>("North South Line", "NS26"));
        rafflesLines.add(new Pair<>("East West Line", "EW14"));
        Interchange rafflesPlace = new Interchange(300, 200, rafflesLines, "Raffles Place", 26);
        System.out.println("Created Interchange: " + rafflesPlace);

        // Test Interchange merging
        ArrayList<Pair<String, String>> circleLineInfo = new ArrayList<>();
        circleLineInfo.add(new Pair<>("Circle Line", "CC1"));
        Interchange rafflesPlaceCC = new Interchange(300, 200, circleLineInfo, "Raffles Place", 26);
        Interchange merged = Interchange.checkExistenceAndMergeLines(rafflesPlaceCC);
        System.out.println("Merged Interchange lines: " + merged.getDifferentLinesInfo());

        // Test Station exceptions
        try {
            new SingleStation(0, 0, "", "Test", "Line", 0);
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    public static void testLineClasses() {
        System.out.println("\n=== Testing Line Classes ===");

        // Create MRT line
        MRTLine northSouthLine = new MRTLine("North South Line", "NS", 1);

        // Add stations
        SingleStation jurongEast = new SingleStation(100, 100, "NS1", "Jurong East", "North South Line", 1);
        SingleStation bukitBatok = new SingleStation(120, 120, "NS2", "Bukit Batok", "North South Line", 2);

        //northSouthLine.addStation(jurongEast);
        //northSouthLine.addStation(bukitBatok);
        northSouthLine.addEdge(jurongEast, bukitBatok, 5);

        System.out.println("North South Line contents:\n" + northSouthLine);

        // Test line exceptions
        try {
            new MRTLine("", "NS", 1);
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    public static void testMetroSystem() {
        System.out.println("\n=== Testing MetroSystem ===");

        MetroSystem singaporeMRT = new MetroSystem("Singapore");

        // Create North South Line
        MRTLine northSouthLine = new MRTLine("North South Line", "NS", 1);
        SingleStation jurongEast = new SingleStation(100, 100, "NS1", "Jurong East", "North South Line", 1);
        SingleStation bukitBatok = new SingleStation(120, 120, "NS2", "Bukit Batok", "North South Line", 2);
        northSouthLine.addEdge(jurongEast, bukitBatok, 3);

        // Create East West Line
        MRTLine eastWestLine = new MRTLine("East West Line", "EW", 2);
        SingleStation pasirRis = new SingleStation(200, 100, "EW1", "Pasir Ris", "East West Line", 1);

        // Create interchange
        ArrayList<Pair<String, String>> rafflesLines = new ArrayList<>();
        rafflesLines.add(new Pair<>("North South Line", "NS26"));
        rafflesLines.add(new Pair<>("East West Line", "EW14"));
        Interchange rafflesPlace = new Interchange(300, 200, rafflesLines, "Raffles Place", 26);

        eastWestLine.addEdge(pasirRis, rafflesPlace, 4);
        northSouthLine.addEdge(bukitBatok, rafflesPlace, 2);

        // Add lines to system
        singaporeMRT.addLine(northSouthLine);
        singaporeMRT.addLine(eastWestLine);

        System.out.println("Singapore MRT System:\n" + singaporeMRT);


    }

    public static void testEdgeCases() {
        System.out.println("\n=== Testing Edge Cases ===");

        MetroSystem testSystem = new MetroSystem("Test");

        // Test null stations
        try {
            testSystem.addEdge(null, null, 2);
            System.out.println("Null station test passed silently (handled in code)");
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }

        // Test invalid connections
        SingleStation s1 = new SingleStation(0, 0, "T1", "Test 1", "Test Line", 1);
        SingleStation s2 = new SingleStation(10, 10, "T2", "Test 2", "Test Line", 2);
        testSystem.addEdge(s1, s2, 3);
        System.out.println("Added test connection between " + s1 + " and " + s2);

        // Test toString() with empty system
        MetroSystem emptySystem = new MetroSystem("Empty");
        System.out.println("Empty system:\n" + emptySystem);
    }

    public static void testDijkstraAlgorithm() {
        System.out.println("\n=== Testing Dijkstra Algorithm ===");

        MetroSystem metro = new MetroSystem("Dijkstra Test System");

        // Create 3 lines
        MRTLine redLine = new MRTLine("Red Line", "RL", 1);
        MRTLine greenLine = new MRTLine("Green Line", "GL", 2);
        MRTLine blueLine = new MRTLine("Blue Line", "BL", 3);

        // FIRST CREATE INTERCHANGES (the hub stations)
        // Central Hub connects Red Line and Green Line
        ArrayList<Pair<String, String>> interchange1Lines = new ArrayList<>();
        interchange1Lines.add(new Pair<>("Red Line", "RL4"));
        interchange1Lines.add(new Pair<>("Green Line", "GL4"));
        Interchange centralHub = new Interchange(160, 260, interchange1Lines, "Central Hub", 4);

        // West Hub connects Green Line and Blue Line
        ArrayList<Pair<String, String>> interchange2Lines = new ArrayList<>();
        interchange2Lines.add(new Pair<>("Green Line", "GL2"));
        interchange2Lines.add(new Pair<>("Blue Line", "BL5"));
        Interchange westHub = new Interchange(320, 320, interchange2Lines, "West Hub", 5);

        // NOW CREATE SINGLE STATIONS, CONNECTING TO INTERCHANGES WHERE NEEDED
        // Red Line Stations
        SingleStation r1 = new SingleStation(100, 100, "RL1", "Red A", "Red Line", 1);
        SingleStation r2 = new SingleStation(120, 120, "RL2", "Red B", "Red Line", 2);
        SingleStation r3 = new SingleStation(140, 140, "RL3", "Red C", "Red Line", 3);
        // r4 is replaced by centralHub (RL4)
        SingleStation r5 = new SingleStation(180, 180, "RL5", "Red E", "Red Line", 5);
        SingleStation r6 = new SingleStation(200, 200, "RL6", "Red F", "Red Line", 6);
        SingleStation r7 = new SingleStation(220, 220, "RL7", "Red G", "Red Line", 7);

        // Green Line Stations
        SingleStation g1 = new SingleStation(100, 300, "GL1", "Green A", "Green Line", 1);
        // g2 is replaced by westHub (GL2)
        SingleStation g3 = new SingleStation(140, 340, "GL3", "Green C", "Green Line", 3);
        // g4 is replaced by centralHub (GL4)
        SingleStation g5 = new SingleStation(180, 380, "GL5", "Green E", "Green Line", 5);
        SingleStation g6 = new SingleStation(200, 400, "GL6", "Green F", "Green Line", 6);
        SingleStation g7 = new SingleStation(220, 420, "GL7", "Green G", "Green Line", 7);

        // Blue Line Stations
        SingleStation b1 = new SingleStation(300, 100, "BL1", "Blue A", "Blue Line", 1);
        SingleStation b2 = new SingleStation(320, 120, "BL2", "Blue B", "Blue Line", 2);
        SingleStation b3 = new SingleStation(340, 140, "BL3", "Blue C", "Blue Line", 3);
        SingleStation b4 = new SingleStation(360, 160, "BL4", "Blue D", "Blue Line", 4);
        // b5 is replaced by westHub (BL5)
        SingleStation b6 = new SingleStation(400, 200, "BL6", "Blue F", "Blue Line", 6);
        SingleStation b7 = new SingleStation(420, 220, "BL7", "Blue G", "Blue Line", 7);

        // BUILD THE LINES WITH PROPER CONNECTIONS
        // Red Line
        redLine.addEdge(r1, r2, 2);
        redLine.addEdge(r2, r3, 3);
        redLine.addEdge(r3, centralHub, 2);  // Connects to Central Hub (RL4)
        redLine.addEdge(centralHub, r5, 4);
        redLine.addEdge(r5, r6, 1);
        redLine.addEdge(r6, r7, 3);

        // Green Line
        greenLine.addEdge(g1, westHub, 2);    // Connects to West Hub (GL2)
        greenLine.addEdge(westHub, g3, 3);
        greenLine.addEdge(g3, centralHub, 2); // Connects to Central Hub (GL4)
        greenLine.addEdge(centralHub, g5, 3);
        greenLine.addEdge(g5, g6, 2);
        greenLine.addEdge(g6, g7, 4);

        // Blue Line
        blueLine.addEdge(b1, b2, 3);
        blueLine.addEdge(b2, b3, 2);
        blueLine.addEdge(b3, b4, 3);
        blueLine.addEdge(b4, westHub, 2);    // Connects to West Hub (BL5)
        blueLine.addEdge(westHub, b6, 4);
        blueLine.addEdge(b6, b7, 1);

        // Add lines to metro system
        metro.addLine(redLine);
        metro.addLine(greenLine);
        metro.addLine(blueLine);

        // Rest of your test code...
        System.out.println("Metro system with 21 stations and 3 lines created");

//        // Test 1: Simple path within one line
//        System.out.println("\nTest 1: Red A to Red D (same line)");
//        ArrayList<Station> path1 = metro.dijkstra(r1, centralHub);
//        printPath(path1);

//        // Test 2: Path requiring one interchange
//        System.out.println("\nTest 2: Red A to Green G (via Central Hub)");
//        ArrayList<Station> path2 = metro.dijkstra(r1, g7);
//        printPath(path2);

        // Test 3: Path requiring two interchanges
        System.out.println("\nTest 3: Blue A to Green G (via West Hub and Central Hub)");
        ArrayList<Station> path3 = metro.dijkstra(b1, g7);
        printPath(path3);

//        // Test 4: No path exists
//        System.out.println("\nTest 4: Blue A to Red G (no connection)");
//        SingleStation isolated = new SingleStation(500, 500, "IS0", "Isolated", "None", 0);
//        ArrayList<Station> path4 = metro.dijkstra(b1, isolated);
//        printPath(path4);

//        // Test 5: Station to itself
//        System.out.println("\nTest 5: Green C to Green C");
//        ArrayList<Station> path5 = metro.dijkstra(g3, g3);
//        printPath(path5);
    }

    private static void printPath(ArrayList<Station> path) {
        if (path.isEmpty()) {
            System.out.println("No path exists!");
            return;
        }

        System.out.println("Path found (" + (path.size()-1) + " stops):");
        for (int i = 0; i < path.size(); i++) {
            Station s = path.get(i);
            System.out.print(s.getName());
            if (i < path.size()-1) {
                System.out.print(" → ");
            }

            // New line every 5 stations for readability
            if (i > 0 && i % 5 == 0 && i < path.size()-1) {
                System.out.println();
            }
        }
        System.out.println();

        // Calculate total time (simplified without edge lookup)
        if (path.size() > 1) {
            int totalTime = path.get(path.size()-1).getEstimate();
            System.out.println("Total travel time: " + totalTime + " minutes");
        }
    }
}