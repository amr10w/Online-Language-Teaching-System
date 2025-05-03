package controllers;

import Main.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignupController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private RadioButton studentRadio;
    
    @FXML
    private RadioButton teacherRadio;
    
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button signUp;


    

    @FXML
    private Button navigateToHome;
    
    @FXML
    private void navigateToHome() throws Exception {
        SceneManager.switchToMainScene(6);
    }
    @FXML
    private Button navigateToLogin;
    
    @FXML
    private void navigateToLogin() throws Exception {
        SceneManager.switchToMainScene(5);
    }
    

    
    @FXML
    private Button navigateToAbout;
    
    @FXML
    private void navigateToAbout() throws Exception  {
        SceneManager.switchToMainScene(1);
    }



    @FXML
    private void navigateToDashboard()
    {

        if(usernameField.getText().isEmpty())
            AlertMessage.alertMessage("Username is empty","Please enter Username.");
        else if(emailField.getText().isEmpty())
            AlertMessage.alertMessage("Email is empty","Please enter Email.");
        else if(passwordField.getText().isEmpty())
            AlertMessage.alertMessage("Password is empty","Please Enter Correct password.");
        else if (languageComboBox.getValue() == null)
            AlertMessage.alertMessage("No Language Selected","Please select a language.");
        else if(studentRadio.isSelected()&&!teacherRadio.isSelected()) {
            User user=new Student(usernameField.getText(),emailField.getText(),passwordField.getText(),"1");
            LoginManager loginManager=new LoginManager();
            boolean check =loginManager.checkUsername(user.getUsername());
            if(!check)
            {
                AlertMessage.alertMessage("Wrong Username","This username Exist, you can't add same username");
            }
            else
            {
                loginManager.signup(user);
                SceneManager.switchToMainScene(0);
            }

        }
        else if(!studentRadio.isSelected()&&teacherRadio.isSelected()) {
            User user=new Teacher(usernameField.getText(),emailField.getText(),passwordField.getText(),"1");
            LoginManager loginManager=new LoginManager();
            boolean check =loginManager.checkUsername(user.getUsername());
            if(!check)
            {
                AlertMessage.alertMessage("Wrong Username","This username Exist, you can't add same username");
            }
            else
            {
                loginManager.signup(user);
                SceneManager.switchToMainScene(0);
            }
        }
    }
    
    @FXML
    public void initialize() {
        ObservableList<String> languages = FXCollections.observableArrayList("English", "German", "French");
        languageComboBox.setItems(languages);
    }
}