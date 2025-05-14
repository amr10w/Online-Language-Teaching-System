package controllers;

import Main.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class TeacherSceneController {
    private Teacher teacher;
    @FXML
    private Label teacherNameLabel;
    
    @FXML
    private Label languageLabel;
    
    @FXML
    private Label lessonsCreatedValue;
    
    @FXML
    private Label studentsValue;
    
    @FXML
    private Label balanceValue;
    
    @FXML
    private TableView<Lesson> lessonsTableView;
    
    @FXML
    private void createLesson() {
        // Lesson creation navigation logic
    }
    
    @FXML
    private void createQuiz() {
        // Quiz creation navigation logic
    }
    
    @FXML
    private void logout() {
        SceneManager.switchToMainScene(6);
    }

    @FXML
    public  void setTeacherScene(Teacher student) {
        teacher = (Teacher) LoginManager.getSelectedUser();

        if (student == null) {
            // Handle null case safely
            teacherNameLabel.setText("Unknown");
            languageLabel.setText("None");
            return;
        }

        teacherNameLabel.setText(teacher.getUsername());
        languageLabel.setText(teacher.getLanguage());


    }
}