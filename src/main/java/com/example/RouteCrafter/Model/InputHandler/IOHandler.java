package com.example.RouteCrafter.Model.InputHandler;

import com.example.RouteCrafter.Model.LineTypes.LRTLine;
import com.example.RouteCrafter.Model.LineTypes.MRTLine;
import com.example.RouteCrafter.Model.LineTypes.MetroLine;
import com.example.RouteCrafter.Model.LineTypes.TourismLine;
import com.example.RouteCrafter.Model.MetroSystem;
import com.example.RouteCrafter.Model.StationTypes.Interchange;
import com.example.RouteCrafter.Model.StationTypes.SingleStation;
import com.example.RouteCrafter.Model.StationTypes.Station;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import javafx.util.Pair;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOHandler {
    private MetroSystem msys;
    private String systemID;
    private String systemName;
    private int exchangeTime;
    private final static String root = System.getProperty("user.home") + File.separator + ".Georgebra/";
    private String thisRoot;
    private ArrayList<String> metroLineIDs = new ArrayList<>();
    private ArrayList<MetroLineData> metroLineDataList = new ArrayList<>();
    private HashMap<String, Pair<Double,Double>> stationNameToCoordinates = new HashMap<>();
    private HashMap<String, Pair<Double,Double>> stationNameToTextCoordinates = new HashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private static Pattern containDigit = Pattern.compile("[0-9]+");

    public IOHandler(String systemName) throws IllegalStateException, InvalidAlgorithmParameterException, InvalidAttributesException {
        this.systemName = systemName.toLowerCase();
        this.exchangeTime = 3;

        Pattern cityNamePattern = Pattern.compile("^[a-zA-Z]{1,170}$");
        Matcher cityNameMatcher = cityNamePattern.matcher(systemName);
        if (!cityNameMatcher.matches()) {
            //Alert idotAlert = new Alert(Alert.AlertType.ERROR);
            //idotAlert.setContentText("City names can only have Roman characters. Please Romanise first!");
            throw new InvalidAttributesException("nonexistent city");
        }

        Path basePath = Paths.get(root);
        thisRoot = basePath + File.separator + this.systemName + File.separator;

        if (!Files.exists(basePath)) {
            try { Files.createDirectories(basePath); }
            catch (IOException e) {}
        }

        /*
        Matcher matcher = MetroSystem.systemIDPattern.matcher(systemID);
        if (!matcher.find()) throw new InputMismatchException("System ID must have 1-10 word characters.");
        if (!systemID.endsWith("MTR")) this.systemID += "MTR";
         */

        //prepare data fields
        this.readJson();
        this.msys = generateMetroSystem();
        for (Station s: msys.getStationList()) {
            stationNameToCoordinates.put(s.getName().toLowerCase(), new Pair<>(0.0,0.0));
            stationNameToTextCoordinates.put(s.getName().toLowerCase(), new Pair<>(0.0, 0.0));
        }
    }

    public MetroSystem getMetroSystem() {
        return this.msys;
    }
    public MetroSystem generateMetroSystem() throws InvalidAlgorithmParameterException, InvalidAttributesException {
        //if(this.msys != null) return this.msys;
        this.msys = new MetroSystem(systemName, exchangeTime, systemID);
        for (MetroLineData lineData: metroLineDataList) { //iterate lines
            HashMap<String, Station> stationsHashMap = new HashMap<>(); //id:Station
            MetroLine newLine;

            //create base data for MetroLine object
            String lineName = lineData.getName();
            String lineCode = lineData.getCode();
            String lineColour = lineData.getColour();

            if (lineData.getLineType().equalsIgnoreCase("MRT")) newLine = new MRTLine(lineName, lineCode, lineColour);
            else if (lineData.getLineType().equalsIgnoreCase("LRT")) newLine = new LRTLine(lineName, lineCode);
            else if (lineData.getLineType().equalsIgnoreCase("Tourism")) newLine = new TourismLine(lineName, lineCode);
            else  throw new MissingFormatArgumentException("A MetroLine must be classified as one of: MRT/LRT/Tourism Line.");

            //add stations to arlist for temp storage
            ArrayList<MetroStationData> lineStationsDatas = lineData.getStations();
            for (MetroStationData metroStationData : lineStationsDatas) { //iterate stations
                Station newStation;

                //get ready common data
                double x = metroStationData.getX();
                double y = metroStationData.getY();
                double textX = metroStationData.getTextX();
                double textY = metroStationData.getTextY();
                String stationName = metroStationData.getName();

                Matcher matcher = containDigit.matcher(stationName);
                if (matcher.find()) throw new InvalidAttributesException("Station Names must not contain digits; " +
                        "If necessary, replace digits with words (e.g. Heathrow Terminal 4 -> Heathrow Terminal Four)");

                if (metroStationData.isInterchange()) {
                    HashMap<String, String> otherDifferentLinesInfo = new HashMap<>();
                    ArrayList<String> lineIDs = metroStationData.getLineIDs();
                    ArrayList<String> lineNames = metroStationData.getLineNames();
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
                    String id = metroStationData.getId();
                    //:linename declared before in outer scope.
                    newStation = new SingleStation(x, y, id, lineName, stationName);

                    //add to Hashmap for edge addition
                    stationsHashMap.put(newStation.getStationID(), newStation);
                }
                newStation.setTextX(textX);
                newStation.setTextY(textY);
            }

            //add edges to metroline
            ArrayList<EdgeData> edgeDatas = lineData.getEdges();
            for (EdgeData edgeData: edgeDatas) {
                Station u = stationsHashMap.get(edgeData.getFrom());
                Station v = stationsHashMap.get(edgeData.getTo());
                int time = edgeData.getTime();

                if (time <= 0)
                    throw new InvalidAlgorithmParameterException("Edge weight must be > 0: " + time);

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

    public void setMetroSystem(MetroSystem msys) {
        this.msys = msys;
    }

    public int getExchangeTime() {
        return this.exchangeTime;
    }

    //i/o methods
    private void readJson() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<MetroLineData>> futures = new ArrayList<>();

        //1. get metro system and use the info to access other files; form data objects
        MetroSystemData metroSystemData = parseMetroSystemJsonData();
        if (metroSystemData == null) {
            String fileName = systemName.toUpperCase() + ((!systemName.toUpperCase().endsWith("MTR")) ? ".json" : "MTR.json");
            throw new IllegalStateException("File not found in classpath or filesystem:\n" + fileName);
        }

        this.metroLineIDs = metroSystemData.getMetroLineIDs();
        this.systemName = metroSystemData.getCityName();

        this.systemID = metroSystemData.getSystemID();
        Matcher systemMatcher = MetroSystem.systemIDPattern.matcher(systemID);
        if (!systemMatcher.find()) throw new InputMismatchException("System ID must have 1-10 word characters.");
        if (!systemID.endsWith("MTR")) this.systemID += "MTR";

        for (String lineID : metroLineIDs) {
            //Matcher lineMatcher = MetroLine.lineCodePattern.matcher(lineID);
            //if (!lineMatcher.find()) throw new InputMismatchException("Line ID must have 1-10 word characters.");

            futures.add(executor.submit(() -> parseMetroLineJsonData(lineID)));
        }

        metroLineDataList.clear();
        for (Future<MetroLineData> f : futures) {
            MetroLineData mld;
            try {
                mld = f.get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            if (mld == null) {
                String fileName = systemName.toUpperCase() + ((!systemName.toUpperCase().endsWith("MTR")) ? ".json" : "MTR.json");
                throw new IllegalStateException("File not found in classpath or filesystem:\n" + fileName);
            }
            else metroLineDataList.add(mld);
        }

        executor.shutdown();
    }
    public void writeJson(Object data, String fileName) {
        //include path if needed (e.g. "...Info/Singapore/SX.json" "Info/Singapore/SX.json)
        try {
            File file = new File(fileName);
            File parent = file.getParentFile();
            if (!parent.exists()) parent.mkdirs();

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAll(MetroSystem msys) {
        for (Station s: msys.getStationList()) {
            stationNameToCoordinates.put(s.getName(), new Pair<>(s.getX(), s.getY()));
            stationNameToTextCoordinates.put(s.getName(), new Pair<>(s.getTextX(), s.getTextY()));
        }

        String systemFile = thisRoot + msys.getCityName().toUpperCase() + "MTR.json";
        MetroSystemData systemData = createMetroSystemData(msys);
        writeJson(systemData, systemFile);

        for (MetroLine line: msys.getLineList()) {
            MetroLineData lineData = createMetroLineData(line);
            String lineFile = thisRoot + msys.getSystemID() + "_Line_" + lineData.getCode() + ".json";
            writeJson(lineData, lineFile);
        }
    }

    public MetroSystemData createMetroSystemData(MetroSystem msys) {
        MetroSystemData msysData = new MetroSystemData();
        msysData.setCityName(msys.getCityName());
        msysData.setMetroLineIDs(new ArrayList<>());
        msysData.setSystemID(msys.getSystemID());

        for (MetroLine line : msys.getLineList()) {
            msysData.getMetroLineIDs().add(line.getLineCode());
        }

        return msysData;
    }

    public MetroLineData createMetroLineData(MetroLine line) {
        MetroLineData mLineData = new MetroLineData();

        mLineData.setCode(line.getLineCode());
        mLineData.setName(line.getLineName());
        mLineData.setColour(line.getLineColour());
        if (line instanceof MRTLine) mLineData.setLineType("MRT");
        else if (line instanceof LRTLine) mLineData.setLineType("LRT");
        else if (line instanceof TourismLine) mLineData.setLineType("Tourism");

        ArrayList<MetroStationData> stationsData = new ArrayList<>();
        HashSet<Station> stations = line.getStationList();
        for (Station s: stations) stationsData.add(createMetroStationData(s));

        //MAKE EDGES!!!!!!!!!!!
        HashMap<Integer,String> indexToStationNameMap = line.getIndexToStationNameMap();
        HashMap<String,Station> stationNameToStationNameMap = line.getStationNameToStationMap();

        ArrayList<EdgeData> edgesData = new ArrayList<>();
        HashMap<Integer, Integer> adjList[] = line.getAdjList();

        for (int u = 0; u < line.getGraphHandler().getNodeCnt(); u++) {
            String uName = indexToStationNameMap.get(u);
            Station uStn = stationNameToStationNameMap.get(uName);
            for (Map.Entry<Integer,Integer> vTimeEntry: adjList[u].entrySet()) {
                int time = vTimeEntry.getValue();
                int v = vTimeEntry.getKey();
                String vName = indexToStationNameMap.get(v);
                Station vStn = stationNameToStationNameMap.get(vName);

                edgesData.add(createEdgeData(uStn, vStn, time, line.getLineName()));
            }
        }

        mLineData.setStations(stationsData);
        mLineData.setEdges(edgesData);

        return mLineData;
    }

    public MetroStationData createMetroStationData(Station s) {
        MetroStationData mStatData = new MetroStationData();

        mStatData.setName(s.getName());
        Pair<Double,Double> coords = stationNameToCoordinates.get(s.getName().toLowerCase());
        Pair<Double,Double> textCoords = stationNameToTextCoordinates.get(s.getName().toLowerCase());
        mStatData.setX(coords.getKey());
        mStatData.setY(coords.getValue());

        mStatData.setTextX(textCoords.getKey());
        mStatData.setTextY(textCoords.getValue());
        if (s instanceof Interchange) {
            mStatData.setId("");
            mStatData.setInterchange(true);

            HashMap<String, String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            ArrayList<String> lineNames = new ArrayList<>();
            ArrayList<String> lineIDs = new ArrayList<>();

            for (Map.Entry<String,String> lineNameIntIDEntry: allLinesInfo.entrySet()) {
                lineNames.add(lineNameIntIDEntry.getKey());
                lineIDs.add(lineNameIntIDEntry.getValue());
            }
            mStatData.setLineNames(lineNames);
            mStatData.setLineIDs(lineIDs);
        }
        else if (s instanceof SingleStation) {
            mStatData.setId(s.getStationID());
            mStatData.setInterchange(false);

            ArrayList<String> lineName = new ArrayList<>();
            lineName.add(s.getLineName());
            mStatData.setLineNames(lineName);

            ArrayList<String> lineID = new ArrayList<>();
            lineID.add(s.getStationID());
            mStatData.setLineIDs(lineID);
        }

        return mStatData;
    }

    public EdgeData createEdgeData(Station u, Station v, int time, String curLineName) {
        String uID, vID;
        if (u instanceof Interchange) {
            uID = ((Interchange) u).getAllLinesInfo().get(curLineName);
            //System.out.println(u.getName() + " " + curLineName + " " + uID);
            //System.out.println(((Interchange) u).getAllLinesInfo());
        }
        else uID = u.getStationID();
        if (v instanceof Interchange) {
            vID = ((Interchange) v).getAllLinesInfo().get(curLineName);
            //System.out.println(u.getName() + " " + curLineName + " " + uID);
        }
        else vID = v.getStationID();

        EdgeData edge = new EdgeData();
        edge.setFrom(uID);
        edge.setTo(vID);
        edge.setTime(time);

        return edge;
    }

    //PARSING JSON
    public MetroSystemData parseMetroSystemJsonData() {
        String fileName = systemName.toUpperCase();
        if (!fileName.endsWith("MTR")) fileName += "MTR.json";
        else fileName += ".json";

        //try user.home
        String relativePath = thisRoot + fileName;
        File file = new File(relativePath);
        if (file.exists()) {
            try {
                return mapper.readValue(file, MetroSystemData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Fell back to classpath");

        //System.err.println("reading " + relativePath + " failed"); //TODO: copying files out of jar + classpath
        try (InputStream in = getClass().getResourceAsStream("/" + relativePath)) {
            if (in != null) {
                return mapper.readValue(in, MetroSystemData.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MetroLineData parseMetroLineJsonData(String jsonFileName) {
        String relativePath = thisRoot + systemID + "_Line_" + jsonFileName + ".json";

        //try user.home
        File file = new File(relativePath);
        if (file.exists()) {
            try {
                return mapper.readValue(file, MetroLineData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Fell back to classpath");

        //try class
        try (InputStream in = getClass().getResourceAsStream("/" + relativePath)) {
            if (in != null) {
                return mapper.readValue(in, MetroLineData.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("File not read in classpath or filesystem: " + relativePath);
    }

    //util
    public static String getAvailableCities() throws IOException {
        String s = "";
        Path targetPath = Paths.get(System.getProperty("user.home"), ".Georgebra", "singapore");
        Files.createDirectories(targetPath);

        //boolean directoryNotFound = false;
        File rootDir = new File(root);
        if (!rootDir.exists() || !rootDir.isDirectory()) throw new NotDirectoryException("No such directory");

        File[] directories = rootDir.listFiles(File::isDirectory);
        /*
        if (directories == null || directories.length == 0) {
            try {
                Files.createDirectories(targetPath);
                copyResourceFolder("Info/singapore", targetPath);
                directories = rootDir.listFiles(File::isDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy default Singapore data ", e);
            }
        }
         */

        if (directories == null || directories.length == 0) return "No metro systems available";

        ArrayList<String> directoryNames = new ArrayList<>();
        for (File dir: directories) {
            directoryNames.add(dir.getName());
        }

        return String.join("\n", directoryNames);
    }

    private static void copyFromJar(String resourcePath, Path targetPath) throws IOException {
        ClassLoader classLoader = IOHandler.class.getClassLoader();

        String[] files = {
                "SINGAPOREMTR.json",
                "SGMTR_Line_NSL.json",
                "SGMTR_Line_EWL.json",
                "SGMTR_Line_NEL.json",
                "SGMTR_Line_CCL.json",
                "SGMTR_Line_DTL.json",
                "SGMTR_Line_TEL.json",
                "SGMTR_Line_PGLRT.json",
                "SGMTR_Line_SKLRT.json",
                "SGMTR_Line_BPLRT.json",
                "SGMTR_Line_SX.json"
        };

        for (String file: files) {
            String resourceFile = resourcePath + "/" + file;
            try (InputStream is = classLoader.getResourceAsStream(resourceFile)) {
                if (is != null) {
                    Path targetFile = targetPath.resolve(file);
                    Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}