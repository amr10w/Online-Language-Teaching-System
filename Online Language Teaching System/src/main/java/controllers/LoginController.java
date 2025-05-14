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

        if(usernameField.getText().isEmpty()) {
            AlertMessage.alertMessage("Username is empty", "Please enter Username.");
            return;
        }
        else if(passwordField.getText().isEmpty()) {
            AlertMessage.alertMessage("Password is empty", "Please enter password.");
            return;
        }

        int status=LoginManager.login(usernameField.getText(),passwordField.getText());
        if(status==-1) AlertMessage.alertMessage("Username doesn't exist","please try again.");
        else if(status==-2) AlertMessage.alertMessage("Password is wrong","please try again.");
        else if(!usernameField.getText().isEmpty()&&!passwordField.getText().isEmpty())
        {
            if(status==1){
                Object controller = SceneManager.switchToMainScene(0);
                if (controller instanceof StudentSceneController) {
                    ((StudentSceneController) controller).setStudentScene((Student) LoginManager.getSelectedUser());
                }
            }
            else if(status==2)   SceneManager.switchToMainScene(9);
        }

    }
}