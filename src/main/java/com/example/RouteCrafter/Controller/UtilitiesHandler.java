package com.example.RouteCrafter.Controller;

import com.example.RouteCrafter.Model.GraphTheoryHandler.GraphHandler;
import com.example.RouteCrafter.Model.LineTypes.MetroLine;
import com.example.RouteCrafter.Model.StationTypes.Interchange;
import com.example.RouteCrafter.Model.StationTypes.SingleStation;
import com.example.RouteCrafter.Model.StationTypes.Station;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

import static com.example.RouteCrafter.Controller.MainController.MAX_EDGE_WIDTH;

public class UtilitiesHandler { //Meant for containing methods repeatedly used/ interacts relatively less with the GUI
    public final DropShadow highlightShadow = new DropShadow(30, Color.rgb(255, 125, 125, 0.99));
    private final MainController main;
    public UtilitiesHandler(MainController main) { this.main = main; }

    //utils
    public void createFillTransition(Circle stn, boolean initiallyBlinking) {
        FillTransition ft = new FillTransition(Duration.seconds(0.5), stn);
        ft.setFromValue(Color.web("#eb4034"));
        ft.setToValue(Color.WHITESMOKE);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);

        if (initiallyBlinking) ft.play();

        String stationName = ((Station) stn.getUserData()).getName();
        main.stationNameToFillTransitionMap.put(stationName, ft);
    }
    public boolean validateStationExistence(String stationString) {
        HashMap<String, Station> stationNameToStationMap = main.msys.getStationNameToStationMap();
        Station s = stationNameToStationMap.get(stationString.toLowerCase());

        if (s == null) return false;
        return true;
    }
    public String firstLettersToUpper(String s, String delimiter) {
        String result = "";

        String [] segments = s.split(delimiter);
        for (String str: segments) {
            result += str.substring(0,1).toUpperCase() + str.substring(1, str.length()) + " ";
        }

        return result;
    }
    public void setCircleColour(Circle circle, boolean highlighted) {
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
    public void installTooltip(Node node, String text, double durationSeconds) {
        Tooltip ttp = new Tooltip(text);
        ttp.setShowDelay(Duration.seconds(durationSeconds));
        Tooltip.install(node, ttp);
    }
    public void showAlert(String text, Alert.AlertType at){
        Alert alert = new Alert(at);
        alert.setContentText(text);
        alert.show();
    }

    protected Group drawMetroStation(Station s, boolean highlighted, boolean searched) {
        Group stationGroup = new Group();

        double x = s.getX();
        double y = s.getY();

        double stationWidth = 12.0;

        String stationIDText = "";
        Circle stn = new Circle(0, 0, stationWidth); //0 0 within its group's position
        stn.setUserData(s);
        createFillTransition(stn, searched);
        setCircleColour(stn, highlighted);

        if (s instanceof Interchange) {
            HashMap<String,String> allLinesInfo = ((Interchange) s).getAllLinesInfo();
            for (Map.Entry<String, String> id: allLinesInfo.entrySet()) stationIDText += (id.getValue() + "/");
            if (stationIDText.length() >= 2) stationIDText = stationIDText.substring(0, stationIDText.length() - 1);

            boolean hasMRTLine = false;
            boolean hasLRTLine = false;
            boolean hasTourismLine = false;
            for (String lineName: allLinesInfo.keySet()) {
                String lineType = main.metroLineNameToTypeMap.get(lineName.trim().toLowerCase());
                if (lineType.trim().equalsIgnoreCase("Tourism")) hasTourismLine = true;
                if (lineType.trim().equalsIgnoreCase("LRT")) hasLRTLine = true;
                if (lineType.trim().equalsIgnoreCase("MRT")) hasMRTLine = true;
            }

            //stationWidth += 5;
            //priority ranking!
            if (hasTourismLine) stn.setRadius(stationWidth / 3.0 * 2.0);
            if (hasLRTLine) stn.setRadius(stationWidth / 5.0 * 4.0);
            if (hasMRTLine) stn.setRadius(stationWidth);

            stn.setStrokeWidth(3);
        }
        else if (s instanceof SingleStation){ //s instanceof SingleStation
            String lineType = main.metroLineNameToTypeMap.get(s.getLineName().trim().toLowerCase());
            //priority ranking!
            if (lineType.trim().equalsIgnoreCase("Tourism")) stn.setRadius(stationWidth / 3.0 * 2.0);
            if (lineType.trim().equalsIgnoreCase("LRT")) stn.setRadius(stationWidth / 5.0 * 4.0);
            if (lineType.trim().equalsIgnoreCase("MRT")) stn.setRadius(stationWidth);

            stn.setStrokeWidth(2);
            stationIDText = s.getStationID();
        }

        //handle text
        Label stationID = new Label(firstLettersToUpper(s.getName(), " "));
        stationID.setTextFill(Color.BLACK);
        stationID.setStyle("-fx-background-color: rgba(240, 240, 240, 0.69);");

        stationID.setLayoutX(s.getTextX());
        stationID.setLayoutY(s.getTextY());
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
            if (main.devMode) {
                offsetX[0] = e.getSceneX() - stationGroup.getLayoutX();
                offsetY[0] = e.getSceneY() - stationGroup.getLayoutY();
            }
            e.consume();
        });
        stationGroup.setOnMouseDragged(e -> {
            if (main.devMode) {
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
            e.consume();
        });
        stationGroup.setOnMouseReleased(e -> {
            if (!dragged[0]) return;
            //virtual grid so it is more tidy :#3
            double newX = e.getSceneX() - offsetX[0];
            double newY = e.getSceneY() - offsetY[0];
            newX = Math.round(newX / main.gridSize) * main.gridSize;
            newY = Math.round(newY / main.gridSize) * main.gridSize;

            stationGroup.setLayoutX(newX);
            stationGroup.setLayoutY(newY);

            Station station = (Station) stationGroup.getUserData();
            if (station != null) {
                station.setX(newX);
                station.setY(newY);
            }
            dragged[0] = false;
            e.consume();
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

        stationGroup.setOnMouseClicked(e -> {
            if (main.devMode || !e.getEventType().equals(MouseEvent.MOUSE_CLICKED)) return;
            Station searchedStationLocal = (Station) ((Group) e.getSource()).getUserData();
            if (searchedStationLocal == null) throw new IllegalStateException("A station must exist in a group");
            main.clearStationInfo();
            main.showSearchedStation(searchedStationLocal);
            e.consume();
        });

        main.stationNameToCurrentGroupMap.put(s.getName(), stationGroup);

        if (highlighted) stationGroup.setEffect(highlightShadow);
        return stationGroup;
    }
    protected Group drawEdge(Station u, Station v, Group UGroup, Group VGroup, Integer edgeWeight) {
        if (u == null || v == null || UGroup == null || VGroup == null) throw new IllegalArgumentException("Stop Trolling");
        if (edgeWeight != null && edgeWeight > 10000) edgeWeight = edgeWeight % 10000;
        Group finalEdge = new Group();

        int counter = 0;
        Line edgeLine = new Line(u.getX(), u.getY(), v.getX(), v.getY());
        edgeLine.setStrokeWidth(MAX_EDGE_WIDTH);
        edgeLine.setStyle("-fx-effect: dropshadow(gaussian, rgba(69,4, 20, 0.7), 0.5, 0.5, 0, 0)");

        //find colour
        HashMap<String, String> allLinesInfoU = new HashMap<>();
        if (u instanceof Interchange) allLinesInfoU = ((Interchange) u).getAllLinesInfo();
        else allLinesInfoU.put(u.getLineName(), u.getStationID());

        HashMap<String, String> allLinesInfoV = new HashMap<>();
        if (v instanceof Interchange) allLinesInfoV = ((Interchange) v).getAllLinesInfo();
        else allLinesInfoV.put(v.getLineName(), v.getStationID());

        HashSet<String> commonLines = new HashSet<>(allLinesInfoU.keySet());
        commonLines.retainAll(allLinesInfoV.keySet());

        ArrayList<String> colours = new ArrayList<>();
        ArrayList<String> lineTypes = new ArrayList<>();
        for (String lineStr: commonLines) {
            lineTypes.add(main.metroLineNameToTypeMap.get(lineStr.trim()));
            colours.add(main.lineNameToColourMap.get(lineStr));
        }

        if (colours.size() == 1) {
            Color colour = Color.web(colours.get(0));
            edgeLine.setStroke(colour);

            //System.out.println(u.getName() + " " + v.getName() + ": " + colours);

            edgeLine.startYProperty().bindBidirectional(UGroup.layoutYProperty());
            edgeLine.endXProperty().bindBidirectional(VGroup.layoutXProperty());
            edgeLine.endYProperty().bindBidirectional(VGroup.layoutYProperty());
            edgeLine.startXProperty().bindBidirectional(UGroup.layoutXProperty());

            if (lineTypes.get(0).equalsIgnoreCase("LRT")) {
                edgeLine.setStrokeWidth(MAX_EDGE_WIDTH / 3 * 2);
            }
            else if (lineTypes.get(0).equalsIgnoreCase("Tourism")) {
                edgeLine.setStrokeWidth(MAX_EDGE_WIDTH / 2);
            }

            finalEdge.getChildren().add(edgeLine);
            edgeLine.toBack();
        }
        else {
            int n = colours.size();
            double spacing = (MAX_EDGE_WIDTH / n) / 2;
            double strokeWidth = MAX_EDGE_WIDTH / n + 1;
            String styleString = "-fx-effect: dropshadow(gaussian, rgba(69,4, 20, 0.7), 0.5, 0.5, 0, 0)";

            double dx = v.getX() - u.getX();
            double dy = v.getY() - u.getY();
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                dx /= length;
                dy /= length;
            }

            double perpX = -dy;
            double perpY = dx;

            for (int i = 0; i < n; i++) {
                double offset = (i - (n - 1) / 2.0) * (n + spacing);

                Line line = new Line(
                        u.getX() + perpX * offset + 1,
                        u.getY() + perpY * offset + 1,
                        v.getX() + perpX * offset + 1,
                        v.getY() + perpY * offset + 1
                );

                line.setStrokeWidth(strokeWidth);
                line.setStroke(Color.web(colours.get(i)));
                line.setStyle(styleString);

                line.startXProperty().bind(UGroup.layoutXProperty().add(perpX * offset + 1));
                line.startYProperty().bind(UGroup.layoutYProperty().add(perpY * offset + 1));
                line.endXProperty().bind(VGroup.layoutXProperty().add(perpX * offset + 1));
                line.endYProperty().bind(VGroup.layoutYProperty().add(perpY * offset + 1));

                String lineType = lineTypes.get(i);
                if (lineType.equalsIgnoreCase("LRT")) {
                    line.setStrokeWidth(strokeWidth / 3 * 2);
                } else if (lineType.equalsIgnoreCase("Tourism")) {
                    line.setStrokeWidth(strokeWidth / 2);
                }

                finalEdge.getChildren().add(line);
                line.toBack();
            }
        }

        if (main.showEdgeWeight) {
            double midX = (u.getX() + v.getX()) / 2.0;
            double midY = (u.getY() + v.getY()) / 2.0;

            double dx = v.getX() - u.getX();
            double dy = v.getY() - u.getY();
            double length = Math.sqrt(dx * dx + dy * dy);

            double offsetX = 0;
            double offsetY = 0;

            if (length > 0) {
                dx /= length;
                dy /= length;
                offsetX = -dy;
                offsetY = dx;
            }

            Text timeLabel = new Text(edgeWeight.toString());
            timeLabel.setFill(Color.BLUE);
            timeLabel.setWrappingWidth(1);
            timeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-stroke: yellow; -fx-stroke-width: 0.8;");

            timeLabel.xProperty().bind(UGroup.layoutXProperty()
                    .add(VGroup.layoutXProperty())
                    .divide(2)
            );
            timeLabel.yProperty().bind(UGroup.layoutYProperty()
                    .add(VGroup.layoutYProperty())
                    .divide(2)
            );
            finalEdge.getChildren().add(timeLabel);
            timeLabel.toFront();
        }

        if (edgeWeight != null) {
            Tooltip edgeWeightTooltip = new Tooltip(edgeWeight.toString() + " min");
            edgeWeightTooltip.setShowDelay(Duration.millis(189));
            Tooltip.install(finalEdge, edgeWeightTooltip);
        }

        return finalEdge;
    }
    protected void drawMetroSystem(Pair<ArrayList<Station>, Integer> bestPath, Station searched, boolean showEdgeWeight) {
        main.bestPathThis = bestPath;

        ArrayList<Station> bestPathStations = (bestPath != null) ? bestPath.getKey(): null;
        int bestPathDistance = (bestPath != null) ? bestPath.getValue(): 0;
        boolean needHighlight = ((bestPathStations != null) && (!bestPathStations.isEmpty()));

        //global fields
        GraphHandler graph = main.msys.getGraphHandler();
        int stationCnt = main.msys.getStationList().size();
        int nodeCnt = graph.getNodeCnt();
        HashMap<Integer, Integer> adjListSystem[] = graph.getAdjList();
        HashMap<Integer, String> indexToStationNameMap = main.msys.getIndexToStationNameMap();
        HashMap<String, Station> stationNameToStationMap = main.msys.getStationNameToStationMap();

        HashSet<Station> stations = new HashSet<>();
        stations.addAll(main.msys.getStationList());

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

        // bfs and plot stations and edges along the way
        Queue<String> q = new ArrayDeque<>();
        q.add((String) stationNameToStationMap.keySet().toArray()[0]);

        HashMap<Integer, Boolean> visited = new HashMap<>();
        for (Station s: stations) visited.put(stationToLocalIndexMap.get(s), false);
        visited.put(stationToLocalIndexMap.get(q.peek()), true);

        HashSet<Pair<Station, Station>> edges = new HashSet<>();
        while (!q.isEmpty()) {
            String curStnName = q.poll();
            Station curStn = stationNameToStationMap.get(curStnName);
            int curStnLocalIndex = stationNameToLocalIndexMap.get(curStnName);
            //Group curGroup = stationGroups.get(curStnName);

            for (Map.Entry<Integer, Integer> entry: adjListLocal[curStnLocalIndex].entrySet()) {
                int nxtIndex = entry.getKey();
                Integer nxtTime = (showEdgeWeight) ? entry.getValue() : null;
                String nxtStnName = localIndexToStationNameMap.get(nxtIndex);
                Station nxtStn = stationNameToStationMap.get(nxtStnName);

                if (!visited.get(nxtIndex)) {
                    visited.put(nxtIndex, true);
                    q.add(nxtStnName);
                }

                //Group nxtGroup = stationGroups.get(nxtStnName);
                Pair<Station, Station> edgePair1 = new Pair<>(curStn, nxtStn);
                Pair<Station, Station> edgePair2 = new Pair<>(nxtStn, curStn);
                if (!edges.contains(edgePair1) && !edges.contains(edgePair2)) {
                    edges.add(edgePair1);
                }
            }
        }

        Platform.runLater(() -> {
            Group currentMapGroup = new Group(); // Or new Pane();

            // draw all stations first
            HashMap<String, Group> stationGroups = new HashMap<>();
            for (Station s: stations) {
                boolean beingSearched = (searched != null) ? (s.getName().equalsIgnoreCase(searched.getName())) : false;

                boolean shouldHighlight = needHighlight && bestPathStationNames.contains(s.getName().toLowerCase());
                Group stnGrp = drawMetroStation(s, shouldHighlight, beingSearched);
                stationGroups.put(s.getName(), stnGrp);
            }

            ArrayList<Group> edgeGroups = new ArrayList<>();

            for (Pair<Station, Station> edgePair : edges) {
                Station u = edgePair.getKey();
                Station v = edgePair.getValue();

                // Ensure we only draw each edge once (e.g., from A->B, but not B->A if already drawn)
                // The original logic with edges.add(edgePair1); edges.add(edgePair2); is a common pattern
                // but for simplicity, let's use the actual set:
                if (!edges.contains(new Pair<>(v, u))) {
                    Group UGroup = stationGroups.get(u.getName());
                    Group VGroup = stationGroups.get(v.getName());

                    Integer nxtTime = (showEdgeWeight) ? adjListLocal[stationNameToLocalIndexMap.get(u.getName().toLowerCase())].get(stationNameToLocalIndexMap.get(v.getName().toLowerCase())) : null;

                    // Calls drawEdge on the JAT
                    Group edgeGroup = drawEdge(u, v, UGroup, VGroup, nxtTime);
                    edgeGroups.add(edgeGroup);
                }
            }


            currentMapGroup.getChildren().addAll(stationGroups.values());
            currentMapGroup.getChildren().addAll(edgeGroups);
            for (Group edge: edgeGroups) edge.toBack();

            main.mapContainer.getChildren().clear();
            main.mapContainer.getChildren().add(currentMapGroup);
        });
    }

    protected void drawLineInfoBox(MetroLine mtl, String lineColourName) {
        double sidePadding = 5;
        double spacing = 10;
        double rectWidth = 35;

        HBox entry = new HBox();
        entry.setMouseTransparent(false);
        entry.setSpacing(10);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPadding(new Insets(5));
        entry.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 5;");

        String lineName = firstLettersToUpper(mtl.getLineName(), " ");
        Text lineNameText = new Text(lineName);
        lineNameText.wrappingWidthProperty()
                .bind(main.lineinfos_vbox.widthProperty()
                        .subtract(rectWidth + spacing + sidePadding * 2));
        lineNameText.setFill(Color.BLACK);
        lineNameText.setStyle("-fx-font-weight: bold;");

        Color lineColour = Color.web(lineColourName);
        Rectangle entryBackground = new Rectangle(35, 20);
        entryBackground.setFill(lineColour);
        entryBackground.setStroke(Color.WHITE);

        //additional poop
        String lineType = mtl.getLineType();
        Text entryText = new Text();
        if (lineType.equalsIgnoreCase("LRT")) {
            entryText.setText("LRT");
            entryText.setFill(Color.BLACK);
        }
        if (lineType.equalsIgnoreCase("Tourism")) {
            entryText.setText("Tour.");
            entryText.setFill(Color.WHITESMOKE);
        }

        StackPane stacked = new StackPane(entryBackground, entryText);

        entry.getChildren().addAll(lineNameText, stacked);
        main.lineinfos_vbox.getChildren().add(entry);
    }
}