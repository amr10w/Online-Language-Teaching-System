// src/main/java/controllers/TeacherSceneController.java
package controllers;

import Main.*; // Import Main package classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Correct import
import javafx.scene.image.Image; // For teacher image
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;


/**
 * Controller for the 'teacherScene.fxml' view (Teacher Dashboard).
 * Displays teacher stats, created lessons, and provides actions like creating content.
 */
public class TeacherSceneController {

    // --- FXML Fields ---
    @FXML private ImageView teacherImageView; // Optional: Display teacher's avatar
    @FXML private Label teacherNameLabel;
    @FXML private Label languageLabel;
    @FXML private Label lessonsCreatedValue;
    @FXML private Label studentsValue;      // Placeholder for student count
    @FXML private Label balanceValue;       // Placeholder for balance
    @FXML private Button createLessonBtn;
    @FXML private Button createQuizBtn;
    @FXML private Button logoutButton;       // Ensure fx:id exists for logout button

    // TableView for Lessons
    @FXML private TableView<Lesson> lessonsTableView; // Specify Lesson type
    @FXML private TableColumn<Lesson, String> lessonTitleCol;
    @FXML private TableColumn<Lesson, String> lessonIdCol;
    @FXML private TableColumn<Lesson, String> lessonLevelCol; // Use String for level display

