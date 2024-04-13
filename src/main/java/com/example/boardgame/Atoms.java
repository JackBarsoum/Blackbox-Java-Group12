package com.example.boardgame;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;

public class Atoms {
    private static boolean gamestart = false;
    private static int count_gamestate = 0;
    //Red spheres are the one the experimenter places
    private static ArrayList<Sphere> spheres_red = new ArrayList<>();
    private static ArrayList<Circle> circles = new ArrayList<>();
    private static ArrayList<Sphere> spheres_guess = new ArrayList<>();

    public static Sphere placeAtomsinHex(MouseEvent event, int atomcount) {
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
            circles.add(sphere1);
            return sphere;
        } else if (spheres_guess.size() < spheres_red.size() && gamestart) {
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

    public static void invisibleAtoms(int atomcount, Pane spherepane, Button start_end_button) {
        if(count_gamestate == 0)
        {
            start_end_button.setText("Start Guessing");
        }
        if(count_gamestate == 1)
        {
            start_end_button.setText("End Game");
        }

        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6) {
            //go through all the children of the pane spherepane
            for (Node child : spherepane.getChildren()) {

                if (child instanceof Polygon) {
                    child.setDisable(false);
                }
                //if the child is a sphere
                if (child instanceof Sphere) {
                    //make the child a sphere so we can use sphere methods
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
            int count = 0;
            gamestart = false;
            for (int i = 0; i < spheres_red.size(); i++)
            {
                for (int j = 0; j < spheres_guess.size(); j++)
                {
                    if(spheres_red.get(i).getLayoutX() == spheres_guess.get(j).getLayoutX() && spheres_red.get(i).getLayoutY() == spheres_guess.get(j).getLayoutY())
                    {
                        count++;
                    }
                }

            }
            if(count < spheres_red.size())
            {
                for (int p = 0; p < (spheres_red.size()-count);p++)
                {
                    GameController.addScore5();
                }
            }

        }
        count_gamestate++;
    }

    public static void removeAtoms(){

        Platform.runLater(() -> { //to avoid threading issues

            //remove red spheres
            for (Sphere s : spheres_red) {
                Parent parent = s.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(s);
                    System.out.println("Sphere removed");
                } else {
                    throw new UnsupportedOperationException("failed to remove sphere");
                }
            }

            //remove guessed spheres
            for (Sphere s : spheres_guess) {
                Parent parent = s.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(s);
                    System.out.println("Sphere removed");
                } else {
                    throw new UnsupportedOperationException("failed to remove sphere");
                }
            }

            //remove guessed spheres
            for (Circle c : circles) {
                Parent parent = c.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(c);
                    System.out.println("Circle removed");
                } else {
                    throw new UnsupportedOperationException("failed to remove circle");
                }
            }

            //reset ArrayLists
            spheres_red.clear();
            spheres_guess.clear();
            circles.clear();

        });
    }
}