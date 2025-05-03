package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;


public class StudentSceneController {
    
    @FXML
    private Label studentNameLabel;
    
    @FXML
    private Label languageLabel;
    
    @FXML
    private Label progressValue;
    
    @FXML
    private Label pointsValue;
    
    @FXML
    private Label rankValue;
    
    @FXML
    private Label currentLessonTitle;
    
    @FXML
    private Label nextLessonTitle;
    
    @FXML
    private ListView<String> activityListView;
    
    @FXML
    private void startLesson() {
        SceneManager.switchToMainScene(4);
    }
    
    @FXML
    private void previewNextLesson() {
        // Preview lesson logic
    }
    
    @FXML
    private void logout() {
        SceneManager.switchToMainScene(6);
    }
}