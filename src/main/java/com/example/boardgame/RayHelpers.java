package com.example.boardgame;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class RayHelpers {

    public static void setStartofRay(Line newLine) {

        double tempendX = newLine.getEndX();
        double tempendY = newLine.getEndY();

        newLine.setStartX(tempendX);
        newLine.setStartY(tempendY);
        newLine.setEndY(newLine.getStartY());
        newLine.setEndX(newLine.getStartX());
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

    public static void setStartsIndsideHelper(int result, Line newLine, Pane p, Rectangle rectangle, TextArea textBox, boolean diagonal) {
        int angle = 0;
        if (!diagonal) {
            if (rectangle.getLayoutX() > 250) {
                angle = 180;
            }
        }else {
            if(GameController.left){
                angle = 121;
            }else {
                angle = 59;
            }
        }
        double angleRadians = Math.toRadians(angle);
        Circle newCircleStart = new Circle();
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);
        for (int i = 0; i < 50; i++) {
            newLine.setEndX(newLine.getEndX() + dx);
            newLine.setEndY(newLine.getEndY() + dy);
        }
        if (result == 1) {
            textBox.appendText("Ray deflected at 180 and exited at " + rectangle.getId() + "\n");
            Circle newCircleEnd = new Circle();
          //  newCircleEnd.setFill(GameController.circleColor);
            newCircleStart.setFill(Color.WHITE);
            newCircleStart.setRadius(10);
            newCircleEnd.setRadius(10);
            newCircleStart.setCenterY(GameController.originalLineY);
            newCircleStart.setCenterX(GameController.originalLineX);
            newCircleEnd.setCenterX(newLine.getEndX() + dx);
            newCircleEnd.setCenterY(newLine.getEndY() + dy);
            //  p.getChildren().add(newCircleEnd);
            // p.getChildren().add(newCircleStart);
        } else {
            newCircleStart.setCenterX(GameController.originalLineX);
            newCircleStart.setCenterY(GameController.originalLineY);
            newCircleStart.setStroke(Color.WHITE);
            newCircleStart.setRadius(10);
            newCircleStart.setFill(Color.BLACK);
           textBox.appendText("Ray hit an atom\n");
        }
        p.getChildren().add(newCircleStart);
        p.getChildren().add(newLine);
    }

    public static void deflection (Color direction, Line oldLine, int CheckTesting, Pane p){
        if (CheckTesting != 0) p.getChildren().add(oldLine);
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
    }

    public static void placeWhiteMarker(Pane p, TextArea textBox, Rectangle b){
        Circle newCircleStart = new Circle();
        newCircleStart.setFill(Color.WHITE);
        newCircleStart.setRadius(10);
        newCircleStart.setCenterY(GameController.originalLineY);
        newCircleStart.setCenterX(GameController.originalLineX);
        p.getChildren().add(newCircleStart);
        textBox.appendText("Ray reflected 180 degrees and exited at " + b.getId());
    }

    public static void placeBlackMarker(Pane p){
        Circle startcircle = new Circle();
        startcircle.setCenterX(GameController.originalLineX);
        startcircle.setCenterY(GameController.originalLineY);
        startcircle.setStroke(Color.WHITE);
        startcircle.setRadius(10);
        startcircle.setFill(Color.BLACK);
        p.getChildren().add(startcircle);
    }

    public static void moveRayPositive(Line newLine, int angle)
    {
        double movingangle = Math.toRadians(angle);
        double dx = Math.cos(movingangle);
        double dy = Math.sin(movingangle);

        newLine.setEndX(newLine.getEndX() + dx);
        newLine.setEndY(newLine.getEndY() + dy);
    }

}
