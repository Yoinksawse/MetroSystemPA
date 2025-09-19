package com.example.georgebra.Controller;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.InputHandler.IOHandler;
import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.MyApplication;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.scene.shape.Line;

import javax.naming.directory.InvalidAttributesException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;

public class MainController {
    //outside fields
    @FXML
    public Button unshow_route;
    public Button clearmap_button;
    public Button compute_fastest_button;
    public Button compute_leastexchange_button;
    public Button open_infowindow_button;
    public Button updatemap_button;
    public Button loadmetrosystem_button;
    public Button save_button;
    public TextField from_station_input_area;
    public TextField search_area;
    public TextField to_station_input_area;
    public TextField map_city_choice;
    public StackPane map_container;
    public ToggleButton switch_devmode;
    public CheckBox displayedgeweight_checkbox;

    //inside fields
    private StackPane mapContainer;
    private boolean devMode = false;
    private boolean isMapShown = false;
    private MetroSystem msys;
    private String systemID;
    private Group currentSystem = new Group();
    private boolean displayingShortestPath;
    private boolean displayingLeastExchangePath;
    private boolean showEdgeWeight;

    private IOHandler ioHandler;
    private String uName;
    private String vName;
    private HashMap<String,String> lineNameToColourMap;
    private HashMap<String,Boolean> orientHorizontallyMap;

    public final DropShadow highlightShadow =
            new DropShadow(30, Color.rgb(255, 125, 125, 0.99));
    private final EventHandler<MouseEvent> preventStationEditFilter = e -> {
        if (e.getTarget() instanceof Group || e.getTarget() instanceof Circle) e.consume();
    };

