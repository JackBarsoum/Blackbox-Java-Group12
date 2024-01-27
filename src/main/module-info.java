module com.example.boardgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.boardgame to javafx.fxml;
    exports com.example.boardgame;
}