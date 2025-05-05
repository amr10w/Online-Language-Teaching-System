// src/main/java/controllers/LessonController.java
package controllers;

import Main.*; // Import necessary Main package classes
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text; // Use Text node for better text flow/wrapping

import java.io.File;

/**
 * Controller for the 'lesson.fxml' view.
 * Displays the content of a selected lesson and allows navigation to the associated quiz.
 */
public class LessonController {

    // --- FXML Elements ---
    @FXML private Label lessonTitleLabel;
    @FXML private ScrollPane contentScrollPane; // ScrollPane containing the content
    @FXML private VBox contentVBox;          // VBox inside ScrollPane to hold the Text node
    @FXML private Button goToQuizButton;
    @FXML private Button backToDashboardButton;
    @FXML private Button markCompleteButton;

    // --- Controller State ---
    private Lesson currentLesson;
    private Student currentStudent; // The student viewing the lesson (if applicable)
    private Quiz associatedQuiz;    // Store the loaded Quiz object

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Sets up the initial state and determines the current user type.
     */
    @FXML
    public void initialize() {
        System.out.println("LessonController initializing...");
        // Determine the current user
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Student) {
            this.currentStudent = (Student) loggedInUser;
            System.out.println("Lesson viewed by Student: " + currentStudent.getUsername());
            markCompleteButton.setVisible(true); // Show complete button for students
            markCompleteButton.setManaged(true);
        } else {
            // Handle non-student viewers (e.g., Teachers previewing)
            System.out.println("Lesson viewed by non-student: " + (loggedInUser != null ? loggedInUser.getUsername() : "Guest"));
            this.currentStudent = null;
            // Hide and disable student-specific actions
            markCompleteButton.setVisible(false);
            markCompleteButton.setManaged(false); // Don't reserve space
            // Quiz button might still be active for preview? Decide policy.
            // goToQuizButton.setDisable(true); // Optionally disable quiz too
        }

        // Set initial placeholder content
        lessonTitleLabel.setText("Loading Lesson...");
        contentVBox.getChildren().clear(); // Clear any default content from FXML
        Text placeholderText = new Text("Please select a lesson from the dashboard.");
        contentVBox.getChildren().add(placeholderText);