    public void initialize() {
        mapContainer = map_container;
        //setting initial access rights
        {
            map_city_choice.clear();
            from_station_input_area.clear();
            search_area.clear();
            to_station_input_area.clear();
            loadmetrosystem_button.setDisable(false);
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false);
            save_button.setDisable(true);
            unshow_route.setDisable(true);
            compute_fastest_button.setDisable(true);
            compute_leastexchange_button.setDisable(true); //assuming in this case, all routes are cleared
            clearmap_button.setDisable(true);
            updatemap_button.setDisable(true);
            from_station_input_area.setDisable(true);
            search_area.setDisable(true);
            to_station_input_area.setDisable(true);
            displayedgeweight_checkbox.setDisable(true);

            //map_container.setDisable(true);
        }
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        uName = null;
        vName = null;
    }

    @FXML
    public void getInput() {
        systemID = map_city_choice.getText();
        try {
            ioHandler = new IOHandler(systemID);
            this.msys = ioHandler.getMetroSystem();
        }
        catch (InvalidAlgorithmParameterException | InvalidAttributesException e) {
            Alert x = new Alert(Alert.AlertType.ERROR);
            x.setContentText("Input Exception! \n" + e);
            x.show();
        }

        if (this.msys != null) {
            lineNameToColourMap = MetroLine.getLineNameToColourMap();//msys.getMetroLineNameToColourMap();
            //System.out.println(MetroLine.getLineNameToColourMap());
            //System.out.println(lineNameToColourMap);
            orientHorizontallyMap = new HashMap<>();
            for (Station s: msys.getStationList()) {
                if (s instanceof Interchange) {
                    orientHorizontallyMap.put(s.getName(), false);
                }
            }

            displayMap();
        }
    }

    @FXML
    public void writeJson() {
        //TODO TODO

    }

    @FXML
    protected void switchMode() {
        if (devMode) {
            devMode = false;
            switch_devmode.setText("Switch to Dev Mode");

            //ENTER USER MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            unshow_route.setDisable(true); //assuming in this case, all routes are cleared
            compute_fastest_button.setDisable(true); //assuming in this case, all routes are cleared
            compute_leastexchange_button.setDisable(true); //assuming in this case, all routes are cleared
            clearmap_button.setDisable(true);
            updatemap_button.setDisable(true);
            save_button.setDisable(false);

            from_station_input_area.setDisable(!isMapShown);
            compute_fastest_button.setDisable(!isMapShown);
            compute_leastexchange_button.setDisable(!isMapShown);
            unshow_route.setDisable(!displayingShortestPath);
            search_area.setDisable(false);
            to_station_input_area.setDisable(!isMapShown);

            mapContainer.getProperties().remove("preventStationEditFilter");

            displayingShortestPath = false;
            displayingLeastExchangePath = false;
            uName = null;
            vName = null;
        }
        else {
            devMode = true;
            switch_devmode.setText("Switch to User Mode"); //dont be tricked

            //ENTER DEV MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            unshow_route.setDisable(true); //assuming in this case, all routes are cleared
            compute_fastest_button.setDisable(true); //assuming in this case, all routes are cleared
            compute_leastexchange_button.setDisable(true); //assuming in this case, all routes are cleared
            clearmap_button.setDisable(true);
            updatemap_button.setDisable(false);
            save_button.setDisable(true);

            from_station_input_area.setDisable(true);
            compute_fastest_button.setDisable(true);
            compute_leastexchange_button.setDisable(true);
            unshow_route.setDisable(true);
            search_area.setDisable(false);
            to_station_input_area.setDisable(true);

            mapContainer.getProperties().put("preventStationEditFilter", preventStationEditFilter);
        }
    }

    @FXML
    protected void showAboutMe() {
        MyApplication.showAboutMe();
    }

    @FXML
    protected void clearMap() {
        isMapShown = false;
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        mapContainer.getChildren().clear();
    }

    @FXML
    protected void clearPath() {
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        mapContainer.getChildren().clear();
    }

    @FXML
    protected void displayShortestPath() {
        displayingShortestPath = true;
        isMapShown = true;
        displayMap();
    }

    @FXML
    protected void displayLeastExchangePath() {
        displayingLeastExchangePath = true;
        isMapShown = true;
        int prevExchangeTime = this.ioHandler.getExchangeTime();
        try {
            this.ioHandler.setExchangeTime(10000);
            this.msys = ioHandler.generateMetroSystem();

            displayMap();

            this.ioHandler.setExchangeTime(prevExchangeTime);
            this.msys = ioHandler.generateMetroSystem();
        }
        catch (InvalidAlgorithmParameterException | InvalidAttributesException e) {
            Alert x = new Alert(Alert.AlertType.ERROR);
            x.setContentText("Input Exception \n" + e);
            x.show();
        }
        finally {
            this.ioHandler.setExchangeTime(prevExchangeTime);
        }
    }

    @FXML
    protected void displayMap() {
        if (msys == null || ioHandler == null) {
            //this should not happen though
            Alert x = new Alert(Alert.AlertType.ERROR);
            x.setContentText("Map has not been loaded yet");
            x.show();
        }
        isMapShown = true;

        from_station_input_area.setDisable(false);
        search_area.setDisable(false);
        to_station_input_area.setDisable(false);
        displayedgeweight_checkbox.setDisable(false);
        showEdgeWeight = displayedgeweight_checkbox.isSelected();

        uName = from_station_input_area.getText();
        vName = to_station_input_area.getText();
        Pair<ArrayList<Station>, Integer> path = null;
        if (displayingLeastExchangePath) path = msys.genLeastExchangePath(uName, vName, ioHandler.getExchangeTime());
        else if (displayingShortestPath) path = msys.genShortestPath(uName, vName);

        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(drawMetroSystem(path));

        final double[] mouseAnchor = new double[2];
        final double[] initialTranslate = new double[2];
        mapContainer.setOnMousePressed(e -> {
            mouseAnchor[0] = e.getSceneX();
            mouseAnchor[1] = e.getSceneY();
            initialTranslate[0] = mapContainer.getTranslateX();
            initialTranslate[1] = mapContainer.getTranslateY();
        });
        mapContainer.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - mouseAnchor[0];
            double dy = e.getSceneY() - mouseAnchor[1];
            mapContainer.setTranslateX(initialTranslate[0] + dx);
            mapContainer.setTranslateY(initialTranslate[1] + dy);
        });
    }

    @FXML
    protected Group drawMetroStation(Station s, boolean highlighted) {
        Group stationGroup = new Group();

        double x = s.getX();
        double y = s.getY();

        double stationWidth = 15.0;
        String stationText = "";
        Circle stn = new Circle(x, y, stationWidth);
        if (s instanceof Interchange) {
            HashMap<String,String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            for (Map.Entry<String, String> id: allLinesInfo.entrySet()) stationText += (id.getValue() + "/");
            if (stationText.length() >= 2) stationText = stationText.substring(0, stationText.length() - 1);
            stn.setStrokeWidth(3);
            stationWidth += 5;
            //stn.setEffect(stationEffectBlendStrokeColourWithFill); //TODO: station effects
        }
        else if (s instanceof SingleStation){ //s instanceof SingleStation
            stn.setStrokeWidth(2);
            stationText = s.getStationID();
        }

        stn.setFill(Color.WHITESMOKE);
        stn.setStroke(Color.BLACK);

        Text stationID = new javafx.scene.text.Text("[" + stationText + "] " + s.getName());
        stationID.setX(x + 15);
        stationID.setY(y + 5);

        stationGroup.getChildren().add(stationID);
        stationGroup.getChildren().add(stn);

        //mouse hover
        Tooltip tooltipStationName = new Tooltip(stationText);//s.getName());
        tooltipStationName.setShowDelay(Duration.minutes(0));
        Tooltip.install(stationGroup, tooltipStationName);

        //mouse drag
        final double[] offsetX = {0};
        final double[] offsetY = {0};
        stationGroup.setOnMousePressed(e -> {
            if (this.devMode) {
                offsetX[0] = e.getSceneX() - stationGroup.getLayoutX();
                offsetY[0] = e.getSceneY() - stationGroup.getLayoutY();
                e.consume();
            }
        });
        stationGroup.setOnMouseDragged(e -> {
            if (this.devMode) {
                stationGroup.setLayoutX(e.getSceneX() - offsetX[0]);
                stationGroup.setLayoutY(e.getSceneY() - offsetY[0]);
                e.consume();
            }
        });

        if (highlighted) {
            stationGroup.setEffect(highlightShadow);
            return stationGroup;
        }
        return stationGroup;
    }

    @FXML
    protected Group drawEdge(Station u, Station v, Group UGroup, Group VGroup) {
        if (u == null || v == null || UGroup == null || VGroup == null) throw new IllegalArgumentException("Troll off.");
        Group finalEdge = new Group();
        HashMap<Integer, Line> lines = new HashMap<>();

        int counter = 0;
        Line edgeLine = new Line(u.getX(), u.getY(), v.getX(), v.getY());
        edgeLine.setStrokeWidth(10);

        //Paint fillColour = ((Circle) node).getFill();
        //Color colour = (fillColour instanceof Color) ? (Color) fillColour : null;

        edgeLine.startXProperty().bindBidirectional(UGroup.layoutXProperty());
        edgeLine.startYProperty().bindBidirectional(UGroup.layoutYProperty());
        edgeLine.endXProperty().bindBidirectional(VGroup.layoutXProperty());
        edgeLine.endYProperty().bindBidirectional(VGroup.layoutYProperty());

        finalEdge.getChildren().add(edgeLine);
        edgeLine.toBack();
        return finalEdge;
    }

    @FXML
    protected Group drawMetroSystem(Pair<ArrayList<Station>, Integer> bestPath) {
        //clean UI
        currentSystem.getChildren().clear();

        ArrayList<Station> bestPathStations = (bestPath != null) ? bestPath.getKey(): null;
        int bestPathDistance = (bestPath != null) ? bestPath.getValue(): 0;
        boolean needHighlight = ((bestPathStations != null) && (!bestPathStations.isEmpty()));

        //global fields
        GraphHandler graph = msys.getGraph();
        int stationCnt = msys.getStationList().size();
        int nodeCnt = graph.getNodeCnt();
        HashSet<Pair<Integer, Integer>> adjListSystem[] = graph.getAdjList();
        HashMap<Integer, String> indexToStationNameMap = msys.getIndexToStationNameMap();
        HashMap<String, Station> stationNameToStationMap = msys.stationNameToStationMap();

        HashSet<Station> stations = new HashSet<>();
        stations.addAll(msys.getStationList());

        //locally created fields to assist graph plotting
        HashMap<Integer, Integer> stationIndexToLocalIndexMap = new HashMap<>();
        HashMap<Station, Integer> stationToLocalIndexMap = new HashMap<>();
        HashMap<String, Integer> stationNameToLocalIndexMap = new HashMap<>();
        HashMap<Integer, Station> localIndexToStationMap = new HashMap<>();
        HashMap<Integer, String> localIndexToStationNameMap = new HashMap<>();

        //generate localIndexToStationMaps
        int curIndex = 0;
        for (Station s: stations) {
            String stnNameLower = s.getName().toLowerCase();
            stationToLocalIndexMap.put(s, curIndex);
            stationNameToLocalIndexMap.put(stnNameLower, curIndex);

            for (Map.Entry<Integer, String> entry: indexToStationNameMap.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(s.getName())) {
                    int globalIndex = entry.getKey();
                    stationIndexToLocalIndexMap.put(globalIndex, curIndex);
                }
            }
            curIndex++;
        }

        for (Map.Entry<Station, Integer> entry: stationToLocalIndexMap.entrySet()) {
            localIndexToStationMap.put(entry.getValue(), entry.getKey());
        }

        //helper conversion stuff
        for (Map.Entry<Integer, Station> entry: localIndexToStationMap.entrySet()) {
            localIndexToStationNameMap.put(entry.getKey(), entry.getValue().getName());
        }

        //final conversion to non-repeat adjListLocal
        HashMap<Integer, Integer> adjListLocal[] = new HashMap[505]; //vlocalindex, weight, [ulocalindex]

        for (int i = 0; i < nodeCnt; i++) {
            int fromIndexSystem = i;
            String uName = indexToStationNameMap.get(fromIndexSystem).toLowerCase();
            int uLocalIndex = stationNameToLocalIndexMap.get(uName);
            if (adjListLocal[uLocalIndex] == null) {
                adjListLocal[uLocalIndex] = new HashMap<>();
            }

            for (Pair<Integer, Integer> destinationWeightEntry: adjListSystem[i]) {
                int toIndexSystem = destinationWeightEntry.getKey();
                int weight = destinationWeightEntry.getValue();
                String vName = indexToStationNameMap.get(toIndexSystem);
                int vLocalIndex = stationNameToLocalIndexMap.get(vName.toLowerCase());
                adjListLocal[uLocalIndex].put(vLocalIndex, weight);
            }
        }

        for (int i = 0; i < nodeCnt; i++) {
            System.out.println(adjListLocal[i]);
        }

        // draw all stations first
        HashMap<String, Group> stationGroups = new HashMap<>();
        for (Station s: stations) {
            Group stnGrp = drawMetroStation(s, needHighlight && bestPathStations.contains(s));
            stationGroups.put(s.getName(), stnGrp);
        }

        // bfs and plot stations and edges along the way
        Queue<String> q = new ArrayDeque<>();
        q.add((String) stationNameToStationMap.keySet().toArray()[0]);

        HashMap<Integer, Boolean> visited = new HashMap<>();
        for (Station s: stations) visited.put(stationToLocalIndexMap.get(s), false);
        visited.put(stationToLocalIndexMap.get(q.peek()), true);

        while (!q.isEmpty()) {
            String curStnName = q.poll();
            Station curStn = stationNameToStationMap.get(curStnName);
            int curStnLocalIndex = stationNameToLocalIndexMap.get(curStnName);
            Group curGroup = stationGroups.get(curStnName);

            for (Map.Entry<Integer, Integer> entry: adjListLocal[curStnLocalIndex].entrySet()) {
                int nxtIndex = entry.getKey();
                String nxtStnName = localIndexToStationNameMap.get(nxtIndex);
                Station nxtStn = stationNameToStationMap.get(nxtStnName);

                if (!visited.get(nxtIndex)) {
                    visited.put(nxtIndex, true);
                    q.add(nxtStnName);
                }

                Group nxtGroup = stationGroups.get(nxtStnName);
                Group edgeGroup = drawEdge(curStn, nxtStn, curGroup, nxtGroup);
                currentSystem.getChildren().add(edgeGroup);
                edgeGroup.toBack();
            }
        }

        for (Group g: stationGroups.values()) {
            currentSystem.getChildren().add(g);
        }

        return currentSystem;
    }

}