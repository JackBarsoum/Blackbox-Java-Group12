package com.example.boardgame;
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
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLOutput;


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
    public TextArea textBox; //Text box where results of rays shots will be displayed

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
       // stage.setFullScreen(true);
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
    public Sphere handleButtonClick(MouseEvent event) {
        // create a new sphere
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
            spherepane = (Pane) hexagon.getParent();
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
        System.out.println("Ray shot from " + b.getId() + "\n");
        textBox.appendText("Ray shot from " + b.getId() + "\n");

        newLine.setStartX(b.getLayoutX() + b.getWidth());
        newLine.setStartY(b.getLayoutY() + b.getHeight() / 2);
        if (b.getLayoutX() <= 250) //Case for being on the left side
        {
            extendRayHorizontalHelper(e, newLine, p, b, +1);
        } else if (b.getLayoutX() > 250) //Case for being to the right
        {
            System.out.println(b.getLayoutX());
            extendRayHorizontalHelper(e, newLine, p, b, -1);

        }
    }

    void extendRayHorizontalHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x) {
        newLine.setEndX(newLine.getStartX() + x * 10);
        newLine.setEndY(newLine.getStartY());

        Line oldLine = new Line();
        oldLine.setStroke(Color.GREEN);

        double angleRadians = Math.toRadians(x);

        int flag = 0;
        double hx = x;
        double hy = 0;

        boolean side = false; //if either top or bottom sides are hit (not middle)

        int checker = 0;



        do {
            newLine.setEndX(newLine.getEndX() + hx); // Increase the line length
            newLine.setEndY(newLine.getEndY() + hy);

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && b != node && ((Rectangle) node).getStroke() == Color.BLACK) {
                        // Line intersects with another button
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        textBox.appendText("Ray hit nothing and exited at " + node.getId() + "\n");
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit a atom");
                        textBox.appendText("Ray hit an atom" + "\n");
                        flag = 1;
                    } else if(node instanceof Circle && ((newLine.getEndY() > node.getLayoutY() + 10) || (newLine.getEndY() + 10 < node.getLayoutY())) && checker != 1){
                        //line intersects with a circle of influence

                        //copy newLine onto oldLine
                        oldLine.setStartY(newLine.getStartY());
                        oldLine.setStartX(newLine.getStartX());
                        oldLine.setEndY(newLine.getEndY());
                        oldLine.setEndX(newLine.getEndX() + (40*x));

                        newLine.setStartX(oldLine.getEndX());
                        newLine.setStartY(oldLine.getEndY());
                        System.out.println("old line: " + oldLine.getStartX() + "," + oldLine.getStartY() + " | " + oldLine.getEndX() + "," + oldLine.getEndY());


                        System.out.println("HIT CIRCLE " + node.getLayoutY());
                        if(newLine.getEndY() > node.getLayoutY() + 10){ //hits near bottom
                            side = true;
                            if(newLine.getEndX() > node.getLayoutX()){ //hits right side
                                angleRadians = Math.toRadians(125);
                                System.out.println("HIT BOTTOM RIGHT");
                            }
                            else{ //left side
                                angleRadians = Math.toRadians(55);
                                System.out.println("HIT BOTTOM LEFT");
                            }

                        }
                        else{ //hits near top
                            side = true;
                            if(newLine.getEndX() + 10 > node.getLayoutX()){ //hits right side
                                angleRadians = Math.toRadians(-125);
                                System.out.println("HIT TOP RIGHT");
                            }
                            else{ //left side
                                angleRadians = Math.toRadians(-55);
                                System.out.println("HIT TOP LEFT");
                            }
                        }
                        hx = Math.cos(angleRadians);
                        hy = Math.sin(angleRadians);

                        checker = 1;

                    }
                }
            }

        } while (flag != 1);

        p.getChildren().add(oldLine);
        p.getChildren().add(newLine);


    }

    @FXML
    void extendLineDiagonalDown(MouseEvent e) {
        Line newLine = new Line();
        newLine.setStroke(Color.RED);
        Rectangle b = (Rectangle) e.getSource();
        Pane p = (Pane) b.getParent();

        textBox.appendText("Ray shot from " + b.getId() + "\n");
        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = 0;
        double startY = 0;
        if(right) {
             startX = b.getLayoutX() + b.getWidth() / 2 + 8;
             startY = b.getLayoutY() + b.getHeight() / 2 + 10;
        }else if (left){
             startX = b.getLayoutX() + b.getWidth() / 2 - 8;
             startY = b.getLayoutY() + b.getHeight() / 2 + 10;
        }
        System.out.println(startX);
        System.out.println(startY);

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            extendLineDiagonalDownHelper(e,newLine,p,b,121,direction_tester);

        } else if (right) {
            extendLineDiagonalDownHelper(e,newLine,p,b,59,direction_tester);
        }
    }

    void extendLineDiagonalDownHelper(MouseEvent e, Line newLine, Pane p, Rectangle b, int x, Color color)
    {
        up = false;
        int checker = 0;
        int line_flag = 0;
        int deflection_account = 0; // This used to help display what happened to the array, e.g if this is equal to 1 the ray is deflected by 60
        //Set the trajectory of the ray
        double angleRadians = Math.toRadians(x);
        double dx = Math.cos(angleRadians);
        double dy = Math.sin(angleRadians);

        //Needed to hold the original line if the ray deflects
        Line oldLine = new Line();

        //What colour nodes the ray needs to be hitting to exit
        if(direction_tester == Color.GREEN )
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
                    if (node instanceof Rectangle && ((Rectangle) node).getStroke() == color && node.getBoundsInParent().intersects(newLine.getBoundsInParent())) {
                        System.out.println(deflection_account);
                        String node_exit;
                        // Line intersects with another rectangle
                        switch (deflection_account) {
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
                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2) {
                        checker = isInsideC((Circle) node, newLine, x);
                        if(checker != -1){
                            deflection_account = 1;
                        }
                        prevNode = node;
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

                                //The color

                                //If the ray deflects at the top of the sphere of influence
                                if (newLine.getEndY() + 10 < node.getLayoutY()) {
                                    if (x == 60) {
                                        if(newLine.getEndX() > node.getLayoutX()){
                                        System.out.println("Downtest1");
                                        angleRadians = Math.toRadians(180);
                                        color = Color.BLACK;
                                    } else {
                                        angleRadians = Math.toRadians(180);
                                        color = Color.BLACK;
                                        System.out.println("Downtest2");
                                    }
                                } else {
                                    if (newLine.getEndX() > node.getLayoutX()) {
                                        System.out.println("Downtest6");
                                        angleRadians = Math.toRadians(0);
                                        color = Color.BLACK;
                                    } else {
                                        angleRadians = Math.toRadians(179);
                                        System.out.println("Downtest7");
                                        color = Color.BLACK;
                                    }
                                }

//                                    if(direction_tester == Color.GREEN) {
//                                        color = Color.GREEN;
//                                    }
//                                    else if (direction_tester == Color.YELLOW)
//                                    {
//                                        color = Color.YELLOW;
//                                    }
                                    //If the ray deflects at the side of the sphere of influence

                                } else {
                                    if(x == 60){
                                        angleRadians = Math.toRadians(60);
                                        System.out.println("Tester");
                                        color = Color.BLACK;
                                    }
                                    else {
                                        if(newLine.getEndX() > node.getLayoutX()){
                                            angleRadians = Math.toRadians(59);
                                            System.out.println("Downtest8");
                                            color = Color.RED;
                                        }else{
                                            angleRadians = Math.toRadians(121);
                                            System.out.println("Downtest9");
                                            color = Color.BLUE;
                                        }
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
                            line_flag+= 2;

                        }
                        //Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && isInsideC((Circle) node, newLine, x) != -1 && node != prevNode) {
                        deflection_account = 2;
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

                            double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                            System.out.println(newLine.getEndY());
                            System.out.println(averageY);
                            averageY -= 45;

                            //If the ray deflects at the top of the sphere of influence
                            if (newLine.getEndY() < averageY && right || newLine.getEndY() < averageY && left) {
                                System.out.println("Downtest10");
                                if(direction_tester == Color.GREEN) {
                                    color = Color.GREEN;
                                    System.out.println("Downtest12");
                                    angleRadians = Math.toRadians(123);
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    System.out.println("Downtest13");
                                    angleRadians = Math.toRadians(59);
                                    color = Color.YELLOW;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                if(x == 59) {
                                    System.out.println("HHello");
                                    angleRadians = Math.toRadians(180);
                                }
                                else angleRadians = Math.toRadians(0);
                                System.out.println("HHHHHELLOOOO");
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
                        line_flag+= 2;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
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

        textBox.appendText("Ray shot from " + b.getId() + "\n");
        System.out.println("Ray shot from " + b.getId());
        // Set the starting point of the line
        double startX = 0;
        double startY  = 0;

        if(right){
            startX = b.getLayoutX() + b.getWidth() / 2 + 4;
            startY = b.getLayoutY() + b.getHeight() / 2 - 13;
        }else{
            startX = b.getLayoutX() + b.getWidth() / 2;
            startY = b.getLayoutY() + b.getHeight() / 2;

        }

        newLine.setStartX(startX);
        newLine.setStartY(startY);

        // Set the initial end point (same as start point)
        newLine.setEndX(startX);
        newLine.setEndY(startY);

        if (left) {
            extendLineDiagonalUpHelper(e, newLine, p, b, 59, direction_tester);
        } else if (right) {
            extendLineDiagonalUpHelper(e, newLine, p, b, 121, direction_tester);
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
        int delfection_account = 0;
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
                        switch (delfection_account) {
                            case 0:
                                System.out.println("Ray hit nothing and exited at " + node.getId());
                                textBox.appendText("Ray hit nothing and exited at " + node.getId() + "\n");
                                break;
                            case 1:
                                System.out.println("Ray deflected at 60 degrees, hit nothing and exited " + node.getId());
                                textBox.appendText("Ray deflected at 60 degrees, hit nothing and exited at " + node.getId() + "\n");
                                break;
                            case 2:
                                System.out.println("Ray deflected at 120 degrees, hit nothing and exited " + node.getId());
                                textBox.appendText("Ray deflected at 120 degrees, hit nothing and exited at " + node.getId() + "\n");
                                break;
                        }

                        flag = 1;
                        break;
                    } else if (node instanceof Circle && checker != 2) {
                        checker = isInsideC((Circle) node, newLine, x);
                        if(checker != -1){
                            delfection_account = 1;
                        }
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
                                if (newLine.getEndY() - 10 > node.getLayoutY()) {
                                    System.out.println(newLine.getEndY());
                                    System.out.println(node.getLayoutY());
                                    if(x == 59) {
                                        if(newLine.getEndX() < node.getLayoutX()) {
                                            System.out.println("Hello1");
                                            angleRadians = Math.toRadians(1);
                                            color = Color.BLACK;
                                        }else{
                                            angleRadians = Math.toRadians(180);
                                            System.out.println("Hell on Earth2");
                                            color = Color.BLACK;
                                        }
                                    }
                                    else{
                                        if(newLine.getEndX() < node.getLayoutX()) {
                                            System.out.println("Hello2");
                                            angleRadians = Math.toRadians(1);
                                            color = Color.BLACK;
                                        }else{
                                            angleRadians = Math.toRadians(179);
                                            System.out.println("Hell on Earth1");
                                            color = Color.BLACK;
                                        }
                                        }
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
                                        angleRadians = Math.toRadians(60);
                                        System.out.println("Tester");
                                    }
                                    else {
                                        if (newLine.getEndX() < node.getLayoutX()) {
                                            angleRadians = Math.toRadians(60);
                                            System.out.println("Lol");
                                            color = Color.YELLOW;
                                        } else {
                                            angleRadians = Math.toRadians(121);
                                            System.out.println("LOL2");
                                            color = Color.GREEN;
                                        }
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
                            line_flag += 2;

                        }
                        //Case if the ray hit 2 circles of influence at the same time
                    } else if (node instanceof Circle && isInsideC((Circle) node, newLine, x) != -1 && node != prevNode) {
                        delfection_account = 2;
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

                            double averageY = (node.getLayoutY() + prevNode.getLayoutY()) / 2;
                            System.out.println(newLine.getEndY());
                            System.out.println(averageY);
                            averageY += 45;

                            //If the ray deflects at the bottom of the sphere of influence
                            if (newLine.getEndY() > averageY && right || newLine.getEndY() > averageY && left) {
                                System.out.println("BOTTOM");

                                //color = Color.BLACK;
                                System.out.println("Test2");
                                System.out.println(direction_tester);
                                if(direction_tester == Color.GREEN) {
                                    System.out.println("Test2.1");
                                    color = Color.RED;
                                    angleRadians = Math.toRadians(238);
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    System.out.println("Test2.2");
                                    angleRadians = Math.toRadians(302);
                                    color = Color.BLUE;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                System.out.println("SIDE");
                                if(direction_tester == Color.GREEN) {
                                    System.out.println("Side1");
                                    angleRadians = Math.toRadians(0);

                                }
                                else{
                                    angleRadians = Math.toRadians(180);
                                }
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
                        line_flag+= 2;
                    }
                    // Handle the case where the line hits another circle at the same location
                    else if (node instanceof Sphere && isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        System.out.println("Ray hit an atom");
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
                else if(child instanceof Circle){
                    Circle circle = (Circle) child;

                    circle.setVisible(!circle.isVisible());
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
