package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane; // Import layout panes if needed for focusing
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
// Removed UUID import as Quiz constructor now handles ID generation

/**
 * Controller for the 'createquiz.fxml' view.
 * Allows teachers to create and save new quizzes, optionally associated with a lesson.
 */
public class CreateQuizController {

    // --- FXML Elements ---

    // Top Bar / Context
    @FXML private Label associatedLessonLabel;
    @FXML private Button backButton; // Ensure fx:id exists

    // Quiz Metadata
    @FXML private TextField quizTitleField;
    // Consider adding ComboBox for level if not derived from lesson

    // Question 1 Elements
    @FXML private TitledPane question1Pane; // To collapse/expand
    @FXML private TextField q1Text;
    @FXML private TextField q1OptA; @FXML private RadioButton q1CorrectA;
    @FXML private TextField q1OptB; @FXML private RadioButton q1CorrectB;
    @FXML private TextField q1OptC; @FXML private RadioButton q1CorrectC;
    @FXML private TextField q1OptD; @FXML private RadioButton q1CorrectD;
    private ToggleGroup correctAnswerGroup1; // Programmatically assigned

    // Question 2 Elements
    @FXML private TitledPane question2Pane;
    @FXML private TextField q2Text;
    @FXML private TextField q2OptA; @FXML private RadioButton q2CorrectA;
    @FXML private TextField q2OptB; @FXML private RadioButton q2CorrectB;
    @FXML private TextField q2OptC; @FXML private RadioButton q2CorrectC;
    @FXML private TextField q2OptD; @FXML private RadioButton q2CorrectD;
    private ToggleGroup correctAnswerGroup2;

    // Question 3 Elements
    @FXML private TitledPane question3Pane;
    @FXML private TextField q3Text;
    @FXML private TextField q3OptA; @FXML private RadioButton q3CorrectA;
    @FXML private TextField q3OptB; @FXML private RadioButton q3CorrectB;
    @FXML private TextField q3OptC; @FXML private RadioButton q3CorrectC;
    @FXML private TextField q3OptD; @FXML private RadioButton q3CorrectD;
    private ToggleGroup correctAnswerGroup3;

    // Question 4 Elements
    @FXML private TitledPane question4Pane;
    @FXML private TextField q4Text;
    @FXML private TextField q4OptA; @FXML private RadioButton q4CorrectA;
    @FXML private TextField q4OptB; @FXML private RadioButton q4CorrectB;
    @FXML private TextField q4OptC; @FXML private RadioButton q4CorrectC;
    @FXML private TextField q4OptD; @FXML private RadioButton q4CorrectD;
    private ToggleGroup correctAnswerGroup4;

    // Action Buttons
    @FXML private Button saveQuizButton; // Ensure fx:id exists

    // --- Controller State ---
    private Teacher currentTeacher;
    private String associatedLessonId;
    private Lesson associatedLesson; // Store the lesson object if available

    // Lists to easily access UI elements for validation/processing
    // Order MUST match the question numbering (1, 2, 3, 4)
    private List<TextField> questionTextFields;
    private List<List<TextField>> optionTextFields;
    private List<ToggleGroup> toggleGroups;
    private List<List<RadioButton>> correctRadioButtons; // Map radio buttons for easier correct answer check

    /**
     * Called automatically after FXML fields are injected.
     * Initializes ToggleGroups and prepares UI element lists.
     */
    @FXML
    public void initialize() {
        System.out.println("CreateQuizController initializing...");
        initializeToggleGroups();
        initializeUIElementLists();

        // Set default state
        associatedLessonLabel.setText("Create New Quiz");
        question1Pane.setExpanded(true); // Start with Q1 expanded
        question2Pane.setExpanded(false);
        question3Pane.setExpanded(false);
        question4Pane.setExpanded(false);

        // Get the logged-in teacher - crucial for saving
        User loggedInUser = LoginManager.getSelectedUser();
        if (loggedInUser instanceof Teacher) {
            this.currentTeacher = (Teacher) loggedInUser;
            System.out.println("Teacher '" + currentTeacher.getUsername() + "' accessed Create Quiz screen.");
        } else {
            System.err.println("CRITICAL ERROR: Non-teacher (" + loggedInUser + ") accessed CreateQuizController!");
            AlertMessage.showError("Access Denied", "Only teachers can create quizzes. Please log in as a teacher.");
            disableForm();
            SceneManager.switchToScene(SceneManager.LOGIN); // Navigate away
        }
    }

