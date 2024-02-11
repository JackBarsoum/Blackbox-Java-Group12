package com.example.boardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.print.attribute.standard.Media;
import java.io.IOException;
    public class Menu extends Application {
        public Menu() {
        }

        public void start(Stage stage) throws IOException {
            Parent root = FXMLLoader.load(this.getClass().getResource("menuScreen.fxml"));
            Scene scene = new Scene(root);
            root.getStylesheets().add(this.getClass().getResource("Scene.css").toExternalForm());
            Image icon = new Image(this.getClass().getResource("GameIcon.jpeg").toExternalForm());
            stage.getIcons().add(icon);
            stage.setTitle("BlackBoard");
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch();
        }
    }


