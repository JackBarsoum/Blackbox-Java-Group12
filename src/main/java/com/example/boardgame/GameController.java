package com.example.boardgame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;

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
    Button button0,button1,button2,button3,button4,button5,button6,button7,button8, button9,
            button10, button11, button12, button13, button14, button15, button16,
            button18, button19, button20, button21, button22, button23, button24, button25,
            button26, button27, button28, button29, button30, button31, button32,
            button34, button35, button36, button37, button38, button39, button40, button41,
            button42, button43, button44, button45, button46, button47, button48, button49,
            button50, button51, button52, button53, button54, button55, button56, button57,
            button58, button59, button60, button61, button62;



    @FXML
    private void handleButtonClick(ActionEvent event){
        // create a new sphere
        Button b = (Button) event.getSource();
        Sphere sphere = new Sphere(25);
        double x = b.getLayoutX();
        double y = b.getLayoutY();

        // set the sphere color to red
        sphere.setMaterial(new PhongMaterial(Color.RED));
        sphere.setLayoutX(x+37);
        sphere.setLayoutY(y+24);

        // get the parent node of the button
        Pane parent = (Pane) b.getParent();

        // replace the button with the sphere
        parent.getChildren().add(sphere);
    }

}


