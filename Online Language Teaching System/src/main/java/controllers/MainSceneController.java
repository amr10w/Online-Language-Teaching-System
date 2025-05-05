package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainSceneController {

    @FXML private Button navigateToLogin;
    @FXML private Button navigateToSignup;
    @FXML private Button navigateToAbout;
    // Button IDs in FXML are navigateToSignup1, navigateToAbout1 - make them match or change FXML
    @FXML private Button getStartedButton; // Matches navigateToSignup1 in FXML if purpose is signup
    @FXML private Button learnMoreButton; // Matches navigateToAbout1 in FXML if purpose is about

    @FXML
    private void navigateToLogin() {
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    @FXML
    private void navigateToSignup() {
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }

    // If using the Get Started button for Signup
    @FXML
    private void handleGetStarted() {
        navigateToSignup();
    }

    @FXML
    private void navigateToAbout() {
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    // If using the Learn More button for About
    @FXML
    private void handleLearnMore() {
        navigateToAbout();
    }
}