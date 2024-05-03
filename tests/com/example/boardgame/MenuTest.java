package com.example.boardgame;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@Execution(ExecutionMode.CONCURRENT)
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


        clickMultiple("#play", "#hex_16_4");
        assertEquals(1, GameController.getAtomcount());


        clickMultiple("#hex_8_7");
        assertEquals(2, GameController.getAtomcount());

        clickMultiple("#hex_4_3");
        assertEquals(3, GameController.getAtomcount());

        clickOn("#start_end_button", MouseButton.PRIMARY);
        waitForFxEvents();


        clickMultiple("#hex_14_1");
        Assertions.assertNotEquals(3, GameController.getAtomcount());
    }

    @Test
    public void test60Ray(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#start_end_button", "#hex_16_4", "#hex_6_2", "#hex_6_5");
        clickOn("#start_end_button", MouseButton.PRIMARY);

        clickOn("#Node_40");
        waitForFxEvents();


        TextArea textArea = lookup("#textBox").query();
        assertEquals("Ray shot from Node_40\nRay deflected and exited at Node_28", textArea.getText().trim());
    }

    @Test
    public void test180Ray(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_6_2", "#hex_4_3", "#hex_8_8", "#start_end_button", "#Node_1");

        TextArea textArea = lookup("#textBox").query();
        assertEquals("Ray shot from Node_1\nRay deflected 180\nRay reflected 180 degrees and exited at Node_1",
                textArea.getText());
    }


    public void clickMultiple(String... hex) {
        for (String id : hex) {
            clickOn(id, MouseButton.PRIMARY);
            waitForFxEvents();
        }
    }
}
