package com.example.boardgame;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Sphere;

public class Atoms {
    public static Sphere placeAtomsinHex(MouseEvent event, int atomcount){
        if (atomcount < 6) {
            Polygon hexagon = (Polygon) event.getSource();
            Sphere sphere = new Sphere(30);
            Circle sphere1 = new Circle(90);
            double x = hexagon.getLayoutX();
            double y = hexagon.getLayoutY();

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
            Pane parent = (Pane) hexagon.getParent();
            GameController.setSpherepane((Pane) hexagon.getParent());
            // replace the button with the sphere
            parent.getChildren().add(sphere);
            parent.getChildren().add(sphere1);
            GameController.setAtomcount();
            return sphere;
        }
        return null;
    }

    public static void invisbleAtoms(ActionEvent e, int atomcount, Pane spherepane) {
        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6) {
            //go through all the children of the pane spherepane
            for (Node child : spherepane.getChildren()) {
                //if the child is a sphere
                if (child instanceof Sphere) {
                    //make the child a sphere so we can use sphere methods
                    Sphere sphere = (Sphere) child;
                    //make the sphere invisible/visible depending on the return of isVisible
                    sphere.setVisible(!sphere.isVisible());
                } else if (child instanceof Circle) {
                    Circle circle = (Circle) child;
                    circle.setVisible(!circle.isVisible());
                }
            }
            GameController.setAtomcount(6);
        }
    }
}
