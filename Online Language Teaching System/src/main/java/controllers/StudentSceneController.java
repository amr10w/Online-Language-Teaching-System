package controllers;

import Main.*;
import static java.lang.Math.round;
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


public class StudentSceneController extends QuizToDashController {
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
    private ProgressBar progressBar;

    @FXML
    private void startLesson() {
        student=(Student) LoginManager.getSelectedUser();
        /*if (student.getLessons().isEmpty()) {
            AlertMessage.alertMessage("No Lessons Available", "There are no lessons assigned to you.\nPlease wait for lessons to be added.");
            return;
        }
        int currentLessonIndex = -1;
        for (int i = 0; i < student.getLessons().size(); i++) {
            if (!student.isLessonCompleted(i)) {
                currentLessonIndex = i;
                break;
            }
        }
        if (currentLessonIndex == -1) {
            AlertMessage.alertMessage("All Lessons Completed", "You have completed all available lessons!");
            return;
        }*/

        Object controller = SceneManager.switchToMainScene(4);
        if (controller instanceof LessonController) {
            //parameter should be student.getLessons().get(currentLessonIndex)
            ((LessonController) controller).setLessonScene(new Lesson("Grammer", "0x123","Learn basic grammar rules." ,"Beginner"));
        }
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
        rankValue.setText(student.getProficiencyLevel());
        pointsValue.setText(String.valueOf((int)( currentProgress*100)));
        progressValue.setText(String.valueOf((int) (currentProgress*10))+"%");
        progressBar.setProgress(currentProgress);
        if (!student.getLessons().isEmpty()) {
            for (int i = 0; i < student.getLessons().size(); i++) {
                if (student.isLessonCompleted(i)) {
                    currentLessonTitle.setText(student.getLessons().get(i).getTitle());
                    nextLessonTitle.setText(i + 1 < student.getLessons().size() ?
                            student.getLessons().get(i + 1).getTitle() : "None");
                    break;
                }
            }
        }


    }


}