    /** Disables all form input elements. */
    private void disableForm() {
        quizTitleField.setDisable(true);
        question1Pane.setDisable(true);
        question2Pane.setDisable(true);
        question3Pane.setDisable(true);
        question4Pane.setDisable(true);
        saveQuizButton.setDisable(true);
    }


    /** Creates and assigns ToggleGroups for each question's radio buttons. */
    private void initializeToggleGroups() {
        correctAnswerGroup1 = new ToggleGroup();
        q1CorrectA.setToggleGroup(correctAnswerGroup1);
        q1CorrectB.setToggleGroup(correctAnswerGroup1);
        q1CorrectC.setToggleGroup(correctAnswerGroup1);
        q1CorrectD.setToggleGroup(correctAnswerGroup1);

        correctAnswerGroup2 = new ToggleGroup();
        q2CorrectA.setToggleGroup(correctAnswerGroup2);
        q2CorrectB.setToggleGroup(correctAnswerGroup2);
        q2CorrectC.setToggleGroup(correctAnswerGroup2);
        q2CorrectD.setToggleGroup(correctAnswerGroup2);

        correctAnswerGroup3 = new ToggleGroup();
        q3CorrectA.setToggleGroup(correctAnswerGroup3);
        q3CorrectB.setToggleGroup(correctAnswerGroup3);
        q3CorrectC.setToggleGroup(correctAnswerGroup3);
        q3CorrectD.setToggleGroup(correctAnswerGroup3);

        correctAnswerGroup4 = new ToggleGroup();
        q4CorrectA.setToggleGroup(correctAnswerGroup4);
        q4CorrectB.setToggleGroup(correctAnswerGroup4);
        q4CorrectC.setToggleGroup(correctAnswerGroup4);
        q4CorrectD.setToggleGroup(correctAnswerGroup4);
    }

    /** Populates lists of UI elements for easier iteration. */
    private void initializeUIElementLists() {
        questionTextFields = List.of(q1Text, q2Text, q3Text, q4Text);

        optionTextFields = List.of(
                List.of(q1OptA, q1OptB, q1OptC, q1OptD),
                List.of(q2OptA, q2OptB, q2OptC, q2OptD),
                List.of(q3OptA, q3OptB, q3OptC, q3OptD),
                List.of(q4OptA, q4OptB, q4OptC, q4OptD)
        );

        toggleGroups = List.of(correctAnswerGroup1, correctAnswerGroup2, correctAnswerGroup3, correctAnswerGroup4);

        // This mapping helps identify which option text corresponds to the selected radio button
        correctRadioButtons = List.of(
                List.of(q1CorrectA, q1CorrectB, q1CorrectC, q1CorrectD),
                List.of(q2CorrectA, q2CorrectB, q2CorrectC, q2CorrectD),
                List.of(q3CorrectA, q3CorrectB, q3CorrectC, q3CorrectD),
                List.of(q4CorrectA, q4CorrectB, q4CorrectC, q4CorrectD)
        );
    }


    /**
     * Method called by the previous controller (e.g., TeacherSceneController)
     * to pass the context (teacher and optional associated lesson ID).
     *
     * @param teacher The teacher creating the quiz.
     * @param lessonId The ID of the lesson this quiz is for (can be null).
     */
    public void setQuizContext(Teacher teacher, String lessonId) {
        Objects.requireNonNull(teacher, "Teacher cannot be null for quiz context");
        this.currentTeacher = teacher;
        this.associatedLessonId = lessonId;

        if (lessonId != null) {
            // Try to load the associated lesson to get its details (like level)
            this.associatedLesson = ApplicationManager.findLessonById(lessonId);
            if (this.associatedLesson != null) {
                associatedLessonLabel.setText("Quiz for Lesson: " + associatedLesson.getTitle() + " (ID: " + lessonId + ")");
                // Optionally pre-fill quiz title or set default level based on lesson
                if (quizTitleField.getText().isEmpty()) {
                    quizTitleField.setText("Quiz: " + associatedLesson.getTitle());
                }
                // TODO: Add level ComboBox and set its value:
                // levelComboBox.setValue(associatedLesson.getLevel());
            } else {
                associatedLessonLabel.setText("Quiz for Lesson ID: " + lessonId + " (Lesson not found!)");
                System.err.println("Warning: Could not load associated lesson object for ID: " + lessonId);
            }
        } else {
            associatedLessonLabel.setText("Creating Standalone Quiz");
        }
    }


