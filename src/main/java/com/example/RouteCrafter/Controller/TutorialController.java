package com.example.RouteCrafter.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TutorialController {
    @FXML
    protected VBox root;
    protected Stage stage;
    protected ImageView imageDisplayer;
    protected Text text;

    final String[] imageURLStrings = {};
    protected Image[] tutorialImages;

    @FXML
    public void initialize() {
        //create images

        // build UI here instead of FXML
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Welcome to the Tutorial!");
        Button next = new Button("Next");

        next.setOnAction(e -> onNextClicked());

        root.getChildren().addAll(label, next);
    }

    protected void onNextClicked() {

    }
}