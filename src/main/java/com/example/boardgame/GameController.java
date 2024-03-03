package com.example.boardgame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class GameController {
    private boolean up = false;
    private Color direction_tester;
    private boolean right = false;
    private boolean left = false;
    private int atomcount = 0;
    private Pane spherepane;

    void setAtomcount(int atomcount1) {
        this.atomcount = atomcount1;
    }

    void setSpherepane(Pane spherepane1) {
        this.spherepane = spherepane1;
    }

    @FXML
    private Button play;

    @FXML
    private Button quit;

    @FXML
    public Rectangle N49;

    @FXML
    private Stage stage;
    private Scene scene;
    private URL boardURL;

    @FXML
    void leave(ActionEvent event) {
        System.out.println("Quitting Game");
        System.exit(0);
    }


    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        boardURL = getClass().getResource("Board.fxml");
        Parent root = FXMLLoader.load(boardURL);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            button10, button11, button12, button13, button14, button15, button16,
            button18, button19, button20, button21, button22, button23, button24, button25,
            button26, button27, button28, button29, button30, button31, button32,
            button34, button35, button36, button37, button38, button39, button40, button41,
            button42, button43, button44, button45, button46, button47, button48, button49,
            button50, button51, button52, button53, button54, button55, button56, button57,
            button58, button59, button60, button61, button62;


    @FXML
    public Sphere handleButtonClick(ActionEvent event) {
        // create a new sphere
        if (atomcount < 6) {
            Button b = (Button) event.getSource();
            Sphere sphere = new Sphere(25);
            Circle sphere1 = new Circle(85);
            double x = b.getLayoutX();
            double y = b.getLayoutY();

            // set the sphere color to red
            sphere.setMaterial(new PhongMaterial(Color.RED));
            sphere.setLayoutX(x + 37);
            sphere.setLayoutY(y + 24);
            sphere1.setLayoutX(x + 37);
            sphere1.setLayoutY(y + 24);
            sphere1.setStroke(Color.WHITE);
            sphere1.setFill(Color.TRANSPARENT);

            // get the parent node of the button
            Pane parent = (Pane) b.getParent();
            spherepane = (Pane) b.getParent();
            // replace the button with the sphere
            parent.getChildren().add(sphere);
            parent.getChildren().add(sphere1);
            atomcount++;

            return sphere;
        }
        return null;
    }

    @FXML
    void extendLineHorizontal(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();
        System.out.println("Ray shot from " + b.getId());

        newLine.setStartX(b.getLayoutX() + b.getWidth());
        newLine.setStartY(b.getLayoutY() + b.getHeight() / 2);
        if (b.getLayoutX() <= 250) //Case for being on the left side
        {
            extendRayHorizontalHelper(e, newLine, p, b, +1);
        } else if (b.getLayoutX() > 250) //Case for being to the right
        {
            extendRayHorizontalHelper(e, newLine, p, b, -1);
        }
    }

    void extendRayHorizontalHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x) {
        newLine.setEndX(newLine.getStartX() + x * 10);
        newLine.setEndY(newLine.getStartY());

        int flag = 0;

        do {
            newLine.setEndX(newLine.getEndX() + x); // Increase the line length

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && b != node) {
                        // Line intersects with another button
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit a node");
                        flag = 1;
                    }
                }

            }

        } while (flag != 1);
        p.getChildren().add(newLine);
    }

    @FXML
    void extendLineDiagonalDown(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            extendLineDiagonalDownHelper(e,newLine,p,b,120,direction_tester);

        } else if (right) {
            extendLineDiagonalDownHelper(e,newLine,p,b,60,direction_tester);
        }
    }

    void extendLineDiagonalDownHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color)
    {
        up = false;
        int checker = 0;
        int line_flag = 0;
        //Set the trajectory of the ray
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        //Needed to hold the original line if the ray deflects
        Line oldLine = new Line();

        //What colour nodes the ray needs to be hitting to exit
        if(direction_tester ==Color.GREEN )
        {
            color = Color.RED;
        }
        else if (direction_tester == Color.YELLOW)
        {
            color = Color.BLUE;
        }
        int flag = 0;
        Node prevNode = null;

        do {

            //If the ray deflects off the circle of influence and goes up the board instead
            if ( (color == Color.GREEN && direction_tester == Color.GREEN) || (color == Color.YELLOW && direction_tester == Color.YELLOW) ) {
                newLine.setEndX(newLine.getEndX() - dx);
                newLine.setEndY(newLine.getEndY() - dy);
            } else {
                newLine.setEndX(newLine.getEndX() + dx);
                newLine.setEndY(newLine.getEndY() + dy);
            }

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color) {
                        // Line intersects with another rectangle
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2) {
                        checker = isInsideC((Circle) node, newLine, x);
                        prevNode = node;
                        if (checker == 1) {
                            if (line_flag == 0) {
                                if(direction_tester == Color.GREEN) {
                                    oldLine.setStroke(Color.RED);
                                    newLine.setStroke(Color.BLUE);
                                }
                                else if(direction_tester == Color.YELLOW)
                                {
                                    oldLine.setStroke(Color.BLUE);
                                    newLine.setStroke(Color.RED);
                                }
                                oldLine.setStartY(newLine.getStartY());
                                oldLine.setStartX(newLine.getStartX());
                                oldLine.setEndY(newLine.getEndY());
                                oldLine.setEndX(newLine.getEndX());

                                //The color

                                //If the ray deflects at the top of the sphere of influence
                                if (newLine.getEndY() < node.getLayoutY() - 80) {
                                    if(x == 60) {
                                        angleRadians = Math.toRadians(1);
                                    }
                                    else{  angleRadians = Math.toRadians(179);}
                                    color = Color.BLACK;
//                                    if(direction_tester == Color.GREEN) {
//                                        color = Color.GREEN;
//                                    }
//                                    else if (direction_tester == Color.YELLOW)
//                                    {
//                                        color = Color.YELLOW;
//                                    }
                                    //If the ray deflects at the side of the sphere of influence
                                } else {
                                    if(x == 60) {
                                        angleRadians = Math.toRadians(120);
                                    }
                                    else angleRadians = Math.toRadians(61);
                                    if(direction_tester == Color.GREEN) {
                                        color = Color.BLUE;
                                    }
                                    else if (direction_tester == Color.YELLOW)
                                    {
                                        color = Color.RED;
                                    }
                                }

                                //Set trajectory of the deflected ray
                                dx = Math.cos(angleRadians);
                                dy = Math.sin(angleRadians);

                                newLine.setStartX(newLine.getEndX());
                                newLine.setStartY(newLine.getEndY());
                                newLine.setEndX(newLine.getEndX() + dx);
                                newLine.setEndY(newLine.getEndY() + dy);
                            }
                            //Circle has been hit by the ray
                            line_flag++;

                        }
                        //Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && isInsideC((Circle) node, newLine, x) != -1 && node != prevNode) {
                        if (line_flag == 0) {
                            //Same as the case above, store the ray before the deflection in a new line
                            if(direction_tester == Color.GREEN) {
                                oldLine.setStroke(Color.RED);
                                //The color of the line
                                newLine.setStroke(Color.BLUE);
                            }
                            else if(direction_tester == Color.YELLOW) {
                                oldLine.setStroke(Color.BLUE);
                                //The color of the line
                                newLine.setStroke(Color.RED);
                            }
                            oldLine.setStartY(newLine.getStartY());
                            oldLine.setStartX(newLine.getStartX());
                            oldLine.setEndY(newLine.getEndY());
                            oldLine.setEndX(newLine.getEndX());


                            //If the ray deflects at the top of the sphere of influence
                            if (newLine.getEndY() < node.getLayoutY() - 80) {
                                if(x == 60) {
                                    angleRadians = Math.toRadians(122);
                                }
                                else{  angleRadians = Math.toRadians(58);}
                                //color = Color.BLACK;
                                if(direction_tester == Color.GREEN) {
                                    color = Color.GREEN;
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    color = Color.YELLOW;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                if(x == 60) {
                                    angleRadians = Math.toRadians(180);
                                }
                                else angleRadians = Math.toRadians(1);
                                color = Color.BLACK;
                            }

                            //Set trajectory of the deflected ray
                            dx = Math.cos(angleRadians);
                            dy = Math.sin(angleRadians);

                            newLine.setStartX(newLine.getEndX());
                            newLine.setStartY(newLine.getEndY());
                            newLine.setEndX(newLine.getEndX() + dx);
                            newLine.setEndY(newLine.getEndY() + dy);
                        }
                        line_flag++;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        //If the ray was deflected then both the line before and after the ray was deflected will need to be printed
        if (line_flag != 1) {
            p.getChildren().add(oldLine);
            p.getChildren().add(newLine);
        } else {
            p.getChildren().add(newLine);
        }
    }

    @FXML
    void extendLineDiagonalUp(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = b.getLayoutX() + b.getWidth() / 2;
        double startY = b.getLayoutY() + b.getHeight() / 2;

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            extendLineDiagonalUpHelper(e, newLine, p, b, 60, direction_tester);
        } else if (right) {
            extendLineDiagonalUpHelper(e, newLine, p, b, 120, direction_tester);
        }
    }

    //Case for up left
    @FXML
    void diagonaldirectionleft() {
        direction_tester = Color.YELLOW;
        left = true;
        right = false;
    }

    //case for up right
    @FXML
    void diagonaldirectionright()
    {
        direction_tester = Color.GREEN;
        right = true;
        left = false;
    }

    void extendLineDiagonalUpHelper(MouseEvent e, Line newLine,Pane p, Rectangle b,int x,Color color)
    {
        up = true;
        int checker = 0;
        int line_flag = 0;
        //Set the trajectory of the ray
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        //Needed to hold the original line if the ray deflects
        Line oldLine = new Line();

        //What colour nodes the ray needs to be hitting to exit
        if(direction_tester ==Color.GREEN )
        {
            color = Color.GREEN;
        }
        else if (direction_tester == Color.YELLOW)
        {
            color = Color.YELLOW;
        }
        int flag = 0;
        Node prevNode = null;

        do {

            //If the ray deflects off the circle of influence and goes up the board instead
            if ( (color == Color.BLUE && direction_tester == Color.GREEN) || (color == Color.RED && direction_tester == Color.YELLOW) ) {
                newLine.setEndX(newLine.getEndX() + dx);
                newLine.setEndY(newLine.getEndY() + dy);
            } else {
                newLine.setEndX(newLine.getEndX() - dx);
                newLine.setEndY(newLine.getEndY() - dy);
            }

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color) {
                        // Line intersects with another rectangle
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2) {
                        checker = isInsideC((Circle) node, newLine, x);
                        prevNode = node;
                        if (checker == 1) {
                            if (line_flag == 0) {
                                if(direction_tester == Color.GREEN) {
                                    oldLine.setStroke(Color.RED);
                                    newLine.setStroke(Color.BLUE);
                                }
                                else if(direction_tester == Color.YELLOW)
                                {
                                    oldLine.setStroke(Color.BLUE);
                                    newLine.setStroke(Color.RED);
                                }
                                oldLine.setStartY(newLine.getStartY());
                                oldLine.setStartX(newLine.getStartX());
                                oldLine.setEndY(newLine.getEndY());
                                oldLine.setEndX(newLine.getEndX());

                                //The color

                                //If the ray deflects at the bottom of the sphere of influence
                                if (newLine.getEndY() > node.getLayoutY() + 80) {
                                    if(x == 60) {
                                        angleRadians = Math.toRadians(1);
                                    }
                                    else{  angleRadians = Math.toRadians(179);}
                                    color = Color.BLACK;
//                                    if(direction_tester == Color.GREEN) {
//                                        color = Color.GREEN;
//                                    }
//                                    else if (direction_tester == Color.YELLOW)
//                                    {
//                                        color = Color.YELLOW;
//                                    }
                                    //If the ray deflects at the side of the sphere of influence
                                } else {
                                    if(x == 60) {
                                        angleRadians = Math.toRadians(120);
                                    }
                                    else angleRadians = Math.toRadians(61);
                                    if(direction_tester == Color.GREEN) {
                                        color = Color.YELLOW;
                                    }
                                    else if (direction_tester == Color.YELLOW)
                                    {
                                        color = Color.GREEN;
                                    }
                                }

                                //Set trajectory of the deflected ray
                                dx = Math.cos(angleRadians);
                                dy = Math.sin(angleRadians);

                                newLine.setStartX(newLine.getEndX());
                                newLine.setStartY(newLine.getEndY());
                                newLine.setEndX(newLine.getEndX() - dx);
                                newLine.setEndY(newLine.getEndY() - dy);
                            }
                            //Circle has been hit by the ray
                            line_flag++;

                        }
                        //Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && isInsideC((Circle) node, newLine, x) != -1 && node != prevNode) {
                        if (line_flag == 0) {
                            //Same as the case above, store the ray before the deflection in a new line
                            if(direction_tester == Color.GREEN) {
                                oldLine.setStroke(Color.RED);
                                //The color of the line
                                newLine.setStroke(Color.BLUE);
                            }
                            else if(direction_tester == Color.YELLOW) {
                                oldLine.setStroke(Color.BLUE);
                                //The color of the line
                                newLine.setStroke(Color.RED);
                            }
                            oldLine.setStartY(newLine.getStartY());
                            oldLine.setStartX(newLine.getStartX());
                            oldLine.setEndY(newLine.getEndY());
                            oldLine.setEndX(newLine.getEndX());


                            //If the ray deflects at the bottom of the sphere of influence
                            if (newLine.getEndY() > node.getLayoutY() + 80) {
                                if(x == 60) {
                                    angleRadians = Math.toRadians(58);
                                }
                                else{  angleRadians = Math.toRadians(230);}
                                //color = Color.BLACK;
                                if(direction_tester == Color.GREEN) {
                                    color = Color.RED;
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    color = Color.BLUE;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                if(x == 60) {
                                    angleRadians = Math.toRadians(180);
                                }
                                else angleRadians = Math.toRadians(1);
                                color = Color.BLACK;
                            }

                            //Set trajectory of the deflected ray
                            dx = Math.cos(angleRadians);
                            dy = Math.sin(angleRadians);

                            newLine.setStartX(newLine.getEndX());
                            newLine.setStartY(newLine.getEndY());
                            newLine.setEndX(newLine.getEndX() - dx);
                            newLine.setEndY(newLine.getEndY() - dy);
                        }
                        line_flag++;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        //If the ray was deflected then both the line before and after the ray was deflected will need to be printed
        if (line_flag != 1) {
            p.getChildren().add(oldLine);
            p.getChildren().add(newLine);
        } else {
            p.getChildren().add(newLine);
        }
    }


    @FXML
    public void toggleAtoms(ActionEvent event) {
        //If we have a normal amount of atoms placed
        if (atomcount >= 3 && atomcount <= 6) {
            //go through all the children of the pane spherepane
            for (Node child: spherepane.getChildren()) {
                //if the child is a sphere
                if (child instanceof Sphere) {
                    //make the child a sphere so we can use sphere methods
                    Sphere sphere = (Sphere) child;
                    //make the sphere invisible/visible depending on the return of isVisible
                    sphere.setVisible(!sphere.isVisible());
                }

            }

        }

    }

    //Method to check that the line actually hit the atom by using the distance formula
    public boolean isInside(Sphere x, Line l) {
        double radius = x.getRadius();

        double distance = Math.sqrt(Math.pow((x.getLayoutX() - l.getEndX()), 2) + Math.pow((x.getLayoutY() - l.getEndY()), 2));
        if (distance <= radius) {
            return true;
        } else {
            return false;
        }
    }

    public int isInsideC(Circle c, Line l, int angle) {
        double radius = c.getRadius();
        Pane p = (Pane) c.getParent();
        int checker = 0;

        //Distance formula to check if the ray actually hit the circle
        double distance = Math.sqrt(Math.pow((c.getLayoutX() - l.getEndX()), 2) + Math.pow((c.getLayoutY() - l.getEndY()), 2));
        if (distance - 25 <= radius) {
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
            for (int i = 0; i < 85; i++) {
                if(up)
                {
                    lineTest.setEndX(lineTest.getEndX() - dx);
                    lineTest.setEndY(lineTest.getEndY() - dy);
                }
                else
                {
                    lineTest.setEndX(lineTest.getEndX() + dx);
                    lineTest.setEndY(lineTest.getEndY() + dy);
                }

                for (Node node: p.getChildren()) {
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


    public Stage getStage() {
        return stage;
    }

    public URL getBoardURL() {
        return boardURL;
    }
}
