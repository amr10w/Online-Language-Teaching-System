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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button navigateToAbout;
    
    @FXML
    private void navigateToAbout() throws Exception  {
        Stage stage = (Stage) navigateToAbout.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/about.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @FXML
    public void initialize() {
        ObservableList<String> languages = FXCollections.observableArrayList("English", "German", "French");
        languageComboBox.setItems(languages);
    }
}