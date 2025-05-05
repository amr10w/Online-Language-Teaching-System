package controllers;

import Main.ApplicationManager;
import Main.LoginManager;
import Main.SceneManager;
import Main.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;


public class StudentSceneController {
    private  Student student;
    @FXML
    private  Label studentNameLabel;
    
    @FXML
    private  Label languageLabel;
    
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



    @FXML
    public  void setStudentScene(Student student) {
        student = (Student) LoginManager.getSelectedUser();

        if (student == null) {
            // Handle null case safely
            studentNameLabel.setText("Unknown");
            languageLabel.setText("None");
            return;
        }

        studentNameLabel.setText(student.getUsername());
        languageLabel.setText(student.getLanguage());
    }
}