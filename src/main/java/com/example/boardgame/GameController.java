package com.example.boardgame;
import static java.lang.System.exit;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class GameController {
    private Color direction_tester;
    private boolean right = false;
    private boolean left = false;
    private static int atomcount = 0;
    private static Pane spherepane;
    private static int score = 0;
    private Color circleColor;
    private static double originalLineX;
    private static double originalLineY;
  //  boolean reflected = false;

    public int getAtomcount() {
        return atomcount;
    }
    public static void setAtomcount(int atomcount1) {
        atomcount = atomcount1;
    }

    static void setSpherepane(Pane spherepane1) {
        spherepane = spherepane1;
    }

    public static void setAtomcount() {
        atomcount++;
    }

    @FXML
    public TextArea textBox; // Text box where results of rays shots will be displayed

    @FXML public TextArea textBoxScore;

    @FXML private Stage stage;
    private URL boardURL;

    private static Button b;

    @FXML
    void leave() {
        System.out.println("Quitting Game");
        exit(0);
    }

    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        b = (Button) event.getSource();
        boardURL = getClass().getResource("Board.fxml");
        assert boardURL != null;
        Parent root = FXMLLoader.load(boardURL);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static int checkTest = 0;
    public void checkTest() {
        //This method would check if user is in test mode or not
        //If the message below will appear letting them know
        if (b.getId().equals("Test") && checkTest == 0) {
            TextArea textArea = textBox;
            textArea.appendText("TESTING MODE ACTIVE\n");
            checkTest++;
        }
    }

    private void RayShotScore()
    {
        textBoxScore.clear();
        score++;
        textBoxScore.appendText("Score: "+score);
    }

    @FXML
    public Sphere handleButtonClick(MouseEvent event) {
        // create a new sphere
        return Atoms.placeAtomsinHex(event, getAtomcount());
    }

    @FXML
    void extendLineHorizontal(MouseEvent e) {
        RayShotScore();
        Circle newCircleStart = new Circle();
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        b.setDisable(true);
        Pane p = (Pane) b.getParent();
        if(checkTest != 0) System.out.println("Ray shot from " + b.getId() + "\n");
        textBox.appendText("Ray shot from " + b.getId() + "\n");
        RandomColorGen();
        newLine.setStartX(b.getLayoutX() + b.getWidth());
        newLine.setStartY(b.getLayoutY() + 10);
        newLine.setEndX(newLine.getStartX());
        newLine.setEndY(newLine.getStartY());
        direction_tester = Color.BLACK;

        if (b.getLayoutX() <= 250) // Case for being on the left side
        {
            int result = DeflectionHelpers.startsInside(newLine, p, 0, 0);
            originalLineX = b.getLayoutX() + b.getWidth();
            originalLineY = b.getLayoutY() + 10;
            newLine.setStartY(newLine.getStartY() - 2);
            newLine.setEndY(newLine.getStartY());
            //If this returns one the node is already in a Circle of Influence so the ray will either be reflected 180 or will hit an atom
            if (result == 1 || result == 3) {
                double angleRadians = Math.toRadians(0);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 50; i++) {
                    newLine.setEndX(newLine.getEndX() + dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {
                    textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                    Circle newCircleEnd = new Circle();
                    newCircleEnd.setFill(circleColor);
                    newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                    //  p.getChildren().add(newCircleEnd);
                    // p.getChildren().add(newCircleStart);
                } else {
                    newCircleStart.setCenterX(originalLineX);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setStroke(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleStart.setFill(Color.BLACK);
                    textBox.appendText("Ray hit an atom\n");
                }
                p.getChildren().add(newCircleStart);
                p.getChildren().add(newLine);
            }else {
                //Otherwise it is not inside a circle of influence, or it will hit an atom when shot
                extendRayHorizontalHelper(e, newLine, p, b, 0, direction_tester);
            }
        } else if (b.getLayoutX() > 250) // Case for being to the right
        {
            int result = DeflectionHelpers.startsInside(newLine, p, 180, 2);
            originalLineX = b.getLayoutX() + b.getWidth();
            originalLineY = b.getLayoutY() + 10;
            if (result == 1 || result == 3) {
                double angleRadians = Math.toRadians(180);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 50; i++) {
                    newLine.setEndX(newLine.getEndX() + dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {
                    textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                    Circle newCircleEnd = new Circle();
                    newCircleEnd.setFill(circleColor);
                    newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                } else {
                    newCircleStart.setCenterX(originalLineX);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setStroke(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleStart.setFill(Color.BLACK);
                    textBox.appendText("Ray hit an atom\n");
                }
                p.getChildren().add(newCircleStart);
                p.getChildren().add(newLine);
            } else {
                extendRayHorizontalHelper(e, newLine, p, b, 180, direction_tester);
            }
        }
    }

    void extendRayHorizontalHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color) {
        Circle startcircle = new Circle();
        int checker = 0;
        int line_flag = 0;
        int deflection_account = 0; // This used to help display what happened to the array, e.g. if this is equal to 1 the ray is deflected by 60
        Pane forcircle = (Pane) b.getParent();
        // Set the trajectory of the ray
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        double tempendX = newLine.getEndX();
        double tempendY = newLine.getEndY();

        newLine.setStartX(tempendX);
        newLine.setStartY(tempendY);
        newLine.setEndY(newLine.getStartY());
        newLine.setEndX(newLine.getStartX());

        // Needed to hold the original line if the ray deflects
        Line oldLine = new Line();
        int flag = 0;
        Node prevNode = null;
        int i = 0;
        do {
            // If the ray deflects off the circle of influence and goes up or down the board
            newLine.setEndX(newLine.getEndX() + dx);
            newLine.setEndY(newLine.getEndY() + dy);
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    //If the ray hits nothing
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && b != node) {
                        Circle newCircleEnd = new Circle();
                        Circle newCircleStart = new Circle();
                            newCircleEnd.setFill(circleColor);
                            newCircleStart.setFill(circleColor);
                        newCircleStart.setRadius(10);
                        newCircleEnd.setRadius(10);
                        newCircleStart.setCenterY(originalLineY);
                        newCircleStart.setCenterX(originalLineX);
                        newCircleEnd.setCenterX(newLine.getEndX() + dx);
                        newCircleEnd.setCenterY(newLine.getEndY() + dy);
                        forcircle.getChildren().add(newCircleEnd);
                        forcircle.getChildren().add(newCircleStart);
                        // Line intersects with another rectangle
                        DeflectionHelpers.printResults(deflection_account, textBox, node);
                        flag = 1;
                        break;
                        //If the ray comes in contact with a circle of influence
                    } else if (node instanceof Circle && checker != 2 && i > 70) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, false);
                        if (checker != -1) {
                            deflection_account = 1;
                        }
                        prevNode = node;
                        //** This is for the 180 case.Check to see that if a ray hit a Circle of Influence that there is not another one directly on top of it
                        if (DeflectionHelpers.checkifDouble(p, newLine, x, prevNode, false, false) != 3) {
                            if (checker == 1) {
                                if (line_flag == 0) {
                                    oldLine.setStroke(Color.GREEN);
                                    newLine.setStroke(Color.YELLOW);

                                    //Stores the line before the ray is deflected
                                    oldLine.setStartY(newLine.getStartY());
                                    oldLine.setStartX(newLine.getStartX());
                                    oldLine.setEndY(newLine.getEndY());
                                    oldLine.setEndX(newLine.getEndX());

                                    // If the ray deflects at the top of the sphere of influence
                                    if (newLine.getEndY() < node.getLayoutY()) {
                                        if (newLine.getEndX() > node.getLayoutX()) {
                                            if (checkTest != 0) System.out.println("Horizontaltest1");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            direction_tester = Color.YELLOW;
                                            extendLineDiagonalUpHelper(e, newLine, p, b, -58, Color.YELLOW);
                                            return;
                                        } else {
                                            if (checkTest != 0) System.out.println("Horizontaltest2");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            direction_tester = Color.GREEN;
                                            extendLineDiagonalUpHelper(e, newLine, p, b, -121, Color.GREEN);
                                            return;
                                        }
                                    } else {
                                        if (newLine.getEndX() > node.getLayoutX()) {
                                            if (checkTest != 0) System.out.println("Horizontaltest3");
                                            direction_tester = Color.YELLOW;
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            extendLineDiagonalDownHelper(e, newLine, p, b, 122, Color.RED);
                                            return;
                                        } else {
                                            if (checkTest != 0) System.out.println("Horizontaltest4");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            direction_tester = Color.GREEN;
                                            extendLineDiagonalDownHelper(e, newLine, p, b, 58, Color.BLUE);
                                            return;
                                        }
                                    }
                                }
                                // Circle has been hit by the ray
                                line_flag += 2;
                            }
                        } else if (DeflectionHelpers.checkifDouble(p, newLine, x, prevNode, false, false) == 3) {
                            if (checkTest != 0) System.out.println("Ray was reflected at 180 degrees and exited at " + b.getId());
                            Circle newCircleStart = new Circle();
                                newCircleStart.setFill(Color.WHITE);
                            newCircleStart.setRadius(10);
                            newCircleStart.setCenterY(originalLineY);
                            newCircleStart.setCenterX(originalLineX);
                            forcircle.getChildren().add(newCircleStart);
                            textBox.appendText("Ray was reflected at 180 degrees and exited at " + b.getId());
                            flag = 1;
                            break;
                        }
                    } // Case if the ray hit 2 circles of influence at the same time
                    else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, false) != -1 && node != prevNode && i > 70) {
                        deflection_account = 2;
                        if (line_flag == 0) {
                            // Same as the case above, store the ray before the deflection in a new line
                            oldLine.setStroke(Color.YELLOW);
                            // The color of the line
                            newLine.setStroke(Color.GREEN);

                            oldLine.setStartY(newLine.getStartY());
                            oldLine.setStartX(newLine.getStartX());
                            oldLine.setEndY(newLine.getEndY());
                            oldLine.setEndX(newLine.getEndX());

                            double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;

                            // If the ray deflects at the top of the sphere of influence
                            if (newLine.getEndY() < averageY) {
                                if (x == 0) {
                                    if (checkTest != 0) System.out.println("Horizontal120test");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalUpHelper(e, newLine, p, b, -60, Color.YELLOW);
                                } else {
                                    if (checkTest != 0) System.out.println("Horizontal120test1");
                                    if(checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalUpHelper(e, newLine, p, b, -120, Color.GREEN);
                                }
                                return;
                                // If the ray deflects at the side of the sphere of influence
                            } else {
                                if (x == 0) {
                                    if (checkTest != 0) System.out.println("Horizontal120test2");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.BLUE);
                                    return;
                                } else {
                                    if (checkTest != 0) System.out.println("Horizontal120test3");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalDownHelper(e, newLine, p, b, 58, Color.RED);
                                    return;
                                }
                            }
                        }
                        line_flag += 2;
                    }

                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2 && i > 30) { // If the node hits the atom
                        startcircle.setCenterX(originalLineX);
                        startcircle.setCenterY(originalLineY);
                        startcircle.setStroke(Color.WHITE);
                        startcircle.setRadius(10);
                        startcircle.setFill(Color.BLACK);
                        textBox.appendText("Ray hit an atom" + "\n");
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }
        } while (flag != 1);

        // If the ray was deflected then both the line before and after the ray was
        // deflected will need to be
        if (checkTest != 0) {
            if (line_flag != 1) {
                p.getChildren().add(oldLine);
                p.getChildren().add(newLine);
            } else {
                p.getChildren().add(newLine);
            }
        }
        forcircle.getChildren().add(startcircle);
    }
    @FXML
    void extendLineDiagonalDown(MouseEvent e) {
        Circle startcircle = new Circle();
        RayShotScore();
        RandomColorGen();
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        b.setDisable(true);
        Pane p = (Pane) b.getParent();

        textBox.appendText("Ray shot from " + b.getId() + "\n");
        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = 0;
        double startY = 0;
        if (right) {
            startX = b.getLayoutX() + b.getWidth() / 2 + 8;
            startY = b.getLayoutY() + b.getHeight() / 2 + 10;
        } else if (left) {
            startX = b.getLayoutX() + b.getWidth() / 2 - 8;
            startY = b.getLayoutY() + b.getHeight() / 2 + 10;
        }

        newLine.setStartX(startX);
        newLine.setStartY(startY);
        originalLineY = startY;
        originalLineX = startX;
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            int result = DeflectionHelpers.startsInside(newLine, p, 121, 0);
            if (result == 1 || result == 3) {
                double angleRadians = Math.toRadians(121);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 50; i++) {
                    newLine.setEndX(newLine.getEndX() + dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {
                   // reflected = true;
                    Circle newCircleEnd = new Circle();
                    Circle newCircleStart = new Circle();
                        newCircleEnd.setFill(circleColor);
                        newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                  //  p.getChildren().add(newCircleEnd);
                    p.getChildren().add(newCircleStart);
                    textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                } else {
                    startcircle.setCenterX(originalLineX);
                    startcircle.setCenterY(originalLineY);
                    startcircle.setStroke(Color.WHITE);
                    startcircle.setRadius(10);
                    startcircle.setFill(Color.BLACK);
                    textBox.appendText("Ray hit an atom\n");
                }
                p.getChildren().add(newLine);
                p.getChildren().add(startcircle);
            } else {
                extendLineDiagonalDownHelper(e, newLine, p, b, 121, direction_tester);
            }
        } else if (right) {
            int result = DeflectionHelpers.startsInside(newLine, p, 59, 1);
            if (result == 1 || result == 3) {
                double angleRadians = Math.toRadians(59);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 50; i++) {
                    newLine.setEndX(newLine.getEndX() + dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                   // reflected = true;
                    Circle newCircleEnd = new Circle();
                    Circle newCircleStart = new Circle();
                        newCircleEnd.setFill(circleColor);
                        newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                  //  p.getChildren().add(newCircleEnd);
                    p.getChildren().add(newCircleStart);
                } else {
                    //Circle startcircle = new Circle();
                    startcircle.setCenterX(originalLineX);
                    startcircle.setCenterY(originalLineY);
                    startcircle.setStroke(Color.WHITE);
                    startcircle.setRadius(10);
                    startcircle.setFill(Color.BLACK);
                    p.getChildren().add(startcircle);
                    textBox.appendText("Ray hit and atom\n");
                }
                p.getChildren().add(newLine);
            } else {
                extendLineDiagonalDownHelper(e, newLine, p, b, 59, direction_tester);
            }
        }
    }

    void extendLineDiagonalDownHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color) {
        Circle startcircle = new Circle();
        int checker = 0;
        int line_flag = 0;
        int deflection_account = 0; // This used to help display what happened to the array, e.g if this
        // is equal to 1 the ray is deflected by 60
        // Set the trajectory of the ray
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);


        // Needed to hold the original line if the ray deflects
        Line oldLine = new Line();

        double tempendX = newLine.getEndX();
        double tempendY = newLine.getEndY();

        newLine.setStartX(tempendX);
        newLine.setStartY(tempendY);
        newLine.setEndY(newLine.getStartY());
        newLine.setEndX(newLine.getStartX());
        Circle newCircleEnd = new Circle();
        Circle newCircleStart = new Circle();
        newCircleEnd.setRadius(10);
        newCircleStart.setRadius(10);

        // What colour nodes the ray needs to be hitting to exit
        if (direction_tester == Color.GREEN) {
            color = Color.RED;
        } else if (direction_tester == Color.YELLOW) {
            color = Color.BLUE;
        }
        int flag = 0;
        Node prevNode = null;

        int i = 0;
        int dFlag = 0;
        do {
            // If the ray deflects off the circle of influence and goes up the board
            // instead
            if ((color == Color.GREEN && direction_tester == Color.GREEN) || (color == Color.YELLOW && direction_tester == Color.YELLOW)) {
                newLine.setEndX(newLine.getEndX() - dx);
                newLine.setEndY(newLine.getEndY() - dy);
            } else {
                newLine.setEndX(newLine.getEndX() + dx);
                newLine.setEndY(newLine.getEndY() + dy);
            }
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        // Line intersects with another rectangle
                        newCircleEnd.setCenterY(newLine.getEndY());
                        newCircleEnd.setCenterX(newLine.getEndX());
                            newCircleEnd.setFill(circleColor);
                            newCircleStart.setFill(circleColor);
                            startcircle.setFill(circleColor);
                        newCircleStart.setCenterX(originalLineX);
                        newCircleStart.setCenterY(originalLineY);
                        p.getChildren().add(newCircleStart);
                        p.getChildren().add(newCircleEnd);
                        DeflectionHelpers.printResults(deflection_account, textBox, node);
                        line_flag = 1;
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2 && i > 40) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, false);
                        if (checker != -1) {
                            deflection_account = 1;
                        }
                        prevNode = node;
                        if(dFlag != 1) {
                            dFlag = DeflectionHelpers.checkifDouble(p, newLine, x, node, false, true);
                        }
                        if(dFlag == 4){
                            textBox.appendText("Ray deflected 180\n");
                            newCircleStart.setFill(Color.WHITE);
                            newCircleStart.setCenterX(originalLineX);
                            newCircleStart.setCenterY(originalLineY);
                            p.getChildren().add(newCircleStart);
                            flag = 1;
                            break;
                        }
                        if (checker == 1) {
                            if (line_flag == 0) {
                                if (direction_tester == Color.GREEN) {
                                    oldLine.setStroke(Color.RED);
                                    newLine.setStroke(Color.BLUE);
                                } else if (direction_tester == Color.YELLOW) {
                                    oldLine.setStroke(Color.BLUE);
                                    newLine.setStroke(Color.RED);
                                }
                                oldLine.setStartY(newLine.getStartY());
                                oldLine.setStartX(newLine.getStartX());
                                oldLine.setEndY(newLine.getEndY());
                                oldLine.setEndX(newLine.getEndX());

                                // The color
                                // If the ray deflects at the top of the sphere of influence
                                if (newLine.getEndY() + 10 < node.getLayoutY()) {
                                    if (x == 58) {
                                        if (newLine.getEndX() > node.getLayoutX()) {
                                            if (checkTest != 0) System.out.println("Downtest1");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            extendRayHorizontalHelper(e, newLine, p, b, 1, Color.BLACK);
                                            return;
                                        } else {
                                            if (checkTest != 0) System.out.println("Downtest2");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            extendRayHorizontalHelper(e, newLine, p, b, 180, Color.BLACK);
                                            return;
                                        }
                                    } else {
                                        if (newLine.getEndX() > node.getLayoutX()) {
                                            if (checkTest != 0) System.out.println("Downtest6");
                                            if(checkTest != 0) p.getChildren().add(oldLine);
                                            extendRayHorizontalHelper(e, newLine, p, b, 0, Color.BLACK);
                                            return;
                                        } else {
                                            if (checkTest != 0) System.out.println("Downtest7");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            extendRayHorizontalHelper(e, newLine, p, b, 179, Color.BLACK);
                                            return;
                                        }
                                    }
                                    // If the ray deflects at the side of the sphere of influence
                                } else {
                                    if (x == 60) {
                                        if (checkTest != 0) System.out.println("Tester");
                                        return;
                                    } else {
                                        if (newLine.getEndX() > node.getLayoutX()) {
                                            if (checkTest != 0) System.out.println("Downtest8");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            direction_tester = Color.GREEN;
                                            extendLineDiagonalDownHelper(e, newLine, p, b, 58, Color.RED);
                                            return;
                                        } else {
                                            if (checkTest != 0) System.out.println("Downtest9");
                                            if (checkTest != 0) p.getChildren().add(oldLine);
                                            direction_tester = Color.YELLOW;
                                            extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.BLUE);
                                            return;
                                        }
                                    }
                                }
                            }
                            // Circle has been hit by the ray
                            line_flag += 2;
                        }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, false) != -1 && node != prevNode && i > 40) {
                        if (DeflectionHelpers.checkTriple(newLine, x, p, node, prevNode, false) == 1) {
                            if (checkTest != 0) System.out.println("OH BABY A TRIPLE!!");
                              //  newCircleEnd.setFill(circleColor);
                                newCircleStart.setFill(Color.WHITE);
                            newCircleStart.setRadius(10);
                          //  newCircleEnd.setRadius(10);
                            newCircleStart.setCenterY(originalLineY);
                            newCircleStart.setCenterX(originalLineX);
                          //  newCircleEnd.setCenterX(newLine.getEndX() + dx);
                            //newCircleEnd.setCenterY(newLine.getEndY() + dy);
                          //  p.getChildren().add(newCircleEnd);
                            p.getChildren().add(newCircleStart);
                            textBox.appendText("Ray reflected 180 degress and exited at " + b.getId());
                            flag = 1;
                            break;
                        }
                        deflection_account = 2;
                        if (line_flag == 0) {
                            // Same as the case above, store the ray before the deflection in
                            // a new line
                            if (direction_tester == Color.GREEN) {
                                oldLine.setStroke(Color.RED);
                                // The color of the line
                                newLine.setStroke(Color.BLUE);
                            } else if (direction_tester == Color.YELLOW) {
                                oldLine.setStroke(Color.BLUE);
                                // The color of the line
                                newLine.setStroke(Color.RED);
                            }
                            oldLine.setStartY(newLine.getStartY());
                            oldLine.setStartX(newLine.getStartX());
                            oldLine.setEndY(newLine.getEndY());
                            oldLine.setEndX(newLine.getEndX());

                            double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                            averageY -= 45;

                            // If the ray deflects at the top of the sphere of influence
                            if (newLine.getEndY() < averageY) {
                                if (direction_tester == Color.GREEN) {
                                    if (checkTest != 0) System.out.println("Downtest12");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalUpHelper(e, newLine, p, b, -122, Color.GREEN);
                                    return;
                                } else if (direction_tester == Color.YELLOW) {
                                    if (checkTest != 0) System.out.println("Downtest13");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalUpHelper(e, newLine, p, b, -59, Color.YELLOW);
                                    return;
                                }
                                // If the ray deflects at the side of the sphere of influence
                            } else {
                                if (x == 121) {
                                    if (checkTest != 0) System.out.println("HHello");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendRayHorizontalHelper(e, newLine, p, b, 0, Color.BLACK);
                                    return;
                                } else {
                                    if (checkTest != 0) System.out.println("HHHHHELLOOOO");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendRayHorizontalHelper(e, newLine, p, b, 180, Color.BLACK);
                                    return;
                                }
                            }
                            // Set trajectory of the deflected ray
                            dx = Math.cos(angleRadians);
                            dy = Math.sin(angleRadians);

                            newLine.setStartX(newLine.getEndX());
                            newLine.setStartY(newLine.getEndY());
                            newLine.setEndX(newLine.getEndX() + dx);
                            newLine.setEndY(newLine.getEndY() + dy);
                        }
                        line_flag += 2;
                    }
                    // Handle the case where the line hits another circle at the same
                    // location
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        startcircle.setCenterX(originalLineX);
                        startcircle.setCenterY(originalLineY);
                        startcircle.setStroke(Color.WHITE);
                        startcircle.setRadius(10);
                        startcircle.setFill(Color.BLACK);
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        // If the ray was deflected then both the line before and after the ray was
        // deflected will need to be
        if (checkTest != 0) {
            if (line_flag != 1) {
                p.getChildren().add(oldLine);
                p.getChildren().add(newLine);
            } else {
                p.getChildren().add(newLine);
            }
        }
        p.getChildren().add(startcircle);
    }

    @FXML
    void extendLineDiagonalUp(MouseEvent e) {
        RayShotScore();
        Circle newCircleStart = new Circle();
        RandomColorGen();
        newCircleStart.setFill(circleColor);
        newCircleStart.setRadius(10);
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        b.setDisable(true);
        Pane p = (Pane) b.getParent();

        textBox.appendText("Ray shot from " + b.getId() + "\n");
        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX;
        double startY;

        if (right) {
            startX = b.getLayoutX() + b.getWidth() / 2 + 4;
            startY = b.getLayoutY() + b.getHeight() / 2 - 13;
        } else {
            startX = b.getLayoutX() + b.getWidth() / 2;
            startY = b.getLayoutY() + b.getHeight() / 2;
        }

        newLine.setStartX(startX);
        newLine.setStartY(startY);
        originalLineY = startY;
        originalLineX = startX;
//        newCircleStart.setCenterX(startX);
//        newCircleStart.setCenterY(startY);
//        p.getChildren().add(newCircleStart);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            int result = DeflectionHelpers.startsInside(newLine, p, -59, 3);
            if (result == 1 || result == 3) {
                System.out.println("Hello");
                double angleRadians = Math.toRadians(-59);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 60; i++) {
                    newLine.setEndX(newLine.getEndX() - dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {
                    textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                    Circle newCircleEnd = new Circle();
                    newCircleEnd.setFill(circleColor);
                    newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                } else {
                    newCircleStart.setCenterX(originalLineX);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setStroke(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleStart.setFill(Color.BLACK);
                    textBox.appendText("Ray hit an atom\n");
                }
                p.getChildren().add(newCircleStart);
                p.getChildren().add(newLine);
            } else {
                extendLineDiagonalUpHelper(e, newLine, p, b, -59, direction_tester);
            }
        } else if (right) {
            int result = DeflectionHelpers.startsInside(newLine, p, -121, 4);
            if (result == 1 || result == 3) {
                System.out.println("Hello");
                double angleRadians = Math.toRadians(-121);
                double dx = Math.cos(angleRadians);
                double dy = Math.sin(angleRadians);
                for (int i = 0; i < 50; i++) {
                    newLine.setEndX(newLine.getEndX() - dx);
                    newLine.setEndY(newLine.getEndY() + dy);
                }
                if (result == 1) {
                    textBox.appendText("Ray deflected at 180 and exited at " + b.getId() + "\n");
                    Circle newCircleEnd = new Circle();
                        newCircleEnd.setFill(circleColor);
                        newCircleStart.setFill(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleEnd.setRadius(10);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setCenterX(originalLineX);
                    newCircleEnd.setCenterX(newLine.getEndX() + dx);
                    newCircleEnd.setCenterY(newLine.getEndY() + dy);
                   // p.getChildren().add(newCircleEnd);
                   // p.getChildren().add(newCircleStart);
                } else {
                    textBox.appendText("Ray hit an atom");
                    newCircleStart.setCenterX(originalLineX);
                    newCircleStart.setCenterY(originalLineY);
                    newCircleStart.setStroke(Color.WHITE);
                    newCircleStart.setRadius(10);
                    newCircleStart.setFill(Color.BLACK);
                }
                p.getChildren().add(newCircleStart);
                p.getChildren().add(newLine);
            } else {
                extendLineDiagonalUpHelper(e, newLine, p, b, -121, direction_tester);
            }
        }
    }

    void RandomColorGen()
    {
        Random random = new Random();
        //Plus 1 is so that we never get black as that is saved for absorbed
        int r = random.nextInt(255)+1;
        int g = random.nextInt(255)+1;
        int b = random.nextInt(255)+1;
        circleColor = Color.rgb(r,g,b);
    }

    // Case for up left
    @FXML
    void diagonaldirectionleft() {
        direction_tester = Color.YELLOW;
        left = true;
        right = false;
    }

    // case for up right
    @FXML
    void diagonaldirectionright() {
        direction_tester = Color.GREEN;
        right = true;
        left = false;
    }

    void extendLineDiagonalUpHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color) {
        Circle startcircle = new Circle();
        int deflection_account = 0;
        int checker = 0;
        int line_flag = 0;
        // Set the trajectory of the ray
        double angleRadians = Math.toRadians(-x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        double tempendX = newLine.getEndX();
        double tempendY = newLine.getEndY();
        Circle newCircleEnd = new Circle();
        newCircleEnd.setFill(circleColor);
        newCircleEnd.setRadius(10);
        startcircle.setRadius(10);

        newLine.setStartX(tempendX);
        newLine.setStartY(tempendY);
        newLine.setEndY(newLine.getStartY());
        newLine.setEndX(newLine.getStartX());
        // Needed to hold the original line if the ray deflects

        Line oldLine = new Line();
        oldLine.setStroke(Color.PURPLE);

        // What colour nodes the ray needs to be hitting to exit
        if (direction_tester == Color.GREEN) {
            color = Color.GREEN;
        } else if (direction_tester == Color.YELLOW) {
            color = Color.YELLOW;
        }
        int flag = 0;
        Node prevNode = null;

        int i = 0;
        int dFlag = 0;
        do {
            // If the ray deflects off the circle of influence and goes up the board
            // instead
            if ((color == Color.BLUE && direction_tester == Color.GREEN) || (color == Color.RED && direction_tester == Color.YELLOW)) {
                newLine.setEndX(newLine.getEndX() + dx);
                newLine.setEndY(newLine.getEndY() + dy);
            } else {
                newLine.setEndX(newLine.getEndX() - dx);
                newLine.setEndY(newLine.getEndY() - dy);
            }


            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color) {
                        // Line intersects with another rectangle
                        Circle newCircleStart = new Circle();
                            newCircleEnd.setFill(circleColor);
                            newCircleStart.setFill(circleColor);
                        newCircleStart.setRadius(10);
                        newCircleEnd.setRadius(10);
                        newCircleStart.setCenterY(originalLineY);
                        newCircleStart.setCenterX(originalLineX);
                        newCircleEnd.setCenterX(newLine.getEndX() + dx);
                        newCircleEnd.setCenterY(newLine.getEndY() + dy);
                        p.getChildren().add(newCircleEnd);
                        p.getChildren().add(newCircleStart);
                        DeflectionHelpers.printResults(deflection_account, textBox, node);
                        flag = 1;
                        line_flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2 && i > 60) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, true);
                        if (checker != -1) {
                            deflection_account = 1;
                        }
                        prevNode = node;

                        if(dFlag != 1) {
                            dFlag = DeflectionHelpers.checkifDouble(p, newLine, x, node, true, true);
                        }
                        if(dFlag == 4){
                            textBox.appendText("Ray deflected 180\n");
                            startcircle.setFill(Color.WHITE);
                            startcircle.setCenterX(originalLineX);
                            startcircle.setCenterY(originalLineY);
                            p.getChildren().add(startcircle);
                            flag = 1;
                            line_flag += 2;
                            break;
                        }
                       if (checker == 1) {
                           if (line_flag == 0) {
                               if (direction_tester == Color.GREEN) {
                                   oldLine.setStroke(Color.RED);
                                   newLine.setStroke(Color.BLUE);
                               } else if (direction_tester == Color.YELLOW) {
                                        oldLine.setStroke(Color.BLUE);
                                        newLine.setStroke(Color.RED);
                                    }
                                    oldLine.setStartY(newLine.getStartY());
                                    oldLine.setStartX(newLine.getStartX());
                                    oldLine.setEndY(newLine.getEndY());
                                    oldLine.setEndX(newLine.getEndX());

                                    // The color

                                    // If the ray deflects at the bottom of the sphere of influence
                                    if (newLine.getEndY() - 10 > node.getLayoutY()) {
                                        if (x == 59) {
                                            if (newLine.getEndX() < node.getLayoutX()) {
                                                if (checkTest != 0) System.out.println("Hello1");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                extendRayHorizontalHelper(e, newLine, p, b, 1, Color.BLACK);
                                                return;
                                            } else  {
                                                if (checkTest != 0) System.out.println("Hell on Earth2");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                extendRayHorizontalHelper(e, newLine, p, b, 180, Color.BLACK);
                                                return;
                                            }
                                        } else {
                                            if (newLine.getEndX() < node.getLayoutX()) {
                                                if (checkTest != 0) System.out.println("Hello2");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                extendRayHorizontalHelper(e, newLine, p, b, 181, Color.BLACK);
                                                return;
                                            } else {
                                                if (checkTest != 0) System.out.println("Hell on Earth1");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                extendRayHorizontalHelper(e, newLine, p, b, 0, Color.BLACK);
                                                return;
                                            }
                                        }
                                        // If the ray deflects at the side of the sphere of influence
                                    } else {
                                        if (x == 60) {
                                            angleRadians = Math.toRadians(60);
                                            if (checkTest != 0) System.out.println("Tester");
                                        } else {
                                            if (newLine.getEndX() < node.getLayoutX()) {
                                                if (checkTest != 0) System.out.println("Lol");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                direction_tester = Color.YELLOW;
                                                extendLineDiagonalUpHelper(e, newLine, p, b, -59, Color.YELLOW);
                                                return;
                                            } else {
                                                if (checkTest != 0) System.out.println("LOL2");
                                                if (checkTest != 0) p.getChildren().add(oldLine);
                                                direction_tester = Color.GREEN;
                                                extendLineDiagonalUpHelper(e, newLine, p, b, -121, Color.GREEN);
                                                return;
                                            }
                                        }
                                    }
                                    // Set trajectory of the deflected ray
                                    dx = Math.cos(angleRadians);
                                    dy = Math.sin(angleRadians);

                                    newLine.setStartX(newLine.getEndX());
                                    newLine.setStartY(newLine.getEndY());
                                    newLine.setEndX(newLine.getEndX() - dx);
                                    newLine.setEndY(newLine.getEndY() - dy);
                                }
                                // Circle has been hit by the ray
                                line_flag += 2;
                            }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, true) != -1 && node != prevNode && i > 60) {
                       if (DeflectionHelpers.checkTriple(newLine, x, p, node, prevNode, true) == 1) {
                            if (checkTest != 0) System.out.println("OH BABY A TRIPLE!!");
                            textBox.appendText("Ray reflected 180 degress and exited at " + b.getId());
                           Circle newCircleStart = new Circle();
                               newCircleEnd.setFill(circleColor);
                               newCircleStart.setFill(Color.WHITE);
                           newCircleStart.setRadius(10);
                           newCircleEnd.setRadius(10);
                           newCircleStart.setCenterY(originalLineY);
                           newCircleStart.setCenterX(originalLineX);
                           newCircleEnd.setCenterX(newLine.getEndX() + dx);
                           newCircleEnd.setCenterY(newLine.getEndY() + dy);
                           p.getChildren().add(newCircleStart);
                            flag = 1;
                            break;
                        }
                        deflection_account = 2;
                        if (line_flag == 0) {
                            // Same as the case above, store the ray before the deflection in
                            // a new line
                            if (direction_tester == Color.GREEN) {
                                oldLine.setStroke(Color.RED);
                                // The color of the line
                                newLine.setStroke(Color.BLUE);
                            } else if (direction_tester == Color.YELLOW) {
                                oldLine.setStroke(Color.BLUE);
                                // The color of the line
                                newLine.setStroke(Color.RED);
                            }
                            oldLine.setStartY(newLine.getStartY());
                            oldLine.setStartX(newLine.getStartX());
                            oldLine.setEndY(newLine.getEndY());
                            oldLine.setEndX(newLine.getEndX());

                            double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                            averageY += 45;

                            // If the ray deflects at the bottom of the sphere of influence
                            if (newLine.getEndY() > averageY) {
                                // color = Color.BLACK;
                                if (direction_tester == Color.GREEN) {
                                    if (checkTest != 0) System.out.println("Test2.1");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalDownHelper(e, newLine, p, b, 59, Color.RED);
                                    return;
                                } else if (direction_tester == Color.YELLOW) {
                                    if (checkTest != 0) System.out.println("Test2.2");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.BLUE);
                                    return;
                                }
                                // If the ray deflects at the side of the sphere of influence
                            } else {
                                if (direction_tester == Color.GREEN) {
                                    if (checkTest != 0) System.out.println("Side1");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendRayHorizontalHelper(e, newLine, p, b, 179, Color.BLACK);
                                    return;
                                } else {
                                    if (checkTest != 0) System.out.println("Side2");
                                    if (checkTest != 0) p.getChildren().add(oldLine);
                                    extendRayHorizontalHelper(e, newLine, p, b, 1, Color.BLACK);
                                    return;
                                }
                            }

                            // Set trajectory of the deflected ray
                            dx = Math.cos(angleRadians);
                            dy = Math.sin(angleRadians);

                            newLine.setStartX(newLine.getEndX());
                            newLine.setStartY(newLine.getEndY());
                            newLine.setEndX(newLine.getEndX() - dx);
                            newLine.setEndY(newLine.getEndY() - dy);
                        }
                        line_flag += 2;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        startcircle.setCenterX(originalLineX);
                        startcircle.setCenterY(originalLineY);
                        startcircle.setStroke(Color.WHITE);
                        startcircle.setRadius(10);
                        startcircle.setFill(Color.BLACK);
                        System.out.println("Ray hit an atom");
                        System.out.println("Ray hit an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        // If the ray was deflected then both the line before and after the ray was deflected will need to be printed
        if (checkTest != 0) {
            if (line_flag != 1) {
                p.getChildren().add(oldLine);
                p.getChildren().add(newLine);
            }
            else {
                p.getChildren().add(newLine);
            }
        }
        p.getChildren().add(startcircle);
    }

    @FXML
    public void toggleAtoms(ActionEvent event) {
        Atoms.invisibleAtoms(event, getAtomcount(), spherepane);
    }

    public Stage getStage() {
        return stage;
    }

    public URL getBoardURL() {
        return boardURL;
    }
}