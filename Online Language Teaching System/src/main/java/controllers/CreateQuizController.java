package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {

    @FXML private Label lessonTitleLabel; // To show which lesson this quiz is for (optional)

    // --- Question 1 UI Elements ---
    @FXML private TextField question1Field;
    @FXML private TextField q1OptionAField;
    @FXML private TextField q1OptionBField;
    @FXML private TextField q1OptionCField;
    @FXML private TextField q1OptionDField;
    @FXML private ToggleGroup correctAnswer1Group; // Make sure fx:id matches in FXML
    @FXML private RadioButton q1OptionACorrect; // Need fx:id on RadioButtons if using ToggleGroup lookup
    @FXML private RadioButton q1OptionBCorrect;
    @FXML private RadioButton q1OptionCCorrect;
    @FXML private RadioButton q1OptionDCorrect;

    // --- Question 2 UI Elements ---
    @FXML private TextField question2Field;
    @FXML private TextField q2OptionAField;
    @FXML private TextField q2OptionBField;
    @FXML private TextField q2OptionCField;
    @FXML private TextField q2OptionDField;
    @FXML private ToggleGroup correctAnswer2Group;
    @FXML private RadioButton q2OptionACorrect;
    @FXML private RadioButton q2OptionBCorrect;
    @FXML private RadioButton q2OptionCCorrect;
    @FXML private RadioButton q2OptionDCorrect;

    // --- Question 3 UI Elements ---
    @FXML private TextField question3Field;
    @FXML private TextField q3OptionAField;
    @FXML private TextField q3OptionBField;
    @FXML private TextField q3OptionCField;
    @FXML private TextField q3OptionDField;
    @FXML private ToggleGroup correctAnswer3Group;
    @FXML private RadioButton q3OptionACorrect;
    @FXML private RadioButton q3OptionBCorrect;
    @FXML private RadioButton q3OptionCCorrect;
    @FXML private RadioButton q3OptionDCorrect;

    // --- Question 4 UI Elements ---
    @FXML private TextField question4Field;
    @FXML private TextField q4OptionAField;
    @FXML private TextField q4OptionBField;
    @FXML private TextField q4OptionCField;
    @FXML private TextField q4OptionDField;
    @FXML private ToggleGroup correctAnswer4Group;
    @FXML private RadioButton q4OptionACorrect;
    @FXML private RadioButton q4OptionBCorrect;
    @FXML private RadioButton q4OptionCCorrect;
    @FXML private RadioButton q4OptionDCorrect;

    // Add ComboBox for proficiency level if not tied to a lesson
    @FXML private TextField quizTitleField; // Need a field for the Quiz title


    private Teacher currentTeacher;
    private String associatedLessonId; // Store the ID of the lesson this quiz belongs to

    @FXML
    public void initialize() {
        // Get the logged-in teacher
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Teacher) {
            this.currentTeacher = (Teacher) loggedInUser;
        } else {
            AlertMessage.showError("Access Denied", "Only teachers can create quizzes.");
            // Disable fields
            disableAllFields();
            return;
        }
        // TODO: Receive associatedLessonId if passed from another controller
        // For now, assume it's null or set manually
        this.associatedLessonId = null; // Example: "L1020"
        if(this.associatedLessonId != null) {
            lessonTitleLabel.setText("Quiz for Lesson: " + associatedLessonId); // Update label
        } else {
            lessonTitleLabel.setText("Create Standalone Quiz");
        }
    }

    // Method to be called from another controller to pass data
    public void setQuizContext(Teacher teacher, String lessonId) {
        this.currentTeacher = teacher;
        this.associatedLessonId = lessonId;
        if(this.associatedLessonId != null) {
            // Optionally load lesson title to display
            lessonTitleLabel.setText("Quiz for Lesson: " + associatedLessonId);
        } else {
            lessonTitleLabel.setText("Create Standalone Quiz");
        }
    }

    @FXML
    private void saveQuiz() {
        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save quiz. Teacher information missing.");
            return;
        }

        List<Question> questions = new ArrayList<>();
        String quizTitle = quizTitleField.getText().trim(); // Get quiz title
        // Get proficiency level (e.g., from a ComboBox or based on lesson)
        String proficiencyLevel = "Beginner"; // Placeholder

        if (quizTitle.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a quiz title.");
            return;
        }


        try {
            // --- Process Question 1 ---
            Question q1 = processQuestion(question1Field, q1OptionAField, q1OptionBField, q1OptionCField, q1OptionDField, correctAnswer1Group);
            if (q1 != null) questions.add(q1);
            else return; // Stop if processing failed

            // --- Process Question 2 ---
            Question q2 = processQuestion(question2Field, q2OptionAField, q2OptionBField, q2OptionCField, q2OptionDField, correctAnswer2Group);
            if (q2 != null) questions.add(q2);
            else return;

            // --- Process Question 3 ---
            Question q3 = processQuestion(question3Field, q3OptionAField, q3OptionBField, q3OptionCField, q3OptionDField, correctAnswer3Group);
            if (q3 != null) questions.add(q3);
            else return;

            // --- Process Question 4 ---
            Question q4 = processQuestion(question4Field, q4OptionAField, q4OptionBField, q4OptionCField, q4OptionDField, correctAnswer4Group);
            if (q4 != null) questions.add(q4);
            else return;


            if(questions.size() < 4) { // Or check based on how many are required
                AlertMessage.showWarning("Incomplete Quiz", "Please complete all required questions before saving.");
                return;
            }

            // Create the Quiz object (constructor handles saving)
            Quiz newQuiz = new Quiz(quizTitle, proficiencyLevel, questions, associatedLessonId, currentTeacher.getID());

            if (newQuiz.getFilePath() != null && !newQuiz.getFilePath().isEmpty()) {
                // Update teacher's list
                currentTeacher.addCreatedQuiz(newQuiz.getQuizId());
                // TODO: Persist teacher's updated list

                AlertMessage.showInformation("Quiz Saved", "Quiz '" + newQuiz.getTitle() + "' (ID: " + newQuiz.getQuizId() + ") created successfully.");
                // Optionally clear fields or navigate back
                clearAllFields();
                // backToDashboard();
            } else {
                AlertMessage.showError("Save Error", "Failed to save the quiz. Check logs.");
            }


        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Input", "Error creating question/quiz: " + e.getMessage());
        } catch (Exception e) {
            AlertMessage.showError("Unexpected Error", "An error occurred while saving the quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper to process UI elements for a single question
    private Question processQuestion(TextField qField, TextField oA, TextField oB, TextField oC, TextField oD, ToggleGroup correctGroup) {
        String qText = qField.getText().trim();
        String optA = oA.getText().trim();
        String optB = oB.getText().trim();
        String optC = oC.getText().trim();
        String optD = oD.getText().trim();

        // Basic validation for the current question
        if (qText.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
            AlertMessage.showWarning("Incomplete Question", "Please fill in the question text and all four options for: " + qField.getPromptText()); // Use prompt text or ID to identify
            return null; // Indicate failure
        }

        RadioButton selectedCorrect = (RadioButton) correctGroup.getSelectedToggle();
        if (selectedCorrect == null) {
            AlertMessage.showWarning("Missing Correct Answer", "Please select the correct answer for question: " + qText.substring(0, Math.min(qText.length(), 20)) + "...");
            return null; // Indicate failure
        }

        // Determine the text of the correct answer based on which RadioButton was selected
        String correctAnswerText = "";
        // IMPORTANT: Assumes fx:id matches the variable names qXOptionYCorrect
        // This is fragile. A better way is to set UserData on the RadioButton in FXML or code.
        // Example using UserData (set userData="A" in FXML for q1OptionACorrect etc.):
        // String correctLetter = (String) selectedCorrect.getUserData();
        // switch (correctLetter) {
        //    case "A": correctAnswerText = optA; break; ...
        // }

        // --- Current Less Robust Way (Relies on fx:id mapping) ---
        if (selectedCorrect == q1OptionACorrect || selectedCorrect == q2OptionACorrect || selectedCorrect == q3OptionACorrect || selectedCorrect == q4OptionACorrect) correctAnswerText = optA;
        else if (selectedCorrect == q1OptionBCorrect || selectedCorrect == q2OptionBCorrect || selectedCorrect == q3OptionBCorrect || selectedCorrect == q4OptionBCorrect) correctAnswerText = optB;
        else if (selectedCorrect == q1OptionCCorrect || selectedCorrect == q2OptionCCorrect || selectedCorrect == q3OptionCCorrect || selectedCorrect == q4OptionCCorrect) correctAnswerText = optC;
        else if (selectedCorrect == q1OptionDCorrect || selectedCorrect == q2OptionDCorrect || selectedCorrect == q3OptionDCorrect || selectedCorrect == q4OptionDCorrect) correctAnswerText = optD;
        else {
            AlertMessage.showError("Internal Error", "Could not determine correct answer text. Check RadioButton fx:ids.");
            return null;
        }
        // --- End Less Robust Way ---


        String[] options = {optA, optB, optC, optD};

        try {
            return new Question(qText, options, correctAnswerText);
        } catch (IllegalArgumentException e) {
            // Catch specific errors from Question constructor if needed
            throw e; // Re-throw to be caught by saveQuiz
        }
    }


    @FXML
    private void backToDashboard() {
        // Navigate back to the appropriate dashboard
        if (currentTeacher != null) {
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }

    private void disableAllFields() {
        // Disable text fields
        quizTitleField.setDisable(true);
        question1Field.setDisable(true); q1OptionAField.setDisable(true); q1OptionBField.setDisable(true); q1OptionCField.setDisable(true); q1OptionDField.setDisable(true);
        question2Field.setDisable(true); q2OptionAField.setDisable(true); q2OptionBField.setDisable(true); q2OptionCField.setDisable(true); q2OptionDField.setDisable(true);
        question3Field.setDisable(true); q3OptionAField.setDisable(true); q3OptionBField.setDisable(true); q3OptionCField.setDisable(true); q3OptionDField.setDisable(true);
        question4Field.setDisable(true); q4OptionAField.setDisable(true); q4OptionBField.setDisable(true); q4OptionCField.setDisable(true); q4OptionDField.setDisable(true);
        // Disable radio buttons
        q1OptionACorrect.setDisable(true); q1OptionBCorrect.setDisable(true); q1OptionCCorrect.setDisable(true); q1OptionDCorrect.setDisable(true);
        q2OptionACorrect.setDisable(true); q2OptionBCorrect.setDisable(true); q2OptionCCorrect.setDisable(true); q2OptionDCorrect.setDisable(true);
        q3OptionACorrect.setDisable(true); q3OptionBCorrect.setDisable(true); q3OptionCCorrect.setDisable(true); q3OptionDCorrect.setDisable(true);
        q4OptionACorrect.setDisable(true); q4OptionBCorrect.setDisable(true); q4OptionCCorrect.setDisable(true); q4OptionDCorrect.setDisable(true);
        // Disable buttons
        // saveButton.setDisable(true); // Assuming a save button exists
    }

    private void clearAllFields() {
        quizTitleField.clear();
        question1Field.clear(); q1OptionAField.clear(); q1OptionBField.clear(); q1OptionCField.clear(); q1OptionDField.clear(); if(correctAnswer1Group.getSelectedToggle() != null) correctAnswer1Group.getSelectedToggle().setSelected(false);
        question2Field.clear(); q2OptionAField.clear(); q2OptionBField.clear(); q2OptionCField.clear(); q2OptionDField.clear(); if(correctAnswer2Group.getSelectedToggle() != null) correctAnswer2Group.getSelectedToggle().setSelected(false);
        question3Field.clear(); q3OptionAField.clear(); q3OptionBField.clear(); q3OptionCField.clear(); q3OptionDField.clear(); if(correctAnswer3Group.getSelectedToggle() != null) correctAnswer3Group.getSelectedToggle().setSelected(false);
        question4Field.clear(); q4OptionAField.clear(); q4OptionBField.clear(); q4OptionCField.clear(); q4OptionDField.clear(); if(correctAnswer4Group.getSelectedToggle() != null) correctAnswer4Group.getSelectedToggle().setSelected(false);
    }
}