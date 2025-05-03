package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private SceneManager sceneManager;
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button navigateToHome;

    @FXML
    private Button login;
    
    @FXML
    private void navigateToHome()  {
        SceneManager.switchToMainScene(6);
    }
    
    @FXML
    private Button navigateToSignup;
    
    @FXML
    private void navigateToSignup()  {
        SceneManager.switchToMainScene(8);
    }
    
    @FXML
    private Button navigateToAbout;
    
    @FXML
    private void navigateToAbout()   {
        SceneManager.switchToMainScene(1);
    }
    @FXML
    private void navigateToDashboard()  {
        SceneManager.switchToMainScene(9);
    }
}