    /**
     * Handles the action of saving the quiz.
     * Validates all input fields, creates Question objects, creates the Quiz object (which saves the file),
     * and provides feedback to the user.
     */
    @FXML
    private void saveQuiz() {
        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save quiz. Teacher information is missing. Please log in again.");
            disableForm();
            return;
        }

        // --- Validate Quiz Metadata ---
        String quizTitle = quizTitleField.getText().trim();
        if (quizTitle.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a Quiz Title.");
            quizTitleField.requestFocus();
            return;
        }
        // Determine the proficiency level
        String level;
        if (associatedLesson != null) {
            level = associatedLesson.getLevel(); // Get level from associated lesson
        } else {
            // TODO: Get level from a ComboBox if it's a standalone quiz
            level = ProficiencyLevel.BEGINNER; // Default if standalone and no level selector exists
            AlertMessage.showWarning("Level Defaulted", "Quiz level set to 'Beginner'. Add a level selector for standalone quizzes.");
        }


        // --- Validate and Collect Questions ---
        List<Question> questions = new ArrayList<>();
        boolean allQuestionsValid = true;

        for (int i = 0; i < 4; i++) { // Assuming exactly 4 questions based on FXML
            Question q = processQuestionInput(i);
            if (q != null) {
                questions.add(q);
            } else {
                allQuestionsValid = false;
                // Focus the first problematic element in the invalid question's pane
                questionTextFields.get(i).requestFocus(); // Example: focus the question text
                // Optionally expand the TitledPane for the invalid question
                // getTitledPaneByIndex(i).setExpanded(true);
                break; // Stop processing further questions after the first error
            }
        }

        if (!allQuestionsValid) {
            // Error message already shown by processQuestionInput
            return;
        }

        // Although loop runs 4 times, check if we actually got 4 valid questions
        if (questions.size() != 4) {
            // This case shouldn't be reached if processQuestionInput works correctly, but as a safeguard:
            AlertMessage.showError("Quiz Error", "Could not collect all 4 questions correctly. Please review inputs.");
            return;
        }

