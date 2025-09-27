package com.example.RouteCrafter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MyApplication extends Application {
    private static final String DARK_THEME_CLASS = "dark-theme";
    private static Scene mainScene, aboutMeScene, tutorialScene;
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

    public static void showTutorial() {
        try {
            AtomicInteger index = new AtomicInteger(0);
            String[] images = {
                    "/com/example/RouteCrafter.View/tutorial0.png",
                    "/com/example/RouteCrafter.View/tutorial1.png",
                    "/com/example/RouteCrafter.View/tutorial2.png",
                    "/com/example/RouteCrafter.View/tutorial3.png",
                    "/com/example/RouteCrafter.View/tutorial4.png",
                    "/com/example/RouteCrafter.View/tutorial5.png",
                    "/com/example/RouteCrafter.View/tutorial6.png",
                    "/com/example/RouteCrafter.View/tutorial7.png",
                    "/com/example/RouteCrafter.View/tutorial8.png",
                    "/com/example/RouteCrafter.View/tutorial9.png"
            };
            String[] messages = {
                    "Welcome to Tutorial. Click to continue!",
                    "This is a the Metro Map, which you can select. \n" +
                            "To load a new Metro Map, go to our repository (In About Me) and download some packages! \n" +
                            "unzip them into the \"RouteFinderData\" folder next to the jar.",
                    "Stations are represented by circles. Largest circles are MRT \n " +
                            "stations, followed by LRT lines, followed by Miscellaneous\n" +
                            " types (such as Tourism Lines and Maglevs). Click on a station circle to select it.",
                    "Type in the search field to search a station Name.",
                    "Type source and destination station names in the fields, \n" +
                            "and press enter to compute fastest route.",
                    "You can also compute the route with least exchanges if you feel lazy to walk.",
                    "The 2 options at the bottom-left corner can change layout \n" +
                            "and enable timing between stations to be shown.",
                    "If you focus the cursor (click) on the \"from\" and \"to\" and click on \n" +
                            "a station, the station's name will be automatically filled in to the field!",
                    "In Editing Mode, one can drag around the stations to arrange them to your liking! \n" +
                            "There is an invisible 20px by 20px grid on the screen that helps you align them nicely.\n" +
                            "You can save your layout too.",
                    "If you've messed up your map, all you have to do is to press Load/Reload to restore the graph.\n" +
                            "(Provided you haven't saved your unwanted layout.) That's all!"
            };

            Label label = new Label(messages[0]);
            label.setFont(new Font("Arial", 14));
            label.setWrapText(true);
            label.setMaxWidth(900);
            label.setTextAlignment(TextAlignment.CENTER);

            ImageView imageView = new ImageView(new Image(
                    Objects.requireNonNull(MyApplication.class.getResource(images[0])).toExternalForm()
            ));
            imageView.setPreserveRatio(true);
            VBox.setVgrow(imageView, javafx.scene.layout.Priority.ALWAYS);

            VBox sceneRoot = new VBox(20, label, imageView);
            sceneRoot.setAlignment(Pos.TOP_CENTER);
            sceneRoot.setOnMouseClicked(ev -> {
                int i = index.incrementAndGet();
                if (i < images.length) {
                    label.setText(messages[i]);
                    imageView.setImage(new Image(
                            Objects.requireNonNull(MyApplication.class.getResource(images[i])).toExternalForm()
                    ));
                } else ((Stage) sceneRoot.getScene().getWindow()).close();
            });

            imageView.fitWidthProperty().bind(sceneRoot.widthProperty());

            Scene myScene = new Scene(sceneRoot, 1000, 600);
            myScene.getStylesheets().add(MyApplication.class.getResource("/com/example/RouteCrafter.View/style.css").toExternalForm());
            tutorialScene = myScene;

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Tutorial!");
            stage.getIcons().add(new Image(MyApplication.class.getResource("/com/example/RouteCrafter.View/GrapherLogo.png").toExternalForm()));
            stage.setScene(myScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDarkMode() {
        if (mainScene != null) mainScene.getRoot().getStyleClass().add(DARK_THEME_CLASS);
        //if (aboutMeScene != null) aboutMeScene.getRoot().getStyleClass().add(DARK_THEME_CLASS);
    }

    public static void setLightMode() {
        if (mainScene != null) mainScene.getRoot().getStyleClass().remove(DARK_THEME_CLASS);
        //if (aboutMeScene != null) aboutMeScene.getRoot().getStyleClass().remove(DARK_THEME_CLASS);
    }
}