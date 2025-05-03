package controllers;

import Main.ApplicationController;
import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutController {
    @FXML
    private Button navigateToHome;
    
    @FXML
    private void navigateToHome()  {
        SceneManager.switchToMainScene(6);
    }
    @FXML
    private Button navigateToLogin;
    
    @FXML
    private void navigateToLogin()  {
        SceneManager.switchToMainScene(5);
    }

    @FXML
    private Button navigateToSignup;
    @FXML
    private void navigateToSignup() {
        SceneManager.switchToMainScene(8);
    }
 
}