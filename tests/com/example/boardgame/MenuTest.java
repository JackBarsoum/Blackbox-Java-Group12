package com.example.boardgame;

import javafx.scene.Node;
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

import java.awt.*;
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

        clickMultiple("#play", "#hex_2_3", "#hex_6_4", "#hex_10_5", "#hex_14_4", "#hex_18_3", "#start_end_button");
        TextArea textArea = lookup("#textBox").query();

        //down diagonal left
        clickOn("#Node_48");
        waitForFxEvents();
        assertEquals("Ray shot from Node_48\nRay deflected and exited at Node_34", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_46");
        waitForFxEvents();
        assertEquals("Ray shot from Node_46\nRay deflected and exited at Node_32", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_44");
        waitForFxEvents();
        assertEquals("Ray shot from Node_44\nRay deflected and exited at Node_30", textArea.getText().trim());
        textArea.clear();

        //up diagonal left
        clickOn("#Node_26");
        waitForFxEvents();
        assertEquals("Ray shot from Node_26\nRay deflected and exited at Node_40", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_28");
        waitForFxEvents();
        assertEquals("Ray shot from Node_28\nRay deflected and exited at Node_42", textArea.getText().trim());
        textArea.clear();

        //down diagonal right
        clickOn("#Node_53");
        waitForFxEvents();
        assertEquals("Ray shot from Node_53\nRay deflected and exited at Node_13", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_1");
        waitForFxEvents();
        assertEquals("Ray shot from Node_1\nRay deflected and exited at Node_15", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_3");
        waitForFxEvents();
        assertEquals("Ray shot from Node_3\nRay deflected and exited at Node_17", textArea.getText().trim());
        textArea.clear();
        
        //up diagonal right
        clickOn("#Node_21");
        waitForFxEvents();
        assertEquals("Ray shot from Node_21\nRay deflected and exited at Node_7", textArea.getText().trim());
        textArea.clear();
        clickOn("#Node_19");
        waitForFxEvents();
        assertEquals("Ray shot from Node_19\nRay deflected and exited at Node_5", textArea.getText().trim());
    }

    @Test
    public void test180Reflection(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_6_2", "#hex_4_3", "#hex_8_8", "#hex_12_2", "#hex_12_3", "#hex_13_3", "#start_end_button", "#Node_1");

        TextArea textArea = lookup("#textBox").query();
        assertEquals("Ray shot from Node_1\nRay reflected and exited at Node_1\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_28", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_28\nRay reflected and exited at Node_28\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_17", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_17\nRay reflected and exited at Node_17\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_41", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_41\nRay reflected and exited at Node_41\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_38", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_38\nRay reflected and exited at Node_38\n", textArea.getText());
    }

    @Test
    public void test120Deflection(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_6_2", "#hex_12_5", "#hex_8_2", "#hex_12_6", "#hex_14_5", "#start_end_button");

        TextArea textArea = lookup("#textBox").query();
        clickOn("#Node_5", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_5\nRay deflected and exited at Node_6\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_53", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_53\nRay deflected and exited at Node_44\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_30", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_30\nRay deflected and exited at Node_33\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_14", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_14\nRay deflected and exited at Node_21\n", textArea.getText());
        textArea.clear();
    }

    @Test
    public void textComplexCases(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_8_1", "#hex_6_3", "#hex_14_5", "#hex_8_7", "#hex_10_4", "#hex_10_5","#start_end_button");

        TextArea textArea = lookup("#textBox").query();
        clickOn("#Node_3", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_3\nRay deflected and exited at Node_15\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_51", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_51\nRay deflected and exited at Node_35\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_19", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_19\nRay deflected and exited at Node_21\n", textArea.getText());
        textArea.clear();
        clickOn("#Node_30", MouseButton.PRIMARY);
        assertEquals("Ray shot from Node_30\nRay deflected and exited at Node_37\n", textArea.getText());
        textArea.clear();
    }

    //Tests score throughout and at the end of the game
    @Test
    public void testScore(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_4_4", "#hex_10_3", "#hex_16_2", "#hex_16_3", "#start_end_button");
        TextArea scoreTextArea = lookup("#scoreTextBox").query();

        assertEquals("Score: 0", scoreTextArea.getText());
        assertEquals(0, GameController.getScore());

        //shoot and guess
        clickMultiple("#Node_13", "#Node_6", "#Node_37", "#Node_46",  "#hex_16_3", "#hex_4_4", "#hex_8_7", "#hex_14_5", "#start_end_button");
        assertEquals("Score: 14", scoreTextArea.getText());
        assertEquals(14, GameController.getScore());

        //---------------------------------------- NEXT PLAYER ---------------------------------------
        clickMultiple("#start_end_button", "#nextPlayer", "#hex_12_4", "#hex_12_5", "#hex_12_6", "#start_end_button");
        assertEquals("Score: 0", scoreTextArea.getText());
        assertEquals(0, GameController.getScore());

        clickMultiple("#Node_46", "#Node_35", "#Node_37", "#hex_6_2", "#hex_6_3", "#hex_6_4", "#start_end_button");
        assertEquals("Score: 18", scoreTextArea.getText());
        assertEquals(18, GameController.getScore());

        clickMultiple("#start_end_button", "#nextPlayer");
        assertEquals("FINAL SCORE: 14 - 18\n", scoreTextArea.getText());
    }

    //Tests intricacies such as moving onto player 2, winner, TextArea texts, atomcount and node disabling
    @Test
    public void testMisc(){
        verifyThat("#play", isVisible());

        clickMultiple("#play", "#hex_4_4", "#hex_10_3", "#hex_16_2", "#hex_16_3");
        assertEquals(4, GameController.getAtomcount());
        clickOn("#start_end_button");
        TextArea textArea = lookup("#textBox").query();
        Node hex46 = lookup("#Node_46").query(); //testing disabling and enabling for nodes

        assertFalse(hex46.isDisabled());

        //shoot and guess
        clickMultiple("#Node_13", "#Node_6", "#Node_37", "#Node_46",  "#hex_16_3", "#hex_4_4", "#hex_8_7", "#hex_14_5", "#start_end_button");
        assertTrue(hex46.isDisabled());

        //---------------------------------------- NEXT PLAYER ---------------------------------------
        clickMultiple("#start_end_button", "#nextPlayer");
        assertEquals(0, GameController.getAtomcount()); //atoms reset
        assertFalse(hex46.isDisabled());

        clickMultiple( "#hex_12_4", "#hex_12_5", "#hex_12_6");
        assertEquals(3, GameController.getAtomcount());

        clickOn("#start_end_button", MouseButton.PRIMARY);
        assertEquals("PLAYER 2\n", textArea.getText());

        clickMultiple("#Node_46", "#Node_35", "#Node_37", "#hex_6_2", "#hex_6_3", "#hex_6_4", "#start_end_button");
        assertTrue(hex46.isDisabled());

        clickMultiple("#start_end_button", "#nextPlayer");
        assertTrue(textArea.getText().contains("PLAYER 1 WINS!!!"));
        assertFalse(textArea.getText().contains("PLAYER 2 WINS!!!")); //double-checking
    }



    public void clickMultiple(String... hex) {
        for (String id : hex) {
            clickOn(id, MouseButton.PRIMARY);
            waitForFxEvents();
        }
    }
}