        // Configure goToQuizButton initial state
        goToQuizButton.setText("Loading Quiz...");
        goToQuizButton.setDisable(true);
    }


    /**
     * Sets the data for the lesson to be displayed.
     * This method should be called by the controller that navigates to this scene
     * (e.g., StudentSceneController or TeacherSceneController).
     *
     * @param lesson The Lesson object to display.
     */
    public void setLessonData(Lesson lesson) {
        if (lesson == null) {
            AlertMessage.showError("Error", "Could not load lesson data. Lesson object is null.");
            lessonTitleLabel.setText("Error Loading Lesson");
            contentVBox.getChildren().clear();
            Text errorText = new Text("Failed to load lesson content.");
            contentVBox.getChildren().add(errorText);
            disableButtons(); // Disable actions if lesson fails to load
            return;
        }
        // Check if lesson loading failed internally (indicated by L_DEFAULT ID)
        if ("L_DEFAULT".equals(lesson.getLessonId())) {
            AlertMessage.showError("Error", "The selected lesson file could not be loaded correctly.");
            lessonTitleLabel.setText("Lesson Load Failed");
            contentVBox.getChildren().clear();
            Text errorText = new Text("Failed to parse lesson content from file:\n" + lesson.getFilePath());
            errorText.wrappingWidthProperty().bind(contentScrollPane.widthProperty().subtract(20));
            contentVBox.getChildren().add(errorText);
            disableButtons();
            return;
        }


        this.currentLesson = lesson;
        System.out.println("Displaying lesson: " + currentLesson.getLessonId() + " - " + currentLesson.getTitle());

        // --- Display Lesson Content ---
        lessonTitleLabel.setText(currentLesson.getTitle());

        // Use a Text node for content display to handle wrapping properly within ScrollPane
        Text contentTextNode = new Text(currentLesson.getContent() != null ? currentLesson.getContent() : "");
        // Bind the wrapping width to the ScrollPane's viewport width for responsiveness
        contentTextNode.wrappingWidthProperty().bind(contentScrollPane.widthProperty().subtract(25)); // Subtract padding/scrollbar allowance
        contentVBox.getChildren().clear(); // Clear placeholder
        contentVBox.getChildren().add(contentTextNode); // Add the Text node

        // --- Find and Prepare Associated Quiz ---
        findAndPrepareQuiz();

        // --- Update Button States ---
        updateCompleteButtonState(); // Update based on student's progress
    }

    /** Disables action buttons. */
    private void disableButtons() {
        goToQuizButton.setDisable(true);
        goToQuizButton.setText("N/A");
        markCompleteButton.setDisable(true);
    }

    /** Finds the quiz associated with the current lesson. */
    private void findAndPrepareQuiz() {
        this.associatedQuiz = null; // Reset quiz state
        goToQuizButton.setDisable(true); // Disable initially
        goToQuizButton.setText("No Quiz Found");

        // --- Logic to find the associated quiz ID ---
        // Option 1: Quiz ID derived from Lesson ID (e.g., L101 -> Q101) - Fragile
        // Option 2: Lesson object stores associatedQuizId - Needs implementation in Lesson load/save
        // Option 3: Scan quiz files for a matching associatedLessonId field - Most Robust

        // Implementing Option 3 (Simplified Scan):
        String quizzesDir = ApplicationManager.getQuizzesDirectory();
        if (quizzesDir == null || currentLesson == null || currentLesson.getLessonId() == null) {
            System.err.println("Cannot search for quiz: Quizzes directory or lesson data missing.");
            return;
        }

        File dir = new File(quizzesDir);
        File[] quizFiles = dir.listFiles((d, name) -> name.toLowerCase().startsWith("quiz_q") && name.toLowerCase().endsWith(".txt"));

        if (quizFiles != null) {
            for (File quizFile : quizFiles) {
                try {
                    Quiz potentialQuiz = new Quiz(quizFile.getAbsolutePath()); // Load quiz to check its fields
                    // Check if load succeeded and if associated ID matches current lesson
                    if (!"Q_DEFAULT".equals(potentialQuiz.getQuizId()) &&
                            currentLesson.getLessonId().equals(potentialQuiz.getAssociatedLessonId()))
                    {
                        this.associatedQuiz = potentialQuiz; // Found the quiz!
                        System.out.println("Found associated quiz: " + associatedQuiz.getQuizId() + " for lesson " + currentLesson.getLessonId());
                        goToQuizButton.setText("Go to Quiz: " + associatedQuiz.getTitle());
                        goToQuizButton.setDisable(false);
                        break; // Stop searching once found
                    }
                } catch (Exception e) {
                    System.err.println("Error loading potential quiz file " + quizFile.getName() + " during search: " + e.getMessage());
                    // Continue searching other files
                }
            }
        } else {
            System.err.println("Could not list files in quizzes directory: " + quizzesDir);
        }

        if (this.associatedQuiz == null) {
            System.out.println("No associated quiz found for lesson " + currentLesson.getLessonId());
        }
    }


    /**
     * Handles the action of navigating to the associated quiz.
     */
    @FXML
    private void goToQuiz() {
        if (currentLesson == null) {
            AlertMessage.showError("Error", "No lesson is currently loaded.");
            return;
        }
        if (associatedQuiz == null) {
            AlertMessage.showWarning("Quiz Not Available", "There is no quiz associated with this lesson, or it failed to load.");
            return;
        }

        // --- Policy Decision: Mark lesson complete *before* starting quiz? ---
        // Let's mark it complete if the user is a student and hasn't completed it yet.
        if (currentStudent != null && !currentStudent.isLessonCompleted(currentLesson.getLessonId())) {
            System.out.println("Auto-marking lesson '" + currentLesson.getTitle() + "' complete before starting quiz.");
            markLessonCompleteAction(); // Call the action method to update UI and save
        }
        // --- End Policy Decision ---


        System.out.println("Navigating to quiz: " + associatedQuiz.getQuizId());
        // Switch to the Quiz scene and pass the loaded Quiz object
        Object controller = SceneManager.switchToScene(SceneManager.QUIZ_VIEW);
        if (controller instanceof QuizController) {
            ((QuizController) controller).setQuizData(associatedQuiz); // Pass the specific quiz object
        } else {
            AlertMessage.showError("Navigation Error", "Failed to load the quiz screen controller.");
            // Attempt to switch back to a safe state, e.g., the dashboard
            backToDashboard();
        }
    }

    /**
     * Handles the action of the "Mark as Complete" button.
     * Only applicable if the current user is a student.
     */
    @FXML
    private void markLessonCompleteAction() { // Renamed to avoid conflict with interface
        if (currentStudent != null && currentLesson != null) {
            if (!currentStudent.isLessonCompleted(currentLesson.getLessonId())) {
                // Mark complete using the student's progress tracking method
                currentStudent.markLessonCompleted(currentLesson.getLessonId()); // This now handles saving progress
                AlertMessage.showInformation("Progress Updated", "Lesson '" + currentLesson.getTitle() + "' marked as complete!");
                updateCompleteButtonState(); // Update button appearance/state
                // NOTE: Student's progress is saved within markLessonCompleted()
            } else {
                // Optional: Provide feedback if already completed, though button should be disabled.
                AlertMessage.showInformation("Already Completed", "You have already completed this lesson.");
                updateCompleteButtonState(); // Ensure button state is correct
            }
        } else {
            // This case shouldn't be reached if button visibility/management is correct
            AlertMessage.showWarning("Action Failed", "Cannot mark lesson complete (User must be a student and lesson must be loaded).");
        }
    }

    /** Updates the text and disabled state of the "Mark Complete" button based on student progress. */
    private void updateCompleteButtonState() {
        // Ensure the button is only updated if the user is a student and the button exists
        if (currentStudent != null && currentLesson != null && markCompleteButton != null) {
            boolean isCompleted = currentStudent.isLessonCompleted(currentLesson.getLessonId());
            markCompleteButton.setText(isCompleted ? "Lesson Completed" : "Mark as Complete");
            markCompleteButton.setDisable(isCompleted); // Disable button if already completed
        }
        // If not a student, the button should already be invisible/unmanaged from initialize()
    }


    /** Handles the action of navigating back to the previous dashboard. */
    // Inside LessonController.java
    @FXML
    private void backToDashboard() {
        User loggedInUser = LoginManager.getSelectedUser(); // Get current user

        if (loggedInUser instanceof Student) {
            Object controller = SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
            if (controller instanceof StudentSceneController) {
                // ** RE-SET DATA ON RETURN **
                ((StudentSceneController) controller).setStudentData((Student) loggedInUser);
            } else {
                System.err.println("Error: Could not get StudentSceneController to refresh dashboard.");
                SceneManager.switchToScene(SceneManager.LOGIN); // Fallback
            }
        } else if (loggedInUser instanceof Teacher) { // Could be a teacher previewing?
            Object controller = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
            if (controller instanceof TeacherSceneController) {
                // ** RE-SET DATA ON RETURN **
                ((TeacherSceneController) controller).setTeacherData((Teacher) loggedInUser);
            } else {
                System.err.println("Error: Could not get TeacherSceneController to refresh dashboard.");
                SceneManager.switchToScene(SceneManager.LOGIN); // Fallback
            }
        }
        else { // No logged-in user or unknown type?
            SceneManager.switchToScene(SceneManager.LOGIN); // Fallback to login
        }
    }

}