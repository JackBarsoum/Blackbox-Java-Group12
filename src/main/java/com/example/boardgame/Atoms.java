package com.example.boardgame;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;

/**
 * @author Jack Barsoum, Oisin Lynch, Carol Ezzeddine
 * This class handles the logic behind Atoms in our code this includes the circle of influence as well
 */
public class Atoms {
    private static boolean gamestart = false;
    private static int count_gamestate = 0;

    //Red spheres are the one the experimenter places
    private static ArrayList<Sphere> spheres_red = new ArrayList<>();
    private static ArrayList<Circle> circles = new ArrayList<>();
    private static ArrayList<Sphere> spheres_guess = new ArrayList<>();

    public static void resetvalues() {
        gamestart = false;
        count_gamestate = 0;
    }

    /**
     * @param event the original mouse event where the user clicked
     * @return sphere returns a sphere unless in the case where we have the max allowed atoms placed
     * This method handles the logic of placing an atom and its corresponding circle of influence
     */
    public static Sphere placeAtomsinHex(MouseEvent event) {
        int atomcount = GameController.getAtomcount();
        Polygon hexagon = (Polygon) event.getSource();
        Sphere sphere = new Sphere(30);
        double x = hexagon.getLayoutX();
        double y = hexagon.getLayoutY();
        Pane parent = (Pane) hexagon.getParent();
        if (atomcount < 6) {
            Circle sphere1 = new Circle(90);
            sphere.setMaterial(new PhongMaterial(Color.RED));
            sphere.setLayoutX(x);
            sphere.setLayoutY(y);
            sphere1.setLayoutX(x);
            sphere1.setLayoutY(y);
            sphere1.setStroke(Color.WHITE);
            sphere1.setFill(Color.TRANSPARENT);
            sphere1.setMouseTransparent(true);
            GameController.setSpherepane((Pane) hexagon.getParent());
            parent.getChildren().add(sphere);
            parent.getChildren().add(sphere1);
            GameController.setAtomcount();
            hexagon.setDisable(true);
            spheres_red.add(sphere);
            circles.add(sphere1);
            return sphere;
        } else if (spheres_guess.size() < spheres_red.size() && gamestart) {
            sphere.setMaterial(new PhongMaterial(Color.LIGHTGREY));
            sphere.setLayoutX(x);
            sphere.setLayoutY(y);
            GameController.setSpherepane((Pane) hexagon.getParent());
            parent.getChildren().add(sphere);
            GameController.setAtomcount();
            hexagon.setDisable(true);
            spheres_guess.add(sphere);
        }
        return null;
    }

    /**
     * @param spherepane the pane that contains all of our lines, circles and spheres
     *                   This method goes through all nodes in our pane and sets the visibility of
     *                   Spheres,Circles and lines to true to reveal our game history
     */
    public static void showHistory(Pane spherepane) {
        for (Node p : spherepane.getChildren()) {
            if (p instanceof Sphere || p instanceof Line || p instanceof Circle) {
                p.setVisible(true);
            }
        }
    }

    /**
     * @param spherepane       the pane with all our spheres,lines and circles
     * @param start_end_button the button to start/end our game
     * @param nextPlayer       the button to create player 2
     *                         This method turns atoms invisible depending on our game state and
     *                         handles the logic for the state of our button text and when to reveal history
     */
    public static void invisibleAtoms(Pane spherepane, Button start_end_button, Button nextPlayer) {
        int atomcount = GameController.getAtomcount();
        if (count_gamestate == 1) {
            showHistory(spherepane);
        } else if (count_gamestate == 0) {
            start_end_button.setText("Reveal Board");
        } else {
            start_end_button.setVisible(false);
            nextPlayer.setVisible(true);
        }
        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6 && count_gamestate == 0) {
            count_gamestate++;
            //go through all the children of the pane spherepane
            for (Node child : spherepane.getChildren()) {
                if (child instanceof Polygon) {
                    child.setDisable(false);
                }
                //if the child is a sphere
                if (child instanceof Sphere) {
                    //make the child a sphere, so we can use sphere methods
                    Sphere sphere = (Sphere) child;
                    //make the sphere invisible/visible depending on the return of isVisible
                    sphere.setVisible(!sphere.isVisible());
                    //Check for radius is because we do not want to get rid of markers
                } else if (child instanceof Circle && ((Circle) child).getRadius() > 10) {
                    Circle circle = (Circle) child;
                    circle.setVisible(!circle.isVisible());
                }
            }
            gamestart = true;
            GameController.setAtomcount(6);
        } else if (gamestart) {
            count_gamestate++;
            int count = 0;
            gamestart = false;
            for (int i = 0; i < spheres_red.size(); i++) {
                for (int j = 0; j < spheres_guess.size(); j++) {
                    boolean sphereXequal = spheres_red.get(i).getLayoutX() == spheres_guess.get(j).getLayoutX();
                    boolean sphereYequal = spheres_red.get(i).getLayoutY() == spheres_guess.get(j).getLayoutY();
                    if (sphereXequal && sphereYequal) {
                        count++;
                    }
                }
            }
            if (count < spheres_red.size()) {
                for (int p = 0; p < (spheres_red.size() - count); p++) {
                    GameController.addScore5();
                }
            }
        }
    }

    /**
     * @param spherepane the pane with all of our spheres,circles and lines
     *                   This method handles the removal of atoms for when we want to reset the board
     *                   for player 2
     */
    public static void removeAtoms(Pane spherepane) {
        Platform.runLater(() -> { //to avoid threading issues

            //remove red spheres
            for (Sphere s : spheres_red) {
                Parent parent = s.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(s);
                } else {
                    throw new UnsupportedOperationException("failed to remove sphere");
                }
            }

            //remove guessed spheres
            for (Sphere s : spheres_guess) {
                Parent parent = s.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(s);
                } else {
                    throw new UnsupportedOperationException("failed to remove sphere");
                }
            }

            //remove guessed spheres
            for (Circle c : circles) {
                Parent parent = c.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(c);
                } else {
                    throw new UnsupportedOperationException("failed to remove circle");
                }
            }
            //Allow you to click polygons again
            for (Node child : spherepane.getChildren()) {

                if (child instanceof Polygon) {
                    child.setDisable(false);
                }
            }
            resetvalues();
            //reset ArrayLists
            spheres_red.clear();
            spheres_guess.clear();
            circles.clear();

        });
    }
}