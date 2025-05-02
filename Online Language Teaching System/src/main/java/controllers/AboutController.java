package controllers;

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
    private void navigateToHome() throws Exception {
        Stage stage = (Stage) navigateToHome.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/mainScene.fxml"));
        stage.setScene(new Scene(root));
        stage.show();         // Navigation logic
    }
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
 
}