package com.example.RouteCrafter;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class SplashScreen extends Application {
    private static final String SPLASH_GIF = "/com/example/RouteCrafter.View/train-drivethrough.gif";
    private static final int SPLASH_WIDTH = 720;
    private static final int SPLASH_HEIGHT = 480;

    @Override
    public void start(Stage splashStage) {
        URI gifURI;
        try { gifURI = Objects.requireNonNull(getClass().getResource(SPLASH_GIF)).toURI(); }
        catch (URISyntaxException e) { throw new RuntimeException(e); }

        Image image = new Image(gifURI.toString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(SPLASH_WIDTH);
        imageView.setFitHeight(SPLASH_HEIGHT);
        imageView.fitWidthProperty().bind(splashStage.widthProperty());
        imageView.fitHeightProperty().bind(splashStage.heightProperty());
        imageView.setPreserveRatio(false);
        imageView.setOpacity(0.69);

        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(SPLASH_WIDTH - 20);
        Label text = new Label("Loading, please wait...");
        text.setStyle("-fx-background-color: rgba(240, 240, 240, 0.69);"
                + "-fx-font-size: 15px; ");
        Label header = new Label("RouteCrafter v1.0");
        header.setStyle("-fx-font-family: 'Segoe UI'; "
                + "-fx-font-size: 48px; "
                + "-fx-font-style: italic; "
                + "-fx-text-fill: white; "
                + "-fx-effect: dropshadow(gaussian, black, 2, 0.5, 0, 0);");

        VBox components = new VBox(10, header, bar, text);
        //header.setAlignment(Pos.CENTER_LEFT);
        components.setAlignment(Pos.BOTTOM_CENTER);
        components.setStyle("-fx-padding: 10; -fx-background-color: transparent; "
                + "-fx-border-width: 3; -fx-border-color: chocolate;");
        components.setEffect(new DropShadow());

        StackPane root = new StackPane(imageView, components);

        Scene splashScene = new Scene(root, SPLASH_WIDTH, SPLASH_HEIGHT);
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setResizable(false);
        splashStage.setScene(splashScene);
        splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        splashStage.show();

        //THREAD 1: javafx, media playing

        //THREAD 2: updating (must start after media has loaded tho)
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                int steps = 5;
                //should be exactly for the train to go through
                int[] loadingTime = {1024, 420, 1729, 911, 1800};
                String[] messages = {"Eating Cookies...",
                        "Absorbing CPU Power...",
                        "Preventing Expiration...",
                        "Detecting Fire Hazards...",
                        "Welcome!"};
                try {
                    for (int i = 0; i < steps; i++) {
                        Thread.sleep((loadingTime[i]));
                        updateProgress(i, steps);
                        updateMessage(messages[i]);
                    }
                } catch (InterruptedException e) {}

                Thread.sleep(loadingTime[4]);
                return null;
            }
        };

        //add bindings
        bar.progressProperty().bind(loadTask.progressProperty());
        text.textProperty().bind(loadTask.messageProperty());

        loadTask.setOnSucceeded(e -> {
            //actual fade transition
            FadeTransition fade = new FadeTransition(Duration.seconds(1.2), root);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(ev -> {
                splashStage.hide();
                try {
                    new MyApplication().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            fade.play();
        });

        new Thread(loadTask).start();
    }

    private void launchMainApp(Stage splashStage) {
        splashStage.hide();
        try {
            new MyApplication().start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}