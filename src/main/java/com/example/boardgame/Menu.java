package com.example.boardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

    public class Menu extends Application {

        private static Stage primaryStage;

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

            primaryStage = stage;
        }

        public static void setPrimaryStage(Stage stage) {
            primaryStage = stage;
        }

        public static Stage getPrimaryStage() {
            return primaryStage;
        }

        public static void main(String[] args) {
            launch();
        }



    }


