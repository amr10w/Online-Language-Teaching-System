package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TeacherSceneController {
    
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
        // Logout logic
    }
}