package com.example.georgebra.Model.InputHandler;

import com.example.georgebra.Model.LineTypes.LRTLine;
import com.example.georgebra.Model.LineTypes.MRTLine;
import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.LineTypes.TourismLine;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOHandler {
    private MetroSystem msys;
    private String systemID;
    private String systemName;
    private int exchangeTime;
    private ArrayList<String> metroLineIDs = new ArrayList<>();
    private ArrayList<MetroLineData> metroLineDataList = new ArrayList<>();

    private final Pattern systemIDPattern = Pattern.compile("^[a-zA-Z]{1,10}$");
    private final Pattern lineIDPattern = Pattern.compile("^[a-zA-Z]{1,10}$");
    private final ObjectMapper mapper = new ObjectMapper();

    public IOHandler(String systemID) throws InputMismatchException, InvalidAlgorithmParameterException, InvalidAttributesException {
        systemID = systemID.toUpperCase();
        this.systemID = systemID;
        this.exchangeTime = 3;

        Matcher matcher = systemIDPattern.matcher(systemID);
        if (!matcher.find()) throw new InputMismatchException("System ID must have 1-10 word characters.");
        if (!systemID.endsWith("MTR")) this.systemID += "MTR";

        //prepare data fields
        this.readJson();
        this.msys = this.generateMetroSystem();
    }
    //TODO: add tooltip to the getfile input area, showing available system IDs.

    public MetroSystem getMetroSystem() {
        return this.msys;
    }
    public MetroSystem generateMetroSystem() throws InvalidAlgorithmParameterException, InvalidAttributesException {
        //if(this.msys != null) return this.msys;
        //get ready base data for metrosystem
        this.msys = new MetroSystem(systemName, exchangeTime);

        //create stations + lines + stuff -> msys and return
        for (MetroLineData lineData: metroLineDataList) { //iterate lines
            HashMap<String, Station> stationsHashMap = new HashMap<>(); //id:Station
            MetroLine newLine;

            //create base data for MetroLine object
            String lineName = lineData.getName();
            String lineCode = lineData.getCode();
            int lineId = lineData.getId();
            String lineColour = lineData.getColour();

            if (lineData.getLineType().equalsIgnoreCase("MRT")) newLine = new MRTLine(lineName, lineCode, lineId, lineColour);
            else if (lineData.getLineType().equalsIgnoreCase("LRT")) newLine = new LRTLine(lineName, lineCode, lineId);
            else if (lineData.getLineType().equalsIgnoreCase("Tourism")) newLine = new TourismLine(lineName, lineCode, lineId);
            else {
                throw new MissingFormatArgumentException("A MetroLine must be classified as one of: MRT/LRT/Tourism Line.");
            }

            //add stations to arlist for temp storage
            ArrayList<StationData> lineStationsDatas = lineData.getStations();
            for (StationData stationData: lineStationsDatas) { //iterate stations
                Station newStation;

                //get ready common data
                double x = stationData.getX();
                double y = stationData.getY();
                String stationName = stationData.getName();

                Pattern containDigit = Pattern.compile("[0-9]+");
                Matcher matcher = containDigit.matcher(stationName);
                if (matcher.find()) throw new InvalidAttributesException("Station Names must not contain digits; " +
                        "If necessary, replace digits with words (e.g. Heathrow Terminal 4 -> Heathrow Terminal Four)");

                if (stationData.isInterchange()) {
                    HashMap<String, String> otherDifferentLinesInfo = new HashMap<>();
                    ArrayList<String> lineIDs = stationData.getLineIDs();
                    ArrayList<String> lineNames = stationData.getLineNames();
                    for (int i = 0; i < lineIDs.size(); i++) {
                        otherDifferentLinesInfo.put(lineNames.get(i), lineIDs.get(i));
                    }
                    newStation = new Interchange(x, y, otherDifferentLinesInfo, stationName);

                    //add to Hashmap for edge addition
                    for (String id: lineIDs) {
                        stationsHashMap.put(id, newStation);
                    }
                }
                else {
                    String id = stationData.getId();
                    //:linename declared before in outer scope.
                    newStation = new SingleStation(x, y, id, lineName, stationName);

                    //add to Hashmap for edge addition
                    stationsHashMap.put(newStation.getStationID(), newStation);
                }
            }

            //add edges to metroline
            ArrayList<EdgeData> edgeDatas = lineData.getEdges();
            for (EdgeData edgeData: edgeDatas) {
                Station u = stationsHashMap.get(edgeData.getFrom());
                Station v = stationsHashMap.get(edgeData.getTo());
                int time = edgeData.getTime();

                if (time <= 0)
                    throw new InvalidAlgorithmParameterException("Edge weight must be >0: " + time);

                newLine.addEdge(u, v, time);
            }

            //finally add the line
            msys.addLine(newLine);
        }
        return this.msys;
    }

    public void setExchangeTime(int exchangeTime) {
        this.exchangeTime = exchangeTime;
    }
    public int getExchangeTime() {
        return this.exchangeTime;
    }

    //output
    /*
    public void writeMetroSystem() {
        //TODO
    }
     */

    private void readJson() {
        //1. get metro system and use the info to access other files; form data objects
        MetroSystemData metroSystemData = parseMetroSystemJsonData(this.systemID);
        this.metroLineIDs = metroSystemData.getMetroLineIDs();
        this.systemName = metroSystemData.getCityName();

        for (String lineID : metroLineIDs) {
            Matcher matcher = lineIDPattern.matcher(lineID);
            if (!matcher.find()) throw new InputMismatchException("Line ID must have 1-10 word characters.");

            metroLineDataList.add(parseMetroLineJsonData(lineID));
        }

        //interchangesData = this.parseInterchangesJsonData(systemID);
    }

    /*
    public void writeMetroSystemJsonData(String jsonFileName) {
        String filePath = "/Info/" + jsonFileName + ".json";
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            if (in == null) throw new IllegalStateException("File not found: " + filePath);
            return mapper.readValue(in, MetroSystemData.class);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public void writeMetroLineJsonData(String jsonFileName) {
        String filePath = "/Info/" + this.systemID + "_Line_" + jsonFileName + ".json";
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            if (in == null) throw new IllegalStateException("File not found: " + filePath);
            return mapper.readValue(in, MetroLineData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */

    public MetroSystemData parseMetroSystemJsonData(String jsonFileName) {
        String filePath = "/Info/" + jsonFileName + ".json";
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            if (in == null) throw new IllegalStateException("File not found: " + filePath);
            return mapper.readValue(in, MetroSystemData.class);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public MetroLineData parseMetroLineJsonData(String jsonFileName) {
        String filePath = "/Info/" + this.systemID + "_Line_" + jsonFileName + ".json";
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            if (in == null) throw new IllegalStateException("File not found: " + filePath);
            return mapper.readValue(in, MetroLineData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    public InterchangesData parseInterchangesJsonData(String jsonFileName) {
        String filePath = "/Info/" + this.systemID + "_Interchanges.json";
        try {
            InputStream in = getClass().getResourceAsStream(filePath);
            if (in == null) throw new IllegalStateException("File not found: " + filePath);
            return mapper.readValue(in, InterchangesData.class);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }
     */
}
