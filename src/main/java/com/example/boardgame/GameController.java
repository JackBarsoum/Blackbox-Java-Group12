package com.example.boardgame;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController {

    @FXML
    private Button play;

    @FXML
    private Button quit;

    @FXML
    private Stage stage;
    private Scene scene;

    @FXML
    void leave(ActionEvent event) {
        System.out.println("Quitting Game");
        System.exit(0);
    }



    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Board.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button button; // the reference to the button in the FXML file

    @FXML
    private void handleButtonClick() {
        // create a new sphere
        Sphere sphere = new Sphere(25);

        // set the sphere color to red
        sphere.setMaterial(new PhongMaterial(Color.RED));
        sphere.setLayoutX(455);
        sphere.setLayoutY(395);

        // get the parent node of the button
        Pane parent = (Pane) button.getParent();

        // replace the button with the sphere
        parent.getChildren().add(sphere);
    }

}


