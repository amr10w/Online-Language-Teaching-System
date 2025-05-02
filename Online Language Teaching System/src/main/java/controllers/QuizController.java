package controllers;

import Main.Question;
import Main.Quiz;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class QuizController implements Initializable {
    private Quiz quiz;
    @FXML
    private Label q1;
    @FXML
    private RadioButton Q1O1;
    @FXML
    private RadioButton Q1O2;
    @FXML
    private RadioButton Q1O3;
    @FXML
    private RadioButton Q1O4;
    @FXML
    private Label quizTitleLabel;
    
    @FXML
    private ScrollPane quizScrollPane;
    
    @FXML
    private VBox resultsOverlay;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label feedbackLabel;

    @FXML
    private void finishQuiz() {
        // Quiz completion logic
    }
    
    @FXML
    private void goToNextLesson() {
        // Next lesson navigation logic
    }
    
    @FXML
    private void backToDashboard() {
        // Dashboard navigation logic
    }


    public void setQuiz()
    {
        quiz=new Quiz("src/main/resources/quiz1.txt");
        q1.setText(quiz.getQuestions().get(0).getQuestion());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setQuiz();
    }



}