package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AboutController {
    @FXML private Button navigateToHome;
    @FXML private Button navigateToLogin;
    @FXML private Button navigateToSignup;

    @FXML
    private void navigateToHome() {
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    @FXML
    private void navigateToLogin() {
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    @FXML
    private void navigateToSignup() {
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }
}