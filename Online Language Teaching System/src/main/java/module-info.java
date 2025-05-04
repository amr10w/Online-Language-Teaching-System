module Main.onlinelanguageteachingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;
    requires javafx.media;


    opens Main to javafx.fxml;
    exports Main;
    
    opens controllers to javafx.fxml;
    exports controllers;
}