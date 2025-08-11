package com.example.georgebra.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

public class MainController {
    //outside fields
    @FXML
    private Button clear_dijikstra;
    private Button run_dijkstra;
    private Button switch_devmode1;
    private Button updatemap_button;
    private TextField from_station_input_area;
    private TextField search_area;
    private TextField to_station_input_area;
    private TextArea io_area;
    private ComboBox<?> map_city_choice;
    private StackPane map_container;
    private ToggleButton switch_devmode;

    //inside fields
    public StackPane mapContainer;


    public void initialize() {

    }

    public void updateMap() {
        //mapContainer.getChildren().clear();
        //mapContainer.getChildren().add();
    }
}
