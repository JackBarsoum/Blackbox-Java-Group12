package com.example.boardgame;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;

/**
 * @author Jack Barsoum, Oisin Lynch, Carol
 * This class handles the tricky logic of deflection cases
 */
public class DeflectionHelpers {
    /**
     * @param x the current sphere we are near
     * @param l our current line/ray
     * @return a boolean showing if our line/ray is currently in a sphere
     * This method checks to see if our ray is currently in a sphere
     */
    public static boolean isInside(Sphere x, Line l) {
        double radius = x.getRadius();

        double distance = Math.sqrt(Math.pow((x.getLayoutX() - l.getEndX()), 2) + Math.pow((x.getLayoutY() - l.getEndY()), 2));
        return distance <= radius;
    }

    /**
     * @param l         our current line/ray
     * @param boardPane the pane which contains all the circles of influence
     * @param angle     the current angle of our line
     * @param direction the current direction of our line
     * @return an integer flag stating if we started inside a circle of influence or not
     * This method checks the state of our ray and the circle of influence
     * it potentially starts in
     */
    public static int startsInside(Line l, Pane boardPane, int angle, int direction) {
        l.setVisible(false);
        for (Node node : boardPane.getChildren()) {
            if (l.getBoundsInParent().intersects(node.getBoundsInParent()) && node instanceof Circle) {
                double distance = switch (direction) {
                    case 0, 3 -> Math.hypot(node.getLayoutX() - l.getStartX(), node.getLayoutY() - l.getStartY());
                    case 1, 2, 4 -> Math.hypot(l.getStartX() - node.getLayoutX(), l.getStartY() - node.getLayoutY());
                    default -> 0;
                };
                boolean condition2 = isInsideC((Circle) node, l, angle, true) == 2;
                boolean condition = (direction == 0 || direction == 2 || direction == 1) && isInsideC((Circle) node, l, angle, false) == 2;
                if (distance < 85) {
                    if (condition || condition2) {
                        return 3;
                    } else {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * @param c     the circle of influence we are near
     * @param l     our current line/ray
     * @param angle the angle our ray is heading
     * @param up    boolean to see if the ray is heading up or down
     * @return an integer flag depending on the current condition of our line and circle of influence
     * This method aims to check the state of a line and if it is currently inside a
     * circle of influence
     */
    public static int isInsideC(Circle c, Line l, int angle, boolean up) {
        l.setVisible(false);
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
                    lineTest.setEndY(lineTest.getEndY() - dy);
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

    /**
     * @param newLine        our current line/ray
     * @param angle_of_ray   an integer containing information on the current angle of our ray
     * @param prevNode       the second potential circle/circle of influence
     * @param direction_up   a boolean to see if our ray is heading up or down
     * @param direction_diag a boolean to see if our ray is heading diagonal or horizontal
     * @return an integer flag depending on the current condition of our line
     * and the circle of influence
     * This method aims to check the state of our ray and if it is currently inside two
     * circle of influences
     */
    public static int checkifDouble(Line newLine, int angle_of_ray, Node prevNode, boolean direction_up, boolean direction_diag) {
        Pane p = (Pane) prevNode.getParent();
        newLine.setVisible(false);
        double angleRadians = Math.toRadians(angle_of_ray);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        Line tempLine = new Line();
        int correct = 0;

        tempLine.setStartX(newLine.getEndX());
        tempLine.setStartY(newLine.getEndY());

        for (Node node : p.getChildren()) {
            if (node != prevNode && node instanceof Circle circle) {
                Circle circle2 = (Circle) prevNode;
                double radius = circle.getRadius();

                for (int j = 0; j < 10; j++) {
                    if (direction_up) {
                        newLine.setEndX(newLine.getEndX() - dx);
                        newLine.setEndY(newLine.getEndY() - dy);
                    } else {
                        newLine.setEndX(newLine.getEndX() + dx);
                        newLine.setEndY(newLine.getEndY() + dy);
                    }


                    // Check if the tempLine intersects with the circle's bounds
                    if (newLine.getBoundsInParent().intersects(circle.getBoundsInParent()) && circle.getRadius() == 90) {
                        double distance = Math.hypot(circle.getLayoutX() - newLine.getEndX(), circle.getLayoutY() - newLine.getEndY());
                        double distance1 = Math.hypot(circle2.getLayoutX() - newLine.getEndX(), circle2.getLayoutY() - newLine.getEndY());
                        boolean inBounds1 = distance <= radius + 15 && distance1 <= radius + 15;
                        boolean inBounds2 = direction_diag && distance <= radius + 20 && distance1 <= radius + 20;
                        if (inBounds1 || inBounds2) {
                            correct = 1;
                            break;
                        } else {
                            correct = 0;
                            break;
                        }
                    }
                }

                double distBetweenCircles = Math.hypot(circle.getLayoutX() - circle2.getLayoutX(), circle.getLayoutY() - circle2.getLayoutY());
                boolean circleXequal = circle.getLayoutX() == circle2.getLayoutX();
                if ((circleXequal && direction_diag && correct == 1) || (direction_diag && distBetweenCircles <= 90 && correct == 1)) {
                    newLine.setEndX(tempLine.getStartX());
                    newLine.setEndY(tempLine.getStartY());
                    return 1;
                } else if (correct == 1 && direction_diag && distBetweenCircles > 90) {
                    newLine.setEndX(tempLine.getStartX());
                    newLine.setEndY(tempLine.getStartY());
                    return 4;
                } else if (!direction_diag) {
                    double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                    double averageX = (node.getLayoutX() + prevNode.getLayoutX()) / 2;
                    boolean xCorrect = circle.getLayoutX() == circle2.getLayoutX();
                    boolean yInbounds = newLine.getEndY() > averageY - 10 && newLine.getEndY() < averageY + 10;
                    boolean yInbounds2 = newLine.getEndX() > averageX - 60 && newLine.getEndX() < averageX + 60;
                    if (yInbounds && correct == 1 && yInbounds2 && xCorrect) {
                        return 3;
                    }
                }
            }
        }
        newLine.setEndX(tempLine.getStartX());
        newLine.setEndY(tempLine.getStartY());
        return correct;
    }

    /**
     * @param newLine      our current line/ray
     * @param angleRay     an integer containing information on the current angle of our ray
     * @param node1        one of the potential circle of influences near our ray
     * @param node2        one of the potential circle of influences near our ray
     * @param direction_up a boolean containing information if our ray is heading up or down
     * @return an integer flag depending on the state of the line and the
     * amount of circle of influences it touches
     * This method aims to check the state of the ray and if it is currently inside three
     * circle of influences
     */
    public static int checkTriple(Line newLine, int angleRay, Node node1, Node node2, boolean direction_up) {
        Pane p = (Pane) node1.getParent();
        newLine.setVisible(false);
        double angleRadians = Math.toRadians(angleRay);
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
            boolean nodesXLayout_correct = node2 != node1 && node.getLayoutX() != node1.getLayoutX() && node2.getLayoutX() != node.getLayoutX();
            if (node != node1 && node != node2 && node instanceof Circle circle && nodesXLayout_correct) {
                double radius = circle.getRadius();

                for (int j = 0; j < 10; j++) {
                    if (direction_up) {
                        newLine.setEndX(newLine.getEndX() - dx);
                        newLine.setEndY(newLine.getEndY() - dy);
                    } else {
                        newLine.setEndX(newLine.getEndX() + dx);
                        newLine.setEndY(newLine.getEndY() + dy);
                    }
                    // Check if the tempLine intersects with the circle's bounds
                    if (newLine.getBoundsInParent().intersects(circle.getBoundsInParent())) {
                        double distance = Math.hypot(circle.getLayoutX() - newLine.getEndX(), circle.getLayoutY() - newLine.getEndY());
                        double distance1 = Math.hypot(node1.getLayoutX() - newLine.getEndX(), node1.getLayoutY() - newLine.getEndY());
                        double distance2 = Math.hypot(node2.getLayoutX() - newLine.getEndX(), node2.getLayoutY() - newLine.getEndY());
                        if (distance <= radius && distance2 <= radius && distance1 <= radius) {
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
