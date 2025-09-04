package com.example.georgebra.Model.TesterClasses;

import com.example.georgebra.Model.IOHandler;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.Station;

import java.util.ArrayList;

public class InputTester {
    private static Station Utest;
    private static Station Vtest;

    private static Station UAtest;
    private static Station VAtest;

    private static Station UBtest;
    private static Station VBtest;

    private static Station UCtest;
    private static Station VCtest;
    //Tester class
    public static void main(String[] args) {
        try {
            IOHandler ioHandler = new IOHandler("SG");
            MetroSystem msys = ioHandler.getMetroSystem();

            //System.out.println(msys);

            //ArrayList<Station> shortestPath = msys.dijkstra(Utest, Vtest); printPath(shortestPath); System.out.println();

            ArrayList<Station> shortestPathA = msys.dijkstra(UAtest, VAtest); printPath(shortestPathA); System.out.println();

            //ArrayList<Station> shortestPathB = msys.dijkstra(UBtest, VBtest); printPath(shortestPathA); System.out.println();

            //ArrayList<Station> shortestPathC = msys.dijkstra(UCtest, VCtest); printPath(shortestPathA);
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

            //if (s instanceof Interchange) exchangeTime += 5;

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
