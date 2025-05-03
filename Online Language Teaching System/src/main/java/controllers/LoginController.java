package controllers;

import Main.*;
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

        if(usernameField.getText().isEmpty())
            AlertMessage.alertMessage("Username is empty","Please enter Username.");
        else if(passwordField.getText().isEmpty())
            AlertMessage.alertMessage("Password is empty","Please enter password.");

        LoginManager loginManager=new LoginManager();
        int status=loginManager.login(usernameField.getText(),passwordField.getText());
        if(status==1)
            SceneManager.switchToMainScene(9);
    }
}