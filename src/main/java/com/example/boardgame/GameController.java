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
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GameController {
    private int atomcount = 0;

    private Pane spherepane;

    void setAtomcount(int atomcount1)
    {
        this.atomcount = atomcount1;
    }

    void setSpherepane(Pane spherepane1)
    {
        this.spherepane = spherepane1;
    }

    @FXML
    private Button play;

    @FXML
    private Button quit;

    @FXML
    private Stage stage;
    private Scene scene;
    private URL boardURL;

    @FXML
    void leave(ActionEvent event) {
        System.out.println("Quitting Game");
        System.exit(0);
    }



    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        boardURL = getClass().getResource("Board.fxml");
        Parent root = FXMLLoader.load(boardURL);
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
    public Sphere handleButtonClick(ActionEvent event){
        // create a new sphere
        if(atomcount < 6) {
            Button b = (Button) event.getSource();
            Sphere sphere = new Sphere(25);
            double x = b.getLayoutX();
            double y = b.getLayoutY();

            // set the sphere color to red
            sphere.setMaterial(new PhongMaterial(Color.RED));
            sphere.setLayoutX(x + 37);
            sphere.setLayoutY(y + 24);

            // get the parent node of the button
            Pane parent = (Pane) b.getParent();
            spherepane = (Pane) b.getParent();
            // replace the button with the sphere
            parent.getChildren().add(sphere);
            atomcount++;

            return sphere;
        }
        return null;
    }

    @FXML
    public void toggleAtoms(ActionEvent event)
    {
        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6)
        {
            //go through all of the children of the pane spherepane
            for (Node child : spherepane.getChildren())
            {
                //if the child is a sphere
                if(child instanceof Sphere)
                {
                    //make the child a sphere so we can use sphere methods
                    Sphere sphere = (Sphere) child;
                    //make the sphere invisible/visible depending on the return of isVisible
                    sphere.setVisible(!sphere.isVisible());
                }

            }

        }

    }


    public Stage getStage() {
        return stage;
    }

    public URL getBoardURL() {
        return boardURL;
    }
}


