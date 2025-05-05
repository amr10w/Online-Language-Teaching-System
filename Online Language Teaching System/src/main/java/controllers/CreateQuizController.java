package controllers;

import Main.*; // Import necessary classes
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import ToggleGroup etc.

import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {

    // --- Teacher/Lesson Context ---
    private Teacher currentTeacher;
    private String associatedLessonId;

    // --- UI Elements ---
    @FXML private Label associatedLessonLabel;
    @FXML private TextField quizTitleField;

    // Question 1 Fields
    @FXML private TextField q1Text;
    @FXML private TextField q1OptA;
    @FXML private TextField q1OptB;
    @FXML private TextField q1OptC;
    @FXML private TextField q1OptD;
    @FXML private RadioButton q1CorrectA;
    @FXML private RadioButton q1CorrectB;
    @FXML private RadioButton q1CorrectC;
    @FXML private RadioButton q1CorrectD;
    private ToggleGroup correctAnswerGroup1;

    // Question 2 Fields
    @FXML private TextField q2Text;
    @FXML private TextField q2OptA;
    @FXML private TextField q2OptB;
    @FXML private TextField q2OptC;
    @FXML private TextField q2OptD;
    @FXML private RadioButton q2CorrectA;
    @FXML private RadioButton q2CorrectB;
    @FXML private RadioButton q2CorrectC;
    @FXML private RadioButton q2CorrectD;
    private ToggleGroup correctAnswerGroup2;

    // Question 3 Fields
    @FXML private TextField q3Text;
    @FXML private TextField q3OptA;
    @FXML private TextField q3OptB;
    @FXML private TextField q3OptC;
    @FXML private TextField q3OptD;
    @FXML private RadioButton q3CorrectA;
    @FXML private RadioButton q3CorrectB;
    @FXML private RadioButton q3CorrectC;
    @FXML private RadioButton q3CorrectD;
    private ToggleGroup correctAnswerGroup3;

    // Question 4 Fields
    @FXML private TextField q4Text;
    @FXML private TextField q4OptA;
    @FXML private TextField q4OptB;
    @FXML private TextField q4OptC;
    @FXML private TextField q4OptD;
    @FXML private RadioButton q4CorrectA;
    @FXML private RadioButton q4CorrectB;
    @FXML private RadioButton q4CorrectC;
    @FXML private RadioButton q4CorrectD;
    private ToggleGroup correctAnswerGroup4;


    // Method called by TeacherSceneController to pass context
    public void setQuizContext(Teacher teacher, String lessonId) {
        this.currentTeacher = teacher;
        this.associatedLessonId = lessonId;

        if (lessonId != null) {
            associatedLessonLabel.setText("Quiz for Lesson ID: " + lessonId);
        } else {
            associatedLessonLabel.setText("Creating Standalone Quiz");
        }
        initializeToggleGroups(); // Ensure groups are set after context is received
    }

    // Called automatically after FXML fields are injected
    @FXML
    public void initialize() {
        initializeToggleGroups(); // Initialize groups when the FXML is loaded
    }

    private void initializeToggleGroups() {
        // Prevent NullPointerException if called before FXML injection
        if (q1CorrectA == null) return;

        if (correctAnswerGroup1 == null) correctAnswerGroup1 = new ToggleGroup();
        q1CorrectA.setToggleGroup(correctAnswerGroup1);
        q1CorrectB.setToggleGroup(correctAnswerGroup1);
        q1CorrectC.setToggleGroup(correctAnswerGroup1);
        q1CorrectD.setToggleGroup(correctAnswerGroup1);

        if (correctAnswerGroup2 == null) correctAnswerGroup2 = new ToggleGroup();
        q2CorrectA.setToggleGroup(correctAnswerGroup2);
        q2CorrectB.setToggleGroup(correctAnswerGroup2);
        q2CorrectC.setToggleGroup(correctAnswerGroup2);
        q2CorrectD.setToggleGroup(correctAnswerGroup2);

        if (correctAnswerGroup3 == null) correctAnswerGroup3 = new ToggleGroup();
        q3CorrectA.setToggleGroup(correctAnswerGroup3);
        q3CorrectB.setToggleGroup(correctAnswerGroup3);
        q3CorrectC.setToggleGroup(correctAnswerGroup3);
        q3CorrectD.setToggleGroup(correctAnswerGroup3);

        if (correctAnswerGroup4 == null) correctAnswerGroup4 = new ToggleGroup();
        q4CorrectA.setToggleGroup(correctAnswerGroup4);
        q4CorrectB.setToggleGroup(correctAnswerGroup4);
        q4CorrectC.setToggleGroup(correctAnswerGroup4);
        q4CorrectD.setToggleGroup(correctAnswerGroup4);
    }

    @FXML
    private void saveQuiz() {
        if (currentTeacher == null) {
            AlertMessage.showError("Error", "Cannot save quiz. Teacher information missing.");
            return;
        }

        String quizTitle = quizTitleField.getText().trim();
        if (quizTitle.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a Quiz Title.");
            quizTitleField.requestFocus();
            return;
        }

        List<Question> questions = new ArrayList<>();
        boolean valid = true;

        valid &= addQuestionToList(questions, q1Text, q1OptA, q1OptB, q1OptC, q1OptD, correctAnswerGroup1, 1);
        valid &= addQuestionToList(questions, q2Text, q2OptA, q2OptB, q2OptC, q2OptD, correctAnswerGroup2, 2);
        valid &= addQuestionToList(questions, q3Text, q3OptA, q3OptB, q3OptC, q3OptD, correctAnswerGroup3, 3);
        valid &= addQuestionToList(questions, q4Text, q4OptA, q4OptB, q4OptC, q4OptD, correctAnswerGroup4, 4);

        if (!valid) {
            return;
        }
        if (questions.size() < 4) {
            AlertMessage.showWarning("Incomplete Quiz", "Please ensure all 4 questions are filled out correctly.");
            return;
        }

        try {
            Quiz newQuiz = new Quiz(quizTitle, questions, currentTeacher.getID(), associatedLessonId);
            if (newQuiz.getQuizId() != null && !newQuiz.getQuizId().equals("Q_DEFAULT")) {
                AlertMessage.showInformation("Quiz Saved", "Quiz '" + newQuiz.getTitle() + "' (ID: " + newQuiz.getQuizId() + ") saved successfully.");
                clearFields();
            } else {
                AlertMessage.showError("Save Error", "Failed to save the quiz. Check logs or Quiz constructor.");
            }
        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Input", "Error creating quiz: " + e.getMessage());
        } catch (Exception e) {
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while saving the quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean addQuestionToList(List<Question> list, TextField qText,
                                      TextField optA, TextField optB, TextField optC, TextField optD,
                                      ToggleGroup group, int questionNumber) {
        // Ensure ToggleGroup exists before using it
        if (group == null) {
            AlertMessage.showError("Internal Error", "ToggleGroup not initialized for Question " + questionNumber);
            return false;
        }

        String questionText = qText.getText().trim();
        String optionA = optA.getText().trim();
        String optionB = optB.getText().trim();
        String optionC = optC.getText().trim();
        String optionD = optD.getText().trim();

        if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please fill in the question and all four options for Question " + questionNumber + ".");
            qText.requestFocus();
            return false;
        }

        RadioButton selectedRadio = (RadioButton) group.getSelectedToggle();
        if (selectedRadio == null) {
            AlertMessage.showWarning("Input Required", "Please select the correct answer for Question " + questionNumber + ".");
            return false;
        }

        String correctAnswer = "";
        if (selectedRadio.equals(q1CorrectA) || selectedRadio.equals(q2CorrectA) || selectedRadio.equals(q3CorrectA) || selectedRadio.equals(q4CorrectA)) correctAnswer = optionA;
        else if (selectedRadio.equals(q1CorrectB) || selectedRadio.equals(q2CorrectB) || selectedRadio.equals(q3CorrectB) || selectedRadio.equals(q4CorrectB)) correctAnswer = optionB;
        else if (selectedRadio.equals(q1CorrectC) || selectedRadio.equals(q2CorrectC) || selectedRadio.equals(q3CorrectC) || selectedRadio.equals(q4CorrectC)) correctAnswer = optionC;
        else if (selectedRadio.equals(q1CorrectD) || selectedRadio.equals(q2CorrectD) || selectedRadio.equals(q3CorrectD) || selectedRadio.equals(q4CorrectD)) correctAnswer = optionD;
        else {
            AlertMessage.showError("Internal Error", "Could not determine correct answer for Question " + questionNumber + ".");
            return false;
        }


        String[] options = {optionA, optionB, optionC, optionD};
        try {
            list.add(new Question(questionText, options, correctAnswer));
            return true;
        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Validation Error", "Question " + questionNumber + ": " + e.getMessage());
            return false;
        }
    }

    private void clearFields() {
        quizTitleField.clear();

        q1Text.clear(); q1OptA.clear(); q1OptB.clear(); q1OptC.clear(); q1OptD.clear();
        if (correctAnswerGroup1 != null && correctAnswerGroup1.getSelectedToggle() != null) correctAnswerGroup1.getSelectedToggle().setSelected(false);

        q2Text.clear(); q2OptA.clear(); q2OptB.clear(); q2OptC.clear(); q2OptD.clear();
        if (correctAnswerGroup2 != null && correctAnswerGroup2.getSelectedToggle() != null) correctAnswerGroup2.getSelectedToggle().setSelected(false);

        q3Text.clear(); q3OptA.clear(); q3OptB.clear(); q3OptC.clear(); q3OptD.clear();
        if (correctAnswerGroup3 != null && correctAnswerGroup3.getSelectedToggle() != null) correctAnswerGroup3.getSelectedToggle().setSelected(false);

        q4Text.clear(); q4OptA.clear(); q4OptB.clear(); q4OptC.clear(); q4OptD.clear();
        if (correctAnswerGroup4 != null && correctAnswerGroup4.getSelectedToggle() != null) correctAnswerGroup4.getSelectedToggle().setSelected(false);

        associatedLessonLabel.setText("Create Quiz"); // Reset label
    }


    @FXML
    private void backToDashboard() {
        if (currentTeacher != null) {
            SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
        } else {
            SceneManager.switchToScene(SceneManager.LOGIN);
        }
    }
}