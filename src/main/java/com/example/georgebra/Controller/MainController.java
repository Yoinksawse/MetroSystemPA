package com.example.georgebra.Controller;

import com.example.georgebra.Model.MetroSystem;
import com.example.georgebra.MyApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

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

    @FXML
    public void parseInput(String inputs) {
        
    }

    @FXML
    protected void switchMode() {
        if (devMode) {
            devMode = false;
            switch_devmode.setText("Dev Mode");

            //TODO: ENTER USER MODE
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

            //TODO: ENTER DEV MODE
            map_city_choice.setDisable(false);
            switch_devmode.setDisable(false);
            open_infowindow_button.setDisable(false); //assuming in this case, all routes are cleared
            clear_dijikstra.setDisable(true); //assuming in this case, all routes are cleared
            run_dijkstra.setDisable(true); //assuming in this case, all routes are cleared
            updatemap_button.setDisable(true);

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
    protected void clearMap() {
        isMapShown = false;
        mapContainer.getChildren().clear();
    }

    @FXML
    protected void showAboutMe() {
        MyApplication.showAboutMe();
    }
}
