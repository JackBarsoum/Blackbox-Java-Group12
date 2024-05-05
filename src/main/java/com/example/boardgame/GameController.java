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

/**
 * @author Jack Barsoum, Oisin Lynch, Carol Ezzeddine
 * This class handles the main logic of our game. Being the following
 * 1. Ray logic (creating the line and firing it)
 * 2. Advanced Ray Logic (How to react to the flags of our DeflectionHelper methods)
 * 3. Color Logic (The color of markers being placed depending on the result of the ray)
 * 4. The current score of the player playing
 * 5. Final score logic (Comparing the scores of the 2 players and determining a winner)
 * 6. Displaying our score to the players
 * 7. Resetting the board
 * 8. Pausing and quitting the game
 */
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
    public Button quit;
    public Scene scene;
    public int gameStatus = 0;

    public static int getAtomcount() {
        return atomcount;
    }

    public GameController() {
    }

    public static int getScore() {
        return score;
    }

    public static void addScore5() {
        score += 5;
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
    public TextArea textBox; // Where results of rays shots will be displayed

    @FXML
    public TextArea textBoxScore; // Displays the player score separately above textBox

    /**
     * prints the score in textBoxScore by appending score variable
     */
    @FXML
    public void printScore() {
        textBoxScore.clear();
        textBoxScore.appendText("Score: " + score);
    }

    @FXML
    private Stage stage;
    private URL boardURL;

    @FXML
    void leave() {
        System.out.println("Quitting Game");
        Stage stage = (Stage) quit.getScene().getWindow(); // Assuming quitButton is your quit button
        stage.close();
    }

    /**
     * @param event the original event for swapping to our board
     * @throws IOException if the board cannot be loaded
     *                     This method swaps us from the menu to the board when the play button is clicked
     *                     on the menu
     */
    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        boardURL = getClass().getResource("Board.fxml");
        assert boardURL != null;
        Parent root = FXMLLoader.load(boardURL);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Increments the score in textBoxScore after every ray shot
     * Used when a ray is shot in extendLineHorizontal(), extendLineDiagonalDown() and extendLineDiagonalUp()
     */
    private void RayShotScore() {
        textBoxScore.clear();
        score++;
        textBoxScore.appendText("Score: " + score);
    }


    @FXML
    public Sphere handleButtonClick(MouseEvent event) {
        // create a new sphere
        return Atoms.placeAtomsinHex(event);
    }

    /**
     * @param e the mouse event where the user clicked to fire the ray
     *          This method fires a ray horizontal noting the id and if we need to deflect/absorb
     */
    @FXML
    void extendLineHorizontal(MouseEvent e) {
        if (gameStatus == 1) {
            RayShotScore(); //update score
            Line newLine = new Line();
            newLine.setVisible(false);
            newLine.setStroke(Color.RED);
            Rectangle node = (Rectangle) e.getSource();
            node.setDisable(true);
            arrows.add(node);
            Pane boardPane = (Pane) node.getParent();
            textBox.appendText("Ray shot from " + node.getId() + "\n");
            RandomColorGen();
            RayHelpers.setStartHorizontalRay(newLine, node);
            direction_tester = Color.BLACK;

            if (node.getLayoutX() <= 250) // Case for being on the left side
            {
                int result = DeflectionHelpers.startsInside(newLine, boardPane, 0, 0);
                originalLineX = node.getLayoutX() + node.getWidth();
                originalLineY = node.getLayoutY() + 10;
                newLine.setStartY(newLine.getStartY() - 2);
                newLine.setEndY(newLine.getStartY());
                //If this returns one the node is already in a Circle of Influence so the ray will either be reflected 180 or will hit an atom
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, node, textBox, 0);
                } else {
                    //Otherwise it is not inside a circle of influence, or it will hit an atom when shot
                    extendRayHorizontalHelper(node, newLine, 0, direction_tester);
                }
            } else if (node.getLayoutX() > 250) // Case for being to the right
            {
                int result = DeflectionHelpers.startsInside(newLine, boardPane, 180, 2);
                originalLineX = node.getLayoutX() + node.getWidth();
                originalLineY = node.getLayoutY() + 10;
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, node, textBox, 0);
                } else {
                    extendRayHorizontalHelper(node, newLine, 180, direction_tester);
                }
            }
        }
    }

    /**
     * @param newLine       our original ray before we are potentially changing the direction
     * @param rayAngle      our current angle for the ray
     * @param exitNodeColor the color of the stroke for the  ray
     *                      (used for logic where we check stroke color for rectangle to see if we hit the end)
     *                      This method helps our original horizontal by handling more complex scenarios e.g deflections
     */
    void extendRayHorizontalHelper(Rectangle inputNode, Line newLine, int rayAngle, Color exitNodeColor) {
        int checker = 0, flag = 0, i = 0; // This used to help display what happened to the array, e.g. if this is equal to 1 the ray is deflected by 60
        Pane boardPane = (Pane) inputNode.getParent();
        RayHelpers.setStartofRay(newLine);
        Line oldLine = new Line(); // Needed to hold the original line if the ray deflects
        oldLine.setVisible(false);
        newLine.setVisible(false);
        Node prevNode = null;
        do {
            // If the ray deflects off the circle of influence and goes up or down the board
            RayHelpers.moveRay(newLine, rayAngle, true);
            for (Node node : boardPane.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    //If the ray hits nothing
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == exitNodeColor && inputNode != node) {
                        RayHelpers.markerHelper(circleColor, newLine, boardPane, rayAngle);
                        textBox.appendText("Ray deflected and exited at " + node.getId() + "\n"); // Line intersects with another rectangle
                        flag = 1;
                        break;
                        //If the ray comes in contact with a circle of influence
                    } else if (node instanceof Circle && checker != 2 && i > 70) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, false);
                        prevNode = node;
                        //** This is for the 180 case.Check to see that if a ray hit a Circle of Influence that there is not another one directly on top of it
                        if (DeflectionHelpers.checkifDouble(newLine, rayAngle, prevNode, false, false) != 3) {
                            if (checker == 1) {
                                RayHelpers.setOldRay(oldLine, newLine, Color.GREEN);
                                newLine.setStroke(Color.YELLOW);
                                one_circle_deflection(newLine, oldLine, node, inputNode);
                                return;
                            }
                        } else if (DeflectionHelpers.checkifDouble(newLine, rayAngle, prevNode, false, false) == 3) {
                            RayHelpers.placeWhiteMarker(textBox, inputNode);
                            flag = 1;
                            break;
                        }
                    }
                    // Case if the ray hit 2 circles of influence at the same time
                    else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, false) != -1 && node != prevNode && i > 70) {
                        newLine.setStroke(Color.GREEN);
                        two_circle_deflection(newLine, oldLine, inputNode, node, prevNode, rayAngle);
                        return;
                    }
                    // Handle case when a horizontal Ray hit an atom
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2 && i > 30) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom \n");
                        RayHelpers.placeBlackMarker(boardPane);
                        flag = 1;
                        break;
                    }
                }
            }
        } while (flag != 1);
        // If the ray was deflected then both the line before and after the ray was deflected will need to be
        RayHelpers.addRay(boardPane, oldLine);
        boardPane.getChildren().add(newLine);
        newLine.setVisible(false);
        lines.add(newLine);
    }

    /**
     * @param e the mouse event where the user clicked to fire the ray
     *          This method fires a ray diagonally down noting the id and if we need to deflect/absorb
     */
    @FXML
    void extendLineDiagonalDown(MouseEvent e) {
        if (gameStatus == 1) {
            RayShotScore();
            RandomColorGen();
            Line newLine = new Line();
            newLine.setVisible(false);
            newLine.setStroke(Color.RED);
            Rectangle inputNode = (Rectangle) e.getSource();
            inputNode.setDisable(true);
            arrows.add(inputNode);
            Pane boardPane = (Pane) inputNode.getParent();

            textBox.appendText("Ray shot from " + inputNode.getId() + "\n");
            // Set the starting point of the line
            RayHelpers.setStartofDiagonalHelper(inputNode, newLine, true);

            if (left) {
                int result = DeflectionHelpers.startsInside(newLine, boardPane, 121, 0);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, inputNode, textBox, 1);
                } else {
                    extendLineDiagonalDownHelper(newLine, inputNode, 121, direction_tester);
                }
            } else if (right) {
                int result = DeflectionHelpers.startsInside(newLine, boardPane, 59, 1);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, inputNode, textBox, 1);
                } else {
                    extendLineDiagonalDownHelper(newLine, inputNode, 59, direction_tester);

                }
            }
        }
    }

    /**
     * @param newLine   our original ray before we are potentially changing the direction
     * @param rayAngle  our current angle for the ray
     * @param color     the color of the stroke for the  ray
     * @param inputNode the invisible rectangle from where we shot our ray
     *                  This method helps our original diagonal down by handling more complex scenarios e.g deflections
     */
    void extendLineDiagonalDownHelper(Line newLine, Rectangle inputNode, int rayAngle, Color color) {
        Pane boardPane = (Pane) inputNode.getParent();
        int checker = 0;
        Line oldLine = new Line();
        oldLine.setVisible(false);
        newLine.setVisible(false);
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
            RayHelpers.moveRay(newLine, rayAngle, true);
            for (Node node : boardPane.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        RayHelpers.markerHelper(circleColor, newLine, boardPane, rayAngle); // Line intersects with another rectangle
                        textBox.appendText("Ray deflected and exited at " + node.getId() + "\n");
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2 && i > 70) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, false);
                        prevNode = node;
                        if (dFlag != 1) dFlag = DeflectionHelpers.checkifDouble(newLine, rayAngle, node, false, true);
                        if (dFlag == 4) {
                            RayHelpers.placeWhiteMarker(textBox, inputNode);
                            flag = 1;
                            break;
                        }
                        if (checker == 1) {
                            RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                            one_circle_deflection_down(newLine, oldLine, node, inputNode, rayAngle);
                            return;
                        }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, false) != -1 && node != prevNode && i > 70) {
                        if (DeflectionHelpers.checkTriple(newLine, rayAngle, node, prevNode, false) == 1) {
                            RayHelpers.placeWhiteMarker(textBox, inputNode);
                            flag = 1;
                            break;
                        }
                        RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                        two_circle_deflection_down(node, prevNode, oldLine, newLine, inputNode, rayAngle);
                        return;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) { // If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        RayHelpers.placeBlackMarker(boardPane);
                        flag = 1;
                        break;
                    }
                }
            }
        } while (flag != 1);
        // If the ray was deflected then both the line before and after the ray was deflected will need to be
        RayHelpers.addRay(boardPane, oldLine);
        boardPane.getChildren().add(newLine);
        newLine.setVisible(false);
        lines.add(newLine);
    }

    /**
     * @param e the mouse event where the user clicked to fire the ray
     *          This method fires a ray diagonally up noting the id and if we need to deflect/absorb
     */
    @FXML
    void extendLineDiagonalUp(MouseEvent e) {
        if (gameStatus == 1) {
            RayShotScore();
            Circle newCircleStart = new Circle();
            RandomColorGen();
            newCircleStart.setFill(circleColor);
            newCircleStart.setRadius(10);
            Line newLine = new Line();
            newLine.setVisible(false);
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
                    RayHelpers.setStartsIndsideHelper(result, newLine, b, textBox, 2);
                } else {
                    direction_tester = Color.YELLOW;
                    extendLineDiagonalUpHelper(newLine, b, 59, direction_tester);
                }
            } else if (right) {
                int result = DeflectionHelpers.startsInside(newLine, p, 121, 4);
                if (result == 1 || result == 3) {
                    RayHelpers.setStartsIndsideHelper(result, newLine, b, textBox, 2);
                } else {
                    direction_tester = Color.GREEN;
                    extendLineDiagonalUpHelper(newLine, b, 121, direction_tester);
                }
            }
        }
    }

    /**
     * @param newLine   our original ray before we are potentially changing the direction
     * @param rayAngle  our current angle for the ray
     * @param color     the color of the stroke for the  ray
     * @param inputNode the invisible rectangle from where we shot our ray
     *                  (used for logic where we check stroke color for rectangle to see if we hit the end)
     *                  This method helps our original diagonal up by handling more complex scenarios e.g deflections
     */
    void extendLineDiagonalUpHelper(Line newLine, Rectangle inputNode, int rayAngle, Color color) {
        int checker = 0;
        newLine.setStroke(Color.RED);
        Pane boardPane = (Pane) inputNode.getParent();
        RayHelpers.setStartofRay(newLine); // Set the trajectory of the ray
        // Needed to hold the original line if the ray deflects
        Line oldLine = new Line();
        oldLine.setVisible(false);
        newLine.setVisible(false);
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
            RayHelpers.moveRay(newLine, rayAngle, false);
            for (Node node : boardPane.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    i++;
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        RayHelpers.markerHelper(circleColor, newLine, boardPane, rayAngle);
                        textBox.appendText("Ray deflected and exited at " + node.getId() + "\n");
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2 && i > 60) {
                        checker = DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, true);
                        prevNode = node;
                        if (dFlag != 1) dFlag = DeflectionHelpers.checkifDouble(newLine, rayAngle, node, true, true);
                        if (dFlag == 4) {
                            RayHelpers.placeWhiteMarker(textBox, inputNode);
                            flag = 1;
                            break;
                        }
                        if (checker == 1) {
                            RayHelpers.setOldRay(oldLine, newLine, Color.RED);
                            one_deflection_helper_up(node, oldLine, newLine, inputNode, rayAngle);
                            return;
                        }
                        // Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && DeflectionHelpers.isInsideC((Circle) node, newLine, rayAngle, true) != -1 && node != prevNode && i > 60) {
                        if (DeflectionHelpers.checkTriple(newLine, rayAngle, node, prevNode, true) == 1) {
                            RayHelpers.placeWhiteMarker(textBox, inputNode);
                            flag = 1;
                            break;
                        }
                        RayHelpers.setOldRay(oldLine, newLine, Color.BLUE);
                        two_deflection_helper_up(node, prevNode, inputNode, rayAngle, newLine, oldLine);
                        return;
                    } else if (node instanceof Sphere && DeflectionHelpers.isInside((Sphere) node, newLine) && checker == 2) {
                        RayHelpers.placeBlackMarker(boardPane);
                        textBox.appendText("Ray hit an atom\n");
                        flag = 1;
                        break;
                    }
                }
            }
        } while (flag != 1);

        RayHelpers.addRay(boardPane, oldLine);
        boardPane.getChildren().add(newLine);
        newLine.setVisible(false);
        lines.add(newLine);
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     *                  This method handles the case of deflecting off one circle of influence
     */
    public void one_circle_deflection(Line newLine, Line oldLine, Node node, Rectangle inputNode) {
        Pane boardPane = (Pane) inputNode.getParent();
        if (newLine.getEndY() < node.getLayoutY()) {
            if (newLine.getEndX() > node.getLayoutX()) {
                RayHelpers.deflection(Color.YELLOW, oldLine, boardPane);
                extendLineDiagonalUpHelper(newLine, inputNode, 59, Color.YELLOW);
            } else {
                RayHelpers.deflection(Color.GREEN, oldLine, boardPane);
                extendLineDiagonalUpHelper(newLine, inputNode, 121, Color.GREEN);
            }
        } else {
            if (newLine.getEndX() > node.getLayoutX()) {
                RayHelpers.deflection(Color.YELLOW, oldLine, boardPane);
                extendLineDiagonalDownHelper(newLine, inputNode, 121, Color.RED);
            } else {
                RayHelpers.deflection(Color.GREEN, oldLine, boardPane);
                extendLineDiagonalDownHelper(newLine, inputNode, 59, Color.BLUE);
            }
        }
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     * @param rayAngle  the current angle of our ray
     *                  This method handles the case of deflecting off one circle of influence
     *                  specifically in a down scenario
     */
    public void one_circle_deflection_down(Line newLine, Line oldLine, Node node, Rectangle inputNode, int rayAngle) {
        Pane boardPane = (Pane) inputNode.getParent();
        oldLine.setVisible(false);
        newLine.setVisible(false);
        if (newLine.getEndY() + 10 < node.getLayoutY()) {
            if (rayAngle == 59) {
                if (newLine.getEndX() > node.getLayoutX()) {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 1, Color.BLACK);
                } else {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 180, Color.BLACK);
                }
            } else {
                if (newLine.getEndX() > node.getLayoutX()) {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 0, Color.BLACK);
                } else {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 179, Color.BLACK);
                }
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (newLine.getEndX() > node.getLayoutX()) {
                RayHelpers.addRay(boardPane, oldLine);
                direction_tester = Color.GREEN;
                extendLineDiagonalDownHelper(newLine, inputNode, 59, Color.RED);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                direction_tester = Color.YELLOW;
                extendLineDiagonalDownHelper(newLine, inputNode, 121, Color.BLUE);
            }
        }
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     * @param rayAngle  the current angle of our ray
     * @param prevNode  the second circle of influence we have collided with
     *                  This method handles the case of deflecting off two circle of influences
     *                  specifically in a down scenario
     */
    public void two_circle_deflection_down(Node node, Node prevNode, Line oldLine, Line newLine, Rectangle inputNode, int rayAngle) {
        Pane boardPane = (Pane) inputNode.getParent();
        oldLine.setVisible(false);
        newLine.setVisible(false);
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        averageY -= 45;
        if (newLine.getEndY() < averageY) {
            if (rayAngle == 59) {
                RayHelpers.addRay(boardPane, oldLine);
                extendLineDiagonalUpHelper(newLine, inputNode, 121, Color.GREEN);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                extendLineDiagonalUpHelper(newLine, inputNode, 59, Color.YELLOW);
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (rayAngle == 121) {
                RayHelpers.addRay(boardPane, oldLine);
                extendRayHorizontalHelper(inputNode, newLine, 0, Color.BLACK);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                extendRayHorizontalHelper(inputNode, newLine, 180, Color.BLACK);
            }
        }
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     * @param rayAngle  the current angle of our ray
     * @param prevNode  the second circle of influence we have collided with
     *                  This method handles the case of deflecting off two circle of influences
     */
    public void two_circle_deflection(Line newLine, Line oldLine, Rectangle inputNode, Node node, Node prevNode, int rayAngle) {
        Pane boardPane = (Pane) inputNode.getParent();
        oldLine.setVisible(false);
        newLine.setVisible(false);
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        Color deflectionColor;
        int angle;
        if (newLine.getEndY() < averageY) {
            deflectionColor = (rayAngle == 0) ? Color.YELLOW : Color.GREEN;
            angle = (rayAngle == 0) ? -60 : -120;
        } else {
            deflectionColor = (rayAngle == 0) ? Color.BLUE : Color.RED;
            angle = (rayAngle == 0) ? 121 : 58;
        }
        RayHelpers.deflection(deflectionColor, oldLine, boardPane);
        if (newLine.getEndY() < averageY) {
            extendLineDiagonalUpHelper(newLine, inputNode, angle, deflectionColor);
        } else {
            extendLineDiagonalDownHelper(newLine, inputNode, angle, deflectionColor);
        }
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     * @param rayAngle  the current angle of our ray
     *                  This method handles the case of deflecting off one circle of influence
     *                  specifically in a up scenario
     */
    public void one_deflection_helper_up(Node node, Line oldLine, Line newLine, Rectangle inputNode, int rayAngle) {
        Pane boardPane = (Pane) inputNode.getParent();
        if (newLine.getEndY() - 10 > node.getLayoutY()) {
            if (rayAngle == 59) {
                if (newLine.getEndX() < node.getLayoutX()) {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 180, Color.BLACK);
                } else {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 0, Color.BLACK);
                }
            } else {
                if (newLine.getEndX() < node.getLayoutX()) {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 181, Color.BLACK);
                } else {
                    RayHelpers.addRay(boardPane, oldLine);
                    extendRayHorizontalHelper(inputNode, newLine, 0, Color.BLACK);
                }
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (newLine.getEndX() < node.getLayoutX()) {
                direction_tester = Color.YELLOW;
                RayHelpers.addRay(boardPane, oldLine);
                extendLineDiagonalUpHelper(newLine, inputNode, 59, Color.YELLOW);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                direction_tester = Color.GREEN;
                extendLineDiagonalUpHelper(newLine, inputNode, 121, Color.GREEN);
            }
        }
    }

    /**
     * @param newLine   our ray after a deflection has occurred
     * @param oldLine   our ray before a deflection has occurred
     * @param node      a circle of influence we have collided with
     * @param inputNode the original invisible rectangle where we fired our ray from
     * @param rayAngle  the current angle of our ray
     * @param prevNode  the second circle of influence we have collided with
     *                  This method handles the case of deflecting off two circle of influences
     *                  specifically in a up scenario
     */
    public void two_deflection_helper_up(Node node, Node prevNode, Rectangle inputNode, int rayAngle, Line newLine, Line oldLine) {
        Pane boardPane = (Pane) inputNode.getParent();
        oldLine.setVisible(false);
        newLine.setVisible(false);
        double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
        averageY += 45;

        // If the ray deflects at the bottom of the sphere of influence
        if (newLine.getEndY() > averageY) {
            if (rayAngle == 121) {
                RayHelpers.addRay(boardPane, oldLine);
                extendLineDiagonalDownHelper(newLine, inputNode, 59, Color.RED);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                extendLineDiagonalDownHelper(newLine, inputNode, 121, Color.BLUE);
            }
            // If the ray deflects at the side of the sphere of influence
        } else {
            if (rayAngle == 121) {
                RayHelpers.addRay(boardPane, oldLine);
                extendRayHorizontalHelper(inputNode, newLine, 179, Color.BLACK);
            } else {
                RayHelpers.addRay(boardPane, oldLine);
                extendRayHorizontalHelper(inputNode, newLine, 1, Color.BLACK);
            }
        }
    }

    void RandomColorGen() {
        Random random = new Random();
        //Plus 1 is so that we never get black as that is saved for absorbed
        int r = random.nextInt(255) + 1;
        int g = random.nextInt(255) + 1;
        int b = random.nextInt(255) + 1;
        circleColor = Color.rgb(r, g, b);
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
        Atoms.invisibleAtoms(spherepane, start_end_button, nextPlayer);
        if (atomcount == 6) {
            gameStatus = 1;
        }

    }

    private void calculateScore() {
        textBoxScore.clear();
        textBoxScore.appendText("FINAL SCORE: " + player1Score + " - " + player2Score + "\n");
        if (player1Score < player2Score) {
            textBox.appendText("""
                    PLAYER 1 WINS!!!
                       \\\\
                       (o>
                    \\\\_//)
                      \\_/_)
                        _|_\t winner winner chicken dinner
                    """);
        } else if (player2Score < player1Score) {
            textBox.appendText("""
                    PLAYER 2 WINS!!!
                       \\\\
                       (o>
                    \\\\_//)
                      \\_/_)
                        _|_\t winner winner chicken dinner
                    """);
        } else {
            textBox.appendText("IT'S A TIE!! EVERYONE'S A WINNER :D\n");
        }
    }

    private void removeLines() {

        Platform.runLater(() -> { // avoid threading issues
            for (Line line : lines) {
                Parent parent = line.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(line);
                } else {
                    throw new UnsupportedOperationException("failed to remove line");
                }
            }

            lines.clear(); // Clear arrayList
        });

    }

    private void reenableArrows() {
        for (Rectangle arrow : arrows) {
            arrow.setDisable(false);
        }
    }


    public void restart() {
        Atoms.removeAtoms(spherepane);
        removeLines();
        RayHelpers.removeRayMarkers();
        reenableArrows();

        textBox.clear();

        if (player1Score < 0) {
            player1Score = getScore();
            textBox.appendText("PLAYER 2\n");
            nextPlayer.setVisible(false);
            nextPlayer.setText("Final Results");
            start_end_button.setText("Start");
            start_end_button.setVisible(true);
            printScore();
        } else if (player2Score < 0) {
            printScore();
            player2Score = getScore();
            start_end_button.setVisible(false);
            nextPlayer.setText("Quit");
            calculateScore();
        } else {
            quit();
        }

        setAtomcount(0);
        setScore(0);
    }

    public Stage getStage() {
        return stage;
    }

    public void setScore(int x) {
        score = x;
    }

    public URL getBoardURL() {
        return boardURL;
    }

    @FXML
    public void pauseGame(KeyEvent k) throws IOException {
        KeyCode key = k.getCode();
        if (key == KeyCode.ESCAPE) {
            pausePopUp();
        }
    }

    @FXML
    public static Stage stage2 = new Stage();

    /**
     * @throws IOException in case we cannot load the pause menu
     *                     Method that pops up our pause menu when user presses esc key
     */
    @FXML
    void pausePopUp() throws IOException {
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
    void quit() {
        exit(0);
    }

    @FXML
    void pauseContinue() {
        stage2.close();
    }


}