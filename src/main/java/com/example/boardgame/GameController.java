package com.example.boardgame;
import static java.lang.System.exit;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class GameController {
    public static Color direction_tester;
    public static boolean right = false;
    public static boolean left = false;
    private static int atomcount = 0;
    private static Pane spherepane;
    private static int score = 0;
    private int player1Score = -1;
    private int player2Score = -1;
    private Color circleColor;
    static double originalLineX;
    static double originalLineY;

    public static ArrayList<Line> lines = new ArrayList<>();
    public static ArrayList<Rectangle> arrows = new ArrayList<>();
    public Button start_end_button;
    public Button nextPlayer;

    public Scene scene;
    public int gameStatus = 0;
  //  boolean reflected = false;

    public static int getAtomcount() {
        return atomcount;
    }

    public static int getScore(){return score;}

    public static void addScore5(){
        score += 5;}
    public static void setAtomcount(int atomcount1) {
        atomcount = atomcount1;
    }
    public static Pane getSpherepane(){return spherepane;}
    static void setSpherepane(Pane spherepane1) {
        spherepane = spherepane1;
    }

    public static void setAtomcount() {
        atomcount++;
    }

    @FXML
    public TextArea textBox; // Text box where results of rays shots will be displayed

    @FXML public TextArea textBoxScore;

    @FXML
    public void printScore()
    {
        textBoxScore.clear();
        textBoxScore.appendText("Score: "+score);
    }

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
        scene = new Scene(root);
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
        if(gameStatus == 1) {
            RayShotScore();
            Line newLine = new Line();
            newLine.setStroke(Color.RED);
            Rectangle b = (Rectangle) e.getSource();
            b.setDisable(true);
            arrows.add(b);
            Pane p = (Pane) b.getParent();
            textBox.appendText("Ray shot from " + b.getId() + "\n");
            RandomColorGen();
            RayHelpers.setStartHorizontalRay(newLine, b);
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
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 0);
                } else {
                    //Otherwise it is not inside a circle of influence, or it will hit an atom when shot
                    extendRayHorizontalHelper(e, newLine, 0, direction_tester);
                }
            } else if (b.getLayoutX() > 250) // Case for being to the right
            {
                int result = DeflectionHelpers.startsInside(newLine, p, 180, 2);
                originalLineX = b.getLayoutX() + b.getWidth();
                originalLineY = b.getLayoutY() + 10;
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 0);
                } else {
                    extendRayHorizontalHelper(e, newLine, 180, direction_tester);
                }
            }
        }
    }

    void extendRayHorizontalHelper(MouseEvent e, Line newLine, int x, Color color) {
        Rectangle b = (Rectangle) e.getSource();
        int checker = 0, flag = 0, i = 0; // This used to help display what happened to the array, e.g. if this is equal to 1 the ray is deflected by 60
        Pane p = (Pane) b.getParent();
        RayHelpers.setStartofRay(newLine);
        Line oldLine = new Line(); // Needed to hold the original line if the ray deflects
        Node prevNode = null;
        do {
            // If the ray deflects off the circle of influence and goes up or down the board
            RayHelpers.moveRay(newLine, x, true);
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    //If the ray hits nothing
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && b != node) {
                        RayHelpers.markerHelper(circleColor, newLine, p, x);
                        textBox.appendText("Ray deflected and exited at "+ node.getId() + "\n"); // Line intersects with another rectangle
                        flag = 1; break;
                        //If the ray comes in contact with a circle of influence
                    } else if (node instanceof Circle && checker != 2 && i > 70) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, false);
                        prevNode = node;
                        //** This is for the 180 case.Check to see that if a ray hit a Circle of Influence that there is not another one directly on top of it
                        if (DeflectionHelpers.checkifDouble(p, newLine, x, prevNode, false, false) != 3) {
                            if (checker == 1) {
                                RayHelpers.setOldRay(oldLine, newLine, Color.GREEN);
                                newLine.setStroke(Color.YELLOW);
                                one_circle_deflection(e, newLine, oldLine, node, p, b);
                                return;
                            }
                        } else if (DeflectionHelpers.checkifDouble(p, newLine, x, prevNode, false, false) == 3) {
                            RayHelpers.placeWhiteMarker(p, textBox, b);
                            flag = 1; break;
                        }
                    }
                    // Case if the ray hit 2 circles of influence at the same time
                    else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, false) != -1 && node != prevNode && i > 70) {
                        newLine.setStroke(Color.GREEN);
                        two_circle_deflection(e,newLine, oldLine, p, b, node, prevNode, x);
                        return;
                    }
                    // Handle case when a horizontal Ray hit an atom
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2 && i > 30) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom \n");
                        RayHelpers.placeBlackMarker(p);
                        flag = 1; break;
                    }
                }
            }
        } while (flag != 1);
        // If the ray was deflected then both the line before and after the ray was deflected will need to be
        if (checkTest != 0) {
            System.out.println("adding lines");
            p.getChildren().add(oldLine);
            p.getChildren().add(newLine);

            lines.add(oldLine);
            lines.add(newLine);
        }
    }

    @FXML
    void extendLineDiagonalDown(MouseEvent e) {
        if(gameStatus == 1) {
            RayShotScore();
            RandomColorGen();
            Line newLine = new Line();
            newLine.setStroke(Color.RED);
            Rectangle b = (Rectangle) e.getSource();
            b.setDisable(true);
            arrows.add(b);
            Pane p = (Pane) b.getParent();

            textBox.appendText("Ray shot from " + b.getId() + "\n");
            // Set the starting point of the line
            RayHelpers.setStartofDiagonalHelper(b, newLine, true);

            if (left) {
                int result = DeflectionHelpers.startsInside(newLine, p, 121, 0);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 1);
                } else {
                    extendLineDiagonalDownHelper(e, newLine, p, b, 121, direction_tester);
                }
            } else if (right) {
                int result = DeflectionHelpers.startsInside(newLine, p, 59, 1);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 1);
                } else {
                    extendLineDiagonalDownHelper(e, newLine, p, b, 59, direction_tester);

                }
            }
        }
    }

    void extendLineDiagonalDownHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color) {
        int checker = 0;
        Line oldLine = new Line();
        RayHelpers.setStartofRay(newLine);
        if (direction_tester == Color.GREEN) {
            color = Color.RED;
        } else if (direction_tester == Color.YELLOW) {
            color = Color.BLUE;
        }
        int flag = 0, i = 0, dFlag = 0;
        Node prevNode = null;
        newLine.setStroke(Color.RED);
        do {
            RayHelpers.moveRay(newLine, x, true);
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        RayHelpers.markerHelper(circleColor, newLine, p, x); // Line intersects with another rectangle
                        textBox.appendText("Ray deflected and exited at " + node.getId() + "\n");
                        flag = 1;break;
                    } else if (node instanceof Circle && checker != 2 && i > 70) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, false);
                        prevNode = node;
                        if(dFlag != 1) dFlag = DeflectionHelpers.checkifDouble(p, newLine, x, node, false, true);
                        if(dFlag == 4){
                            textBox.appendText("Ray deflected 180\n");
                            RayHelpers.placeWhiteMarker(p, textBox, b);
                            flag = 1;break;
                        }
                        if (checker == 1) {
                            RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                            one_circle_deflection_down(e, newLine, oldLine, node, p, b, x);
                            return;
                        }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, false) != -1 && node != prevNode && i > 70) {
                        if (DeflectionHelpers.checkTriple(newLine, x, p, node, prevNode, false) == 1) {
                            RayHelpers.placeWhiteMarker(p, textBox, b);
                            flag = 1;break;
                        }
                        RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                        two_circle_deflection_down(e, node, prevNode, oldLine, newLine, p, b, x);
                        return;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        RayHelpers.placeBlackMarker(p);
                        flag = 1;break;
                    }
                }
            }
        } while (flag != 1);
        // If the ray was deflected then both the line before and after the ray was deflected will need to be
        if (checkTest != 0) {
            p.getChildren().add(oldLine);
            p.getChildren().add(newLine);

            lines.add(oldLine);
            lines.add(newLine);
        }
    }

    @FXML
    void extendLineDiagonalUp(MouseEvent e) {
        if(gameStatus == 1) {
            RayShotScore();
            Circle newCircleStart = new Circle();
            RandomColorGen();
            newCircleStart.setFill(circleColor);
            newCircleStart.setRadius(10);
            Line newLine = new Line();
            newLine.setStroke(Color.RED);
            Rectangle b = (Rectangle) e.getSource();
            b.setDisable(true);
            arrows.add(b);
            Pane p = (Pane) b.getParent();

            textBox.appendText("Ray shot from " + b.getId() + "\n");
            // Set the starting point of the line
            RayHelpers.setStartofDiagonalHelper(b, newLine, false);

            if (left) {
                int result = DeflectionHelpers.startsInside(newLine, p, 59, 3);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 2);
                } else {
                    direction_tester = Color.YELLOW;
                    extendLineDiagonalUpHelper(e, newLine, b,59, direction_tester);
                }
            } else if (right) {
                int result = DeflectionHelpers.startsInside(newLine, p, 121, 4);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, p, b, textBox, 2);
                } else {
                    direction_tester = Color.GREEN;
                    extendLineDiagonalUpHelper(e, newLine, b, 121, direction_tester);
                }
            }
        }
    }
    void extendLineDiagonalUpHelper(MouseEvent e, Line newLine, Rectangle b, int x, Color color) {
        int checker = 0;
        newLine.setStroke(Color.RED);
        Pane p = (Pane) b.getParent();
        RayHelpers.setStartofRay(newLine); // Set the trajectory of the ray
        // Needed to hold the original line if the ray deflects
        Line oldLine = new Line();
        oldLine.setStroke(Color.PURPLE);
        // What colour nodes the ray needs to be hitting to exit
        if (direction_tester == Color.GREEN) {
            color = Color.GREEN;
        } else if (direction_tester == Color.YELLOW) {
            color = Color.YELLOW;
        }
        int flag = 0, i = 0, dFlag = 0;
        Node prevNode = null;
        do {
            // If the ray deflects off the circle of influence and goes up the board instead
            RayHelpers.moveRay(newLine, x, false);
            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        RayHelpers.markerHelper(circleColor, newLine, p, x);
                        textBox.appendText("Ray deflected and exited at " + node.getId() + "\n");
                        flag = 1;break;
                    } else if (node instanceof Circle && checker != 2 && i > 60 ) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, x, true);
                        prevNode = node;
                        if (dFlag != 1) dFlag = DeflectionHelpers.checkifDouble(p, newLine, x, node, true, true);
                        if (dFlag == 4) {
                            textBox.appendText("Ray deflected 180\n");
                            RayHelpers.placeWhiteMarker(p, textBox, b);
                            flag = 1;break;
                        }
                        if (checker == 1) {
                            RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                            one_deflection_helper_up(e, node, oldLine, newLine, p, b, x);
                            return;
                        }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, x, true) != -1 && node != prevNode && i > 60) {
                        if (DeflectionHelpers.checkTriple(newLine, x, p, node, prevNode, true) == 1) {
                            textBox.appendText("Ray reflected 180 degrees and exited at " + b.getId());
                            RayHelpers.placeWhiteMarker(p, textBox, b);
                            flag = 1;break;
                        }
                        RayHelpers.setOldRay(oldLine, newLine, Color.BLUE);
                        two_deflection_helper_up(e, node, prevNode, p,b, x, newLine, oldLine);
                        return;
                    } else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) {
                        RayHelpers.placeBlackMarker(p);
                        textBox.appendText("Ray hit an atom\n");
                        flag = 1;break;
                    }
                }
            }
        } while (flag != 1);

        if (checkTest != 0) {
            p.getChildren().add(oldLine);
            p.getChildren().add(newLine);

            lines.add(oldLine);
            lines.add(newLine);
        }
    }


    public void one_circle_deflection(MouseEvent e, Line newLine, Line oldLine, Node node, Pane p, Rectangle b){
        if (newLine.getEndY() < node.getLayoutY()) {
            if (newLine.getEndX() > node.getLayoutX()) {
                RayHelpers.deflection(Color.YELLOW, oldLine, checkTest, p);
                extendLineDiagonalUpHelper(e, newLine, b, 59, Color.YELLOW);
            } else {
                RayHelpers.deflection(Color.GREEN, oldLine, checkTest, p);
                extendLineDiagonalUpHelper(e, newLine, b, 121, Color.GREEN);
            }
        } else {
            if (newLine.getEndX() > node.getLayoutX()) {
                RayHelpers.deflection(Color.YELLOW, oldLine, checkTest, p);
                extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.RED);
            } else {
                RayHelpers.deflection(Color.GREEN, oldLine, checkTest, p);
                extendLineDiagonalDownHelper(e, newLine, p, b, 59, Color.BLUE);
            }
        }
    }

    public void one_circle_deflection_down(MouseEvent e, Line newLine, Line oldLine, Node node, Pane p, Rectangle b, int x){
        if (newLine.getEndY() + 10 < node.getLayoutY()) {
            if (x == 59) {
                if (newLine.getEndX() > node.getLayoutX()) {
                    if (checkTest != 0) {
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 1, Color.BLACK);
                } else {
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine,  180, Color.BLACK);
                }
            } else {
                if (newLine.getEndX() > node.getLayoutX()) {
                    if (checkTest != 0) System.out.println("Downtest6");
                    if(checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 0, Color.BLACK);
                } else {
                    if (checkTest != 0) System.out.println("Downtest7");
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 179, Color.BLACK);
                }
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (newLine.getEndX() > node.getLayoutX()) {
                if (checkTest != 0) System.out.println("Downtest8");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                direction_tester = Color.GREEN;
                extendLineDiagonalDownHelper(e, newLine, p, b, 59, Color.RED);
            } else {
                if (checkTest != 0) System.out.println("Downtest9");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                direction_tester = Color.YELLOW;
                extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.BLUE);
            }
        }
    }

    public void two_circle_deflection_down(MouseEvent e, Node node, Node prevNode, Line oldLine, Line newLine, Pane p, Rectangle b, int x){
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        averageY -= 45;
        if (newLine.getEndY() < averageY) {
            if (x == 59) {
                if (checkTest != 0) System.out.println("Downtest12");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendLineDiagonalUpHelper(e, newLine,  b, 121, Color.GREEN);
            } else {
                if (checkTest != 0) System.out.println("Downtest13");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendLineDiagonalUpHelper(e, newLine, b, 59, Color.YELLOW);
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (x == 121) {
                if (checkTest != 0) System.out.println("HHello");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendRayHorizontalHelper(e, newLine, 0, Color.BLACK);
            } else {
                if (checkTest != 0) System.out.println("HHHHHELLOOOO");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendRayHorizontalHelper(e, newLine, 180, Color.BLACK);
            }
        }
    }


    public void two_circle_deflection(MouseEvent e, Line newLine, Line oldLine, Pane p, Rectangle b, Node node, Node prevNode, int x){
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        Color deflectionColor;
        int angle;
        if (newLine.getEndY() < averageY) {
            deflectionColor = (x == 0) ? Color.YELLOW : Color.GREEN;
            angle = (x == 0) ? -60 : -120;
        } else {
            deflectionColor = (x == 0) ? Color.BLUE : Color.RED;
            angle = (x == 0) ? 121 : 58;
        }
        RayHelpers.deflection(deflectionColor, oldLine, checkTest, p);
        if (newLine.getEndY() < averageY) {
            extendLineDiagonalUpHelper(e, newLine, b, angle, deflectionColor);
        }
        else {
            extendLineDiagonalDownHelper(e, newLine, p, b, angle, deflectionColor);
        }
    }

    public void one_deflection_helper_up(MouseEvent e, Node node, Line oldLine, Line newLine, Pane p, Rectangle b, int x){
        if (newLine.getEndY() - 10 > node.getLayoutY()) {
            if (x == 59) {
                if (newLine.getEndX() < node.getLayoutX()) {
                    if (checkTest != 0) System.out.println("Hello1");
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 180, Color.BLACK);
                } else {
                    if (checkTest != 0) System.out.println("Hell on Earth2");
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 0, Color.BLACK);
                }
            } else {
                if (newLine.getEndX() < node.getLayoutX()) {
                    if (checkTest != 0) System.out.println("Hello2");
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 181, Color.BLACK);
                } else {
                    if (checkTest != 0) System.out.println("Hell on Earth1");
                    if (checkTest != 0){
                        p.getChildren().add(oldLine);
                        lines.add(oldLine);
                    }
                    extendRayHorizontalHelper(e, newLine, 0, Color.BLACK);
                }
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (newLine.getEndX() < node.getLayoutX()) {
                if (checkTest != 0) System.out.println("Lol");
                direction_tester = Color.YELLOW;
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendLineDiagonalUpHelper(e, newLine, b, 59, Color.YELLOW);
            } else {
                if (checkTest != 0) System.out.println("LOL2");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                direction_tester = Color.GREEN;
                extendLineDiagonalUpHelper(e, newLine, b, 121, Color.GREEN);
            }
        }
    }

    public void two_deflection_helper_up(MouseEvent e, Node node, Node prevNode, Pane p, Rectangle b, int x, Line newLine, Line oldLine) {
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        averageY += 45;

        // If the ray deflects at the bottom of the sphere of influence
        if (newLine.getEndY() > averageY) {
            if (x == 121) {
                if (checkTest != 0) System.out.println("Test2.1");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendLineDiagonalDownHelper(e, newLine, p, b, 59, Color.RED);
            } else {
                if (checkTest != 0) System.out.println("Test2.2");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendLineDiagonalDownHelper(e, newLine, p, b, 121, Color.BLUE);
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (x == 121) {
                if (checkTest != 0) System.out.println("Side1");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendRayHorizontalHelper(e, newLine, 179, Color.BLACK);
            } else {
                if (checkTest != 0) System.out.println("Side2");
                if (checkTest != 0){
                    p.getChildren().add(oldLine);
                    lines.add(oldLine);
                }
                extendRayHorizontalHelper(e, newLine, 1, Color.BLACK);
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


    @FXML
    public void toggleAtoms() {
        Atoms.invisibleAtoms(getAtomcount(), spherepane,start_end_button);
        if(atomcount == 6){
            gameStatus = 1;
        }
    }

    private void calculateScore(){
        textBox.appendText("FINAL SCORE: " + player1Score + " - " + player2Score + "\n");
        if(player1Score < player2Score){
            textBox.appendText("""
                    PLAYER 1 WINS!!!
                       \\\\
                       (o>
                    \\\\_//)
                      \\_/_)
                        _|_\t winner winner chicken dinner
                    """);
        } else if(player2Score < player1Score){
            textBox.appendText("""
                    PLAYER 2 WINS!!!
                       \\\\
                       (o>
                    \\\\_//)
                      \\_/_)
                        _|_\t winner winner chicken dinner
                    """);
        } else{
            textBox.appendText("IT'S A TIE!! EVERYONE'S A WINNER :D\n");
        }
    }

    private void removeLines(){

        Platform.runLater(() -> { // avoid threading issues
            for (Line line : lines) {
                Parent parent = line.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(line);
                    System.out.println("Line removed");
                } else {
                    throw new UnsupportedOperationException("failed to remove line");
                }
            }

            lines.clear(); // Clear arrayList
        });

    }

    private void reenableArrows(){
        for(Rectangle arrow : arrows){
            arrow.setDisable(false);
        }
    }


    public void restart(){
        Atoms.removeAtoms(spherepane);
        removeLines();
        RayHelpers.removeRayMarkers();
        reenableArrows();

        textBox.clear();
        if(player1Score < 0){
            player1Score = getScore();
            textBox.appendText("PLAYER 2");
            nextPlayer.setText("Final results");
            start_end_button.setText("Start");
        } else if(player2Score < 0){
            player2Score = getScore();
            start_end_button.setVisible(false);
            nextPlayer.setText("Quit");
            calculateScore();
        } else{
            quit();
        }

        setAtomcount(0);
        setScore(0);
        printScore();
    }

    public Stage getStage() {
        return stage;
    }

    public void setScore(int x){
        score = x;
    }

    public URL getBoardURL() {
        return boardURL;
    }

    @FXML
    public void pauseGame(KeyEvent k) throws IOException {
        KeyCode key = k.getCode();
        if(key == KeyCode.ESCAPE){
            pausePopUp();
        }
    }
    @FXML
    public static Stage stage2 = new Stage();
    @FXML
    void pausePopUp()throws  IOException{
        URL boardURL2 = getClass().getResource("Pause.fxml");
        assert boardURL2 != null;
        Parent root = FXMLLoader.load(boardURL2);
        Image icon = new Image(this.getClass().getResource("GameIcon.jpeg").toExternalForm());
        stage2.getIcons().add(icon);
        stage2.setTitle("BlackBoard Pause");
        Scene scene2 = new Scene(root);
        stage2.setScene(scene2);
        stage2.show();
    }
    @FXML
    void quit(){
        exit(0);
    }

    @FXML
    void pauseContinue(){
        stage2.close();
    }

}