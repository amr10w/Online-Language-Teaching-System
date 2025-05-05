package controllers;

import Main.*; // Import necessary classes from Main package
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button; // Import Button
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the 'createlesson.fxml' view.
 * Allows teachers to create and save new lessons.
 */
public class CreateLessonController {

    // --- FXML Elements ---
    @FXML private TextField lessonTitleField;
    @FXML private TextArea lessonContentArea;
    @FXML private ComboBox<String> levelComboBox; // For selecting proficiency level
    @FXML private Button saveLessonButton; // Ensure fx:id="saveLessonButton" in FXML
    @FXML private Button createQuizButton; // Ensure fx:id="createQuizButton"
    @FXML private Button cancelButton;     // Ensure fx:id="cancelButton"
    @FXML private Button backButton;       // Ensure fx:id="backButton"

    // Add fields for prerequisites if needed (e.g., a TextField for comma-separated IDs)
    // @FXML private TextField prerequisitesField;

    // --- Controller State ---
    private Teacher currentTeacher; // Store the logged-in teacher creating the lesson

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("CreateLessonController initializing...");
        // Populate proficiency levels ComboBox
        levelComboBox.setItems(FXCollections.observableArrayList(
                ProficiencyLevel.BEGINNER, // Use constants for internal values
                ProficiencyLevel.INTERMEDIATE,
                ProficiencyLevel.ADVANCED
        ));
        // Set display values (Capitalized)
        levelComboBox.setConverter(new javafx.util.StringConverter<String>() {
            @Override public String toString(String object) {
                if (object == null) return null;
                return object.substring(0, 1).toUpperCase() + object.substring(1); // Capitalize
            }
            @Override public String fromString(String string) { return string.toLowerCase(); /* Convert back if needed */ }
        });

        levelComboBox.setValue(ProficiencyLevel.BEGINNER); // Default selection

