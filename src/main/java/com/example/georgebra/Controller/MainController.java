package com.example.georgebra.Controller;

import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.Model.StationTypes.SingleStation;
import com.example.georgebra.Model.StationTypes.Station;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.MyApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.nio.file.*;

public class MainController {
    //outside fields
    @FXML
    public Button clear_dijikstra;
    public Button run_dijkstra;
    public Button open_infowindow_button;
    public Button updatemap_button;
    public Button loadmetrosystem;
    public TextField from_station_input_area;
    public TextField search_area;
    public TextField to_station_input_area;
    public TextArea io_area;
    public TextField map_city_choice;
    public StackPane map_container;
    public ToggleButton switch_devmode;

    //inside fields
    private StackPane mapContainer;
    private boolean devMode = false;
    private boolean isMapShown = false;
    private MetroSystem msys;


    public void initialize() {
        //setting initial access rights
        {
            map_city_choice.clear();
            from_station_input_area.clear();
            search_area.clear();
            to_station_input_area.clear();
            io_area.clear();
            loadmetrosystem.setDisable(false);
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false);
            clear_dijikstra.setDisable(true);
            run_dijkstra.setDisable(true);
            updatemap_button.setDisable(true);
            from_station_input_area.setDisable(true);
            search_area.setDisable(true);
            to_station_input_area.setDisable(true);
            io_area.setDisable(true);
            //map_container.setDisable(true);
        }
    }

//    public void parseInput(String inputs) {
//        //input stations
//        //make id:station <stationname, x, y, interchangetruefalse> map
//        HashMap<String, Station> idToStation = new HashMap<>();
//        HashMap<String, String> idToLineName = new HashMap<>();
//
//        //input all lines (stations given as id, so need the map to get info back)
//        boolean gotAllStations = false, gotAllLines = false;
//        int stationCntLocal = 0;
//        Scanner s = new Scanner(inputs);
//        while (s.hasNext()) {
//            String curline = s.nextLine();
//            if (!curline.contains("-") && !gotAllLines) gotAllLines = true;
//            if (curline.contains("+") && !gotAllStations) gotAllStations = true;
//
//            int x = 0, y = 0;
//            if (!gotAllLines) {
//                //TODO: inputting a line of line info
//                String lineName;
//
//                String[] parts = curline.split(":");
//                String lineNameString = parts[0];
//                String lineIndexesString = parts[1];
//
//                if (lineNameString.contains("*")) {
//                    lineName = lineNameString.substring(2); //remove -*
//                    String[] stationIDs = parts[1].split(",");
//                    for (String id: stationIDs) {
//                        idToLineName.put(id, lineName);
//                    }
//                }
//                else {
//                    lineName = lineNameString.substring(1); //remove -
//                    idToLineName.put(lineIndexesString, lineName);
//                }
//            }
//            else if (!gotAllStations) {
//                //TODO: inputting a line of station info like:
//                //NS23:Somerset 0 100
//                //*NS22/TE14:Orchard 199 213
//
//                Station newStation;
//                stationCntLocal++;
//
//                //TODO: get the x and y
//                String[] parts = curline.split(":");
//                String stationIndexString = parts[0];
//                String stationNameString = parts[1];
//
//                if (stationIndexString.contains("*")) {
//                    //it is interchange
//                    String stationIndexes = stationIndexString.substring(1);
//                    String[] idParts = stationIndexes.split("/");
//
//                    ArrayList<Pair<String, String>> otherDifferentLinesInfo = new ArrayList<>();
//                    for (String stationID : idParts) {
//                        String actualIDCase = "";
//                        char[] stationIDChars = stationID.toCharArray();
//                        for (char c: stationIDChars) {
//                            if (Character.isLetter(c)) {
//                                actualIDCase += ("" + c);
//                            }
//                            else break;
//                        }
//                        otherDifferentLinesInfo.add(new Pair<>(stationID, idToLineName.get(actualIDCase)));
//                    }
//                    newStation = new Interchange(x, y, otherDifferentLinesInfo, stationNameString, stationCntLocal);
//
//                    for (String stationID : idParts) idToStation.put(stationID, newStation);
//                }
//                else {
//                    //it is terminal/singlestation
//                    String actualIDCase = "";
//                    char[] stationIDChars = stationIndexString.toCharArray();
//                    for (char c: stationIDChars) {
//                        if (Character.isLetter(c)) {
//                            actualIDCase += ("" + c);
//                        }
//                        else break;
//                    }
//
//                    newStation = new SingleStation(x, y, stationIndexString, idToLineName.get(actualIDCase), stationNameString, stationCntLocal);
//                    idToStation.put(stationIndexString, newStation);
//                }
//            }
//            else {
//                //TODO: inputting a line info like:
//                //+1:North South Line,NSL
//                //NS28 NS27 2
//                if (curline.contains("+")) {
//                    String lineInfoString = curline.substring(1);
//
//                    String[] parts = curline.split("[:,]");
//                    String lineNo  = parts[0];
//                    String lineName = parts[1];
//                    String lineID = parts[2];
//
//                    //TODO: use ID to Station hashmap, get all IDs and convert to Stations
//                }
//                else {
//                    String[] edgeParts = curline.split(" ");
//                    String uID = edgeParts[0];
//                    String vID = edgeParts[1];
//                    String uvTime = edgeParts[2];
//
//                    //TODO
//                }
//            }
//        }
//
//        //(add all the edges with station info, then stations will be automatically added
//
//        //make 1 metro system
//        //ADD ALL LINES IN :D
//
//        //TODO
//        this.msys = null;
//    }

    @FXML
    protected void switchMode() {
        if (devMode) {
            devMode = false;
            switch_devmode.setText("Dev Mode");

            //ENTER USER MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            clear_dijikstra.setDisable(true); //assuming in this case, all routes are cleared
            run_dijkstra.setDisable(true); //assuming in this case, all routes are cleared
            updatemap_button.setDisable(true);

            from_station_input_area.setDisable(!isMapShown);
            search_area.setDisable(false);
            to_station_input_area.setDisable(!isMapShown);
            io_area.setDisable(true);

            //cannot edit map directly
            mapContainer.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> e.consume());
            mapContainer.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, e -> e.consume());
        }
        else {
            devMode = true;
            switch_devmode.setText("User Mode"); //dont be tricked

            //ENTER DEV MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            clear_dijikstra.setDisable(true); //assuming in this case, all routes are cleared
            run_dijkstra.setDisable(true); //assuming in this case, all routes are cleared
            updatemap_button.setDisable(false);

            from_station_input_area.setDisable(true);
            search_area.setDisable(false);
            to_station_input_area.setDisable(true);
            io_area.setDisable(false);

            mapContainer.removeEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> e.consume());
            mapContainer.removeEventFilter(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, e -> e.consume());
        }
    }

    @FXML
    protected void displayMap() {
        if(msys == null) return;
        isMapShown = true;

        from_station_input_area.setDisable(false);
        search_area.setDisable(false);
        to_station_input_area.setDisable(false);
        io_area.setDisable(true);

        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(msys.draw());

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
    protected void updateMap() {
        //parseInput(io_area.getText());
        displayMap();
    }

    public void getInputFromFile(String filename) throws IOException {
        String wholeFile = Files.readString(Path.of(filename));
        //parseInput(wholeFile);
    }


    @FXML
    protected void clearMap() {
        isMapShown = false;
        mapContainer.getChildren().clear();
    }

    @FXML
    protected void showAboutMe() {
        MyApplication.showAboutMe();
    }
}