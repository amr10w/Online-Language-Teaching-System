package controllers;

import Main.*;
import java.io.IOException;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.ToggleGroup;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuizController extends QuizToDashController implements Initializable {
    private Student student;
    private Quiz quiz;
    @FXML
    private Label q1;
    @FXML
    private ToggleGroup question1Group;

    @FXML
    private ToggleGroup question2Group;

    @FXML
    private ToggleGroup question3Group;

    @FXML
    private ToggleGroup question4Group;
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
        // record his answers and calculate the score, update the progress, next lesson
                // Get selected RadioButton for each question
        RadioButton selectedQ1 = (RadioButton) question1Group.getSelectedToggle();
        RadioButton selectedQ2 = (RadioButton) question2Group.getSelectedToggle();
        RadioButton selectedQ3 = (RadioButton) question3Group.getSelectedToggle();
        RadioButton selectedQ4 = (RadioButton) question4Group.getSelectedToggle();
        
        // Get answers text or default "No answer"
        String answer1 = selectedQ1 != null ? selectedQ1.getText() : "";
        String answer2 = selectedQ2 != null ? selectedQ2.getText() : "";
        String answer3 = selectedQ3 != null ? selectedQ3.getText() : "";
        String answer4 = selectedQ4 != null ? selectedQ4.getText() : "";
        
        // Set user answers in question objects
        if (selectedQ1 != null) quiz.questions.get(0).setUserAnswer(selectedQ1.getText());
        if (selectedQ2 != null) quiz.questions.get(1).setUserAnswer(selectedQ2.getText());
        if (selectedQ3 != null) quiz.questions.get(2).setUserAnswer(selectedQ3.getText());
        if (selectedQ4 != null) quiz.questions.get(3).setUserAnswer(selectedQ4.getText());

        // Count correct answers
        int score = 0;
        int total = 4;
        for (Question q : quiz.questions) {
            if (q.isCorrect()) {
                score++;
            }
        }

        //===============now we calculate the student score===============
        //===============lets display the result =========================
        
            // Show the overlay
            resultsOverlay.setVisible(true);
            // Update score label
            scoreLabel.setText("Your Score: " + score + "/" + total);
            // Give dynamic feedback
            String feedback;
            double percent = (double) score / total;
            if (percent == 1.0) {
                feedback = "Excellent! You've mastered this lesson.";
            } else if (percent >= 0.75) {
                feedback = "Great job! Just a little more to perfect it.";
            } else if (percent >= 0.5) {
                feedback = "Good effort! Review and try again.";
            } else {
                feedback = "Keep practicing! You'll get there.";
            }
            feedbackLabel.setText(feedback);
    
    }
    
    @FXML
    private void goToNextLesson() {
        // Next lesson navigation logic
    }
    
    @FXML
    private void backToDashboard() {
        Object controller = SceneManager.switchToMainScene(0);
        if (controller instanceof StudentSceneController) {
            ((StudentSceneController) controller).setStudentScene((Student) LoginManager.getSelectedUser());
        }
    }
////
    @FXML
    private void goToDashboardAfterQuiz() {
     currentProgress = calculateNewProgress(currentProgress);
        backToDashboard();
    
    }
    

    public void setQuiz(Quiz quiz)
    {
        this.quiz=quiz;
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

    public void setStudent(Student student) {
        this.student=student;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

 public double calculateNewProgress(double currentProgress) {
        currentProgress += 0.1; // Increase by 10%
        if (currentProgress > 1.0) {
            currentProgress = 1.0;
        }
        return currentProgress;
    }   

}


