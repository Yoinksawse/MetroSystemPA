package com.example.georgebra.Model;

import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InputReader {
    MetroSystem msys;
    String text;

    public InputReader(String text) {
        this.text = text;
    }

    public void read() {
        parseInput(text);
    }

    public MetroSystem getMetroSystem() {
        this.read();
        return this.msys;
    }

    public void parseInput(String inputs) {
        //input stations
        //make id:station <stationname, x, y, interchangetruefalse> map
        HashMap<String, Station> idToStation = new HashMap<>();
        HashMap<String, String> idToLineName = new HashMap<>();
        ArrayList<MetroLine> metroLines = new ArrayList<>();
        ArrayList<Station> stations = new ArrayList<>();

        //TODO: setup regex patterns
        String multiLineIndexRegex = ""; //-*Punggol LRT: PTC, PE, PW
        String singleLineIndexRegex = ""; //-Sentosa Express: SE
        String normalStationRegex = ""; //NS28:Marina South Pier
        String interchangeStationRegex = ""; //*NS27/CE2/TE20:Marina Bay
        String lineEdgesHeaderRegex = ""; //+1:North South Line,NSL
        String lineEdgeRegex = ""; //NS28 NS27 2


        //input all lines (stations given as id, so need the map to get info back)
        boolean gotAllStations = false, gotAllLines = false;
        int stationCntLocal = 0;
        Scanner s = new Scanner(inputs);
        while (s.hasNext()) {
            String curline = s.nextLine();
            if (!curline.contains("-") && !gotAllLines) gotAllLines = true;
            if (curline.contains("+") && !gotAllStations) gotAllStations = true;

            int x = 0, y = 0;
            if (!gotAllLines) {
                //inputting a line of line info
                String lineName;

                String[] parts = curline.split(":");
                String lineNameString = parts[0];
                String lineIndexesString = parts[1];

                if (lineNameString.contains("*")) {
                    lineName = lineNameString.substring(2); //remove -*
                    String[] stationIDs = parts[1].split(",");
                    for (String id: stationIDs) {
                        idToLineName.put(id, lineName);
                    }
                }
                else {
                    lineName = lineNameString.substring(1); //remove -
                    idToLineName.put(lineIndexesString, lineName);
                }
            }
            else if (!gotAllStations) {
                //inputting a line of station info like:
                //NS23:Somerset 0 100
                //*NS22/TE14:Orchard 199 213

                Station newStation;
                stationCntLocal++;

                //get the x and y
                String[] parts = curline.split(":");
                String stationIndexString = parts[0];
                String stationNameString = parts[1];

                if (stationIndexString.contains("*")) {
                    //it is interchange
                    String stationIndexes = stationIndexString.substring(1);
                    String[] idParts = stationIndexes.split("/");

                    ArrayList<Pair<String, String>> otherLinesInfo = new ArrayList<>();
                    for (String stationID : idParts) {
                        String actualIDCase = "";
                        char[] stationIDChars = stationID.toCharArray();
                        for (char c: stationIDChars) {
                            if (Character.isLetter(c)) {
                                actualIDCase += ("" + c);
                            }
                            else break;
                        }
                        otherLinesInfo.add(new Pair<>(stationID, idToLineName.get(actualIDCase)));
                    }
                    newStation = new Interchange(x, y, otherLinesInfo, stationNameString, stationCntLocal);

                    for (String stationID : idParts) idToStation.put(stationID, newStation);
                }
                else {
                    //it is terminal/singlestation
                    String actualIDCase = "";
                    char[] stationIDChars = stationIndexString.toCharArray();
                    for (char c: stationIDChars) {
                        if (Character.isLetter(c)) {
                            actualIDCase += ("" + c);
                        }
                        else break;
                    }

                    newStation = new SingleStation(x, y, stationIndexString, idToLineName.get(actualIDCase), stationNameString, stationCntLocal);
                    idToStation.put(stationIndexString, newStation);
                }
            }
            else { //!getLines
                //TODO: inputting a line info :
                //+1:North South Line,NSL
                //NS28 NS27 2

                if (curline.contains("+")) {
                    String lineInfoString = curline.substring(1);

                    String[] parts = curline.split("[:,]");
                    String lineNo  = parts[0];
                    String lineName = parts[1];
                    String lineID = parts[2];

                    //TODO: use ID to Station hashmap, get all IDs and convert to Stations
                    stations.add(idToStation.get(lineID));


                }
                else {
                    String[] edgeParts = curline.split(" ");
                    String uID = edgeParts[0];
                    String vID = edgeParts[1];
                    String uvTime = edgeParts[2];

                    //TODO
                }
            }
        }

        //(add all the edges with station info, then stations will be automatically added

        //make 1 metro system
        //ADD ALL LINES IN :D

        //TODO (build msys)
        msys = null;
    }
}
