package com.example.boardgame;
import static org.junit.Assert.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class JunitTest {

    @BeforeClass
    //Initializes the JavaFX runtime environment, Needed for some tests to run
    public static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @Test
    public void testToggleAtoms() {
        // Create an instance of Controller class
        GameController testingclass = new GameController();

        //Create a temp pane and testing spheres
        Pane temp = new Pane();
        Sphere sphere = new Sphere();
        Sphere sphere2 = new Sphere();
        Sphere sphere3 = new Sphere();

        //Add the spheres to the child of the pane
        temp.getChildren().addAll(sphere,sphere2,sphere3);
        //Set the visibilities
        sphere.setVisible(true);
        sphere2.setVisible(true);
        sphere3.setVisible(false);
        //Set our atomcount and spherepane
        testingclass.setSpherepane(temp);
        testingclass.setAtomcount(3);
        //Call method
        testingclass.toggleAtoms(new ActionEvent());
        //Tests for visible to not visible
        assertFalse(sphere.isVisible());
        assertFalse(sphere2.isVisible());
        //Tests for non visible to visible
        assertTrue(sphere3.isVisible());

    }

    @Test
    public void testSwitchToBoard() throws IOException {
        Runnable runnable = () -> {
        //check if file exists
        String boardPath = GameController.class.getResource("Board.fxml").getFile();
        File f = new File(boardPath);
        assertTrue(f.exists());

        Parent ex = new Parent(){};

        Scene exScene = new Scene(ex);
        GameController gc = new GameController();

        assertNotNull(gc.getStage().getScene()); //scene set
        assertTrue(gc.getStage().isShowing()); //stage is showing
        assertEquals(boardPath,  gc.getBoardURL().getFile()); //correct fxml file loaded
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    @Test
    public void testPlacingAtoms(){

        Button mockButton = new Button();
        mockButton.setLayoutX(30);
        mockButton.setLayoutY(20);
        Pane mockParent = new Pane();

        mockParent.getChildren().add(mockButton);

        //Creates a mock action where a button is pressed
        ActionEvent mockEvent = new ActionEvent(mockButton, null);

        // Create an instance of GameController
        GameController testing = new GameController();

        // Call handleButtonClick
        Sphere sphere1 = testing.handleButtonClick(mockEvent);


        assertNotNull(sphere1);//Checks tha a sphere is placed
        //These tests ensure the X and Y positions are in the correct positions
        assertEquals(mockButton.getLayoutX() + 37, sphere1.getLayoutX(), 0.001);
        assertEquals(mockButton.getLayoutY() + 24, sphere1.getLayoutY(), 0.001);

        // Verify that the sphere is added to the parent
        assertTrue(mockParent.getChildren().contains(sphere1));
    }


    @Test @Ignore
    public void testRayDeflection(){
        GameController gc = new GameController();

        Pane temp = new Pane();
        Rectangle r50 = new Rectangle(); //mock arrow for pos8

        //r50 properties
        r50.setLayoutX(465); r50.setLayoutY(45); r50.setId("Node_50");


        Rectangle r1 = new Rectangle(); //mock arrow for pos1

        //r1 properties
        r1.setRotate(60); r1.setLayoutX(250); r1.setLayoutY(17); r1.setId("Node_1");
        //javafx.scene.input.MouseEvent e = (javafx.scene.input.MouseEvent) r1.getOnMouseClicked();
        //MouseEvent e = EventType<MouseEvent> MOUSE_CLICKED;


        //mock spheres
        Sphere s1 = new Sphere();
        Sphere s2 = new Sphere();
        Sphere s3 = new Sphere(); //need minimum 3 spheres, won't be used

        //grid 8,3
        s1.setLayoutX(332);
        s1.setLayoutY(325);

        //grid 8,1
        s2.setLayoutX(416);
        s2.setLayoutY(325);

        temp.getChildren().addAll(s1,s2,s3);

        /*
        assertEquals("Node_50", gc.extendLineDiagonalDown(new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                0, 0,
                0, 0,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, true, true, true,
                true,
                true,
                null
        )));

         */
    }

}
