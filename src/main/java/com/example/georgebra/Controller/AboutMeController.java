package com.example.georgebra.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AboutMeController {
    @FXML
    public Button closebtn;
    public Text myname;

    @FXML
    protected void close() {
        Stage stage = (Stage) closebtn.getScene().getWindow();
        stage.close();
    }
}
