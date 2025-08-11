package com.example.georgebra.Model;

import com.example.georgebra.Model.*;
import com.example.georgebra.Model.LineTypes.*;
import com.example.georgebra.Model.StationTypes.*;
import javafx.util.Pair;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) {
        testStationClasses();
        testLineClasses();
        testMetroSystem();
        testEdgeCases();
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

        northSouthLine.addStation(jurongEast);
        northSouthLine.addStation(bukitBatok);
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

        // Test duplicate station addition
        try {
            singaporeMRT.addStation(jurongEast);
        } catch (Exception e) {
            System.out.println("Caught expected duplicate station exception: " + e.getMessage());
        }
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
}