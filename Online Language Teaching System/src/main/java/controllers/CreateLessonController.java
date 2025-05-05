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
import java.util.UUID; // For generating Lesson ID if needed

public class CreateLessonController {

    @FXML private TextField lessonTitleField;
    @FXML private TextArea lessonContentArea;
    @FXML private ComboBox<String> levelComboBox;

    private Teacher currentTeacher;
    // Store the most recently saved lesson temporarily if needed for createQuiz button
    private Lesson lastSavedLesson = null;

    @FXML
    public void initialize() {
        levelComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        levelComboBox.setValue("Beginner");

        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Teacher) {
            this.currentTeacher = (Teacher) loggedInUser;
        } else {
            handleInvalidAccess();
        }
        // Disable create quiz button initially until a lesson is saved
        // You might need an @FXML reference to the button if you add one to the FXML
        // createQuizButton.setDisable(true); // Assuming fx:id="createQuizButton"
    }

    private void handleInvalidAccess() {
        AlertMessage.showError("Access Denied", "Only teachers can create lessons.");
        lessonTitleField.setDisable(true);
        lessonContentArea.setDisable(true);
        levelComboBox.setDisable(true);
        // Consider navigating away immediately
        // SceneManager.switchToScene(SceneManager.LOGIN);
    }

    // Modified saveLesson to return the created Lesson object (or null on failure)
    @FXML
    private Lesson saveLesson() { // Return type changed
        lastSavedLesson = null; // Reset last saved lesson
        // createQuizButton.setDisable(true); // Disable again if re-saving

        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save lesson. Teacher information missing.");
            return null; // Return null on failure
        }

        String title = lessonTitleField.getText().trim();
        String content = lessonContentArea.getText(); // Keep original formatting
        String level = levelComboBox.getValue();
        List<String> prerequisites = new ArrayList<>(); // Placeholder - Add UI for this if needed

        // Basic Validation
        if (title.isEmpty() || content.trim().isEmpty() || level == null) {
            AlertMessage.showWarning("Input Required", "Please enter Title, Content, and select Level.");
            if (title.isEmpty()) lessonTitleField.requestFocus();
            else if (content.trim().isEmpty()) lessonContentArea.requestFocus();
            else levelComboBox.requestFocus();
            return null; // Return null on failure
        }

        try {
            // Create new Lesson - Assuming constructor handles ID generation and saving
            Lesson newLesson = new Lesson(title, content, level, prerequisites, currentTeacher.getID());

            // Check if lesson seems valid and saved (constructor should ensure this)
            // A better check: verify file existence or have constructor throw on save failure
            if (newLesson.getLessonId() != null && !newLesson.getLessonId().equals("L_DEFAULT") &&
                    newLesson.getFilePath() != null && !newLesson.getFilePath().isEmpty())
            {
                // Update the teacher's list of created lessons
                currentTeacher.addCreatedLesson(newLesson.getLessonId());
                // TODO: Persist teacher's updated list if Teacher objects are saved separately

                AlertMessage.showInformation("Lesson Saved", "Lesson '" + newLesson.getTitle() + "' (ID: " + newLesson.getLessonId() + ") created successfully.");
                lastSavedLesson = newLesson; // Store the successfully saved lesson
                // createQuizButton.setDisable(false); // Enable create quiz button
                clearFields(); // Clear fields after successful save
                return newLesson; // Return the new lesson object

            } else {
                AlertMessage.showError("Save Error", "Failed to save the lesson (Invalid ID or FilePath). Check Lesson constructor and logs.");
                return null; // Return null on failure
            }

        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Input", "Error creating lesson: " + e.getMessage());
            return null; // Return null on failure
        } catch (Exception e) {
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while saving the lesson: " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on failure
        }
    }


    private void clearFields() {
        lessonTitleField.clear();
        lessonContentArea.clear();
        levelComboBox.setValue("Beginner");
        // Clear prerequisite input field if added
    }

    @FXML
    private void createQuizForLesson() {
        // 1. Ensure a lesson is saved or try saving the current state
        Lesson lessonToQuiz = lastSavedLesson; // Use the last successfully saved lesson

        if (lessonToQuiz == null) {
            // Option A: Try saving the current content first
            boolean confirmed = AlertMessage.showConfirmation("Save Lesson?", "You need to save the lesson before creating a quiz for it. Save now?");
            if (confirmed) {
                lessonToQuiz = saveLesson(); // Try saving
                if (lessonToQuiz == null) {
                    AlertMessage.showError("Save Failed", "Could not save the lesson. Cannot create quiz.");
                    return; // Stop if save failed
                }
            } else {
                AlertMessage.showWarning("Action Cancelled", "Lesson must be saved to create an associated quiz.");
                return; // Stop if user cancels save
            }

            // Option B: Show error if no lesson saved yet
            // AlertMessage.showWarning("Save Required", "Please save the lesson using the 'Save Lesson' button before creating a quiz for it.");
            // return;
        }

        // We should now have a valid lessonToQuiz object with an ID
        if (lessonToQuiz != null && lessonToQuiz.getLessonId() != null && !lessonToQuiz.getLessonId().equals("L_DEFAULT")) {
            System.out.println("Navigating to Create Quiz for Lesson ID: " + lessonToQuiz.getLessonId());
            Object controller = SceneManager.switchToScene(SceneManager.CREATE_QUIZ);
            if (controller instanceof CreateQuizController) {
                ((CreateQuizController) controller).setQuizContext(currentTeacher, lessonToQuiz.getLessonId());
            } else {
                AlertMessage.showError("Navigation Error", "Failed to load the Create Quiz screen controller.");
            }
        } else {
            // This case should ideally be caught earlier by save checks
            AlertMessage.showError("Error", "Cannot create quiz. No valid saved lesson reference found.");
        }
    }


    @FXML
    private void cancel() {
        if (!lessonTitleField.getText().isEmpty() || !lessonContentArea.getText().isEmpty()) {
            boolean confirmed = AlertMessage.showConfirmation("Confirm Cancel", "Are you sure you want to cancel? Unsaved changes will be lost.");
            if (!confirmed) {
                return;
            }
        }
        backToDashboard();
    }

    @FXML
    private void backToDashboard() {
        if (currentTeacher != null) {
            // **CRITICAL FIX for Problem 2:** Refresh the Teacher Dashboard
            Object controller = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
            if (controller instanceof TeacherSceneController) {
                // Re-set the teacher data to force UI refresh including lesson list
                ((TeacherSceneController) controller).setTeacherData(currentTeacher);
            } else {
                System.err.println("Error: Could not get TeacherSceneController to refresh dashboard.");
            }
        } else {
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }
}