package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AboutController {
    // Ensure these fx:id attributes match EXACTLY in about.fxml
    @FXML private Button navigateToHome;
    @FXML private Button navigateToLogin;
    @FXML private Button navigateToSignup;
    // Note: The 'About' button in the side menu is disabled in FXML, so no fx:id needed

    @FXML
    private void initialize() {
        // Initialization code if needed when the view loads
        System.out.println("AboutController initialized.");
    }


    @FXML
    private void navigateToHome() {
        System.out.println("Navigating to Home scene...");
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    @FXML
    private void navigateToLogin() {
        System.out.println("Navigating to Login scene...");
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    @FXML
    private void navigateToSignup() {
        System.out.println("Navigating to Signup scene...");
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }

    // Add other methods if the 'About' page has more interactive elements
}