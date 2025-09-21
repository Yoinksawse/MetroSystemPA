package com.example.georgebra.Controller;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.InputHandler.IOHandler;
import com.example.georgebra.Model.LineTypes.MetroLine;
import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.MyApplication;
import javafx.scene.control.Alert.*;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
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
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    //outside fields
    @FXML
    public Button unshow_route;
    public Button clearmap_button;
    public Button compute_fastest_button;
    public Button compute_leastexchange_button;
    public Button open_infowindow_button;
    public Button loadmetrosystem_button;
    public Button save_button;
    public TextField from_station_input_field;
    public TextField to_station_input_field;
    public TextField search_area;
    public TextField map_city_choice;
    public StackPane map_container;
    public ToggleButton switch_devmode;
    public CheckBox displayedgeweight_checkbox;
    public Text selectedstation_field;
    public Text selectedline_field;
    public ToolBar selectedstation_infobox;

    //inside fields
    private StackPane mapContainer;
    private boolean devMode = false;
    private boolean isMapShown = false;
    private MetroSystem msys;
    private String systemID;
    private String cityName;
    private Group currentSystem = new Group();
    private boolean displayingShortestPath;
    private boolean displayingLeastExchangePath;
    private boolean showEdgeWeight;
    private TextField currentlyFocusedTextField;

    private IOHandler ioHandler;
    private String uName;
    private String vName;
    private HashMap<String,String> lineNameToColourMap;
    private HashMap<String,Boolean> orientHorizontallyMap;
    private Pair<ArrayList<Station>, Integer> bestPathThis;
    private Station searchedStation;

    public final DropShadow highlightShadow =
            new DropShadow(30, Color.rgb(255, 125, 125, 0.99));
    private final EventHandler<MouseEvent> preventStationEditFilter = e -> {
        if (e.getTarget() instanceof Group || e.getTarget() instanceof Circle) e.consume();
    };
    private final double gridSize = 20.0;

    //TODO: ABSTRACT CLASS NEED ABSTRACT METHODSSSS
    //TODO: Implement actual graph editing e.g. addstations etc

    public void initialize() {
        //clear & update some data
        searchedStation = null;
        mapContainer = map_container;
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        uName = null;
        vName = null;

        //get ready some basic eventhandling that will follow all the way
        mapContainer.setOnMouseClicked(e -> {
            if (msys != null && searchedStation != null) {
                clearStationInfo();
            }
            e.consume();
        });
        from_station_input_field.setOnAction(e -> {
            boolean stationExists = validateStationExistence(from_station_input_field.getText());
            if (!stationExists) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Invalid Station Name; Does not exist");
                alert.show();
            }

            to_station_input_field.requestFocus();
            to_station_input_field.positionCaret(0);
            e.consume();
        });
        to_station_input_field.setOnAction(e -> {
            boolean stationExists = validateStationExistence(to_station_input_field.getText());
            if (!stationExists) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Invalid Station Name; Does not exist");
                alert.show();
            }

            displayShortestPath();
            e.consume();
        });

        from_station_input_field.setOnKeyReleased(e -> {
            String stationName = from_station_input_field.getText();
            //System.out.println(stationName);

            Pattern stationNamePattern = Pattern.compile("^[\\D]{1,60}$");
            Matcher stationNameMatcher = stationNamePattern.matcher(stationName);

            if (stationNameMatcher.matches()) {
                if (from_station_input_field.getStyleClass().contains("invalidTextBox")){
                    from_station_input_field.getStyleClass().removeAll("invalidTextBox");
                }
                from_station_input_field.getStyleClass().removeAll("validTextBox");
                from_station_input_field.getStyleClass().add("validTextBox");
            }
            else {
                if (from_station_input_field.getStyleClass().contains("validTextBox")){
                    from_station_input_field.getStyleClass().removeAll("validTextBox");
                }
                from_station_input_field.getStyleClass().removeAll("invalidTextBox");
                from_station_input_field.getStyleClass().add("invalidTextBox");
            }

            e.consume();
        });
        to_station_input_field.setOnKeyReleased(e -> {
            String stationName = to_station_input_field.getText();
            //System.out.println(stationName);

            Pattern stationNamePattern = Pattern.compile("^[\\D]{1,60}$");
            Matcher stationNameMatcher = stationNamePattern.matcher(stationName);

            if (stationNameMatcher.matches()) {
                if (to_station_input_field.getStyleClass().contains("invalidTextBox")){
                    to_station_input_field.getStyleClass().removeAll("invalidTextBox");
                }
                to_station_input_field.getStyleClass().removeAll("validTextBox");
                to_station_input_field.getStyleClass().add("validTextBox");
            }
            else {
                if (to_station_input_field.getStyleClass().contains("validTextBox")){
                    to_station_input_field.getStyleClass().removeAll("validTextBox");
                }
                to_station_input_field.getStyleClass().removeAll("invalidTextBox");
                to_station_input_field.getStyleClass().add("invalidTextBox");
            }

            e.consume();
        });

        from_station_input_field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) currentlyFocusedTextField = from_station_input_field;
            if (!newValue && currentlyFocusedTextField.equals(from_station_input_field))
                currentlyFocusedTextField = null;
        });

        to_station_input_field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) currentlyFocusedTextField = to_station_input_field;
            if (!newValue && currentlyFocusedTextField.equals(to_station_input_field))
                currentlyFocusedTextField = null;
        });

        //setting initial access rights
        {
            map_city_choice.clear();
            from_station_input_field.clear();
            search_area.clear();
            to_station_input_field.clear();
            loadmetrosystem_button.setDisable(false);
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false);
            save_button.setDisable(true);
            unshow_route.setDisable(true);
            compute_fastest_button.setDisable(true);
            compute_leastexchange_button.setDisable(true); //assuming in this case, all routes are cleared
            clearmap_button.setDisable(true);
            from_station_input_field.setDisable(true);
            search_area.setDisable(true);
            to_station_input_field.setDisable(true);
            displayedgeweight_checkbox.setDisable(true);
        }

        //set helping tooltips
        Tooltip switchEditingModeButtonExplain =
                new Tooltip("Editing Mode: Enable customisation of your map!\n" +
                        "User Mode: Enable usage of graph to compute and plan routes.");
        switchEditingModeButtonExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(switch_devmode, switchEditingModeButtonExplain);

        Tooltip computeFastestRouteButtonExplain = new Tooltip("Find the fastest route between 2 stations");
        computeFastestRouteButtonExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(compute_fastest_button, computeFastestRouteButtonExplain);

        Tooltip computeLeastExchangesButtonExplain = new Tooltip("Find the route between 2 stations\n" +
                "requiring least interchange exchanges (for lazy people)");
        computeLeastExchangesButtonExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(compute_leastexchange_button, computeLeastExchangesButtonExplain);

        Tooltip clearMapButtonExplain = new Tooltip("Clear Loaded Metro System");
        clearMapButtonExplain.setShowDelay(Duration.seconds(1));
        Tooltip.install(clearmap_button, clearMapButtonExplain);

        Tooltip saveMapButtonExplain = new Tooltip("[Only in Editing Mode] Save Edited Metro Map");
        saveMapButtonExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(save_button, saveMapButtonExplain);

        Tooltip loadMetroSystemButtonExplain = new Tooltip("Load/Reload Data of Given Metro System");
        loadMetroSystemButtonExplain.setShowDelay(Duration.seconds(0.01));
        Tooltip.install(loadmetrosystem_button, loadMetroSystemButtonExplain);

        String clearSelectedString = "Press anywhere on the \nMAP to clear your selection";
        Tooltip howToClearSelectedStationExplain = new Tooltip(clearSelectedString);
        howToClearSelectedStationExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(selectedstation_infobox, howToClearSelectedStationExplain);

        Tooltip fromStationFieldExplain = new Tooltip("Right click this field and select a station!");
        fromStationFieldExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(from_station_input_field, fromStationFieldExplain);

        Tooltip toStationFieldExplain = new Tooltip("Right click this field and select a station!");
        toStationFieldExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(to_station_input_field, toStationFieldExplain);

        String availableCities = "None";
        try {
            availableCities = IOHandler.getAvailableCities();
        } catch (IOException e) { //TODO
            System.err.println("Fatal Error: Home Directory not found. Filesystem is incompatible.");
        }
        Tooltip mapCityChoiceButtonExplain = new Tooltip("Load a Metro System! Detected: \n" + availableCities);
        loadMetroSystemButtonExplain.setShowDelay(Duration.seconds(0.1));
        Tooltip.install(map_city_choice, mapCityChoiceButtonExplain);
    }

    @FXML
    public void getInput() {
        //systemID = map_city_choice.getText();
        cityName = map_city_choice.getText();
        try {
            ioHandler = new IOHandler(cityName);
            this.msys = ioHandler.getMetroSystem();
        }
        catch (IllegalStateException | InvalidAlgorithmParameterException | InvalidAttributesException e) {
            showAlert("Input Exception! \n" + e, AlertType.ERROR);
        }

        if (this.msys != null) {
            compute_fastest_button.setDisable(false);
            compute_leastexchange_button.setDisable(false);

            lineNameToColourMap = MetroLine.getLineNameToColourMap();//msys.getMetroLineNameToColourMap();
            //System.out.println(MetroLine.getLineNameToColourMap());
            //System.out.println(lineNameToColourMap);
            orientHorizontallyMap = new HashMap<>();
            for (Station s: msys.getStationList()) {
                if (s instanceof Interchange) {
                    orientHorizontallyMap.put(s.getName(), false);
                }
            }

            search_area.setOnAction(e -> {
                showSearchedStation();
            });

            displayMap();
        }
    }

    @FXML
    public void saveFile() {
        ioHandler.setMetroSystem(this.msys); //update
        for (Node node: mapContainer.getChildren()) {
            try {
                Station station = (Station) node.getUserData();
                station.setX(node.getLayoutX());
                station.setY(node.getLayoutY());
            }
            catch (NullPointerException e) {
                System.out.println("Expected skipping of non-station circle groups occurred.");
            }
        }

        ioHandler.writeAll(this.msys);
    }

    @FXML
    protected void switchMode() {
        if (devMode) {
            devMode = false;
            switch_devmode.setText("Switch to Editing Mode");

            //ENTER USER MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            unshow_route.setDisable(true); //assuming in this case, all routes are cleared
            compute_fastest_button.setDisable(true); //assuming in this case, all routes are cleared
            compute_leastexchange_button.setDisable(true); //assuming in this case, all routes are cleared
            clearmap_button.setDisable(true);
            save_button.setDisable(true);

            from_station_input_field.setDisable(!isMapShown);
            compute_fastest_button.setDisable(!isMapShown);
            compute_leastexchange_button.setDisable(!isMapShown);
            unshow_route.setDisable(!displayingShortestPath);
            search_area.setDisable(msys != null);
            to_station_input_field.setDisable(!isMapShown);

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
            save_button.setDisable(false);

            from_station_input_field.setDisable(true);
            compute_fastest_button.setDisable(true);
            compute_leastexchange_button.setDisable(true);
            unshow_route.setDisable(true);
            search_area.setDisable(msys != null);
            to_station_input_field.setDisable(true);

            if (displayingShortestPath || displayingLeastExchangePath) clearPath();
            displayingShortestPath = false;
            displayingLeastExchangePath = false;
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
        map_city_choice.clear();

        mapContainer.getChildren().clear();
    }

    @FXML
    protected void clearPath() {
        displayingShortestPath = false;
        displayingLeastExchangePath = false;

        from_station_input_field.getStyleClass().removeAll("invalidTextBox");
        from_station_input_field.getStyleClass().removeAll("validTextBox");
        to_station_input_field.getStyleClass().removeAll("invalidTextBox");
        to_station_input_field.getStyleClass().removeAll("validTextBox");
        from_station_input_field.clear();
        to_station_input_field.clear();
        bestPathThis = null;
        displayMap();
    }

    @FXML
    protected void displayShortestPath() {
        displayingShortestPath = true;
        isMapShown = true;
        displayMap();
        unshow_route.setDisable(false);
        clearmap_button.setDisable(false);
    }

    @FXML
    protected void displayLeastExchangePath() {
        saveFile();
        displayingLeastExchangePath = true;
        isMapShown = true;
        int prevExchangeTime = this.ioHandler.getExchangeTime();
        try {
            this.ioHandler.setExchangeTime(10000);
            this.msys = ioHandler.generateMetroSystem();

            displayMap();
            unshow_route.setDisable(false);

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
        if (ioHandler == null) {
            //this should not happen though
            Alert x = new Alert(Alert.AlertType.ERROR);
            x.setContentText("Map has not been loaded yet");
            x.show();
        }
        isMapShown = true;

        from_station_input_field.setDisable(false);
        search_area.setDisable(false);
        to_station_input_field.setDisable(false);
        displayedgeweight_checkbox.setDisable(false);
        showEdgeWeight = displayedgeweight_checkbox.isSelected();

        uName = from_station_input_field.getText();
        vName = to_station_input_field.getText();
        Pair<ArrayList<Station>, Integer> path = null;

        msys = (msys == null) ? ioHandler.getMetroSystem() : this.msys;
        if (displayingLeastExchangePath) path = msys.genLeastExchangePath(uName, vName, ioHandler.getExchangeTime());
        else if (displayingShortestPath) path = msys.genShortestPath(uName, vName);

        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(drawMetroSystem(path, searchedStation));

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
    protected Group drawMetroStation(Station s, boolean highlighted, boolean searched) {
        Group stationGroup = new Group();

        double x = s.getX();
        double y = s.getY();

        double stationWidth = 15.0;
        String stationIDText = "";
        Circle stn = new Circle(0, 0, stationWidth); //0 0 within its group's position

        if (s instanceof Interchange) {
            HashMap<String,String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            for (Map.Entry<String, String> id: allLinesInfo.entrySet()) stationIDText += (id.getValue() + "/");
            if (stationIDText.length() >= 2) stationIDText = stationIDText.substring(0, stationIDText.length() - 1);
            stn.setStrokeWidth(3);
            stationWidth += 5;
        }
        else if (s instanceof SingleStation){ //s instanceof SingleStation
            stn.setStrokeWidth(2);
            stationIDText = s.getStationID();
        }

        setCircleColour(stn, highlighted);
        if (searched) {
            FillTransition ft = new FillTransition(Duration.seconds(0.5), stn);
            ft.setFromValue(Color.web("#4D8D6A"));
            ft.setToValue(Color.WHITESMOKE);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
        }

        //handle text
        Label stationID = new Label(s.getName().substring(0, 1).toUpperCase()
                        + s.getName().substring(1).toLowerCase()
        );
        stationID.setTextFill(Color.BLACK);
        stationID.setStyle("-fx-background-color: rgba(240, 240, 240, 0.69);");

        stationID.setLayoutX(s.getTextX());
        stationID.setLayoutY(s.getTextY());
        //stationID.setLayoutX(0 + stationWidth + 10);
        //stationID.setLayoutY(0 + 5); //x and y with respect to its group's position
        stationID.toFront();

        stationGroup.getChildren().add(stationID);
        stationGroup.getChildren().add(stn);
        stationGroup.setLayoutX(x); //layoutx is position of the group
        stationGroup.setLayoutY(y);
        stationGroup.setUserData(s);

        //mouse hover
        Tooltip tooltipStation = new Tooltip(stationIDText); //s.getName());
        tooltipStation.setShowDelay(Duration.minutes(0));
        Tooltip.install(stationGroup, tooltipStation);

        //mouse drag for station groups
        final double[] offsetX = {0};
        final double[] offsetY = {0};
        final boolean[] dragged = {false};
        stationGroup.setOnMousePressed(e -> {
            if (this.devMode) {
                offsetX[0] = e.getSceneX() - stationGroup.getLayoutX();
                offsetY[0] = e.getSceneY() - stationGroup.getLayoutY();
                e.consume();
            }
        });
        stationGroup.setOnMouseDragged(e -> {
            if (this.devMode) {
                dragged[0] = true;
                double newX = e.getSceneX() - offsetX[0];
                double newY = e.getSceneY() - offsetY[0];

                stationGroup.setLayoutX(newX);
                stationGroup.setLayoutY(newY);
                e.consume();

                Station station = (Station) stationGroup.getUserData();
                if (station != null) {
                    station.setX(newX);
                    station.setY(newY);
                }
            }
        });
        stationGroup.setOnMouseReleased(e -> {
            if (!dragged[0]) return;
            //virtual grid so it is more tidy :#3
            double newX = e.getSceneX() - offsetX[0];
            double newY = e.getSceneY() - offsetY[0];
            newX = Math.round(newX / gridSize) * gridSize;
            newY = Math.round(newY / gridSize) * gridSize;

            stationGroup.setLayoutX(newX);
            stationGroup.setLayoutY(newY);

            Station station = (Station) stationGroup.getUserData();
            if (station != null) {
                station.setX(newX);
                station.setY(newY);
            }
            e.consume();

            dragged[0] = false;
        });

        //mouse drag for tooltip
        final double[] offsetXIDText = {0};
        final double[] offsetYIDText = {0};
        final double[] newXEFFFINAL = {0};
        final double[] newYEFFFINAL = {0};
        final double[] stationWidthEffective = {stationWidth};
        stationID.setOnMousePressed(e -> {
            offsetXIDText[0] = e.getSceneX() - stationID.getLayoutX();
            offsetYIDText[0] = e.getSceneY() - stationID.getLayoutY();
            e.consume();
        });
        stationID.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - offsetXIDText[0];
            double newY = e.getSceneY() - offsetYIDText[0];
            //double newX = e.getX();
            //double newY = e.getY();

            newXEFFFINAL[0] = (stationID.getLayoutX() - stn.getCenterX());
            newYEFFFINAL[0] = (stationID.getLayoutY() - stn.getCenterY());

            double maxExtensionPositiveX = (stationWidthEffective[0]);
            double maxExtensionPositiveY = (2 * stationWidthEffective[0]);
            if (-(maxExtensionPositiveY * 3) < newX && newX < maxExtensionPositiveX){
                stationID.setLayoutX(newX); // a bit weird and problematic, i tried to make as even as possible
            }
            if (-maxExtensionPositiveY < newY && newY < maxExtensionPositiveY){
                stationID.setLayoutY(newY); // a bit weird and problematic, i tried to make as even as possible
            }
            e.consume();
        });
        stationID.setOnMouseReleased(e -> {
            Station station = (Station) stationGroup.getUserData();
            if (station != null) {
                station.setTextX(newXEFFFINAL[0]);
                station.setTextY(newYEFFFINAL[0]);
            }
            e.consume();
        });

        if (highlighted) {
            stationGroup.setEffect(highlightShadow);
            return stationGroup;
        }

        stationGroup.setOnMouseClicked(e -> {
            if (devMode) return;
            if (!e.getEventType().equals(MouseEvent.MOUSE_CLICKED)) return;
            Station searchedStationLocal = (Station) ((Group) e.getSource()).getUserData();
            if (searchedStationLocal == null) throw new IllegalStateException("A station must exist in a group");
            showSearchedStation(searchedStationLocal);
            e.consume();
        });
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
    protected Group drawMetroSystem(Pair<ArrayList<Station>, Integer> bestPath, Station searched) {
        //clean UI
        currentSystem.getChildren().clear();
        this.bestPathThis = bestPath;

        ArrayList<Station> bestPathStations = (bestPath != null) ? bestPath.getKey(): null;
        int bestPathDistance = (bestPath != null) ? bestPath.getValue(): 0;
        boolean needHighlight = ((bestPathStations != null) && (!bestPathStations.isEmpty()));

        //global fields
        GraphHandler graph = msys.getGraphHandler();
        int stationCnt = msys.getStationList().size();
        int nodeCnt = graph.getNodeCnt();
        HashMap<Integer, Integer> adjListSystem[] = graph.getAdjList();
        HashMap<Integer, String> indexToStationNameMap = msys.getIndexToStationNameMap();
        HashMap<String, Station> stationNameToStationMap = msys.getStationNameToStationMap();

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

            for (Map.Entry<Integer, Integer> destinationWeightEntry: adjListSystem[i].entrySet()) {
                int toIndexSystem = destinationWeightEntry.getKey();
                int weight = destinationWeightEntry.getValue();
                String vName = indexToStationNameMap.get(toIndexSystem);
                int vLocalIndex = stationNameToLocalIndexMap.get(vName.toLowerCase());
                adjListLocal[uLocalIndex].put(vLocalIndex, weight);
            }
        }

        //for (int i = 0; i < nodeCnt; i++) System.out.println(adjListLocal[i]);

        HashSet<String> bestPathStationNames = new HashSet<>();
        if (bestPathStations != null) {
            for (Station s: bestPathStations) {
                bestPathStationNames.add(s.getName().toLowerCase());
            }
        }

        // draw all stations first
        HashMap<String, Group> stationGroups = new HashMap<>();
        for (Station s: stations) {
            boolean beingSearched = (searched != null) ? (s.getName().equalsIgnoreCase(searched.getName())) : false;

            boolean shouldHighlight = needHighlight && bestPathStationNames.contains(s.getName().toLowerCase());
            Group stnGrp = drawMetroStation(s, shouldHighlight, beingSearched);
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

    private void showSearchedStation(Station s) {
        if (msys == null) {
            //System.out.println("fudge is delicious");
            return;
        }

        if (!(msys.getStationList().contains(s))) {
            Alert noSuchStationAlert = new Alert(AlertType.INFORMATION);
            noSuchStationAlert.setContentText("Station not found!");
            noSuchStationAlert.show();
            //System.out.println("what");
            return;
        }

        if (currentlyFocusedTextField != null) {
            currentlyFocusedTextField.setText(s.getName());
        }

        searchedStation = s;
        showStationInfo(s);
        displayMap();
    }
    private void showSearchedStation() {
        if (msys == null) return;
        String stationName = search_area.getText();
        HashMap<String, Station> stationNameToStationMap = msys.getStationNameToStationMap();
        Station s = stationNameToStationMap.get(stationName.trim().toLowerCase());
        this.showSearchedStation(s);
    }

    public void showStationInfo(Station s) {
        //System.out.println(s);
        if (s instanceof Interchange) {
            String lineNames = "";
            HashMap<String, String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            for (Map.Entry<String, String> entry: allLinesInfo.entrySet()) {
                String cur = entry.getKey() + " (" + entry.getValue() + "), ";
                lineNames += cur;
            }
            if (lineNames.length() > 2) {
                lineNames = lineNames.substring(0, lineNames.length() - 2); //remove last 2 chars which are extra
            }

            lineNames = firstLettersToUpper(lineNames, " ");

            this.selectedstation_field.setText(firstLettersToUpper(s.getName(), " "));
            this.selectedline_field.setWrappingWidth(220);
            this.selectedline_field.setText(lineNames);
        }
        else {
            String lineNames = s.getLineName() + " (" + s.getStationID() + ")";
            lineNames = firstLettersToUpper(lineNames, " ");

            this.selectedstation_field.setText(firstLettersToUpper(s.getName(), " "));
            this.selectedline_field.setText(lineNames);
        }
    }

    public boolean validateStationExistence(String stationString) {
        HashMap<String, Station> stationNameToStationMap = this.msys.getStationNameToStationMap();
        Station s = stationNameToStationMap.get(stationString.toLowerCase());

        if (s == null) return false;
        return true;
    }

    public void clearStationInfo() {
        if (searchedStation != null) {
            search_area.clear();
            selectedstation_field.setText("");
            selectedline_field.setText("");
            searchedStation = null;
            displayMap();
        }
    }

    //more of utils
    public String firstLettersToUpper(String s, String delimiter) {
        String result = "";

        String [] segments = s.split(delimiter);
        for (String str: segments) {
            result += str.substring(0,1).toUpperCase() + str.substring(1, str.length()) + " ";
        }

        return result;
    }

    private void setCircleColour(Circle circle, boolean highlighted) {
        if (!highlighted) {
            circle.setStroke(Color.BLACK);
            //circle.setFill(Color.WHITESMOKE);
            circle.getStyleClass().add("notPathStation");
        }
        else if (highlighted) { // Blinking
            circle.setStroke(Color.BLACK);
            circle.setFill(Color.web("#4D8D6A"));
            circle.getStyleClass().add("pathStation");
        }
    }

    public void showAlert(String text, AlertType at){
        Alert alert = new Alert(at);
        alert.setContentText(text);
        alert.show();
    }
}