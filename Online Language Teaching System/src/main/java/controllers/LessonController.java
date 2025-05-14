package controllers;

import Main.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LessonController {
    private Student student;
    private Lesson currentLesson;

    @FXML
    private Label lessonTitleLabel;
    
    @FXML
    private Label lessonContentLabel;
    
    @FXML
    private void goToQuiz() {
        // Quiz navigation logic

        Object controller = SceneManager.switchToMainScene(7);
        if (controller instanceof QuizController) {
            ((QuizController) controller).setQuiz(new Quiz("src/main/resources/quiz1.txt"));
        }
    }
    
    @FXML
    private void backToDashboard() {
        Object controller = SceneManager.switchToMainScene(0);
        if (controller instanceof StudentSceneController) {
            ((StudentSceneController) controller).setStudentScene((Student) LoginManager.getSelectedUser());
        }
    }




    public void setLessonScene(Lesson lesson) {
        this.currentLesson = lesson;
        if (lesson != null) {
            lessonTitleLabel.setText(lesson.getTitle());
            lessonContentLabel.setText(lesson.getContent());
        } else {
            lessonTitleLabel.setText("No Lesson");
            lessonContentLabel.setText("No content available");
        }
    }

}