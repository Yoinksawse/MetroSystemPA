package com.example.georgebra.Model;

import com.example.georgebra.Model.*;
import com.example.georgebra.Model.LineTypes.*;
import com.example.georgebra.Model.StationTypes.*;
import javafx.util.Pair;
import java.util.*;

public class SingaporeTester {
    public static void main(String[] args) {
        MetroSystem singaporeMRT = new MetroSystem("Singapore");

        // Create all lines with stations and connections
        createNorthSouthLine(singaporeMRT);
        createEastWestLine(singaporeMRT);
        createNorthEastLine(singaporeMRT);
        createCircleLine(singaporeMRT);
        //createDowntownLine(singaporeMRT);
        //createThomsonEastCoastLine(singaporeMRT);
        //createBukitPanjangLRT(singaporeMRT);
        //createSengkangLRT(singaporeMRT);
        //createPunggolLRT(singaporeMRT);

        // Print complete system
        System.out.println("=== COMPLETE SINGAPORE MRT/LRT SYSTEM ===");
        System.out.println(singaporeMRT);

        // Test connections
        testInterchanges(singaporeMRT);
    }

    private static void createNorthSouthLine(MetroSystem system) {
        MRTLine line = new MRTLine("North South Line", "NS", 1);

        // Stations with codes and names
        String[][] stations = {
                {"NS1", "Jurong East"}, {"NS2", "Bukit Batok"}, {"NS3", "Bukit Gombak"},
                {"NS4", "Choa Chu Kang"}, {"NS5", "Yew Tee"}, {"NS6", "Kranji"},
                {"NS7", "Marsiling"}, {"NS8", "Woodlands"}, {"NS9", "Admiralty"},
                {"NS10", "Sembawang"}, {"NS11", "Canberra"}, {"NS12", "Yishun"},
                {"NS13", "Khatib"}, {"NS14", "Yio Chu Kang"}, {"NS15", "Ang Mo Kio"},
                {"NS16", "Bishan"}, {"NS17", "Braddell"}, {"NS18", "Toa Payoh"},
                {"NS19", "Novena"}, {"NS20", "Newton"}, {"NS21", "Orchard"},
                {"NS22", "Somerset"}, {"NS23", "Dhoby Ghaut"}, {"NS24", "City Hall"},
                {"NS25", "Raffles Place"}, {"NS26", "Marina Bay"}, {"NS27", "Marina South Pier"}
        };

        // Travel times between stations (minutes)
        int[] travelTimes = {3, 2, 3, 2, 3, 3, 2, 4, 3, 2, 2, 3, 2, 3, 3, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2};

        // Create stations and connections
        createLineWithTravelTimes(system, line, stations, travelTimes);
    }

    private static void createEastWestLine(MetroSystem system) {
        MRTLine line = new MRTLine("East West Line", "EW", 2);

        String[][] stations = {
                {"EW1", "Pasir Ris"}, {"EW2", "Tampines"}, {"EW3", "Simei"},
                {"EW4", "Tanah Merah"}, {"EW5", "Bedok"}, {"EW6", "Kembangan"},
                {"EW7", "Eunos"}, {"EW8", "Paya Lebar"}, {"EW9", "Aljunied"},
                {"EW10", "Kallang"}, {"EW11", "Lavender"}, {"EW12", "Bugis"},
                {"EW13", "City Hall"}, {"EW14", "Raffles Place"}, {"EW15", "Tanjong Pagar"},
                {"EW16", "Outram Park"}, {"EW17", "Tiong Bahru"}, {"EW18", "Redhill"},
                {"EW19", "Queenstown"}, {"EW20", "Commonwealth"}, {"EW21", "Buona Vista"},
                {"EW22", "Dover"}, {"EW23", "Clementi"}, {"EW24", "Jurong East"},
                {"EW25", "Chinese Garden"}, {"EW26", "Lakeside"}, {"EW27", "Boon Lay"},
                {"EW28", "Pioneer"}, {"EW29", "Joo Koon"}, {"EW30", "Gul Circle"},
                {"EW31", "Tuas Crescent"}, {"EW32", "Tuas West Road"}, {"EW33", "Tuas Link"},
                {"CG1", "Expo"}, {"CG2", "Changi Airport"}
        };

        int[] travelTimes = {
                2, 2, 4, 3, 2, 3, 3, 2, 3, 2, 3, 1, 1, 2, 3, 2, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 2,
                3, 3 // Changi branch
        };

        createLineWithTravelTimes(system, line, stations, travelTimes);
    }

