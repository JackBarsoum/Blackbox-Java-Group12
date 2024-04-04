package com.example.boardgame;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;

public class DeflectionHelpers {
    public static boolean isInside(Sphere x, Line l) {
        double radius = x.getRadius();

        double distance = Math.sqrt(Math.pow((x.getLayoutX() - l.getEndX()), 2) + Math.pow((x.getLayoutY() - l.getEndY()), 2));
        return distance <= radius;
    }

    public static int startsInside(Line l, Pane p, int angle, int direction) {
        for (Node node : p.getChildren()) {
            if (l.getBoundsInParent().intersects(node.getBoundsInParent()) && node instanceof Circle) {
                double distance = switch (direction) {
                    case 0, 3 -> Math.hypot(node.getLayoutX() - l.getStartX(), node.getLayoutY() - l.getStartY());
                    case 1, 2, 4 -> Math.hypot(l.getStartX() - node.getLayoutX(), l.getStartY() - node.getLayoutY());
                    default -> 0;
                };
                if (distance < 85) {
                    if ((direction == 0 || direction == 2 || direction == 1) && isInsideC((Circle) node, l, angle, false) == 2 || (direction != 0 && direction != 2 && direction != 1) && isInsideC((Circle) node, l, angle, true) == 2) {
                        return 3;
                    } else {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }


    public static int isInsideC(Circle c, Line l, int angle, boolean up) {
        double radius = c.getRadius();
        Pane p = (Pane) c.getParent();
        double distance = Math.hypot(c.getLayoutX() - l.getEndX(), c.getLayoutY() - l.getEndY());
        int checker = distance <= radius ? 1 : -1;

        if (checker == 1) {
            double tempEndX = l.getEndX();
            double tempEndY = l.getEndY();
            Line lineTest = new Line(tempEndX, tempEndY, tempEndX, tempEndY);

            double angleRadians = Math.toRadians(angle);
            double dx = Math.cos(angleRadians);
            double dy = Math.sin(angleRadians);

            for (int i = 0; i < 90; i++) {
                if (up) {
                    lineTest.setEndX(lineTest.getEndX() - dx);
                    lineTest.setEndY(lineTest.getEndY() + dy);
                } else {
                    lineTest.setEndX(lineTest.getEndX() + dx);
                    lineTest.setEndY(lineTest.getEndY() + dy);
                }

                for (Node node : p.getChildren()) {
                    if (lineTest.getBoundsInParent().intersects(node.getBoundsInParent()) && node instanceof Sphere && isInside((Sphere) node, lineTest)) {
                        return 2;
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

    public static int checkifDouble(Pane p, Line newLine, int x, Node prevNode, boolean up, boolean diagonal) {
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        Line tempLine = new Line();
        int correct = 0;

        tempLine.setStartX(newLine.getEndX());
        tempLine.setStartY(newLine.getEndY());

        for (Node node : p.getChildren()) {
            if (node != prevNode && node instanceof Circle) {
                Circle circle = (Circle) node;
                Circle circle2 = (Circle) prevNode;
                double radius = circle.getRadius();

                for (int j = 0; j < 10; j++) {
                    if (up) newLine.setEndX(newLine.getEndX() - dx);
                    else newLine.setEndX(newLine.getEndX() + dx);
                    newLine.setEndY(newLine.getEndY() + dy);

                    // Check if the tempLine intersects with the circle's bounds
                    if (newLine.getBoundsInParent().intersects(circle.getBoundsInParent()) && circle.getRadius() == 90) {
                        double distance = Math.hypot(circle.getLayoutX() - newLine.getEndX(), circle.getLayoutY() - newLine.getEndY());
                        double distance1 = Math.hypot(circle2.getLayoutX() - newLine.getEndX(), circle2.getLayoutY() - newLine.getEndY());
                        if ((distance <= radius + 15 && distance1 <= radius + 15 ) || (diagonal && distance <= radius + 20 && distance1 <= radius + 20)) {
                            correct = 1;
                            break;
                        } else {
                            correct = 0;
                            break;
                        }
                    }
                }

                double distBetweenCircles = Math.hypot(circle.getLayoutX() - circle2.getLayoutX(), circle.getLayoutY() - circle2.getLayoutY());

                if ((circle.getLayoutX() == circle2.getLayoutX() && diagonal && correct == 1) || (diagonal && distBetweenCircles <= 90 && correct == 1)) {
                    newLine.setEndX(tempLine.getStartX());
                    newLine.setEndY(tempLine.getStartY());
                    return 1;
                } else if (correct == 1 && diagonal && distBetweenCircles > 90) {
                    newLine.setEndX(tempLine.getStartX());
                    newLine.setEndY(tempLine.getStartY());
                    return 4;
                } else if (!diagonal) {
                    double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                    double averageX = (node.getLayoutX() + prevNode.getLayoutX()) / 2;
                    if (newLine.getEndY() > averageY - 10 && newLine.getEndY() < averageY + 10 && correct == 1 && newLine.getEndX() > averageX - 60 && newLine.getEndX() < averageX + 60 && circle.getLayoutX() == circle2.getLayoutX()) {
                        return 3;
                    }
                }
            }
        }
        newLine.setEndX(tempLine.getStartX());
        newLine.setEndY(tempLine.getStartY());
        return correct;
    }

    public static int checkTriple(Line newLine, int x, Pane p, Node node1, Node node2, boolean up) {
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        Line tempLine = new Line();
        int correct = 0;

        if (node2 == null) {
            return 0;
        }

        tempLine.setStartX(newLine.getEndX());
        tempLine.setStartY(newLine.getEndY());

        for (Node node : p.getChildren()) {
            if (node != node1 && node != node2 && node instanceof Circle circle && node2 != node1 && node.getLayoutX() != node1.getLayoutX() && node2.getLayoutX() != node.getLayoutX()) {
                double radius = circle.getRadius();

                for (int j = 0; j < 10; j++) {
                    if (up) {
                        newLine.setEndX(newLine.getEndX() - dx);
                        newLine.setEndY(newLine.getEndY() + dy);
                    } else {
                        newLine.setEndX(newLine.getEndX() + dx);
                        newLine.setEndY(newLine.getEndY() + dy);
                    }
                    // Check if the tempLine intersects with the circle's bounds
                    if (newLine.getBoundsInParent().intersects(circle.getBoundsInParent())) {
                        double distance = Math.hypot(circle.getLayoutX() - newLine.getEndX(), circle.getLayoutY() - newLine.getEndY());
                        if (distance <= radius) {
                            correct = 1;
                            newLine.setEndX(tempLine.getStartX());
                            newLine.setEndY(tempLine.getStartY());
                            return correct;
                        }
                    }
                }
            }
        }

        newLine.setEndX(tempLine.getStartX());
        newLine.setEndY(tempLine.getStartY());
        return correct;
    }
}
