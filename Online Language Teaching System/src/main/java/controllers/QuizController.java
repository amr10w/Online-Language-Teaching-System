package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
// Removed Initializable import as setQuizData is used
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; // For styling correct/incorrect answers

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the 'quiz.fxml' view.
 * Displays quiz questions, collects answers, calculates score, and shows results.
 */
public class QuizController {

    // --- FXML Elements ---

    // Top Bar
    @FXML private Button backToDashboardButton;

    // Quiz View (within ScrollPane)
    @FXML private ScrollPane quizScrollPane;
    @FXML private Label quizTitleLabel;

    // Question 1 UI Elements
    @FXML private VBox question1Box; // Container VBox for Q1
    @FXML private Label q1;          // Label for Q1 text
    @FXML private ToggleGroup question1Group; // ToggleGroup for Q1 RadioButtons
    @FXML private RadioButton Q1O1; @FXML private RadioButton Q1O2;
    @FXML private RadioButton Q1O3; @FXML private RadioButton Q1O4;

    // Question 2 UI Elements
    @FXML private VBox question2Box;
    @FXML private Label q2;
    @FXML private ToggleGroup question2Group;
    @FXML private RadioButton Q2O1; @FXML private RadioButton Q2O2;
    @FXML private RadioButton Q2O3; @FXML private RadioButton Q2O4;

    // Question 3 UI Elements
    @FXML private VBox question3Box;
    @FXML private Label q3;
    @FXML private ToggleGroup question3Group;
    @FXML private RadioButton Q3O1; @FXML private RadioButton Q3O2;
    @FXML private RadioButton Q3O3; @FXML private RadioButton Q3O4;

    // Question 4 UI Elements
    @FXML private VBox question4Box;
    @FXML private Label q4;
    @FXML private ToggleGroup question4Group;
    @FXML private RadioButton Q4O1; @FXML private RadioButton Q4O2;
    @FXML private RadioButton Q4O3; @FXML private RadioButton Q4O4;

    // Action Button
    @FXML private Button finishQuizButton;

    // Results Overlay View
    @FXML private VBox resultsOverlay; // Container for results
    @FXML private Label scoreLabel;     // Displays score (e.g., "3/4 (75%)")
    @FXML private Label feedbackLabel;  // Displays feedback text
    @FXML private VBox feedbackDetailsBox; // VBox to show question feedback
    @FXML private Button nextLessonButton; // Button to go to next lesson (optional)

    // --- Controller State ---
    private Quiz currentQuiz;
    private List<Question> questions;
    private Student currentStudent; // To record score if logged in as student

