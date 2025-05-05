package controllers;

import Main.*; // Import necessary classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Ensure this import exists

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TeacherSceneController {

    // --- FXML Fields ---
    @FXML private Label teacherNameLabel;
    @FXML private Label languageLabel;
    @FXML private Label lessonsCreatedValue;
    @FXML private Label studentsValue;
    @FXML private Label balanceValue;
    @FXML private TableView<Lesson> lessonsTableView; // Should be Lesson type
    @FXML private TableColumn<Lesson, String> lessonTitleCol;
    @FXML private TableColumn<Lesson, String> lessonIdCol;
    @FXML private TableColumn<Lesson, String> lessonLevelCol; // Assumes Lesson has getLevel() or levelProperty() returning String

    // --- Instance Variables ---
    private Teacher currentTeacher;
    private ObservableList<Lesson> teacherLessons = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set initial label text
        teacherNameLabel.setText("Loading...");
        languageLabel.setText("");
        lessonsCreatedValue.setText("0");
        studentsValue.setText("0"); // Placeholder - requires logic
        balanceValue.setText("$0.00"); // Placeholder

        // **Setup TableView Columns Programmatically**
        lessonTitleCol.setCellValueFactory(new PropertyValueFactory<>("title")); // Matches Lesson.getTitle()
        lessonIdCol.setCellValueFactory(new PropertyValueFactory<>("lessonId")); // Matches Lesson.getLessonId()
        lessonLevelCol.setCellValueFactory(new PropertyValueFactory<>("level")); // Matches Lesson.getLevel()

        // **Set Column Resize Policy Programmatically**
        lessonsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set placeholder text for empty table
        lessonsTableView.setPlaceholder(new Label("You haven't created any lessons yet."));

        // Link the observable list to the table
        lessonsTableView.setItems(teacherLessons);

        // Optional: Add double-click listener to edit lesson
        lessonsTableView.setRowFactory(tv -> {
            TableRow<Lesson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Lesson rowData = row.getItem();
                    editSelectedLesson(rowData); // Call edit method
                }
            });
            return row;
        });
    }

    // Method to receive teacher data from LoginController or SignupController
    public void setTeacherData(Teacher teacher) {
        if (teacher == null) {
            AlertMessage.showError("Error", "Failed to load teacher data.");
            SceneManager.switchToScene(SceneManager.LOGIN); // Go back to login on error
            return;
        }
        this.currentTeacher = teacher;

        // Populate UI elements with teacher data
        teacherNameLabel.setText(teacher.getUsername());
        languageLabel.setText(teacher.getLanguage()); // Assuming Teacher has getLanguage()
        balanceValue.setText(String.format("$%.2f", teacher.getBalance())); // Assuming Teacher has getBalance()

        // Load lessons created specifically by this teacher
        loadTeacherLessons();

        // Update stats based on loaded data (example)
        lessonsCreatedValue.setText(String.valueOf(teacherLessons.size()));
        studentsValue.setText("N/A"); // TODO: Implement logic to count students if needed
    }

    private void loadTeacherLessons() {
        teacherLessons.clear(); // Start with an empty list
        if (currentTeacher == null) {
            System.err.println("Cannot load lessons, currentTeacher is null.");
            return;
        }

        List<String> createdIds = currentTeacher.getCreatedLessonIds();
        if (createdIds == null) {
            System.err.println("Teacher's createdLessonIds list is null.");
            createdIds = new ArrayList<>(); // Avoid NullPointerException later
        }
        System.out.println("Loading lessons for teacher: " + currentTeacher.getUsername() + " with IDs: " + createdIds);


        File lessonsDir = new File(ApplicationManager.getLessonsDirectory());
        if (!lessonsDir.exists() || !lessonsDir.isDirectory()) {
            System.err.println("Lessons directory not found or invalid: " + lessonsDir.getAbsolutePath());
            lessonsTableView.setPlaceholder(new Label("Lessons folder not found."));
            return;
        }

        File[] lessonFiles = lessonsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt") && name.toLowerCase().startsWith("lesson_"));

        if (lessonFiles == null || lessonFiles.length == 0) {
            System.out.println("No lesson files found in the directory.");
            // Placeholder is already set in initialize, no need to set again unless empty list found
            return;
        }

        System.out.println("Found " + lessonFiles.length + " potential lesson files.");
        for (File file : lessonFiles) {
            try {
                // Load lesson using constructor that takes file path
                Lesson lesson = new Lesson(file.getAbsolutePath());

                // Check if loading was successful (Lesson constructor should handle errors gracefully)
                if (lesson.getLessonId() == null || lesson.getLessonId().equals("L_DEFAULT")) {
                    System.err.println("Skipping invalid or default lesson loaded from: " + file.getName());
                    continue;
                }

                // **Filter based on the teacher's list of created IDs**
                if (createdIds.contains(lesson.getLessonId())) {
                    teacherLessons.add(lesson);
                    System.out.println("Added lesson to table: " + lesson.getTitle() + " (ID: " + lesson.getLessonId() + ")");
                } else {
                    // Optional: Log lessons not belonging to this teacher
                    // System.out.println("Skipping lesson " + lesson.getLessonId() + ", not created by " + currentTeacher.getUsername());
                }

            } catch (Exception e) {
                System.err.println("Error loading lesson from file " + file.getName() + ": " + e.getMessage());
                e.printStackTrace(); // Log the full error for debugging
            }
        }

        // No need to call refresh(), the ObservableList updates the TableView automatically.
        lessonsCreatedValue.setText(String.valueOf(teacherLessons.size())); // Update count after loading

        // Update placeholder text if list is empty *after* trying to load
        if (teacherLessons.isEmpty()) {
            lessonsTableView.setPlaceholder(new Label("You haven't created any lessons yet."));
        }
        System.out.println("Finished loading teacher lessons. Count: " + teacherLessons.size());
    }


    // --- Action Handlers ---

    @FXML
    private void createLesson() {
        System.out.println("Navigating to Create Lesson screen...");
        // CreateLessonController should get the current teacher from LoginManager
        SceneManager.switchToScene(SceneManager.CREATE_LESSON);
    }

    @FXML
    private void createQuiz() {
        Lesson selectedLesson = lessonsTableView.getSelectionModel().getSelectedItem();
        String lessonIdForQuiz = null;

        if (selectedLesson != null) {
            lessonIdForQuiz = selectedLesson.getLessonId();
            System.out.println("Navigating to Create Quiz screen for lesson: " + selectedLesson.getTitle());
        } else {
            System.out.println("Navigating to Create Quiz screen (standalone).");
            // Optional: Could show a dialog asking if they meant to select a lesson first
        }

        Object controller = SceneManager.switchToScene(SceneManager.CREATE_QUIZ);
        if (controller instanceof CreateQuizController) {
            // Pass the current teacher and the selected lesson ID (which might be null)
            ((CreateQuizController) controller).setQuizContext(currentTeacher, lessonIdForQuiz);
        } else {
            AlertMessage.showError("Navigation Error", "Could not get CreateQuizController instance.");
        }
    }

    private void editSelectedLesson(Lesson lesson) {
        if (lesson == null) return;
        // TODO: Implement lesson editing functionality
        // 1. Navigate to CreateLesson scene (or a dedicated EditLesson scene)
        // 2. Pass the Lesson object to its controller
        // 3. Populate fields in that controller
        // 4. Modify save logic to overwrite the existing file
        AlertMessage.showInformation("Not Implemented", "Editing lesson '" + lesson.getTitle() + "' is not yet implemented.");
    }


    @FXML
    private void logout() {
        LoginManager.logout(); // Clear logged-in user state
        SceneManager.switchToScene(SceneManager.MAIN_SCENE); // Go to main/welcome screen
    }
}