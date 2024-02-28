package com.example.boardgame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
    public Rectangle N49;

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
    void extendLineHorizontal(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());

        newLine.setStartX(b.getLayoutX() + b.getWidth());
        newLine.setStartY(b.getLayoutY() + b.getHeight() / 2);

        newLine.setEndX(newLine.getStartX() + 10);
        newLine.setEndY(newLine.getStartY());

        int flag = 0;


        do {
            newLine.setEndX(newLine.getEndX() + 1); // Increase the line length
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && b != node) {

                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    }else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                    System.out.println("Ray hit an atom");
                    flag = 1;
                    }
                }
            }

        } while (flag != 1);
        p.getChildren().add(newLine);
    }

    @FXML
    void extendLineHorizontalL(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());

        newLine.setStartX(b.getLayoutX() - b.getWidth());
        newLine.setStartY(b.getLayoutY() + b.getHeight() / 2);

        newLine.setEndX(newLine.getStartX() - 10);
        newLine.setEndY(newLine.getStartY());

        int flag = 0;


        do {
            newLine.setEndX(newLine.getEndX() - 1); // Increase the line length


            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && b != node) {
                        // Line intersects with another button
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit a node");
                        flag = 1;
                    }
                }

            }

        } while (flag != 1);
        p.getChildren().add(newLine);
    }

    @FXML
    void extendLineDiagonalLeft_down(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        // This makes the line go diagonally (Saw this method online, obviously get rid of this comment)
        double angleRadians = Math.toRadians(120);
        double dx = Math.cos(angleRadians); // Horizontal Distance to Increase
        double dy = Math.sin(angleRadians); // Vertical Distance to Increase

        int flag = 0;



        do {
            //Increase the length of the line
            newLine.setEndX(newLine.getEndX() + dx);
            newLine.setEndY(newLine.getEndY() + dy);

            //Goes through each node in the parent pane
            for (Node node : p.getChildren()) {
                //If the line interacts with a node
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    //Checks to see if the node is a rectangle
                    if (node instanceof Rectangle && b != node && ((Rectangle) node).getStroke() == Color.BLUE) {
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                        //Check to see if the line hit an atom(Sphere), checks that the line is actually inside the sphere to stop any errors
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit an atom");
                        flag = 1;
                    }
                }
            }
        } while (flag != 1);

        //Adds the line formed to the Pane, so it will be displayed
        p.getChildren().add(newLine);
    }
    @FXML
    void extendLineDiagonalRight_down(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();
        System.out.println("Ray shot from " + b.getId());

        // Set the starting point of the line
        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);


        double angleRadians = Math.toRadians(60);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        int flag = 0;

        do {
            newLine.setEndX(newLine.getEndX() + dx);
            newLine.setEndY(newLine.getEndY() + dy);

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == Color.RED) {
                        // Line intersects with another rectangle
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }
        } while (flag != 1);

        p.getChildren().add(newLine);
    }

    @FXML
    void extendLineDiagonalRight_Up(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);


        double angleRadians = Math.toRadians(120);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        int flag = 0;

        do {
            newLine.setEndX(newLine.getEndX() - dx);
            newLine.setEndY(newLine.getEndY() - dy);

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == Color.GREEN) {
                        // Line intersects with another rectangle
                        System.out.println("Ray exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        p.getChildren().add(newLine);
    }
    @FXML
    void extendLineDiagonalLeft_Up(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());

        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);


        newLine.setEndX(startX);
        newLine.setEndY(startY);

        double angleRadians = Math.toRadians(60);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        int flag = 0;
        do {
            newLine.setEndX(newLine.getEndX() - dx);
            newLine.setEndY(newLine.getEndY() - dy);

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == Color.YELLOW) {
                        // Line intersects with another rectangle
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit an atom");
                        flag = 1;
                    }
                }
            }
        } while (flag != 1);

        p.getChildren().add(newLine);
    }

    @FXML
    public void toggleAtoms(ActionEvent event)
    {
        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6)
        {
            //go through all the children of the pane spherepane
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

    //Method to check that the line actually hit the atom by using the distance formula
    public boolean isInside(Sphere x, Line l){
        double radius = x.getRadius();

        double distance = Math.sqrt(Math.pow((x.getLayoutX() - l.getEndX()), 2) + Math.pow((x.getLayoutY() - l.getEndY()), 2));
        if(distance - 25 <= radius){
            return true;
        }else{
            return false;
        }

    }


    public Stage getStage() {
        return stage;
    }

    public URL getBoardURL() {
        return boardURL;
    }
}


