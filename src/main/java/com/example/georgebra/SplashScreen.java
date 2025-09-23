package com.example.georgebra;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SplashScreen extends Application {
    private static final String SPLASH_VIDEO = "/com/example/georgebra.View/train-drivethrough.mp4";
    private static final int SPLASH_WIDTH = 720;
    private static final int SPLASH_HEIGHT = 480;

    @Override
    public void start(Stage splashStage) {
        final CountDownLatch mediaReady = new CountDownLatch(1);

        URL videoURL = MyApplication.class.getResource(SPLASH_VIDEO);
        Media media = new Media(videoURL.toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setMute(true);
        mediaPlayer.setCycleCount(1);

        MediaView mediaView = new MediaView();
        mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.READY) {
                System.out.println("ready");
                //loaded finally
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                mediaReady.countDown();
            } else if (newStatus == MediaPlayer.Status.HALTED || newStatus == MediaPlayer.Status.STALLED) {
                System.err.println("MediaPlayer failed to load or play the video: " + newStatus);
                mediaReady.countDown();
                launchMainApp(splashStage);
            }
        });
        mediaView.setFitWidth(SPLASH_WIDTH);
        mediaView.setFitHeight(SPLASH_HEIGHT);
        mediaView.fitWidthProperty().bind(splashStage.widthProperty());
        mediaView.fitHeightProperty().bind(splashStage.heightProperty());
        mediaView.setPreserveRatio(false);
        mediaView.setOpacity(0.69);

        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(SPLASH_WIDTH - 20);
        Label text = new Label("Loading, please wait...");
        text.setStyle("-fx-background-color: rgba(240, 240, 240, 0.69);"
                + "-fx-font-size: 15px; ");
        Label header = new Label("RouteCraft v1.0");
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

        StackPane root = new StackPane(mediaView, components);

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
                boolean ready = mediaReady.await(5, java.util.concurrent.TimeUnit.SECONDS);
                if (!ready) {
                    System.err.println("Timeout! Media failed to load within 5 seconds. " +
                            "\nDisclaimer: this is an issue of JavaFx media module :)");
                    javafx.application.Platform.runLater(() -> launchMainApp(splashStage));
                    return null;
                }

                int steps = 5;
                //should be exactly for the train to go through
                int[] loadingTime = {1024, 420, 2048, 911, 1800};
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