package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private void signUp() {
        // Signup logic
    }
    
    @FXML
    private void navigateToHome() {
        // Navigation logic
    }
    
    @FXML
    private void navigateToLogin() {
        // Navigation logic
    }
    
    @FXML
    private void navigateToAbout() {
        // Navigation logic
    }
    
    @FXML
    public void initialize() {
        ObservableList<String> languages = FXCollections.observableArrayList("English", "German", "French");
        languageComboBox.setItems(languages);
    }
}