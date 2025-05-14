package controllers;

import Main.LoginManager;
import Main.SceneManager;
import Main.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LessonController {
    private Student student;
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
        Object controller = SceneManager.switchToMainScene(0);
        if (controller instanceof StudentSceneController) {
            ((StudentSceneController) controller).setStudentScene((Student) LoginManager.getSelectedUser());
        }
    }


    public void setLesson()
    {

    }

    @FXML
    public void setLessonScene()
    {

    }

}