    // --- Controller State ---
    private Teacher currentTeacher;
    private ObservableList<Lesson> teacherLessonsList = FXCollections.observableArrayList(); // List bound to TableView

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Sets up the TableView columns and initial UI state.
     */
    @FXML
    public void initialize() {
        System.out.println("TeacherSceneController initializing...");
        // Set initial placeholder text
        teacherNameLabel.setText("Loading...");
        languageLabel.setText("");
        lessonsCreatedValue.setText("0");
        studentsValue.setText("0");   // Placeholder
        balanceValue.setText("$0.00"); // Placeholder

        // --- Configure TableView Columns ---
        // Associate columns with Lesson properties using PropertyValueFactory
        // The string argument MUST match the getter method name in the Lesson class
        // (e.g., "title" corresponds to getTitle(), "lessonId" to getLessonId())
        lessonTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        lessonIdCol.setCellValueFactory(new PropertyValueFactory<>("lessonId"));
        // For the level, we need to get the String representation from ProficiencyLevel
        // Assuming Lesson has a getLevel() method that returns the String.
        lessonLevelCol.setCellValueFactory(new PropertyValueFactory<>("level")); // Matches getLevel() in Lesson

        // Set table placeholder for when no lessons are loaded/created
        lessonsTableView.setPlaceholder(new Label("Loading lessons..."));

        // Bind the ObservableList to the TableView
        lessonsTableView.setItems(teacherLessonsList);

        // Optional: Add double-click listener to edit/view lesson
        lessonsTableView.setRowFactory(tv -> {
            TableRow<Lesson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Lesson clickedLesson = row.getItem();
                    System.out.println("Double-clicked on lesson: " + clickedLesson.getTitle());
                    viewOrEditLesson(clickedLesson); // Call method to handle double-click
                }
            });
            return row;
        });

        // Optional: Set default teacher image
        setDefaultTeacherImage();
    }

    /** Sets a default image for the teacher view. */
    private void setDefaultTeacherImage() {
        try {
            // Load default image from resources
            URL imgUrl = getClass().getResource("/images/person_icon.png"); // Path to a default icon
            if (imgUrl != null) {
                teacherImageView.setImage(new Image(imgUrl.toExternalForm()));
            } else {
                System.err.println("Default teacher image not found.");
            }
        } catch (Exception e) {
            System.err.println("Error loading default teacher image: " + e.getMessage());
        }
    }


    /**
     * Sets the teacher data after login/signup.
     * Populates the dashboard with teacher-specific information and loads their created lessons.
     *
     * @param teacher The logged-in Teacher object.
     */
    public void setTeacherData(Teacher teacher) {
        Objects.requireNonNull(teacher, "Teacher object cannot be null for setTeacherData");
        this.currentTeacher = teacher;
        System.out.println("Setting data for teacher: " + currentTeacher.getUsername());

        // Populate header labels
        teacherNameLabel.setText(teacher.getUsername());
        languageLabel.setText(teacher.getLanguage());
        balanceValue.setText(String.format("$%.2f", teacher.getBalance())); // Format balance

        // TODO: Load teacher-specific image if available
        // loadImageForTeacher(teacher.getUsername());

        // Load the list of lessons created by this specific teacher
        loadTeacherLessons();

        // Update stats based on loaded data
        updateTeacherStats();
    }

    /** Loads lessons created by the currentTeacher. */
    private void loadTeacherLessons() {
        teacherLessonsList.clear(); // Clear previous lessons
        if (currentTeacher == null) {
            System.err.println("Cannot load teacher lessons: currentTeacher is null.");
            lessonsTableView.setPlaceholder(new Label("Teacher data not loaded."));
            return;
        }

        String lessonsDirPath = ApplicationManager.getLessonsDirectory();
        if (lessonsDirPath == null) {
            System.err.println("Lessons directory path is null. Cannot load lessons.");
            lessonsTableView.setPlaceholder(new Label("Error: Lessons directory path not set."));
            return;
        }

        Path dirPath = Paths.get(lessonsDirPath);
        System.out.println("Scanning for lessons created by " + currentTeacher.getUsername() + " in " + dirPath.toAbsolutePath());
        lessonsTableView.setPlaceholder(new Label("Scanning for your lessons..."));


        if (!Files.isDirectory(dirPath)) {
            System.err.println("Lessons path is not a directory: " + dirPath.toAbsolutePath());
            lessonsTableView.setPlaceholder(new Label("Error: Lessons folder not found."));
            return;
        }


        // Use try-with-resources for efficient file streaming
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream
                    .filter(path -> path.getFileName().toString().toLowerCase().startsWith("lesson_l") && path.toString().toLowerCase().endsWith(".txt"))
                    .map(path -> { // Load each potential lesson file
                        try {
                            // Create Lesson object - constructor loads data
                            Lesson lesson = new Lesson(path.toAbsolutePath().toString());
                            // Check if load was successful and if teacher ID matches
                            if (!"L_DEFAULT".equals(lesson.getLessonId()) &&
                                    currentTeacher.getID().equals(lesson.getTeacherId())) // Critical check!
                            {
                                return lesson;
                            } else {
                                // Log mismatch or load failure but don't stop the stream
                                if (!currentTeacher.getID().equals(lesson.getTeacherId())) {
                                    // System.out.println("Skipping lesson " + lesson.getLessonId() + ": Belongs to teacher " + lesson.getTeacherId());
                                } else {
                                    System.err.println("Skipping lesson file due to load failure: " + path.getFileName());
                                }
                                return null; // Indicate failure to load/match this file
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing lesson file " + path.getFileName() + ": " + e.getMessage());
                            return null; // Filter out lessons that cause errors
                        }
                    })
                    .filter(Objects::nonNull) // Remove nulls resulting from errors or non-matching teacher IDs
                    .sorted() // Sort lessons using Lesson's compareTo method
                    .forEach(teacherLessonsList::add); // Add matching lessons to the list bound to the TableView

        } catch (IOException e) {
            System.err.println("Error reading lessons directory (" + lessonsDirPath + "): " + e.getMessage());
            lessonsTableView.setPlaceholder(new Label("Error reading lessons folder."));
            e.printStackTrace();
        }

        // Update placeholder text based on results
        if (teacherLessonsList.isEmpty()) {
            lessonsTableView.setPlaceholder(new Label("You haven't created any lessons yet."));
        }
        System.out.println("Finished loading lessons for teacher " + currentTeacher.getUsername() + ". Count: " + teacherLessonsList.size());
    }


    /** Updates the statistics labels on the dashboard. */
    private void updateTeacherStats() {
        if (currentTeacher == null) return;
        lessonsCreatedValue.setText(String.valueOf(teacherLessonsList.size()));

        // TODO: Implement logic to count students (e.g., assigned to this teacher or learning their language)
        List<Student> allStudents = ApplicationManager.getStudents();
        long studentCount = allStudents.stream()
                .filter(s -> currentTeacher.getLanguage().equalsIgnoreCase(s.getLanguage())) // Example: Count students learning the teacher's language
                .count();
        studentsValue.setText(String.valueOf(studentCount));
        // balanceValue update logic (if any)
    }


    // --- Action Handlers ---

    /** Navigates to the Create Lesson screen. */
    @FXML
    private void createLesson() {
        System.out.println("Navigating to Create Lesson screen...");
        // The CreateLessonController should fetch the currentTeacher from LoginManager
        SceneManager.switchToScene(SceneManager.CREATE_LESSON);
    }

    /** Navigates to the Create Quiz screen, possibly passing selected lesson context. */
    @FXML
    private void createQuiz() {
        Lesson selectedLesson = lessonsTableView.getSelectionModel().getSelectedItem();
        String lessonIdForQuiz = null;
        String contextMessage = "Navigating to Create Quiz screen ";

        if (selectedLesson != null) {
            lessonIdForQuiz = selectedLesson.getLessonId();
            contextMessage += "for lesson: " + selectedLesson.getTitle() + " (ID: " + lessonIdForQuiz + ")";
        } else {
            contextMessage += "(standalone quiz).";
        }
        System.out.println(contextMessage);


        // Switch scene and pass context to the CreateQuizController
        Object controller = SceneManager.switchToScene(SceneManager.CREATE_QUIZ);
        if (controller instanceof CreateQuizController) {
            // Pass the current teacher and the optional lesson ID
            ((CreateQuizController) controller).setQuizContext(currentTeacher, lessonIdForQuiz);
        } else {
            System.err.println("Failed to get CreateQuizController instance after scene switch.");
            AlertMessage.showError("Navigation Error", "Could not load the create quiz screen properly.");
        }
    }

    /** Handles double-click on a lesson row (placeholder for view/edit). */
    private void viewOrEditLesson(Lesson lesson) {
        if (lesson == null) return;
        // Option 1: Navigate to Lesson view (like student view)
        // Option 2: Open an editing interface (requires more UI/logic)

        // For now, let's just navigate to the standard lesson view
        System.out.println("Teacher viewing lesson: " + lesson.getTitle());
        Object controller = SceneManager.switchToScene(SceneManager.LESSON_VIEW);
        if (controller instanceof LessonController) {
            ((LessonController) controller).setLessonData(lesson); // Pass the selected lesson object
        } else {
            System.err.println("Failed to get LessonController instance for viewing.");
            AlertMessage.showError("Navigation Error", "Could not load the lesson view properly.");
        }

        // Placeholder for actual edit functionality:
        // boolean confirmEdit = AlertMessage.showConfirmation("Edit Lesson", "Do you want to edit '" + lesson.getTitle() + "'?");
        // if (confirmEdit) {
        //     System.out.println("Edit action for lesson " + lesson.getLessonId() + " not implemented.");
        //     // Navigate to an edit scene or open a dialog
        // }
    }

    /** Handles the logout action. */
    @FXML
    private void logout() {
        boolean confirm = AlertMessage.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirm) {
            System.out.println("Logging out teacher: " + (currentTeacher != null ? currentTeacher.getUsername() : "Unknown"));
            LoginManager.logout(); // Clear the selected user session
            SceneManager.switchToScene(SceneManager.LOGIN); // Go back to login screen
        }
    }
}