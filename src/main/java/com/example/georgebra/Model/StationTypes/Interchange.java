package com.example.georgebra.Model.StationTypes;

import java.util.*;

public class Interchange extends Station {
    public static ArrayList<Interchange> interchanges = new ArrayList<>();
    private static HashSet<String> interchangeNames = new HashSet<>();
    private HashMap<String, String> allLinesInfo = new HashMap<>();
    //^ above hashset contains entries: (name of one line the interchange is in, id of interchange in that line)

    //primary constructor
    public Interchange(double x, double y, HashMap<String, String> otherAllLinesInfo, String name) throws IllegalArgumentException {
        //Pass  first line name to super, but store all lines
        super(x, y, name);

        for (Map.Entry<String, String> lineInfo: otherAllLinesInfo.entrySet()) {
            String id = lineInfo.getValue();
            if (id.isEmpty()) { // || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
                throw new IllegalArgumentException("Invalid Station ID: " + id);
            }
            //this.allLinesInfo.put(lineInfo.getKey().toLowerCase(), id);
        }

        this.allLinesInfo = new HashMap<>();
        for (Map.Entry<String, String> id: otherAllLinesInfo.entrySet()) {
            allLinesInfo.put(id.getKey().toLowerCase(), id.getValue());
        }

        interchanges.add(this);
        interchangeNames.add(this.name);
    }

    //check if the interchange already exists, if it does, add the lines to existing interchange
    public static void mergeInterchangesLineData(Interchange other) {
        //find existing
        for (Interchange existing: interchanges) {
            if (existing.getName().equalsIgnoreCase(other.getName())) { //it already has an "instance" somewhere
                HashMap<String, String> xAllLinesInfo = other.getAllLinesInfo();
                for (Map.Entry<String, String> stringStringPair : xAllLinesInfo.entrySet()) {
                    existing.addLine(stringStringPair.getKey(), stringStringPair.getValue());
                    //existing.updateExchangeGraph();
                }
            }
        }
        //throw new NoSuchElementException("HELLO! Your interchange does not have a match.");
    }

    public static ArrayList<Interchange> getInterchanges() {
        return new ArrayList<>(interchanges);
    }

    public HashMap<String, String> getAllLinesInfo() {
        return new HashMap<>(allLinesInfo);
    }

    public void addLine(String lineName, String interchangeID) {
        lineName = lineName.toLowerCase();
        if (lineName == null || lineName.isEmpty()) {
            throw new IllegalArgumentException("MetroLine name cannot be null or empty");
        }
        if (interchangeID == null || interchangeID.isEmpty()) {
            throw new IllegalArgumentException("Interchange ID cannot be null or empty");
        }

        boolean alreadyContains = false;
        for (Map.Entry<String, String> pss: this.allLinesInfo.entrySet()) {
            if (pss.getKey().equalsIgnoreCase(lineName)) alreadyContains = true;
        }

        if (!alreadyContains) {
            this.allLinesInfo.put(lineName.toLowerCase(), interchangeID);
        }
    }

    public String toString() {
        String ids = "";
        for (Map.Entry<String, String> id: allLinesInfo.entrySet()) {
            ids += (id.getValue() + "/");
        }
        if (ids.length() >= 2) ids = ids.substring(0, ids.length() - 1);
        return "*" + ids + ": " + this.name + " " + this.x + " " + this.y;
    }
}