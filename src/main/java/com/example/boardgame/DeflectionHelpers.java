package com.example.boardgame;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;

public class DeflectionHelpers {
    public static boolean isInside(Sphere x, Line l) {
        double radius = x.getRadius();

        double distance = Math.sqrt(Math.pow((x.getLayoutX() - l.getEndX()), 2) + Math.pow((x.getLayoutY() - l.getEndY()), 2));
        if (distance <= radius) {
            return true;
        } else {
            return false;
        }
    }

    public static int startsInside(Line l, Pane p, int angle, int direction) {
        int result = 0;
        double distance = 0;
        for (Node node : p.getChildren()) {
            if (l.getBoundsInParent().intersects(node.getBoundsInParent()) && node instanceof Circle) {
                switch (direction) {
                    case 0, 3:
                        distance = Math.sqrt(Math.pow((node.getLayoutX() - l.getStartX()), 2) + Math.pow((node.getLayoutY() - l.getStartY()), 2));
                    case 1, 2, 4:
                        distance = Math.sqrt(Math.pow((l.getStartX() - node.getLayoutX()), 2) + Math.pow((l.getStartY() - node.getLayoutY()), 2));
                }
                if (distance < 85) {
                    if (direction == 0 || direction == 2 || direction == 1) {
                        if (isInsideC((Circle) node, l, angle, false) == 2) {
                            return 3;
                        }
                    } else {
                        System.out.println(320423);
                        if (isInsideC((Circle) node, l, angle, true) == 2) {
                            System.out.println("Hello");
                            return 3;
                        }
                    }
                    return 1;
                }
            }
        }
        return result;
    }


    public static int isInsideC(Circle c, Line l, int angle, boolean up) {
        double radius = c.getRadius();
        Pane p = (Pane) c.getParent();
        int checker = 0;
        //Distance formula to check if the ray actually hit the circle
        double distance = Math.sqrt(Math.pow((c.getLayoutX() - l.getEndX()), 2) + Math.pow((c.getLayoutY() - l.getEndY()), 2));
        if (distance <= radius) {
            checker = 1;
        } else {
            //If it doesn't hit the circle at all
            checker = -1;
        }

        //If the ray does hit the sphere of influence a prediction is needed to see if the ray will hit the atom or deflect
        if (checker == 1) {
            double tempEndX = l.getEndX();
            double tempEndY = l.getEndY();

            //Creates a sample line that starts where the ray hits the circle
            Line lineTest = new Line();
            lineTest.setStartX(tempEndX);
            lineTest.setStartY(tempEndY);
            lineTest.setEndY(tempEndY);
            lineTest.setEndX(tempEndX);

            //Sets the line to go at the trajectory that the ray is going at
            double angleRadians = Math.toRadians(angle);
            double dx = Math.cos(angleRadians);
            double dy = Math.sin(angleRadians);

            //Checks to see if the ray will hit the atom on its current trajectory
            for (int i = 0; i < 90; i++) {
                if (up) {
                    lineTest.setEndX(lineTest.getEndX() - dx);
                    lineTest.setEndY(lineTest.getEndY() + dy);
                } else {
                    lineTest.setEndX(lineTest.getEndX() + dx);
                    lineTest.setEndY(lineTest.getEndY() + dy);
                }

                for (Node node : p.getChildren()) {
                    if (lineTest.getBoundsInParent().intersects(node.getBoundsInParent())) {
                        if (node instanceof Sphere && isInside((Sphere) node, lineTest)) {
                            checker = 2; // If the ray will hit the atom on hits current trajectory than checker is set to two
                        }
                    }
                }
            }
        }

        return checker;
    }

    public static void printResults(int result, TextArea textBox, Node node) {
        switch (result) {
            case 0:
                textBox.appendText("Ray hit nothing and exited at " + node.getId() + "\n");
                System.out.println("Ray hit nothing and exited at " + node.getId());
                break;
            case 1:
                System.out.println("Ray deflected at 60 degrees, hit nothing and exited at " + node.getId());
                textBox.appendText("Ray deflected at 60 degrees, hit nothing and exited at " + node.getId() + "\n");
                break;
            case 2:
                System.out.println("Ray deflected at 120 degrees, hit nothing and exited at " + node.getId());
                textBox.appendText("Ray deflected at 120 degrees, hit nothing and exited at " + node.getId() + "\n");
                break;
        }
    }

    public static int checkifDouble(Pane p, Line newLine, int x, Node prevNode) {
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        Line tempLine = new Line();
        int correct = 0;

        tempLine.setStartX(newLine.getEndX());
        tempLine.setStartY(newLine.getEndY());

        // Track the number of intersecting circles


            // Reset the intersecting circles count for each iteration


            for (Node node : p.getChildren()) {
                if (node != prevNode && node instanceof Circle) {
                    Circle circle = (Circle) node;
                    double radius = circle.getRadius();

                    for (int j = 0; j < 20; j++) {
                        newLine.setEndX(newLine.getEndX() + dx);
                        newLine.setEndY(newLine.getEndY() + dy);
                    // Check if the tempLine intersects with the circle's bounds
                    if (newLine.getBoundsInParent().intersects(circle.getBoundsInParent())) {
                        double distance = Math.sqrt(Math.pow((circle.getLayoutX() - newLine.getEndX()), 2) + Math.pow((circle.getLayoutY() - newLine.getEndY()), 2));
                        System.out.println(distance);
                        System.out.println("RADIUS " + radius);
                        if (distance <= radius) {
                            correct = 1;
                            break;
                        } else {
                            //If it doesn't hit the circle at all
                            correct = 0;
                        }
                        // If two circles are intersecting, return true
                        }
                    }
                    System.out.println("Correctness = " + correct);
                    double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                    System.out.println("Node 1 Layout: " + node.getLayoutY());
                    System.out.println("Node 2 Layout: " + prevNode.getLayoutY());
                    System.out.println("Average: " + averageY);

                    System.out.println("Line end: " + newLine.getEndY());

                    if(newLine.getEndY() > averageY - 5 && newLine.getEndY() < averageY + 5  && correct == 1){
                       return 3;
                    }
                }
            }
            if(correct == 1){
                newLine.setEndX(tempLine.getStartX());
                newLine.setEndY(tempLine.getStartY());
                return 0;
            }
            newLine.setEndX(tempLine.getStartX());
            newLine.setEndY(tempLine.getStartY());
            return 1;
        }


        // No intersection with two circles found after all iterations

    }
