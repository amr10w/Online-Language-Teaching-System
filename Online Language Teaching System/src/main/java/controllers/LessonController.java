package controllers;

import Main.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Import ScrollPane
import javafx.scene.text.Text; // Better for wrapping long text
import javafx.scene.layout.VBox; // To put Text in ScrollPane


public class LessonController {

    @FXML private Label lessonTitleLabel;
    @FXML private ScrollPane contentScrollPane; // Use ScrollPane for content
    @FXML private VBox contentVBox; // VBox inside ScrollPane to hold content Label/Text
    @FXML private Button goToQuizButton;
    @FXML private Button backToDashboardButton;
    @FXML private Button markCompleteButton; // Added Button

    private Lesson currentLesson;
    private Student currentStudent;
    private String associatedQuizId = null; // Store the ID of the quiz for this lesson

    @FXML
    public void initialize() {
        // Get current user
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Student) {
            this.currentStudent = (Student) loggedInUser;
        } else {
            // Handle case where non-student views lesson (maybe disable quiz/complete button)
            System.out.println("Viewing lesson as non-student.");
            goToQuizButton.setDisable(true);
            markCompleteButton.setVisible(false); // Hide complete button
            markCompleteButton.setManaged(false);
        }

        // Content needs to be set via setLessonData
        lessonTitleLabel.setText("Loading Lesson...");
        contentVBox.getChildren().clear(); // Clear placeholder content
        contentVBox.getChildren().add(new Label("Please select a lesson from the dashboard."));
    }


    // Method to be called by the previous controller (e.g., StudentSceneController)
    public void setLessonData(Lesson lesson) {
        if (lesson == null) {
            AlertMessage.showError("Error", "Could not load lesson data.");
            lessonTitleLabel.setText("Error");
            contentVBox.getChildren().clear();
            contentVBox.getChildren().add(new Label("Failed to load lesson content."));
            return;
        }
        this.currentLesson = lesson;
        lessonTitleLabel.setText(lesson.getTitle());

        // Display content using Text for better wrapping inside ScrollPane
        Text contentText = new Text(lesson.getContent());
        contentText.wrappingWidthProperty().bind(contentScrollPane.widthProperty().subtract(20)); // Bind wrapping width
        contentVBox.getChildren().clear();
        contentVBox.getChildren().add(contentText);


        // TODO: Find the associated quiz ID
        // This logic depends on how quizzes are linked to lessons (e.g., naming convention, stored ID in lesson file)
        // Placeholder: Assume quiz ID is derivable or stored somewhere
        // associatedQuizId = findQuizIdForLesson(lesson.getLessonId());
        associatedQuizId = "Q001"; // Hardcoded example based on quiz1.txt sample ID

        if (associatedQuizId == null) {
            goToQuizButton.setText("No Quiz Available");
            goToQuizButton.setDisable(true);
        } else {
            goToQuizButton.setText("Go to Quiz");
            goToQuizButton.setDisable(false);
        }

        // Update complete button based on student progress
        updateCompleteButtonState();

    }

    // Placeholder for finding quiz ID
    private String findQuizIdForLesson(String lessonId) {
        // Implement logic: search quizzes directory, check naming convention, or look up in a map
        return null; // Return null if no quiz found
    }


    @FXML
    private void goToQuiz() {
        if (currentLesson == null || associatedQuizId == null) {
            AlertMessage.showWarning("Quiz Not Available", "There is no quiz associated with this lesson.");
            return;
        }
        // Mark lesson as complete when going to quiz? Or only after quiz? Decide policy.
        if (currentStudent != null && !currentStudent.isLessonCompleted(currentLesson.getLessonId())) {
            markLessonComplete(); // Mark complete before starting quiz
        }


        // Load the specific quiz file based on associatedQuizId
        String quizFilePath = ApplicationManager.getQuizzesDirectory() + "/" + "quiz_" + associatedQuizId + ".txt";
        Quiz quizToLoad = new Quiz(quizFilePath); // Load the quiz

        if (quizToLoad.getQuizId().equals("Q_DEFAULT")) { // Check if load failed
            AlertMessage.showError("Quiz Load Error", "Could not load the quiz file: " + quizFilePath);
            return;
        }

        // Switch to the Quiz scene and pass the loaded Quiz object
        Object controller = SceneManager.switchToScene(SceneManager.QUIZ_VIEW);
        if (controller instanceof QuizController) {
            ((QuizController) controller).setQuizData(quizToLoad); // Pass the specific quiz
        } else {
            AlertMessage.showError("Navigation Error", "Failed to load the quiz screen controller.");
        }
    }

    @FXML
    private void markLessonComplete() {
        if (currentStudent != null && currentLesson != null) {
            if (!currentStudent.isLessonCompleted(currentLesson.getLessonId())) {
                currentStudent.markLessonCompleted(currentLesson.getLessonId());
                AlertMessage.showInformation("Progress Updated", "Lesson '" + currentLesson.getTitle() + "' marked as complete.");
                updateCompleteButtonState(); // Update button appearance
                // TODO: Save student progress
            } else {
                AlertMessage.showInformation("Already Completed", "You have already completed this lesson.");
            }
        } else {
            AlertMessage.showWarning("Action Failed", "Cannot mark lesson complete (User or Lesson missing).");
        }
    }

    private void updateCompleteButtonState() {
        if (currentStudent != null && currentLesson != null && markCompleteButton != null) {
            if (currentStudent.isLessonCompleted(currentLesson.getLessonId())) {
                markCompleteButton.setText("Completed");
                markCompleteButton.setDisable(true); // Disable if already complete
            } else {
                markCompleteButton.setText("Mark as Complete");
                markCompleteButton.setDisable(false);
            }
        }
    }


    @FXML
    private void backToDashboard() {
        // Navigate back to the appropriate dashboard
        if (currentStudent != null) {
            SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
        } else if (LoginManager.getSelectedUser() instanceof Teacher) {
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        }
        else {
            SceneManager.switchToScene(SceneManager.LOGIN); // Fallback
        }
    }
}