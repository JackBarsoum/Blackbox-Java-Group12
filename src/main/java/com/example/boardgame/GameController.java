package com.example.boardgame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameController {

    @FXML
    private Button play;

    @FXML
    private Button quit;

    @FXML
    void leave(ActionEvent event) {
        System.out.println("Quitting Game");
        System.exit(0);
    }

}