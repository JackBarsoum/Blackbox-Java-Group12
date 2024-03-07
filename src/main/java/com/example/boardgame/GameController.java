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
import javafx.scene.shape.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;


public class GameController {
    private boolean up = false;
    private Color direction_tester;
    private boolean right = false;
    private boolean left = false;
    private static int atomcount = 0;
    private static Pane spherepane;

    private static Button start_Button;
    private boolean testActive = false;

    public int getAtomcount(){
        return atomcount;
    }
    public void setAtomcount(int atomcount1) {
        this.atomcount = atomcount1;
    }

    static void setSpherepane(Pane spherepane1) {
        spherepane = spherepane1;
    }

    public static void setAtomcount(){
        atomcount++;
    }

    @FXML
    public TextArea textBox; //Text box where results of rays shots will be displayed

    @FXML
    private Stage stage;
    private Scene scene;
    private URL boardURL;

    private static Button b;

    @FXML
    void leave(ActionEvent event) {
        System.out.println("Quitting Game");
        System.exit(0);
    }


    @FXML
    void switchtoBoard(ActionEvent event) throws IOException {
        b = (Button) event.getSource();
        boardURL = getClass().getResource("Board.fxml");
        Parent root = FXMLLoader.load(boardURL);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
       // stage.setFullScreen(true);
        stage.show();

    }

    public static int checkTest = 0;
    public void checkTest(MouseEvent e){
        if(b.getId().equals("Test") && checkTest == 0){
            TextArea textArea = textBox;
            textArea.appendText("TESTING MODE ACTIVE\n");
            checkTest++;
        }
    }

