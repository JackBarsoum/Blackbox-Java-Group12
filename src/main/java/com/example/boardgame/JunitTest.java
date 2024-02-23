package com.example.boardgame;
import static org.junit.Assert.*;
import javafx.event.ActionEvent;
import javafx.scene.shape.Sphere;
import javafx.scene.layout.Pane;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JunitTest {

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

    @Test @Ignore
    public void testSwitchToBoard() throws IOException {
        //check if file exists
        String boardPath = GameController.class.getResource("Board.fxml").getFile();
        File f = new File(boardPath);
        assertTrue(f.exists());

        //instance of GameController class
        GameController gc= new GameController();
        ActionEvent ae = new ActionEvent();

        gc.switchtoBoard(ae); //method
        assertNotNull(gc.getStage().getScene()); //scene set
        assertTrue(gc.getStage().isShowing()); //stage is showing
        assertEquals(boardPath, gc.getBoardURL()); //correct fxml file loaded
    }


}