    private static void createLineWithTravelTimes(MetroSystem system, Line line, String[][] stations, int[] travelTimes) {
        // Create all stations first
        List<Station> stationObjects = new ArrayList<>();
        for (String[] station : stations) {
            if (isInterchange(station[0])) {
                // Handle interchange creation
                ArrayList<Pair<String, String>> lineInfo = new ArrayList<>();
                lineInfo.add(new Pair<>(line.getLineName(), station[0]));
                stationObjects.add(new Interchange(0, 0, lineInfo, station[1], stationObjects.size()+1));
            } else {
                stationObjects.add(new SingleStation(0, 0, station[0], station[1], line.getLineName(), stationObjects.size()+1));
            }
        }

        // Add stations to line
        for (Station s : stationObjects) {
            line.addStation(s);
        }

        // Create connections with travel times
        for (int i = 0; i < stationObjects.size()-1; i++) {
            line.addEdge(stationObjects.get(i), stationObjects.get(i+1), travelTimes[i]);
        }

        system.addLine(line);
    }

    private static boolean isInterchange(String stationCode) {
        // Check if station code appears in multiple lines
        return stationCode.startsWith("NS") &&
                (stationCode.equals("NS1") || stationCode.equals("NS24") || stationCode.equals("NS25"));
    }

    private static void createNorthEastLine(MetroSystem system) {
        MRTLine line = new MRTLine("North East Line", "NE", 3);

        String[][] stations = {
                {"NE1", "HarbourFront"}, {"NE2", "Outram Park"}, {"NE3", "Chinatown"},
                {"NE4", "Clarke Quay"}, {"NE5", "Dhoby Ghaut"}, {"NE6", "Little India"},
                {"NE7", "Farrer Park"}, {"NE8", "Boon Keng"}, {"NE9", "Potong Pasir"},
                {"NE10", "Woodleigh"}, {"NE11", "Serangoon"}, {"NE12", "Kovan"},
                {"NE13", "Hougang"}, {"NE14", "Buangkok"}, {"NE15", "Sengkang"},
                {"NE16", "Punggol"}
        };

        int[] travelTimes = {2, 2, 3, 3, 2, 2, 1, 2, 2, 3, 2, 2, 3, 2, 0, 3};

        createLineWithTravelTimes(system, line, stations, travelTimes);
    }

    private static void createCircleLine(MetroSystem system) {
        MRTLine line = new MRTLine("Circle Line", "CC", 4);

        String[][] stations = {
                {"CC1", "Dhoby Ghaut"}, {"CC2", "Bras Basah"}, {"CC3", "Esplanade"},
                {"CC4", "Promenade"}, {"CC5", "Nicoll Highway"}, {"CC6", "Stadium"},
                {"CC7", "Mountbatten"}, {"CC8", "Dakota"}, {"CC9", "Paya Lebar"},
                {"CC10", "MacPherson"}, {"CC11", "Tai Seng"}, {"CC12", "Bartley"},
                {"CC13", "Serangoon"}, {"CC14", "Lorong Chuan"}, {"CC15", "Bishan"},
                {"CC16", "Marymount"}, {"CC17", "Caldecott"}, {"CC18", "Botanic Gardens"},
                {"CC19", "Farrer Road"}, {"CC20", "Holland Village"}, {"CC21", "Buona Vista"},
                {"CC22", "one-north"}, {"CC23", "Kent Ridge"}, {"CC24", "Haw Par Villa"},
                {"CC25", "Pasir Panjang"}, {"CC26", "Labrador Park"}, {"CC27", "Telok Blangah"},
                {"CC28", "HarbourFront"}, {"CC29", "Marina Bay"}
        };

        int[] travelTimes = {
                2, 2, 2, 1, 2, 3, 2, 3, 2, 2, 2, 2, 3, 2, 2, 2, 2, 3, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2
        };

        createLineWithTravelTimes(system, line, stations, travelTimes);
    }

    // Similar methods for Downtown Line, Thomson-East Coast Line, and LRT lines...
    // (Implementation follows same pattern as above)

    private static void testInterchanges(MetroSystem system) {
        System.out.println("\n=== INTERCHANGE TESTING ===");

        String[] interchangeNames = {"Raffles Place", "Dhoby Ghaut", "City Hall", "Paya Lebar"};

        for (String name : interchangeNames) {
            for (Station s : system.getStationList()) {
                if (s.getName().equals(name) && s instanceof Interchange) {
                    Interchange interchange = (Interchange)s;
                    System.out.println(name + " serves lines: " +
                            interchange.getDifferentLinesInfo());
                }
            }
        }
    }
}