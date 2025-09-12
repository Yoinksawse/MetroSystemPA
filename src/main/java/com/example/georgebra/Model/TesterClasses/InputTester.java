package com.example.georgebra.Model.TesterClasses;

import com.example.georgebra.Model.IOHandler;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.Station;

import java.util.ArrayList;

public class InputTester {
    //Tester class
    public static void main(String[] args) {
        try {
            IOHandler ioHandler = new IOHandler("SG");
            MetroSystem msys = ioHandler.getMetroSystem();
            //System.out.println(msys);

            //ArrayList<Station> shortestPathA = msys.dijkstra("Kembangan", "Tan Kah Kee"); printPath(shortestPathA);
            //ArrayList<Station> shortestPathB = msys.dijkstra("Bugis", "Tan Kah Kee"); printPath(shortestPathB);
            //ArrayList<Station> shortestPathC = msys.dijkstra("Rochor ", "Promenade"); printPath(shortestPathC);
            //ArrayList<Station> shortestPathD = msys.dijkstra("Rochor ", "Lavender"); printPath(shortestPathD);
            //ArrayList<Station> shortestPathE = msys.dijkstra("City Hall", "Bugis"); printPath(shortestPathE);
            //ArrayList<Station> shortestPathF = msys.dijkstra("Kembangan", "Bugis"); printPath(shortestPathF);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        System.out.println();
    }
}
