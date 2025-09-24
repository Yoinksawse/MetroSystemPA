package com.example.RouteCrafter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MyApplication extends Application {
    private static final String DARK_THEME_CLASS = "dark-theme";
    private static Scene mainScene, aboutMeScene, settingsScene;
    private static boolean nowDarkMode;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("/com/example/RouteCrafter.View/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1448, 800);
        scene.getStylesheets().add(MyApplication.class.getResource("/com/example/RouteCrafter.View/style.css").toExternalForm());

        if (nowDarkMode) setDarkMode();
        mainScene = scene;

        stage.setTitle("RouteCrafter v1.0");
        stage.getIcons().add(new Image(MyApplication.class.getResource("/com/example/RouteCrafter.View/GrapherLogo.png").toExternalForm()));
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(800);
        stage.show();
    }

    public static void showAboutMe() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("/com/example/RouteCrafter.View/about-me.fxml"));
            Scene myScene = new Scene(fxmlLoader.load(), 320, 480);
            myScene.getStylesheets().add(MyApplication.class.getResource("/com/example/RouteCrafter.View/style.css").toExternalForm());

            if (nowDarkMode) setDarkMode();
            aboutMeScene = myScene;

            Stage stage = new Stage();
            stage.setTitle("About Programmer");
            stage.getIcons().add(new Image(MyApplication.class.getResource("/com/example/RouteCrafter.View/GrapherLogo.png").toExternalForm()));
            stage.setScene(myScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDarkMode() {
        if (mainScene != null) mainScene.getRoot().getStyleClass().add(DARK_THEME_CLASS);
        if (aboutMeScene != null) aboutMeScene.getRoot().getStyleClass().add(DARK_THEME_CLASS);
    }

    public static void setLightMode() {
        if (mainScene != null) mainScene.getRoot().getStyleClass().remove(DARK_THEME_CLASS);
        if (aboutMeScene != null) aboutMeScene.getRoot().getStyleClass().remove(DARK_THEME_CLASS);
    }
}