package com.example.georgebra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MyApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/georgebra.View/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1540, 780);
        scene.getStylesheets().add(getClass().getResource("/com/example/georgebra.View/style.css").toExternalForm());

        stage.setTitle("Georgebra MRT Route Planner");
        stage.getIcons().add(new Image(getClass().getResource("/com/example/georgebra.View/GrapherLogo.png").toExternalForm()));
        stage.setScene(scene);
        stage.setMinWidth(1300);
        stage.setMinHeight(800);
        stage.show();

        //mainLoader = fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }
}