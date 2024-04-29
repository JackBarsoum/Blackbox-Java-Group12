package com.example.boardgame;

import com.sun.javafx.geom.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import java.io.IOException;

import static com.sun.javafx.scene.NodeHelper.intersects;
import static javafx.scene.shape.Shape.intersect;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasChildren;
import static org.testfx.util.NodeQueryUtils.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class MenuTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        Menu.setPrimaryStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("menuScreen.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
    }


    @Test
    public void testPlayButton() {
        //If the play button works a new scene will be displayed and the start_end_button will be visible
        verifyThat("#play", NodeMatchers.isVisible());
        clickOn(".button:contains(Play)");
        verifyThat("#start_end_button", NodeMatchers.isVisible());
    }

    @Test
    public void testQuitButton() {
        // Check if the quit button is visible before clicking
        verifyThat("#quit", isVisible());

        // Click on the quit button

        clickOn("#quit");

        // Wait for the stage to close
       waitForFxEvents();

        // Check if the stage is closed
        assertFalse(Menu.getPrimaryStage().isShowing());
    }

    @Test
    public void testSpherePlacement(){
        verifyThat("#play", isVisible());
        clickOn("#play");
        waitForFxEvents();

        Point2D point = new Point2D(600,700);
        Point2D point2 = new Point2D(500,350);
        Point2D point3 = new Point2D(400,300);
        Point2D point4 = new Point2D(350,300);

        clickOn(point.x, point.y, MouseButton.PRIMARY);
        waitForFxEvents();
        assertEquals(1, GameController.getAtomcount());


        clickOn(point2.x, point2.y, MouseButton.PRIMARY);
        waitForFxEvents();
        assertEquals(2, GameController.getAtomcount());

        clickOn(point3.x, point3.y, MouseButton.PRIMARY);
        waitForFxEvents();
        assertEquals(3, GameController.getAtomcount());

        clickOn("#start_end_button", MouseButton.PRIMARY);
        waitForFxEvents();


        clickOn(point4.x, point4.y, MouseButton.PRIMARY);
        waitForFxEvents();
        Assertions.assertNotEquals(3, GameController.getAtomcount());
    }

    @Test
    public void test60ray(){
        verifyThat("#play", isVisible());
        clickOn("#play");
        waitForFxEvents();

        placeAtoms();

        clickOn("#Node_40");
        waitForFxEvents();


        TextArea textArea = lookup("#textBox").query();
        assertEquals("Ray shot from Node_40\nRay deflected and exited at Node_28", textArea.getText().trim());
    }

    public void placeAtoms(){
        Point2D point = new Point2D(600,700);
        Point2D point2 = new Point2D(500,350);
        Point2D point3 = new Point2D(400,300);

        clickOn(point.x, point.y, MouseButton.PRIMARY);
        waitForFxEvents();
        clickOn(point2.x, point2.y, MouseButton.PRIMARY);
        waitForFxEvents();
        clickOn(point3.x, point3.y, MouseButton.PRIMARY);
        waitForFxEvents();
        clickOn("#start_end_button", MouseButton.PRIMARY);
        waitForFxEvents();
    }
}
