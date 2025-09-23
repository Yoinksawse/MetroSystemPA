package com.example.georgebra.Model.TesterClasses;

import com.example.georgebra.Model.InputHandler.IOHandler;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.util.Pair;

import java.util.ArrayList;

public class InputTester {
    //Tester class
    public static void main(String[] args) {
        try {
            IOHandler ioHandler = new IOHandler("SG");
            MetroSystem msys = ioHandler.getMetroSystem();
            //System.out.println(msys);

            //Pair<ArrayList<Station>, Integer> shortestPathA = msys.genShortestPath("Kembangan", "Tan Kah Kee"); printPath(shortestPathA.getKey(), shortestPathA.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathB = msys.genShortestPath("Bugis", "Tan Kah Kee"); printPath(shortestPathB.getKey(), shortestPathB.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathC = msys.genShortestPath("Rochor ", "esplanade"); printPath(shortestPathC.getKey(), shortestPathC.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathD = msys.genShortestPath("Rochor ", "Lavender"); printPath(shortestPathD.getKey(), shortestPathD.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathE = msys.genShortestPath("City Hall", "Bugis"); printPath(shortestPathE.getKey(), shortestPathE.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathF = msys.genShortestPath("tuas link", "punggol point"); printPath(shortestPathF.getKey(), shortestPathF.getValue());
            //Pair<ArrayList<Station>, Integer> shortestPathG = msys.genShortestPath("tuas link", "wooDlands noRth"); printPath(shortestPathF.getKey(), shortestPathF.getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printPath(ArrayList<Station> path, int time) {
        if (path.isEmpty()) {
            System.out.println();
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
        if (path.size() > 1) System.out.println("Total travel time: " + time + " minutes");

        System.out.println();
    }
}