        // --- Create and Save Quiz ---
        try {
            System.out.println("Attempting to create quiz with title: " + quizTitle);
            // Use the correct Quiz constructor (adjust if signature is different)
            // Constructor: (title, level, questions, associatedLessonId, teacherId)
            Quiz newQuiz = new Quiz(quizTitle, level, questions, associatedLessonId, currentTeacher.getID());

            // Constructor throws exception on save failure. If we're here, it succeeded.

            // Update teacher's tracked quizzes (optional, relies on reload scan)
            currentTeacher.addCreatedQuiz(newQuiz.getQuizId());

            AlertMessage.showInformation("Quiz Saved", "Quiz '" + newQuiz.getTitle() + "' (ID: " + newQuiz.getQuizId() + ") saved successfully.");
            clearFields(); // Clear form for next quiz
            // Optionally navigate back: backToDashboard();

        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Input", "Error creating quiz: " + e.getMessage());
        } catch (RuntimeException e) {
            AlertMessage.showError("Save Error", "Failed to save the new quiz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while saving the quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes the input fields for a single question (specified by index).
     * Validates the input and creates a Question object if valid.
     * Shows warning messages directly if validation fails.
     *
     * @param index The index of the question (0 for Q1, 1 for Q2, etc.).
     * @return A Question object if input is valid, otherwise null.
     */
    private Question processQuestionInput(int index) {
        int questionNumber = index + 1; // For user messages (1-based)

        // Get UI elements for this question
        TextField qText = questionTextFields.get(index);
        List<TextField> opts = optionTextFields.get(index);
        ToggleGroup group = toggleGroups.get(index);
        List<RadioButton> radios = correctRadioButtons.get(index);

        // --- Validate Text Fields ---
        String questionTextStr = qText.getText().trim();
        if (questionTextStr.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter the text for Question " + questionNumber + ".");
            qText.requestFocus();
            return null;
        }

        String[] optionTexts = new String[4]; // Assuming 4 options
        for (int j = 0; j < 4; j++) {
            optionTexts[j] = opts.get(j).getText().trim();
            if (optionTexts[j].isEmpty()) {
                AlertMessage.showWarning("Input Required", "Please enter the text for Option " + (char)('A' + j) + " of Question " + questionNumber + ".");
                opts.get(j).requestFocus();
                return null;
            }
        }

        // --- Validate Correct Answer Selection ---
        Toggle selectedToggle = group.getSelectedToggle();
        if (selectedToggle == null) {
            AlertMessage.showWarning("Input Required", "Please select the correct answer for Question " + questionNumber + ".");
            // Focus the first radio button as a hint
            radios.get(0).requestFocus();
            return null;
        }

        // --- Determine Correct Answer Text ---
        String correctAnswerText = null;
        RadioButton selectedRadio = (RadioButton) selectedToggle;
        // Find which option TextField corresponds to the selected RadioButton
        for(int j=0; j<4; j++){
            if(selectedRadio == radios.get(j)){
                correctAnswerText = optionTexts[j]; // The text from the corresponding TextField
                break;
            }
        }

        if (correctAnswerText == null) {
            // This should not happen if FXML setup and lists are correct
            AlertMessage.showError("Internal Error", "Could not determine correct answer text for Question " + questionNumber + ".");
            return null;
        }


        // --- Create Question Object ---
        try {
            return new Question(questionTextStr, optionTexts, correctAnswerText);
        } catch (IllegalArgumentException e) {
            // Catch errors from Question constructor (e.g., correct answer mismatch - should be caught earlier now)
            AlertMessage.showError("Validation Error", "Question " + questionNumber + ": " + e.getMessage());
            return null;
        }
    }


    /** Clears all input fields and resets selections in the form. */
    private void clearFields() {
        quizTitleField.clear();
        associatedLessonLabel.setText("Create New Quiz"); // Reset label

        for (int i = 0; i < 4; i++) {
            questionTextFields.get(i).clear();
            for (TextField optField : optionTextFields.get(i)) {
                optField.clear();
            }
            Toggle selected = toggleGroups.get(i).getSelectedToggle();
            if (selected != null) {
                selected.setSelected(false);
            }
        }
        // Reset TitledPane expansion
        question1Pane.setExpanded(true);
        question2Pane.setExpanded(false);
        question3Pane.setExpanded(false);
        question4Pane.setExpanded(false);

        // Reset associated lesson context
        associatedLesson = null;
        associatedLessonId = null;

        // Reset Level ComboBox if added
        // levelComboBox.setValue(ProficiencyLevel.BEGINNER);

        System.out.println("Quiz creation form cleared.");
    }


    /** Handles the action of navigating back to the Teacher Dashboard. */
    // Inside CreateQuizController.java
    @FXML
    private void backToDashboard() {
        // Should always be a teacher here
        if (currentTeacher != null) {
            Object controller = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
            if (controller instanceof TeacherSceneController) {
                // ** RE-SET DATA ON RETURN **
                ((TeacherSceneController) controller).setTeacherData(currentTeacher);
            } else {
                System.err.println("Error: Could not get TeacherSceneController to refresh dashboard.");
            }
        } else {
            // Fallback or handle error - maybe go to login?
            System.err.println("Error: currentTeacher is null in CreateQuizController. Navigating to Login.");
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }

    // Helper to get TitledPane by index (optional)
    // private TitledPane getTitledPaneByIndex(int index) {
    //     switch (index) {
    //         case 0: return question1Pane;
    //         case 1: return question2Pane;
    //         case 2: return question3Pane;
    //         case 3: return question4Pane;
    //         default: return null;
    //     }
    // }
}