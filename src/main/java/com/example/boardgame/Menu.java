package com.example.boardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;



public class Menu extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menuScreen.fxml"));
        Scene scene = new Scene(root);
        root.getStylesheets().add(getClass().getResource("Scene.css").toExternalForm());
        Image icon = new Image(getClass().getResource("GameIcon.jpeg").toExternalForm());
        stage.getIcons().add(icon);
        stage.setTitle("BlackBoard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}