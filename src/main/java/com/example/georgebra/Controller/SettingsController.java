package com.example.georgebra.Controller;

import com.example.georgebra.MyApplication;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class SettingsController {
    @FXML
    private CheckBox darkmode_checkbox;
    public CheckBox displayedgeweight_checkbox;

    boolean showEdgeWeight;

    @FXML
    public void initialize() {
        displayedgeweight_checkbox.setDisable(true);
        displayedgeweight_checkbox.setDisable(false);
        showEdgeWeight = displayedgeweight_checkbox.isSelected();
        darkmode_checkbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) MyApplication.setDarkMode();
            else MyApplication.setLightMode();
        });
    }
}
