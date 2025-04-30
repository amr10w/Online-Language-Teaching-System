
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateLessonController {
    
    @FXML
    private TextField lessonTitleField;
    
    @FXML
    private TextArea lessonContentArea;
    
    @FXML
    private void saveLesson() {
        // Save lesson logic
    }
    
    @FXML
    private void createQuizForLesson() {
        // Quiz creation navigation logic
    }
    
    @FXML
    private void cancel() {
        // Cancel logic
    }
    
    @FXML
    private void backToDashboard() {
        // Dashboard navigation logic
    }
}