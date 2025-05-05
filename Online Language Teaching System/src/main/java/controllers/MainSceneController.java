// src/main/java/controllers/MainSceneController.java
package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the main welcome screen ('mainScene.fxml').
 * Handles navigation to Login, Signup, and About pages.
 */
public class MainSceneController {

    // --- FXML Elements (Ensure fx:id matches FXML) ---

    // Side Menu Buttons
    @FXML private Button navigateToLogin;
    @FXML private Button navigateToSignup;
    @FXML private Button navigateToAbout;
    // Note: The 'Home' button in the side menu is disabled in FXML, no fx:id needed.

    // Center Content Buttons
    // Ensure FXML uses fx:id="getStartedButton" and fx:id="learnMoreButton"
    @FXML private Button getStartedButton; // Typically navigates to Signup
    @FXML private Button learnMoreButton;  // Typically navigates to About

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("MainSceneController initialized.");
        // Add any initialization logic here if needed
    }

    // --- Navigation Handlers ---

    /** Handles side menu Login button. */
    @FXML
    private void navigateToLogin() {
        System.out.println("Navigating to Login scene...");
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    /** Handles side menu Signup button. */
    @FXML
    private void navigateToSignup() {
        System.out.println("Navigating to Signup scene...");
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }

    /** Handles the "Get Started" button in the center pane (navigates to Signup). */
    @FXML
    private void handleGetStarted() {
        System.out.println("Get Started button clicked, navigating to Signup...");
        navigateToSignup(); // Reuse signup navigation logic
    }

    /** Handles side menu About button. */
    @FXML
    private void navigateToAbout() {
        System.out.println("Navigating to About scene...");
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    /** Handles the "Learn More" button in the center pane (navigates to About). */
    @FXML
    private void handleLearnMore() {
        System.out.println("Learn More button clicked, navigating to About...");
        navigateToAbout(); // Reuse about navigation logic
    }
}