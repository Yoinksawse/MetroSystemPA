package com.example.RouteCrafter.Controller;

import com.example.RouteCrafter.Model.InputHandler.IOHandler;
import com.example.RouteCrafter.Model.LineTypes.MetroLine;
import com.example.RouteCrafter.Model.MetroSystem;
import com.example.RouteCrafter.Model.StationTypes.Interchange;
import com.example.RouteCrafter.Model.StationTypes.Station;
import com.example.RouteCrafter.MyApplication;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert.*;
import javafx.animation.FillTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    //outside fields
    @FXML
    public StackPane map_container;
    public VBox rootVBox, lineinfos_vbox;
    public ToolBar selectedstation_infobox;
    public GridPane rootGrid;
    public ColumnConstraints mapDisplayColumnConstraints ;
    public Button unshow_route, clearmap_button, compute_fastest_button, compute_leastexchange_button, open_infowindow_button, loadmetrosystem_button, save_button;
    public ToggleButton switch_devmode;
    public CheckBox darkmode_checkbox, displayedgeweight_checkbox;
    public TextField from_station_input_field, to_station_input_field, search_area, map_city_choice;
    public Text selectedstation_field, selectedline_field, fastestroutetime_textfield, textA, textB, textC, textD;

    //inside fields
    protected TextField currentlyFocusedTextField; //HOLDERS FOR SAVING STATE
    protected boolean devMode = false;
    protected boolean isMapShown = false;
    protected boolean showEdgeWeight = false;
    protected boolean displayingShortestPath;
    protected boolean displayingLeastExchangePath;
    protected Station searchedStation;

    protected StackPane mapContainer; //CONTAINERS FOR DATA
    protected Group currentSystem = new Group();
    protected IOHandler ioHandler;
    protected MetroSystem msys;
    protected String systemID;
    protected String cityName;
    protected String uName;
    protected String vName;
    protected int averageExchangeTime;

    protected HashMap<String,String> lineNameToColourMap; //MAPPING FIELDS
    protected HashMap<String, Group> stationNameToCurrentGroupMap;
    protected Pair<ArrayList<Station>, Integer> bestPathThis;
    protected HashMap<String, String> metroLineNameToTypeMap = new HashMap<>();
    protected HashMap<String, FillTransition> stationNameToFillTransitionMap;

    protected final double gridSize = 20.0;
    protected static final double MAX_EDGE_WIDTH = 10;
    protected Pattern stationNamePattern = Pattern.compile("^[\\D]{1,60}$");
    protected final EventHandler<MouseEvent> preventStationEditFilter = e -> {
        if (e.getTarget() instanceof Group || e.getTarget() instanceof Circle) e.consume();
    };

    protected final UtilitiesHandler utils = new UtilitiesHandler(this);

    //MAINCONTROLLER is meant for relatively more GUI-related operations & event handling
    @FXML
    public void initialize() {
        //clear & update some data
        searchedStation = null;
        mapContainer = map_container;
        lineinfos_vbox.setPrefWidth(346);
        lineinfos_vbox.setMinWidth(346);
        lineinfos_vbox.setMaxWidth(346);
        lineinfos_vbox.setPickOnBounds(false);
        displayingShortestPath = false;
        displayingLeastExchangePath = false;

        Bindings.createDoubleBinding(() -> rootVBox.widthProperty().get(), rootGrid.widthProperty());

        showEdgeWeight = displayedgeweight_checkbox.isSelected();
        stationNameToCurrentGroupMap = new HashMap<>();
        stationNameToFillTransitionMap = new HashMap<>();
        uName = null;
        vName = null;
        showEdgeWeight = false;
        mapContainer.setMinHeight(3000);
        mapContainer.setMinWidth(3000);
        mapContainer.setMaxHeight(8888);
        mapContainer.setMaxWidth(8888);

        //get ready some basic eventhandling that will follow all the way
        mapContainer.setOnMouseClicked(e -> {
            if (msys != null && searchedStation != null) { mapContainer.requestFocus(); clearStationInfo(); }
        });
        compute_fastest_button.setOnMouseClicked(e -> {
            boolean stationExists = utils.validateStationExistence(from_station_input_field.getText());
            if (!stationExists) { utils.showAlert("Invalid Station Name; Does not exist", AlertType.ERROR); return; }
            to_station_input_field.requestFocus();
            to_station_input_field.positionCaret(0);
        });
        compute_leastexchange_button.setOnMouseClicked(e -> {
            boolean stationExists = utils.validateStationExistence(from_station_input_field.getText());
            if (!stationExists) { utils.showAlert("Invalid Station Name; Does not exist", AlertType.ERROR); return; }
            to_station_input_field.requestFocus();
            to_station_input_field.positionCaret(0);
        });

        from_station_input_field.setOnAction(e -> {
            boolean stationExists = utils.validateStationExistence(from_station_input_field.getText());
            if (!stationExists) { utils.showAlert("Invalid Station Name; Does not exist", AlertType.ERROR); return; }
            to_station_input_field.requestFocus();
            to_station_input_field.positionCaret(0);
        });
        to_station_input_field.setOnAction(e -> {
            boolean stationExists = utils.validateStationExistence(to_station_input_field.getText());
            if (!stationExists) { utils.showAlert("Invalid Station Name; Does not exist", AlertType.ERROR); return; }
            displayShortestPath();
        });

        from_station_input_field.setOnKeyReleased(e -> { validateAndHandleFrom(); e.consume();});
        to_station_input_field.setOnKeyReleased(e -> { validateAndHandleTo(); e.consume();});

        from_station_input_field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) currentlyFocusedTextField = from_station_input_field;
            if (!newValue && currentlyFocusedTextField.equals(from_station_input_field)) currentlyFocusedTextField = null;
            validateAndHandleFrom();
        });

        to_station_input_field.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) currentlyFocusedTextField = to_station_input_field;
            if (!newValue && currentlyFocusedTextField.equals(to_station_input_field)) currentlyFocusedTextField = null;
            validateAndHandleTo();
        });

        displayedgeweight_checkbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) { showEdgeWeight = true; displayMap(showEdgeWeight); }
            if (!isSelected) { showEdgeWeight = false; displayMap(showEdgeWeight); }
        });

        darkmode_checkbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                MyApplication.setDarkMode();
                selectedstation_field.setFill(Color.WHITESMOKE);
                selectedline_field.setFill(Color.WHITESMOKE);
                fastestroutetime_textfield.setFill(Color.WHITESMOKE);
                textA.setFill(Color.WHITESMOKE);
                textB.setFill(Color.WHITESMOKE);
                textC.setFill(Color.WHITESMOKE);
                textD.setFill(Color.WHITESMOKE);
            }
            else {
                MyApplication.setLightMode();
                selectedstation_field.setFill(Color.BLACK);
                selectedline_field.setFill(Color.BLACK);
                fastestroutetime_textfield.setFill(Color.BLACK);
                textA.setFill(Color.BLACK);
                textB.setFill(Color.BLACK);
                textC.setFill(Color.BLACK);
                textD.setFill(Color.BLACK);
            }
        });

        {
            //setting initial access rights
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
            clearmap_button.setDisable(msys != null);
            from_station_input_field.setDisable(true);
            search_area.setDisable(true);
            to_station_input_field.setDisable(true);
            clearmap_button.setDisable(true);
            displayedgeweight_checkbox.setDisable(true);
            displayedgeweight_checkbox.setDisable(false);
        }

        //set helping tooltips
        utils.installTooltip(switch_devmode, "Editing Mode: Enable customisation of your map!\n" +
                "User Mode: Enable usage of graph to compute and plan routes.", 0.5);
        /*
        Tooltip switchEditingModeButtonExplain =
                new Tooltip("Editing Mode: Enable customisation of your map!\n" +
                        "User Mode: Enable usage of graph to compute and plan routes.");
        switchEditingModeButtonExplain.setShowDelay(Duration.seconds(0.5));
        Tooltip.install(switch_devmode, switchEditingModeButtonExplain);
         */

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
        } catch (IOException e) {
            System.err.println("Fatal Error: Home Directory not found. Filesystem is incompatible.");
        }
        Tooltip mapCityChoiceButtonExplain = new Tooltip("Load a Metro System! Detected: \n" + availableCities);
        loadMetroSystemButtonExplain.setShowDelay(Duration.seconds(0.1));
        Tooltip.install(map_city_choice, mapCityChoiceButtonExplain);
    }

    @FXML
    protected void showAboutMe() {
        MyApplication.showAboutMe();
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
            clearmap_button.setDisable(msys != null);
            save_button.setDisable(true);

            from_station_input_field.setDisable(!isMapShown);
            compute_fastest_button.setDisable(!isMapShown);
            compute_leastexchange_button.setDisable(!isMapShown);
            unshow_route.setDisable(!displayingShortestPath);
            search_area.setDisable(msys == null);
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
            clearmap_button.setDisable(msys != null);
            save_button.setDisable(false);

            from_station_input_field.setDisable(true);
            compute_fastest_button.setDisable(true);
            compute_leastexchange_button.setDisable(true);
            unshow_route.setDisable(true);
            search_area.setDisable(msys == null);
            to_station_input_field.setDisable(true);

            if (displayingShortestPath || displayingLeastExchangePath) clearPath();
            displayingShortestPath = false;
            displayingLeastExchangePath = false;
            mapContainer.getProperties().put("preventStationEditFilter", preventStationEditFilter);
        }
    }

    @FXML
    public void getInput() {
        lineinfos_vbox.getChildren().clear();
        //systemID = map_city_choice.getText();
        cityName = map_city_choice.getText();
        try {
            ioHandler = new IOHandler(cityName);
            this.msys = ioHandler.getMetroSystem();

            averageExchangeTime = ioHandler.getExchangeTime();
        }
        catch (InvalidAlgorithmParameterException | InvalidAttributesException e) {
            System.out.println("Input Exception! \n" + e);
        }
        catch (IllegalStateException e) {
            try {
                String availableCities = IOHandler.getAvailableCities();
                utils.showAlert("Metro System not found. Detected: \n" + availableCities, AlertType.WARNING);
            } catch (IOException ex) { }
        }

        if (this.msys != null) {
            clearPath();

            if (devMode) {
                unshow_route.setDisable(true);
                compute_fastest_button.setDisable(true);
                compute_leastexchange_button.setDisable(true);
                from_station_input_field.setDisable(true);
                to_station_input_field.setDisable(true);
            }
            else {
                unshow_route.setDisable(false);
                compute_fastest_button.setDisable(false);
                compute_leastexchange_button.setDisable(false);
                from_station_input_field.setDisable(false);
                to_station_input_field.setDisable(false);
            }
            clearmap_button.setDisable(false);

            lineNameToColourMap = MetroLine.getLineNameToColourMap();//msys.getMetroLineNameToColourMap();

            search_area.setOnKeyReleased(e -> {
                String searchedText = search_area.getText().trim().toLowerCase();
                if (searchedText.isBlank()) { clearStationInfo(); return; }
                showSearchedStations();
            });

            metroLineNameToTypeMap.clear();
            ArrayList<MetroLine> mLines = msys.getLineList();
            for (MetroLine ml: mLines) metroLineNameToTypeMap.put(ml.getLineName().trim().toLowerCase(), ml.getLineType());
            for (MetroLine mtl: msys.getLineList()) utils.drawLineInfoBox(mtl, mtl.getLineColour());

            displayMap(showEdgeWeight);
            isMapShown = true;
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
    protected void displayShortestPath() {
        saveFile();
        displayingShortestPath = true;
        isMapShown = true;
        String uStr = from_station_input_field.getText();
        String vStr = to_station_input_field.getText();
        int prevExchangeTime = this.ioHandler.getExchangeTime();
        try { bestPathThis = msys.genShortestPath(uStr, vStr); }
        catch (IllegalArgumentException e) { utils.showAlert(e.toString(), AlertType.ERROR); return; }
        displayMap(showEdgeWeight);

        fastestroutetime_textfield.setText("Time required: " + bestPathThis.getValue() + "min");
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
            String uStr = from_station_input_field.getText();
            String vStr = to_station_input_field.getText();
            try { bestPathThis = msys.genLeastExchangePath(uStr, vStr, prevExchangeTime); }
            catch (IllegalArgumentException e) { utils.showAlert(e.toString(), AlertType.ERROR); return; }

            displayMap(showEdgeWeight);
            unshow_route.setDisable(false);

            this.ioHandler.setExchangeTime(prevExchangeTime);
            this.msys = ioHandler.generateMetroSystem();

            fastestroutetime_textfield.setText("Time required: " + bestPathThis.getValue() + "min");
        }
        catch (InvalidAlgorithmParameterException | InvalidAttributesException e) {
            utils.showAlert("Input Exception \n" + e, AlertType.ERROR);
        }
        finally { this.ioHandler.setExchangeTime(prevExchangeTime); }
    }

    @FXML
    protected void displayMap(boolean showEdgeWeight) {
        if (ioHandler == null) { utils.showAlert("Map has not been loaded yet", AlertType.ERROR); }
        isMapShown = true;

        from_station_input_field.setDisable(false);
        search_area.setDisable(false);
        to_station_input_field.setDisable(false);

        uName = from_station_input_field.getText();
        vName = to_station_input_field.getText();
        Pair<ArrayList<Station>, Integer> path = null;

        msys = (msys == null) ? ioHandler.getMetroSystem() : this.msys;
        if (displayingLeastExchangePath) {
            try { path = msys.genLeastExchangePath(uName, vName, averageExchangeTime); }
            catch (IllegalArgumentException e) { utils.showAlert(e.toString(), AlertType.ERROR); return; }
        }
        else if (displayingShortestPath) {
            try { path = msys.genShortestPath(uName, vName); }
            catch (IllegalArgumentException e) { utils.showAlert(e.toString(), AlertType.ERROR); return; }
        }
        if (path != null) bestPathThis = path;

        if (bestPathThis != null)
            this.fastestroutetime_textfield.setText("Time required: " + bestPathThis.getValue() + "min");

        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(utils.drawMetroSystem(path, searchedStation, showEdgeWeight));

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
    protected void clearMap() {
        isMapShown = false;
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        map_city_choice.clear();
        clearmap_button.setDisable(true);
        msys = null;
        isMapShown = false;

        lineinfos_vbox.getChildren().clear();
        mapContainer.getChildren().clear();
        from_station_input_field.clear();
        to_station_input_field.clear();
        fastestroutetime_textfield.setText("");

        stationNameToCurrentGroupMap = new HashMap<>();
    }
    @FXML
    protected void clearPath() {
        displayingShortestPath = false;
        displayingLeastExchangePath = false;
        bestPathThis = null;

        from_station_input_field.getStyleClass().removeAll("invalidTextBox");
        from_station_input_field.getStyleClass().removeAll("validTextBox");
        to_station_input_field.getStyleClass().removeAll("invalidTextBox");
        to_station_input_field.getStyleClass().removeAll("validTextBox");
        from_station_input_field.clear();
        to_station_input_field.clear();
        fastestroutetime_textfield.setText("");

        if (isMapShown) {
            displayMap(showEdgeWeight);
        }
    }

    public void clearStationInfo() {
        if (searchedStation != null) {
            search_area.clear();
            selectedstation_field.setText("");
            selectedline_field.setText("");
            searchedStation = null;

            for (FillTransition ft: stationNameToFillTransitionMap.values()) {
                ft.stop();
                Circle stn = (Circle) ft.getShape();
                utils.setCircleColour(stn, false);
            }
        }
    }

    public void validateAndHandleFrom() {
        String stationName = from_station_input_field.getText();
        //System.out.println(stationName);

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
    }
    public void validateAndHandleTo() {
        String stationName = to_station_input_field.getText();
        //System.out.println(stationName);

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
    }

    protected void showSearchedStation(Station s) {
        if (msys == null) return;
        if (!(msys.getStationList().contains(s))) { utils.showAlert("Station not found!", Alert.AlertType.WARNING); return; }

        if (currentlyFocusedTextField != null) currentlyFocusedTextField.setText(s.getName());

        searchedStation = s;
        showStationInfo(s);

        stationNameToFillTransitionMap.get(s.getName()).play();
    }
    protected void showSearchedStations() {
        if (msys == null) return;
        String stationName = search_area.getText().trim().toLowerCase();
        HashMap<String, Station> stationNameToStationMap = msys.getStationNameToStationMap();

        for (String searchedName: stationNameToStationMap.keySet()) {
            if (searchedName.toLowerCase().startsWith(stationName.toLowerCase())) {
                showSearchedStation(stationNameToStationMap.get(searchedName));
            }
            else {
                stationNameToFillTransitionMap.get(searchedName).stop();
                List<Node> nodes = stationNameToCurrentGroupMap.get(searchedName).getChildren();
                for (Node node: nodes) {
                    if (node instanceof Circle) utils.setCircleColour(((Circle) node), false);
                }
            }
        }
    }
    public void showStationInfo(Station s) {
        if (s instanceof Interchange) {
            String lineNames = "";
            HashMap<String, String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            for (Map.Entry<String, String> entry: allLinesInfo.entrySet()) {
                String cur = entry.getKey() + " (" + entry.getValue() + "), ";
                lineNames += cur;
            }
            if (lineNames.length() > 2) lineNames = lineNames.substring(0, lineNames.length() - 2); //remove last 2 chars which are extra

            lineNames = utils.firstLettersToUpper(lineNames, " ");

            selectedstation_field.setText(utils.firstLettersToUpper(s.getName(), " "));
            selectedline_field.setWrappingWidth(220);
            selectedline_field.setText(lineNames);
        }
        else {
            String lineNames = s.getLineName() + " (" + s.getStationID() + ")";
            lineNames = utils.firstLettersToUpper(lineNames, " ");

            selectedstation_field.setText(utils.firstLettersToUpper(s.getName(), " "));
            selectedline_field.setText(lineNames);
        }
    }
}