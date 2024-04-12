package com.example.boardgame;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.Objects;

public class Atoms {
    private static boolean gamestart = false;
    private static int atomcounter = 0;
    private static int counter = 0;
    //Red spheres are the one the experimenter places
    private static final ArrayList<Sphere> spheres_red = new ArrayList<>();
    private static final ArrayList<Sphere> spheres_guess = new ArrayList<>();


    public static Sphere placeAtomsinHex(MouseEvent event, int atomcount, Button start_end_button) {
        Polygon hexagon = (Polygon) event.getSource();
        Sphere sphere = new Sphere(30);
        double x = hexagon.getLayoutX();
        double y = hexagon.getLayoutY();
        Pane parent = (Pane) hexagon.getParent();
        if (atomcount < 6) {
            Circle sphere1 = new Circle(90);
            // set the sphere color to red
            sphere.setMaterial(new PhongMaterial(Color.RED));
            sphere.setLayoutX(x);
            sphere.setLayoutY(y);
            sphere1.setLayoutX(x);
            sphere1.setLayoutY(y);
            sphere1.setStroke(Color.WHITE);
            sphere1.setFill(Color.TRANSPARENT);
            sphere1.setMouseTransparent(true);
            // get the parent node of the button
            GameController.setSpherepane((Pane) hexagon.getParent());
            // replace the button with the sphere
            parent.getChildren().add(sphere);
            parent.getChildren().add(sphere1);
            GameController.setAtomcount();
            hexagon.setDisable(true);
            spheres_red.add(sphere);
            atomcounter++;
            return sphere;
        } else if (spheres_guess.size() < spheres_red.size() && gamestart && !Objects.equals(start_end_button.getText(), "Start Guessing")) {
            // set the sphere color to red
            sphere.setMaterial(new PhongMaterial(Color.LIGHTGREY));
            sphere.setLayoutX(x);
            sphere.setLayoutY(y);
            // get the parent node of the button
            GameController.setSpherepane((Pane) hexagon.getParent());
            // replace the button with the sphere
            parent.getChildren().add(sphere);
            GameController.setAtomcount();
            hexagon.setDisable(true);
            spheres_guess.add(sphere);
        }
        return null;
    }

    public static void invisibleAtoms(int atomcount, Pane spherepane, Button startButton) {
        //If we have a normal amount of atoms placed
        counter++;
            if (atomcount >= 3 && atomcount <= 6) {
                //go through all the children of the pane spherepane
                for (Node child : spherepane.getChildren()) {

                    if (child instanceof Polygon) {
                        child.setDisable(false);
                    }
                    //if the child is a sphere
                    if (child instanceof Sphere sphere) {
                        //make the child a sphere, so we can use sphere methods
                        //make the sphere invisible/visible depending on the return of isVisible
                        if (GameController.checkTest == 0) {
                            sphere.setVisible(!sphere.isVisible());
                        }
                        //Check for radius is because we do not want to get rid of markers
                    } else if (child instanceof Circle circle && ((Circle) child).getRadius() > 10) {
                        if (GameController.checkTest == 0) {
                            circle.setVisible(!circle.isVisible());
                        }
                    }
                }
                gamestart = true;
                GameController.setAtomcount(6);
                startButton.setText("Start Guessing");
            }
            if (gamestart && GameController.getAtomcount() == 6 && counter >= 2) {
                if(counter == 2) {
                    startButton.setText("End Game");
                    gamestart = true;
                    return;
                }
                    gamestart = false;
                    if(atomcounter > spheres_guess.size()){
                        System.out.println("Finish making guesses");
                    }else {
                        for (int i = 0; i < spheres_red.size(); i++) {
                            if (spheres_red.get(i).getLayoutX() != spheres_guess.get(i).getLayoutX() && spheres_red.get(i).getLayoutY() != spheres_guess.get(i).getLayoutY()) {
                                GameController.addScore5();
                            }
                        }
                    }
                }
            }
    }

