package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;


public class MainSceneController {
    
    @FXML
    private Button navigateToLogin;
    
    @FXML
    private void navigateToLogin() throws Exception {
        SceneManager.switchToMainScene(5);   // Navigation logic
    }
    
    @FXML
    private Button navigateToSignup;
    @FXML
    private void navigateToSignup() throws Exception {
        SceneManager.switchToMainScene(8);
    }
    
    @FXML
    private Button navigateToAbout;
    
    @FXML
    private void navigateToAbout() throws Exception  {
        SceneManager.switchToMainScene(1);
    }
}