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
    private void navigateToHome() throws Exception {
        Stage stage = (Stage) navigateToSignup.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/mainScene.fxml"));
        stage.setScene(new Scene(root));
        stage.show();         // Navigation logic
    }
    
    @FXML
    private Button navigateToSignup;
    
    @FXML
    private void navigateToSignup() throws Exception {
        sceneManager.switchToMainScene(7);
    }
    
    @FXML
    private Button navigateToAbout;
    
    @FXML
    private void navigateToAbout() throws Exception  {
        Stage stage = (Stage) navigateToAbout.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/about.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}