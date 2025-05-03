package controllers;

import Main.SceneManager;
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

    private void alertMessage(String header,String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void navigateToDashboard()
    {

        if(usernameField.getText().isEmpty())
            alertMessage("Username is empty","Please enter Username.");
        else if(emailField.getText().isEmpty())
            alertMessage("Email is empty","Please enter Email.");
        else if(passwordField.getText().isEmpty())
            alertMessage("Password is empty","Please Enter Correct password.");
        else if (languageComboBox.getValue() == null)
            alertMessage("No Language Selected","Please select a language.");
        else if(studentRadio.isSelected()&&!teacherRadio.isSelected())
            SceneManager.switchToMainScene(0);
        else if(!studentRadio.isSelected()&&teacherRadio.isSelected())
            SceneManager.switchToMainScene(9);

    }
    
    @FXML
    public void initialize() {
        ObservableList<String> languages = FXCollections.observableArrayList("English", "German", "French");
        languageComboBox.setItems(languages);
    }
}