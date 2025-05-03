package controllers;

import Main.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LessonController {
    
    @FXML
    private Label lessonTitleLabel;
    
    @FXML
    private Label lessonContentLabel;
    
    @FXML
    private void goToQuiz() {
        // Quiz navigation logic
        SceneManager.switchToMainScene(7);
    }
    
    @FXML
    private void backToDashboard() {
        SceneManager.switchToMainScene(0);
    }


    public void setLesson()
    {

    }

}