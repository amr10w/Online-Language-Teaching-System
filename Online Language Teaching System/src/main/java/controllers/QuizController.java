package controllers;

import Main.Question;
import Main.Quiz;
import Main.SceneManager;
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
    private Label q2;
    @FXML
    private RadioButton Q2O1;
    @FXML
    private RadioButton Q2O2;
    @FXML
    private RadioButton Q2O3;
    @FXML
    private RadioButton Q2O4;
    @FXML
    private Label q3;
    @FXML
    private RadioButton Q3O1;
    @FXML
    private RadioButton Q3O2;
    @FXML
    private RadioButton Q3O3;
    @FXML
    private RadioButton Q3O4;
    @FXML
    private Label q4;
    @FXML
    private RadioButton Q4O1;
    @FXML
    private RadioButton Q4O2;
    @FXML
    private RadioButton Q4O3;
    @FXML
    private RadioButton Q4O4;
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
        SceneManager.switchToMainScene(0);
    }


    public void setQuiz()
    {
        quiz=Quiz.setQuiz("src/main/resources/quiz1.txt");
        quizTitleLabel.setText(quiz.getTitle());
        q1.setText(quiz.getQuestions().get(0).getQuestion());
        Q1O1.setText(quiz.getQuestions().get(0).getOptions()[0]);
        Q1O2.setText(quiz.getQuestions().get(0).getOptions()[1]);
        Q1O3.setText(quiz.getQuestions().get(0).getOptions()[2]);
        Q1O4.setText(quiz.getQuestions().get(0).getOptions()[3]);
        q2.setText(quiz.getQuestions().get(1).getQuestion());
        Q2O1.setText(quiz.getQuestions().get(1).getOptions()[0]);
        Q2O2.setText(quiz.getQuestions().get(1).getOptions()[1]);
        Q2O3.setText(quiz.getQuestions().get(1).getOptions()[2]);
        Q2O4.setText(quiz.getQuestions().get(1).getOptions()[3]);
        q3.setText(quiz.getQuestions().get(2).getQuestion());
        Q3O1.setText(quiz.getQuestions().get(2).getOptions()[0]);
        Q3O2.setText(quiz.getQuestions().get(2).getOptions()[1]);
        Q3O3.setText(quiz.getQuestions().get(2).getOptions()[2]);
        Q3O4.setText(quiz.getQuestions().get(2).getOptions()[3]);
        q4.setText(quiz.getQuestions().get(3).getQuestion());
        Q4O1.setText(quiz.getQuestions().get(3).getOptions()[0]);
        Q4O2.setText(quiz.getQuestions().get(3).getOptions()[1]);
        Q4O3.setText(quiz.getQuestions().get(3).getOptions()[2]);
        Q4O4.setText(quiz.getQuestions().get(3).getOptions()[3]);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setQuiz();
    }



}