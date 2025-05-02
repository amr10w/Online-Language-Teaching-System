package controllers;

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
        Stage stage = (Stage) navigateToLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/login.fxml"));
        stage.setScene(new Scene(root));
        stage.show();       // Navigation logic
    }
    
    @FXML
    private Button navigateToSignup;
    @FXML
    private void navigateToSignup() throws Exception {
        Stage stage = (Stage) navigateToSignup.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/signup.fxml"));
        stage.setScene(new Scene(root));
        stage.show();     // Navigation logic
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