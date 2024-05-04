package com.example.boardgame;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class RayHelpers {

    public static ArrayList<Circle> rayMarkerList = new ArrayList<>();

    public static void setStartofRay(Line newLine) {

        double tempendX = newLine.getEndX();
        double tempendY = newLine.getEndY();

        newLine.setStartX(tempendX);
        newLine.setStartY(tempendY);
        newLine.setEndY(newLine.getStartY());
        newLine.setEndX(newLine.getStartX());
    }

    public static void setStartofDiagonalHelper(Rectangle b, Line newLine, boolean direction){
        double startX = 0, startY = 0;
        if(direction) {
            if (GameController.right) {
                startX = b.getLayoutX() + b.getWidth() / 2 + 8;
                startY = b.getLayoutY() + b.getHeight() / 2 + 10;
            } else if (GameController.left) {
                startX = b.getLayoutX() + b.getWidth() / 2 - 8;
                startY = b.getLayoutY() + b.getHeight() / 2 + 10;
            }
        }else {
            if (GameController.right) {
                startX = b.getLayoutX() + b.getWidth() / 2 + 4;
                startY = b.getLayoutY() + b.getHeight() / 2 - 13;
            } else {
                startX = b.getLayoutX() + b.getWidth() / 2;
                startY = b.getLayoutY() + b.getHeight() / 2;
            }
        }

        newLine.setStartX(startX);
        newLine.setStartY(startY);
        GameController.originalLineY = startY;
        GameController.originalLineX = startX;
        newLine.setEndX(startX);
        newLine.setEndY(startY);
    }

    public static void setOldRay(Line oldLine, Line newLine, Color color) {
        oldLine.setStroke(color);
        oldLine.setStartY(newLine.getStartY());
        oldLine.setStartX(newLine.getStartX());
        oldLine.setEndY(newLine.getEndY());
        oldLine.setEndX(newLine.getEndX());
    }

    public static void setStartHorizontalRay(Line newLine, Rectangle rectangle) {
        newLine.setStartX(rectangle.getLayoutX() + rectangle.getWidth());
        newLine.setStartY(rectangle.getLayoutY() + 10);
        newLine.setEndX(newLine.getStartX());
        newLine.setEndY(newLine.getStartY());
    }

    public static void setStartsIndsideHelper(int result, Line newLine, Rectangle inputNode, TextArea textBox, int diagonal) {
        Pane boardPane = (Pane) inputNode.getParent();
        int angle = 0;
        if (diagonal == 0) {
            if (inputNode.getLayoutX() > 250) {
                angle = 180;
            }
        }else if (diagonal == 1) {
            if(GameController.left){
                angle = 121;
            }else {
                angle = 59;
            }
        }else {
            if(GameController.left){
                angle = 59;
            }else {
                angle = 121;
            }
        }
        double angleRadians = Math.toRadians(angle);
        Circle newCircleStart = new Circle();
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        for (int i = 0; i < 50; i++) {
            if(diagonal != 2) {
                newLine.setEndX(newLine.getEndX() + dx);
                newLine.setEndY(newLine.getEndY() + dy);
            }else{
                newLine.setEndX(newLine.getEndX() - dx);
                newLine.setEndY(newLine.getEndY() - dy);
            }
        }
        if (result == 1) {
            placeWhiteMarker(textBox, inputNode);
        } else {
           placeBlackMarker(boardPane);
           textBox.appendText("Ray hit an atom\n");
        }
        boardPane.getChildren().add(newCircleStart);
        boardPane.getChildren().add(newLine);
        GameController.lines.add(newLine);
        rayMarkerList.add(newCircleStart);
    }

    public static void deflection (Color direction, Line oldLine, Pane p){
            p.getChildren().add(oldLine);
            GameController.lines.add(oldLine);
        GameController.direction_tester = direction;
    }

    public static void markerHelper(Color circleColor, Line newLine, Pane p, double angle){
        double actualAngle = Math.toRadians(angle);
        double dx = Math.cos(actualAngle);
        double dy = Math.sin(actualAngle);
        Circle newCircleEnd = new Circle();
        Circle newCircleStart = new Circle();
        newCircleEnd.setFill(circleColor);
        newCircleStart.setFill(circleColor);
        newCircleStart.setRadius(10);
        newCircleEnd.setRadius(10);
        newCircleStart.setCenterY(GameController.originalLineY);
        newCircleStart.setCenterX(GameController.originalLineX);
        newCircleEnd.setCenterX(newLine.getEndX() + dx);
        newCircleEnd.setCenterY(newLine.getEndY() + dy);
        p.getChildren().add(newCircleEnd);
        p.getChildren().add(newCircleStart);
        rayMarkerList.add(newCircleEnd);
        rayMarkerList.add(newCircleStart);
    }

    public static void placeWhiteMarker(TextArea textBox, Rectangle inputNode){
        Pane boardPane = (Pane) inputNode.getParent();
        Circle newCircleStart = new Circle();
        newCircleStart.setFill(Color.WHITE);
        newCircleStart.setRadius(10);
        newCircleStart.setCenterY(GameController.originalLineY);
        newCircleStart.setCenterX(GameController.originalLineX);
        boardPane.getChildren().add(newCircleStart);
        rayMarkerList.add(newCircleStart);
        textBox.appendText("Ray reflected and exited at " + inputNode.getId() + "\n");
    }

    public static void placeBlackMarker(Pane p){
        Circle startcircle = new Circle();
        startcircle.setCenterX(GameController.originalLineX);
        startcircle.setCenterY(GameController.originalLineY);
        startcircle.setStroke(Color.WHITE);
        startcircle.setRadius(10);
        startcircle.setFill(Color.BLACK);
        p.getChildren().add(startcircle);
        rayMarkerList.add(startcircle);
    }

    public static void moveRay(Line newLine, int angle, boolean positive)
    {
        double movingangle = Math.toRadians(angle);
        double dx = Math.cos(movingangle);
        double dy = Math.sin(movingangle);

        if(positive) {
            newLine.setEndX(newLine.getEndX() + dx);
            newLine.setEndY(newLine.getEndY() + dy);
        }else{
            newLine.setEndX(newLine.getEndX() - dx);
            newLine.setEndY(newLine.getEndY() - dy);
        }
    }

    public static void removeRayMarkers(){
        Platform.runLater(() -> { //to avoid threading issues

            //remove red spheres
            for (Circle c : rayMarkerList) {
                Parent parent = c.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(c);
                    System.out.println("marker removed");
                } else {
                    throw new UnsupportedOperationException("failed to remove marker");
                }
            }

            rayMarkerList.clear();
        });
    }

    public static void addRay(Pane boardPane, Line oldLine){
        boardPane.getChildren().add(oldLine);
        oldLine.setVisible(false);
        GameController.lines.add(oldLine);
    }

}