    // Lists to manage UI elements programmatically (ensure order matches FXML/questions)
    private List<Label> questionLabels;
    private List<ToggleGroup> toggleGroups;
    private List<List<RadioButton>> optionRadioButtons; // Nested list for options of each question

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Sets up UI lists and initial visibility states.
     */
    @FXML
    public void initialize() {
        System.out.println("QuizController initializing...");

        // Initialize UI lists for easier access
        questionLabels = List.of(q1, q2, q3, q4);
        toggleGroups = List.of(question1Group, question2Group, question3Group, question4Group);
        optionRadioButtons = List.of(
                List.of(Q1O1, Q1O2, Q1O3, Q1O4),
                List.of(Q2O1, Q2O2, Q2O3, Q2O4),
                List.of(Q3O1, Q3O2, Q3O3, Q3O4),
                List.of(Q4O1, Q4O2, Q4O3, Q4O4)
        );

        // Ensure results overlay is hidden initially
        resultsOverlay.setVisible(false);
        resultsOverlay.setManaged(false); // Don't allocate space for it
        quizScrollPane.setVisible(true); // Quiz content should be visible
        quizScrollPane.setManaged(true);

        // Determine current user type
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Student) {
            this.currentStudent = (Student) loggedInUser;
            System.out.println("Quiz taken by Student: " + currentStudent.getUsername());
        } else {
            this.currentStudent = null; // Not a student (e.g., teacher previewing)
            System.out.println("Quiz taken by non-student: " + (loggedInUser != null ? loggedInUser.getUsername() : "Guest") + ". Score will not be recorded.");
        }
    }

    /**
     * Sets the data for the quiz to be displayed.
     * This method must be called by the controller navigating to this scene.
     *
     * @param quiz The Quiz object containing questions and metadata.
     */
    public void setQuizData(Quiz quiz) {
        Objects.requireNonNull(quiz, "Quiz object cannot be null for setQuizData");

        // Check if quiz loading failed internally
        if ("Q_DEFAULT".equals(quiz.getQuizId())) {
            AlertMessage.showError("Quiz Error", "The selected quiz file could not be loaded correctly.");
            quizTitleLabel.setText("Quiz Load Failed");
            quizScrollPane.setVisible(false); // Hide quiz area
            resultsOverlay.setVisible(false); // Ensure results are hidden too
            return;
        }


        this.currentQuiz = quiz;
        this.questions = quiz.getQuestions(); // Get the list of Question objects

        System.out.println("Setting quiz data for: " + currentQuiz.getQuizId() + " - " + currentQuiz.getTitle());

        // --- Populate UI ---
        quizTitleLabel.setText(currentQuiz.getTitle());

        // Validate question count against UI elements (assuming UI has fixed 4 questions)
        final int expectedQuestions = 4;
        if (questions == null || questions.size() != expectedQuestions) {
            String errorMsg = String.format("Quiz data error: Expected %d questions, but found %d for quiz ID %s.",
                    expectedQuestions, (questions == null ? 0 : questions.size()), currentQuiz.getQuizId());
            System.err.println(errorMsg);
            AlertMessage.showError("Quiz Display Error", errorMsg + "\nCannot display quiz.");
            quizScrollPane.setVisible(false); // Hide the quiz area
            return;
        }

        // Populate each question block
        for (int i = 0; i < expectedQuestions; i++) {
            Question currentQ = questions.get(i);
            Label qLabel = questionLabels.get(i);
            List<RadioButton> currentOptions = optionRadioButtons.get(i);
            ToggleGroup currentGroup = toggleGroups.get(i);

            // Set question text (add number)
            qLabel.setText((i + 1) + ". " + currentQ.getQuestionText());
            qLabel.setWrapText(true); // Ensure long questions wrap

            // Get options from the Question object
            String[] options = currentQ.getOptions();
            // Validate option count
            if (options.length != currentOptions.size()) {
                String optionError = String.format("Mismatch in options for Q%d: Expected %d, found %d.", i + 1, currentOptions.size(), options.length);
                System.err.println(optionError);
                AlertMessage.showError("Quiz Display Error", optionError);
                quizScrollPane.setVisible(false);
                return;
            }

            // Set text for each RadioButton and clear selection
            for (int j = 0; j < options.length; j++) {
                currentOptions.get(j).setText(options[j]);
                currentOptions.get(j).setSelected(false); // Ensure not selected initially
                currentOptions.get(j).setStyle(""); // Clear any previous styling
                currentOptions.get(j).setDisable(false); // Ensure enabled
            }
            // Explicitly clear the toggle group's selection
            if (currentGroup.getSelectedToggle() != null) {
                currentGroup.getSelectedToggle().setSelected(false);
            }
        }

        // Reset visibility states after successful data setting
        resultsOverlay.setVisible(false);
        resultsOverlay.setManaged(false);
        quizScrollPane.setVisible(true);
        quizScrollPane.setManaged(true);
        finishQuizButton.setDisable(false); // Ensure finish button is enabled
        feedbackDetailsBox.getChildren().clear(); // Clear previous feedback details


        System.out.println("Quiz UI populated successfully.");
    }

    /**
     * Handles the "Finish Quiz" button action.
     * Calculates the score, displays results, and records the score if applicable.
     */
    @FXML
    private void finishQuiz() {
        if (currentQuiz == null || questions == null || questions.isEmpty()) {
            AlertMessage.showError("Error", "Cannot finish quiz. Quiz data is not loaded correctly.");
            return;
        }

        int score = 0;
        int totalQuestions = questions.size(); // Should be 4 based on current setup
        boolean allAnswered = true;

        // --- Grade the answers ---
        List<Boolean> correctAnswers = new ArrayList<>(); // Track correctness for feedback
        for (int i = 0; i < totalQuestions; i++) {
            ToggleGroup currentGroup = toggleGroups.get(i);
            Toggle selectedToggle = currentGroup.getSelectedToggle();

            // Check if question was answered
            if (selectedToggle == null) {
                AlertMessage.showWarning("Incomplete Quiz", "Please answer question " + (i + 1) + ".");
                // Highlight the unanswered question visually?
                questionLabels.get(i).setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                // Scroll to the question? (More complex)
                allAnswered = false;
                break; // Stop grading immediately if one is unanswered
            } else {
                // Reset style if previously marked as unanswered
                questionLabels.get(i).setStyle("");
            }

            // Check if the selected answer is correct
            RadioButton selectedRadioButton = (RadioButton) selectedToggle;
            String selectedAnswerText = selectedRadioButton.getText();
            String correctAnswerText = questions.get(i).getCorrectAnswer();

            boolean isCorrect = selectedAnswerText.equals(correctAnswerText);
            correctAnswers.add(isCorrect); // Store result for feedback
            if (isCorrect) {
                score++;
            }
        }

        // If not all questions were answered, stop here
        if (!allAnswered) {
            return;
        }

        // --- Calculate and Display Results ---
        double percentage = (totalQuestions > 0) ? ((double) score / totalQuestions * 100.0) : 0.0;

        scoreLabel.setText(String.format("Your Score: %d / %d (%.0f%%)", score, totalQuestions, percentage));

        // Generate feedback message
        String feedback;
        if (percentage >= 90) feedback = "Excellent! You've mastered this topic!";
        else if (percentage >= 70) feedback = "Good job! You have a solid understanding.";
        else if (percentage >= 50) feedback = "Not bad, but consider reviewing the lesson material.";
        else feedback = "It seems you need more practice. Please review the lesson thoroughly.";
        feedbackLabel.setText(feedback);

        // Provide detailed feedback
        displayFeedbackDetails(correctAnswers);


        // --- Record Score for Student ---
        if (currentStudent != null) {
            currentStudent.recordQuizScore(currentQuiz.getQuizId(), (int) percentage); // Record percentage score
            // Progress is saved within recordQuizScore() method of Student
            System.out.println("Score recorded for student " + currentStudent.getUsername());
        }

        // --- Update UI State ---
        // Show results, hide quiz
        resultsOverlay.setVisible(true);
        resultsOverlay.setManaged(true);
        quizScrollPane.setVisible(false);
        quizScrollPane.setManaged(false);
        finishQuizButton.setDisable(true); // Disable finish button after completion

        // Disable radio buttons to prevent changes after finishing
        for (List<RadioButton> groupOptions : optionRadioButtons) {
            for (RadioButton rb : groupOptions) {
                rb.setDisable(true);
            }
        }
    }

    /**
     * Displays detailed feedback for each question in the results overlay.
     * Highlights correct/incorrect answers.
     * @param correctAnswers List indicating if each answer was correct.
     */
    private void displayFeedbackDetails(List<Boolean> correctAnswers) {
        feedbackDetailsBox.getChildren().clear(); // Clear previous details
        feedbackDetailsBox.setSpacing(15);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean wasCorrect = correctAnswers.get(i);
            Toggle selectedToggle = toggleGroups.get(i).getSelectedToggle();
            String selectedAnswerText = (selectedToggle != null) ? ((RadioButton)selectedToggle).getText() : "N/A";

            VBox questionFeedback = new VBox(5); // VBox for each question's feedback
            questionFeedback.setStyle("-fx-padding: 10; -fx-border-color: lightgrey; -fx-border-width: 1; -fx-border-radius: 5;");

            Label qTextLabel = new Label((i + 1) + ". " + q.getQuestionText());
            qTextLabel.setWrapText(true);
            qTextLabel.setStyle("-fx-font-weight: bold;");
            questionFeedback.getChildren().add(qTextLabel);

            Label resultLabel = new Label(wasCorrect ? "Correct!" : "Incorrect");
            resultLabel.setTextFill(wasCorrect ? Color.GREEN : Color.RED);
            questionFeedback.getChildren().add(resultLabel);

            if (!wasCorrect) {
                Label yourAnswerLabel = new Label("Your answer: " + selectedAnswerText);
                yourAnswerLabel.setTextFill(Color.RED);
                questionFeedback.getChildren().add(yourAnswerLabel);

                Label correctAnswerLabel = new Label("Correct answer: " + q.getCorrectAnswer());
                correctAnswerLabel.setTextFill(Color.GREEN);
                questionFeedback.getChildren().add(correctAnswerLabel);
            } else {
                Label correctAnswerLabel = new Label("Correct answer: " + q.getCorrectAnswer());
                correctAnswerLabel.setTextFill(Color.GREEN);
                questionFeedback.getChildren().add(correctAnswerLabel);
            }

            feedbackDetailsBox.getChildren().add(questionFeedback);
        }
    }


    /**
     * Placeholder for navigating to the next lesson in a sequence.
     * Requires curriculum logic.
     */
    @FXML
    private void goToNextLesson() {
        // This logic is complex and depends on how lessons are structured/ordered.
        // 1. Get current lesson ID (maybe from currentQuiz.getAssociatedLessonId()).
        // 2. Find the 'next' lesson ID based on prerequisites or a defined sequence.
        // 3. Load the next Lesson object using ApplicationManager.findLessonById().
        // 4. If found, switch to LessonController and call setLessonData().

        if (currentQuiz == null || currentQuiz.getAssociatedLessonId() == null) {
            AlertMessage.showWarning("Cannot Find Next Lesson", "Could not determine the current lesson from this quiz.");
            return;
        }
        String currentLessonId = currentQuiz.getAssociatedLessonId();
        AlertMessage.showInformation("Not Implemented", "Navigation to the lesson after '" + currentLessonId + "' is not yet implemented.");

        // Example (needs proper implementation):
        // String nextLessonId = CurriculumManager.findNextLesson(currentLessonId);
        // if (nextLessonId != null) {
        //     Lesson nextLesson = ApplicationManager.findLessonById(nextLessonId);
        //     if (nextLesson != null) {
        //         Object controller = SceneManager.switchToScene(SceneManager.LESSON_VIEW);
        //         if (controller instanceof LessonController) {
        //             ((LessonController) controller).setLessonData(nextLesson);
        //         } else { /* Error handling */ }
        //     } else { /* Error handling: next lesson not found */ }
        // } else { /* Error handling: no next lesson defined */ }
    }

    /** Handles the action of navigating back to the appropriate dashboard. */
    @FXML
    private void backToDashboard() {
        System.out.println("Navigating back to dashboard from Quiz...");
        // Navigate back based on user type
        if (currentStudent != null) {
            SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
        } else if (LoginManager.getSelectedUser() instanceof Teacher) { // Check current session user
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            SceneManager.switchToScene(SceneManager.LOGIN); // Fallback
        }
    }

    // Deprecated initialize method from Initializable interface - not needed if using setQuizData
    @Deprecated
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // This is the old Initializable method.
        // If FXML has fx:controller set, this might still be called BEFORE setQuizData.
        // It's generally safer to rely on setQuizData for data-dependent initialization.
        System.out.println("QuizController initialized (via Initializable). UI lists populated.");
        initialize(); // Call the main initialize method
    }
}