        // Get the logged-in teacher from the LoginManager session
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Teacher) {
            this.currentTeacher = (Teacher) loggedInUser;
            System.out.println("Teacher '" + currentTeacher.getUsername() + "' accessed Create Lesson screen.");
        } else {
            // Handle error: A non-teacher accessed this screen
            System.err.println("CRITICAL ERROR: Non-teacher (" + loggedInUser + ") accessed CreateLessonController!");
            AlertMessage.showError("Access Denied", "Only teachers can create lessons. Please log in as a teacher.");
            // Disable input fields and buttons to prevent interaction
            disableForm();
            // Optionally, navigate back to the login screen immediately
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }

    /** Disables all form input elements. */
    private void disableForm() {
        lessonTitleField.setDisable(true);
        lessonContentArea.setDisable(true);
        levelComboBox.setDisable(true);
        saveLessonButton.setDisable(true);
        createQuizButton.setDisable(true);
        cancelButton.setDisable(true); // Or maybe keep cancel active?
        // Disable prerequisite field if added
        // prerequisitesField.setDisable(true);
    }


    /**
     * Handles the action of saving the lesson data entered in the form.
     * Validates input, creates a new Lesson object (which handles file saving),
     * updates the teacher's tracked lessons, and provides feedback.
     */
    @FXML
    private void saveLesson() {
        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save lesson. Teacher information is missing. Please log in again.");
            disableForm(); // Prevent further action if teacher context is lost
            return;
        }

        // --- Get Input Data ---
        String title = lessonTitleField.getText().trim();
        String content = lessonContentArea.getText(); // Get raw content, could be empty or multi-line
        String level = levelComboBox.getValue(); // Gets the selected internal value (lowercase)
        // Get prerequisites (implement input method, e.g., comma-separated IDs)
        List<String> prerequisites = new ArrayList<>(); // Placeholder - Add logic for prerequisitesField if used
        // String prereqInput = prerequisitesField.getText().trim();
        // if (!prereqInput.isEmpty()) {
        //     prerequisites.addAll(List.of(prereqInput.split("\\s*,\\s*"))); // Split by comma, trim whitespace
        // }

        // --- Basic Validation ---
        if (title.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a lesson title.");
            lessonTitleField.requestFocus();
            return;
        }
        // Allow empty content? Assume yes for now. If not, add validation:
        if (content.trim().isEmpty()) { // Check if required
            boolean confirmEmpty = AlertMessage.showConfirmation("Empty Content", "The lesson content is empty. Do you want to save it anyway?");
            if (!confirmEmpty) return;
        }
        if (level == null) {
            // This shouldn't happen if ComboBox has a default and items, but check anyway
            AlertMessage.showWarning("Input Required", "Please select a proficiency level.");
            levelComboBox.requestFocus();
            return;
        }

        // --- Create and Save Lesson ---
        try {
            // Create new Lesson object - the constructor handles saving and ID generation
            System.out.println("Attempting to create lesson with title: " + title);
            Lesson newLesson = new Lesson(title, content, level, prerequisites, currentTeacher.getID());

            // Lesson constructor throws RuntimeException on save failure, so if we reach here, it saved.

            // Update the teacher's list of created lessons (in memory)
            currentTeacher.addCreatedLesson(newLesson.getLessonId());
            // NOTE: Teacher's created list persistence isn't implemented. It relies on reload scan.

            AlertMessage.showInformation("Lesson Saved", "Lesson '" + newLesson.getTitle() + "' (ID: " + newLesson.getLessonId() + ") was created successfully.");

            // Clear fields for the next lesson or navigate away
            clearFields();
            // Optionally navigate back: backToDashboard();

        } catch (IllegalArgumentException e) {
            // Catch validation errors from Lesson constructor or this method
            AlertMessage.showError("Invalid Input", "Could not create lesson: " + e.getMessage());
        } catch (RuntimeException e) {
            // Catch saving errors from Lesson constructor
            AlertMessage.showError("Save Error", "Failed to save the new lesson: " + e.getMessage());
            // Log the full stack trace for debugging
            e.printStackTrace();
        } catch (Exception e) {
            // Catch any other unexpected errors during the process
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while saving the lesson: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Clears the input fields in the form. */
    private void clearFields() {
        lessonTitleField.clear();
        lessonContentArea.clear();
        levelComboBox.setValue(ProficiencyLevel.BEGINNER); // Reset to default
        // Clear prerequisite input field if added
        // prerequisitesField.clear();
    }

    /**
     * Handles the action to navigate to the Create Quiz screen.
     * Currently suggests saving the lesson first.
     */
    @FXML
    private void createQuizForLesson() {
        // Logic to navigate to CreateQuiz screen, potentially passing the lesson context.
        // Decision: Should the lesson be saved first? Let's assume yes for now.
        // However, the current saveLesson doesn't easily return the created lesson object.
        // Easiest flow: Save lesson, then go back to dashboard and create quiz from there.
        AlertMessage.showInformation("Create Quiz", "Please save the lesson first.\nYou can then create a quiz from the Teacher Dashboard by selecting the lesson or using the 'Create New Quiz' button.");

        // Alternative (more complex):
        // 1. Modify saveLesson to return the created Lesson object (or null on failure).
        // 2. If save is successful:
        //    Lesson savedLesson = saveLessonAndGet();
        //    if (savedLesson != null) {
        //        Object controller = SceneManager.switchToScene(SceneManager.CREATE_QUIZ);
        //        if (controller instanceof CreateQuizController) {
        //            ((CreateQuizController) controller).setQuizContext(currentTeacher, savedLesson.getLessonId());
        //        } else { /* Handle error */ }
        //    }
    }

    /**
     * Handles the cancel action. Asks for confirmation if fields have data,
     * then navigates back to the teacher dashboard.
     */
    @FXML
    private void cancel() {
        // Check if any potentially unsaved data exists
        boolean hasUnsavedData = !lessonTitleField.getText().trim().isEmpty() ||
                !lessonContentArea.getText().isEmpty() ||
                !levelComboBox.getValue().equals(ProficiencyLevel.BEGINNER);
        // Add check for prerequisitesField if used

        if (hasUnsavedData) {
            boolean confirmed = AlertMessage.showConfirmation("Confirm Cancel",
                    "Are you sure you want to cancel? Any unsaved changes to the lesson will be lost.");
            if (!confirmed) {
                return; // User chose not to cancel
            }
        }
        // If confirmed or no unsaved data, navigate back
        System.out.println("Cancelling lesson creation.");
        backToDashboard();
    }

    /**
     * Navigates back to the appropriate dashboard (Teacher Dashboard).
     * Includes a fallback to Login screen if teacher context is somehow lost.
     */
    @FXML
    private void backToDashboard() {
        if (currentTeacher != null) {
            System.out.println("Navigating back to Teacher Dashboard.");
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            // Fallback if teacher data is missing (should not happen normally)
            System.err.println("Teacher context missing, navigating to Login screen as fallback.");
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }
}