    @FXML
    public Sphere handleButtonClick(MouseEvent event) {
        // create a new sphere
       Sphere sphere = Atoms.placeAtomsinHex(event, getAtomcount());
       return sphere;
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
        double hx;
        double hy;



        do {
            newLine.setEndX(newLine.getEndX() + x); // Increase the line length

            for (Node node : p.getChildren()) {
                if (newLine.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    if (node instanceof Rectangle && b != node && ((Rectangle) node).getStroke() == Color.BLACK) {
                        // Line intersects with another button
                        System.out.println("Ray hit nothing and exited at " + node.getId());
                        textBox.appendText("Ray hit nothing and exited at " + node.getId() + "\n");
                        flag = 1;
                        break;
                    } else if (node instanceof Sphere && CircleCalculations.isInside((Sphere) node, newLine)) {
                        System.out.println("Ray hit a atom");
                        textBox.appendText("Ray hit an atom" + "\n");
                        flag = 1;
                    } else if(node instanceof Circle){
                        //line intersects with a circle of influence
                        oldLine.setStartY(newLine.getStartY());
                        oldLine.setStartX(newLine.getStartX());
                        oldLine.setEndY(newLine.getEndY());
                        oldLine.setEndX(newLine.getEndX() + (40*x));

                        newLine.setStartX(oldLine.getEndX());
                        newLine.setStartY(oldLine.getEndY());
                        System.out.println("old line: " + oldLine.getStartX() + "," + oldLine.getStartY() + " | " + oldLine.getEndX() + "," + oldLine.getEndY());


                        if(checkTest != 0) System.out.println("HIT CIRCLE " + node.getLayoutY());
                        if(newLine.getEndY() > node.getLayoutY() + 10){ //hits near bottom
                            if(newLine.getEndX() > node.getLayoutX()){ //hits right side
                                angleRadians = Math.toRadians(135);
                                if(checkTest != 0)System.out.println("HIT BOTTOM RIGHT");
                            }
                            else{ //left side
                                angleRadians = Math.toRadians(45);
                                if(checkTest != 0)System.out.println("HIT BOTTOM LEFT");
                            }

                        }
                        else if(newLine.getEndY() + 10 < node.getLayoutY()){ //hits near top
                            if(newLine.getEndX() + 10 > node.getLayoutX()){ //hits right side
                                angleRadians = Math.toRadians(-135);
                                if(checkTest != 0)System.out.println("HIT TOP RIGHT");
                            }
                            else{ //left side
                                angleRadians = Math.toRadians(-45);
                                if(checkTest != 0)System.out.println("HIT TOP LEFT");
                            }
                        }

                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        hx = Math.cos(angleRadians);
        hy = Math.sin(angleRadians);
        for (Node node : p.getChildren()) {
            if (newLine.getBoundsInParent().intersects(node.getBoundsInParent()) || true) {
                if(!(node instanceof Rectangle)) {
                    newLine.setEndX(newLine.getEndX() + hx);
                    newLine.setEndY(newLine.getEndY() + hy);
                }
            }
        }
        if(checkTest != 0) {
            p.getChildren().add(newLine);
            p.getChildren().add(oldLine);
        }
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
                        checker = CircleCalculations.isInsideC((Circle) node, newLine, x, false);
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
                                            if(checkTest != 0)System.out.println("Downtest1");
                                        angleRadians = Math.toRadians(180);
                                        color = Color.BLACK;
                                    } else {
                                        angleRadians = Math.toRadians(180);
                                        color = Color.BLACK;
                                            if(checkTest != 0)System.out.println("Downtest2");
                                    }
                                } else {
                                    if (newLine.getEndX() > node.getLayoutX()) {
                                        if(checkTest != 0)System.out.println("Downtest6");
                                        angleRadians = Math.toRadians(0);
                                        color = Color.BLACK;
                                    } else {
                                        angleRadians = Math.toRadians(179);
                                        if(checkTest != 0)System.out.println("Downtest7");
                                        color = Color.BLACK;
                                    }
                                }
                                    //If the ray deflects at the side of the sphere of influence
                                } else {
                                    if(x == 60){
                                        angleRadians = Math.toRadians(60);
                                        if(checkTest != 0)System.out.println("Tester");
                                        color = Color.BLACK;
                                    }
                                    else {
                                        if(newLine.getEndX() > node.getLayoutX()){
                                            angleRadians = Math.toRadians(58);
                                            if(checkTest != 0)System.out.println("Downtest8");
                                            color = Color.RED;
                                        }else{
                                            angleRadians = Math.toRadians(121);
                                            if(checkTest != 0)System.out.println("Downtest9");
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
                    } else if (node instanceof Circle && CircleCalculations.isInsideC((Circle) node, newLine, x, false) != -1 && node != prevNode) {
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
                            averageY -= 45;

                            //If the ray deflects at the top of the sphere of influence
                            if (newLine.getEndY() < averageY && right || newLine.getEndY() < averageY && left) {
                                if(direction_tester == Color.GREEN) {
                                    color = Color.GREEN;
                                    if(checkTest != 0) System.out.println("Downtest12");
                                    angleRadians = Math.toRadians(123);
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    if(checkTest != 0)System.out.println("Downtest13");
                                    angleRadians = Math.toRadians(59);
                                    color = Color.YELLOW;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                if(x == 59) {
                                    if(checkTest != 0)System.out.println("HHello");
                                    angleRadians = Math.toRadians(180);
                                }
                                else angleRadians = Math.toRadians(0);
                                if(checkTest != 0)System.out.println("HHHHHELLOOOO");
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
                    else if (node instanceof Sphere && CircleCalculations.isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        System.out.println("Ray hit at an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        //If the ray was deflected then both the line before and after the ray was deflected will need to be
        if(checkTest != 0) {
            if (line_flag != 1) {
                p.getChildren().add(oldLine);
                p.getChildren().add(newLine);
            } else {
                p.getChildren().add(newLine);
            }
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
                        checker = CircleCalculations.isInsideC((Circle) node, newLine, x, true);
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
                                    if(x == 59) {
                                        if(newLine.getEndX() < node.getLayoutX()) {
                                            if(checkTest != 0) System.out.println("Hello1");
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
                                            if(checkTest != 0) System.out.println("Hello2");
                                            angleRadians = Math.toRadians(1);
                                            color = Color.BLACK;
                                        }else{
                                            angleRadians = Math.toRadians(179);
                                            if(checkTest != 0) System.out.println("Hell on Earth1");
                                            color = Color.BLACK;
                                        }
                                        }
                                    //If the ray deflects at the side of the sphere of influence
                                } else {
                                    if(x == 60) {
                                        angleRadians = Math.toRadians(60);
                                        if(checkTest != 0)System.out.println("Tester");
                                    }
                                    else {
                                        if (newLine.getEndX() < node.getLayoutX()) {
                                            angleRadians = Math.toRadians(59);
                                            if(checkTest != 0) System.out.println("Lol");
                                            color = Color.YELLOW;
                                        } else {
                                            angleRadians = Math.toRadians(121);
                                            if(checkTest != 0)  System.out.println("LOL2");
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
                    } else if (node instanceof Circle && CircleCalculations.isInsideC((Circle) node, newLine, x,true) != -1 && node != prevNode) {
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
                            averageY += 45;

                            //If the ray deflects at the bottom of the sphere of influence
                            if (newLine.getEndY() > averageY && right || newLine.getEndY() > averageY && left) {
                                //color = Color.BLACK;
                                if(direction_tester == Color.GREEN) {
                                    if(checkTest != 0) System.out.println("Test2.1");
                                    color = Color.RED;
                                    angleRadians = Math.toRadians(238);
                                }
                                else if (direction_tester == Color.YELLOW)
                                {
                                    if(checkTest != 0)System.out.println("Test2.2");
                                    angleRadians = Math.toRadians(302);
                                    color = Color.BLUE;
                                }
                                //If the ray deflects at the side of the sphere of influence
                            } else {
                                if(direction_tester == Color.GREEN) {
                                    if(checkTest != 0)System.out.println("Side1");
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
                    else if (node instanceof Sphere && CircleCalculations.isInside((Sphere) node, newLine) && checker == 2) { //If the node hits the atom
                        textBox.appendText("Ray hit an atom" + "\n");
                        System.out.println("Ray hit an atom");
                        flag = 1;
                    }
                }
            }

        } while (flag != 1);

        //If the ray was deflected then both the line before and after the ray was deflected will need to be printed
        if(checkTest != 0) {
            if (line_flag != 1) {
                p.getChildren().add(oldLine);
                p.getChildren().add(newLine);
            } else {
                p.getChildren().add(newLine);
            }
        }
    }


    @FXML
    public void toggleAtoms(ActionEvent event) {
        Atoms.invisbleAtoms(event, getAtomcount(), spherepane);
    }

    public Stage getStage() {
        return stage;
    }

    public URL getBoardURL() {
        return boardURL;
    }
}
