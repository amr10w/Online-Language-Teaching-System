package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox; // Added for level selection
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public class CreateLessonController {

    @FXML private TextField lessonTitleField;
    @FXML private TextArea lessonContentArea;
    @FXML private ComboBox<String> levelComboBox; // Added for selecting proficiency level
    // Add fields for prerequisites if needed (e.g., a ListView or TextField for comma-separated IDs)

    private Teacher currentTeacher; // Store the teacher creating the lesson

    @FXML
    public void initialize() {
        // Populate proficiency levels
        levelComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        levelComboBox.setValue("Beginner"); // Default selection

        // Get the logged-in teacher (assuming they are stored in LoginManager)
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Teacher) {
            this.currentTeacher = (Teacher) loggedInUser;
        } else {
            // Handle error: A non-teacher somehow accessed this screen
            AlertMessage.showError("Access Denied", "Only teachers can create lessons.");
            // Optionally navigate back or disable functionality
            lessonTitleField.setDisable(true);
            lessonContentArea.setDisable(true);
            levelComboBox.setDisable(true);
        }
    }


    @FXML
    private void saveLesson() {
        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save lesson. Teacher information missing.");
            return;
        }

        String title = lessonTitleField.getText().trim();
        String content = lessonContentArea.getText(); // Keep original formatting
        String level = levelComboBox.getValue();
        // Get prerequisites (implement input method for this)
        List<String> prerequisites = new ArrayList<>(); // Placeholder

        // Basic Validation
        if (title.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a lesson title.");
            return;
        }
        if (content.trim().isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter lesson content.");
            return;
        }
        if (level == null) {
            AlertMessage.showWarning("Input Required", "Please select a proficiency level.");
            return;
        }


        try {
            // Create new Lesson object - this also handles saving
            Lesson newLesson = new Lesson(title, content, level, prerequisites, currentTeacher.getID());

            // Check if lesson was saved successfully (constructor handles saving now)
            if (newLesson.getFilePath() != null && !newLesson.getFilePath().isEmpty()) {
                // Update the teacher's list of created lessons
                currentTeacher.addCreatedLesson(newLesson.getLessonId());
                // TODO: Persist teacher's updated createdLessonIds list if necessary

                AlertMessage.showInformation("Lesson Saved", "Lesson '" + newLesson.getTitle() + "' (ID: " + newLesson.getLessonId() + ") created successfully.");
                // Optionally navigate back to dashboard or clear fields
                clearFields();
                // navigateToTeacherDashboard(); // Or call backToDashboard
            } else {
                AlertMessage.showError("Save Error", "Failed to save the lesson. Check logs.");
            }

        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Input", "Error creating lesson: " + e.getMessage());
        } catch (Exception e) {
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while saving the lesson: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        lessonTitleField.clear();
        lessonContentArea.clear();
        levelComboBox.setValue("Beginner");
        // Clear prerequisite input field
    }

    @FXML
    private void createQuizForLesson() {
        // Logic to navigate to CreateQuiz screen, potentially passing the lesson context (ID or title)
        // Save the lesson first? Or allow creating quiz without saving lesson yet?
        // Let's assume lesson needs to exist first.
        AlertMessage.showInformation("Not Implemented", "Save the lesson first, then create a quiz from the dashboard or lesson list.");
        // Or implement save & navigate logic:
        // if (saveLesson()) { // Modify saveLesson to return boolean
        //     // Get the new lesson ID
        //     // SceneManager.switchToScene(SceneManager.CREATE_QUIZ, newLessonId); // Need method to pass data
        // }
    }

    @FXML
    private void cancel() {
        // Ask for confirmation if fields have data?
        if (!lessonTitleField.getText().isEmpty() || !lessonContentArea.getText().isEmpty()) {
            boolean confirmed = AlertMessage.showConfirmation("Confirm Cancel", "Are you sure you want to cancel? Unsaved changes will be lost.");
            if (!confirmed) {
                return; // User cancelled the cancel action
            }
        }
        // Navigate back
        backToDashboard();
    }

    @FXML
    private void backToDashboard() {
        // Navigate back to the appropriate dashboard
        if (currentTeacher != null) {
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            // Fallback or handle error - maybe go to login?
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }
}