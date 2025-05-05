package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // Keep if using initialize, but setQuizData is better
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle; // Import Toggle
import javafx.scene.control.ToggleGroup; // Import ToggleGroup
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuizController { // No need for Initializable if data is passed via method

    // --- UI Elements ---
    @FXML private Label quizTitleLabel;
    @FXML private ScrollPane quizScrollPane; // To hide/show quiz vs results
    @FXML private VBox resultsOverlay;     // Container for results view
    @FXML private Label scoreLabel;
    @FXML private Label feedbackLabel;
    @FXML private Button finishQuizButton; // Added fx:id
    @FXML private Button backToDashboardButton; // Added fx:id
    @FXML private Button nextLessonButton; // Added fx:id


    // Question 1 UI
    @FXML private VBox question1Box; // Container for Q1 (optional, for hiding/showing)
    @FXML private Label q1;
    @FXML private ToggleGroup question1Group; // fx:id for the group
    @FXML private RadioButton Q1O1;
    @FXML private RadioButton Q1O2;
    @FXML private RadioButton Q1O3;
    @FXML private RadioButton Q1O4;

    // Question 2 UI
    @FXML private VBox question2Box;
    @FXML private Label q2;
    @FXML private ToggleGroup question2Group;
    @FXML private RadioButton Q2O1;
    @FXML private RadioButton Q2O2;
    @FXML private RadioButton Q2O3;
    @FXML private RadioButton Q2O4;

    // Question 3 UI
    @FXML private VBox question3Box;
    @FXML private Label q3;
    @FXML private ToggleGroup question3Group;
    @FXML private RadioButton Q3O1;
    @FXML private RadioButton Q3O2;
    @FXML private RadioButton Q3O3;
    @FXML private RadioButton Q3O4;

    // Question 4 UI
    @FXML private VBox question4Box;
    @FXML private Label q4;
    @FXML private ToggleGroup question4Group;
    @FXML private RadioButton Q4O1;
    @FXML private RadioButton Q4O2;
    @FXML private RadioButton Q4O3;
    @FXML private RadioButton Q4O4;

    // Store the Quiz data
    private Quiz currentQuiz;
    private List<Question> questions;
    private Student currentStudent; // To record score

    // List of ToggleGroups for easier processing
    private List<ToggleGroup> toggleGroups;
    // List of Question Labels/Options (if needed for dynamic display)
    private List<Label> questionLabels;
    private List<List<RadioButton>> optionButtons;


    // Method to receive data from the calling controller (LessonController)
    public void setQuizData(Quiz quiz) {
        if (quiz == null) {
            AlertMessage.showError("Quiz Error", "Failed to load quiz data.");
            quizTitleLabel.setText("Error Loading Quiz");
            // Optionally disable the quiz view
            quizScrollPane.setVisible(false);
            resultsOverlay.setVisible(false);
            return;
        }
        this.currentQuiz = quiz;
        this.questions = quiz.getQuestions(); // Get questions from the passed Quiz object

        // Get current student
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Student) {
            this.currentStudent = (Student) loggedInUser;
        } else {
            System.err.println("Warning: Non-student taking quiz. Score will not be recorded.");
            this.currentStudent = null;
        }


        // Initialize UI Lists
        toggleGroups = List.of(question1Group, question2Group, question3Group, question4Group);
        questionLabels = List.of(q1, q2, q3, q4);
        optionButtons = List.of(
                List.of(Q1O1, Q1O2, Q1O3, Q1O4),
                List.of(Q2O1, Q2O2, Q2O3, Q2O4),
                List.of(Q3O1, Q3O2, Q3O3, Q3O4),
                List.of(Q4O1, Q4O2, Q4O3, Q4O4)
        );


        // Populate UI elements
        quizTitleLabel.setText(currentQuiz.getTitle());

        // Assuming exactly 4 questions based on FXML
        if (questions.size() != 4) {
            AlertMessage.showError("Quiz Error", "Expected 4 questions, but found " + questions.size() + ". Cannot display quiz.");
            quizScrollPane.setVisible(false);
            return;
        }

        for (int i = 0; i < 4; i++) {
            Question currentQ = questions.get(i);
            questionLabels.get(i).setText((i + 1) + ". " + currentQ.getQuestionText()); // Add question number

            String[] options = currentQ.getOptions();
            List<RadioButton> currentOptions = optionButtons.get(i);
            // Assuming exactly 4 options per question
            if (options.length != 4 || currentOptions.size() != 4) {
                AlertMessage.showError("Quiz Error", "Question " + (i+1) + " does not have exactly 4 options. Cannot display quiz.");
                quizScrollPane.setVisible(false);
                return;
            }
            for (int j = 0; j < 4; j++) {
                currentOptions.get(j).setText(options[j]);
                // Clear any previous selection (important if reusing scene)
                currentOptions.get(j).setSelected(false);
            }
            // Ensure toggle group allows deselection if needed, or clear selection manually
            if (toggleGroups.get(i).getSelectedToggle() != null) {
                toggleGroups.get(i).getSelectedToggle().setSelected(false);
            }
        }

        // Ensure results overlay is hidden initially
        resultsOverlay.setVisible(false);
        resultsOverlay.setManaged(false); // Don't let it take space when hidden
        quizScrollPane.setVisible(true); // Show quiz content
        quizScrollPane.setManaged(true);
    }

    @FXML
    private void finishQuiz() {
        if (currentQuiz == null || questions == null || questions.isEmpty()) {
            AlertMessage.showError("Error", "Quiz data is not loaded correctly.");
            return;
        }

        int score = 0;
        int totalQuestions = questions.size(); // Use the actual number of questions loaded

        for (int i = 0; i < totalQuestions; i++) {
            // Limit to the number of UI elements we have (4)
            if (i >= 4) break;

            ToggleGroup currentGroup = toggleGroups.get(i);
            Toggle selectedToggle = currentGroup.getSelectedToggle();

            if (selectedToggle == null) {
                AlertMessage.showWarning("Incomplete Quiz", "Please answer question " + (i + 1) + ".");
                // Highlight the unanswered question? (e.g., scroll to it)
                return; // Stop processing, force user to answer all
            }

            RadioButton selectedRadioButton = (RadioButton) selectedToggle;
            String selectedAnswerText = selectedRadioButton.getText();
            String correctAnswerText = questions.get(i).getCorrectAnswer();

            if (selectedAnswerText.equals(correctAnswerText)) {
                score++;
            }
        }

        // All questions answered, calculate result
        double percentage = (double) score / totalQuestions * 100.0;

        // Display results
        scoreLabel.setText(String.format("Your Score: %d / %d (%.0f%%)", score, totalQuestions, percentage));

        String feedback;
        if (percentage >= 90) feedback = "Excellent! You've mastered this topic!";
        else if (percentage >= 70) feedback = "Good job! You have a solid understanding.";
        else if (percentage >= 50) feedback = "Not bad, but review the lesson material again.";
        else feedback = "It seems you need more practice. Please review the lesson.";
        feedbackLabel.setText(feedback);

        // Record score for the student
        if (currentStudent != null) {
            currentStudent.recordQuizScore(currentQuiz.getQuizId(), (int) percentage); // Record percentage score
            // TODO: Save student progress data persistently
            System.out.println("Score recorded for student " + currentStudent.getUsername());
        }


        // Show results overlay and hide quiz scroll pane
        resultsOverlay.setVisible(true);
        resultsOverlay.setManaged(true);
        quizScrollPane.setVisible(false);
        quizScrollPane.setManaged(false);

    }

    @FXML
    private void goToNextLesson() {
        // Logic to determine and navigate to the next lesson
        // This requires knowing the curriculum structure
        AlertMessage.showInformation("Not Implemented", "Navigation to the next lesson is not yet implemented.");
        // 1. Find the current lesson ID (maybe from quiz.associatedLessonId)
        // 2. Find the next lesson ID based on curriculum rules/prerequisites
        // 3. Load the next Lesson object
        // 4. Switch to LessonView scene and pass the next lesson object
    }

    @FXML
    private void backToDashboard() {
        // Navigate back based on user type
        if (currentStudent != null) {
            SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
        } else if (LoginManager.getSelectedUser() instanceof Teacher) {
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            SceneManager.switchToScene(SceneManager.LOGIN); // Fallback
        }
    }

    // This method is likely not needed if setQuizData is used.
    // Kept for reference if Initializable was intended.
    @Deprecated
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // System.out.println("QuizController initialized. Waiting for setQuizData.");
        // // Don't load hardcoded quiz here - wait for data to be passed.
        // // setQuiz(); // Remove this call
        // resultsOverlay.setVisible(false); // Ensure results are hidden at start
        // resultsOverlay.setManaged(false);
    }

    // Kept old setQuiz for reference, but it loads a hardcoded file. DO NOT USE.
    @Deprecated
    public void setQuiz_OLD_HARDCODED() {
        // This loads a hardcoded quiz - should use setQuizData(Quiz quiz) instead
        currentQuiz = new Quiz("src/main/resources/quizzes/quiz_Q001.txt"); // Use correct path/name
        questions = currentQuiz.getQuestions(); // Get loaded questions

        if (currentQuiz.getQuizId().equals("Q_DEFAULT") || questions.isEmpty()) {
            AlertMessage.showError("Quiz Load Failed", "Could not load the default quiz file.");
            return;
        }

        quizTitleLabel.setText(currentQuiz.getTitle());
        // ... (rest of the UI population code - duplicated from setQuizData)
        // ...
    }

}