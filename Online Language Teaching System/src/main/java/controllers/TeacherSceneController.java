package controllers;

import Main.*; // Import necessary classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // For TableView

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TeacherSceneController {

    @FXML private Label teacherNameLabel;
    @FXML private Label languageLabel;
    @FXML private Label lessonsCreatedValue;
    @FXML private Label studentsValue; // Needs logic to count students for this teacher/language
    @FXML private Label balanceValue; // fx:id added
    @FXML private TableView<Lesson> lessonsTableView; // Specify Lesson type
    @FXML private TableColumn<Lesson, String> lessonTitleCol; // Column for title
    @FXML private TableColumn<Lesson, String> lessonIdCol;    // Column for ID
    @FXML private TableColumn<Lesson, String> lessonLevelCol; // Column for Level

    // Add other columns as needed (Date Created, Students Completed requires more data)

    private Teacher currentTeacher;
    private ObservableList<Lesson> teacherLessons = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initial state
        teacherNameLabel.setText("Loading...");
        languageLabel.setText("");
        lessonsCreatedValue.setText("0");
        studentsValue.setText("0"); // Placeholder
        balanceValue.setText("$0.00"); // Placeholder

        // Setup TableView columns
        lessonTitleCol.setCellValueFactory(new PropertyValueFactory<>("title")); // Matches Lesson.getTitle()
        lessonIdCol.setCellValueFactory(new PropertyValueFactory<>("lessonId")); // Matches Lesson.getLessonId()
        //lessonLevelCol.setCellValueFactory(cellData -> cellData.getValue().getProficiencyLevel().getLevelProperty()); // Get level string
        // Make sure ProficiencyLevel has a method like:
        // public javafx.beans.property.StringProperty getLevelProperty() { return new javafx.beans.property.SimpleStringProperty(getLevel()); }


        lessonsTableView.setPlaceholder(new Label("No lessons created yet or failed to load."));
        lessonsTableView.setItems(teacherLessons); // Link data list to table

        // Add double-click listener to edit lesson (optional)
        lessonsTableView.setRowFactory(tv -> {
            TableRow<Lesson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Lesson rowData = row.getItem();
                    editSelectedLesson(rowData);
                }
            });
            return row;
        });
    }

    // Method to receive teacher data from LoginController
    public void setTeacherData(Teacher teacher) {
        if (teacher == null) {
            AlertMessage.showError("Error", "Failed to load teacher data.");
            SceneManager.switchToScene(SceneManager.LOGIN);
            return;
        }
        this.currentTeacher = teacher;

        // Populate UI
        teacherNameLabel.setText(teacher.getUsername());
        languageLabel.setText(teacher.getLanguage());
        balanceValue.setText(String.format("$%.2f", teacher.getBalance())); // Format balance

        // Load lessons created by this teacher
        loadTeacherLessons();

        // Update stats based on loaded data
        lessonsCreatedValue.setText(String.valueOf(teacherLessons.size()));
        studentsValue.setText("N/A"); // TODO: Implement student counting logic

    }

    private void loadTeacherLessons() {
        teacherLessons.clear(); // Clear previous data
        if (currentTeacher == null) return;

        // Option 1: Use the list stored in the Teacher object (if persisted)
        List<String> createdIds = currentTeacher.getCreatedLessonIds();
        System.out.println("Teacher " + currentTeacher.getUsername() + " has created lesson IDs: " + createdIds);


        // Option 2: Scan the lessons directory and load files (more robust if teacher object isn't saved)
        File lessonsDir = new File(ApplicationManager.getLessonsDirectory());
        if (!lessonsDir.exists() || !lessonsDir.isDirectory()) {
            System.err.println("Lessons directory not found: " + lessonsDir.getAbsolutePath());
            lessonsTableView.setPlaceholder(new Label("Lessons folder not found."));
            return;
        }

        File[] lessonFiles = lessonsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt") && name.toLowerCase().startsWith("lesson_"));

        if (lessonFiles == null || lessonFiles.length == 0) {
            lessonsTableView.setPlaceholder(new Label("No lesson files found."));
            return;
        }

        for (File file : lessonFiles) {
            try {
                Lesson lesson = new Lesson(file.getAbsolutePath());
                // *** Crucial Part: Need to know WHICH teacher created this lesson. ***
                // This requires the teacher's ID to be saved *inside* the lesson file.
                // Or, rely solely on the teacher's internal list `createdLessonIds`.

                // Using Teacher's internal list (Simpler if list is reliable):
                if (!lesson.getLessonId().equals("L_DEFAULT") && createdIds.contains(lesson.getLessonId())) {
                    teacherLessons.add(lesson);
                    System.out.println("Added lesson to view: " + lesson.getTitle());
                }

                // Using data from file (Requires teacherId in lesson file):
                // String lessonTeacherId = lesson.getTeacherId(); // Need getTeacherId() method in Lesson
                // if (!lesson.getLessonId().equals("L_DEFAULT") && currentTeacher.getID().equals(lessonTeacherId)) {
                //     teacherLessons.add(lesson);
                // }

            } catch (Exception e) {
                System.err.println("Error loading lesson " + file.getName() + " for teacher view: " + e.getMessage());
            }
        }

        lessonsTableView.refresh(); // Refresh table view after loading
        lessonsCreatedValue.setText(String.valueOf(teacherLessons.size())); // Update count


        if (teacherLessons.isEmpty()) {
            lessonsTableView.setPlaceholder(new Label("You haven't created any lessons yet."));
        }
    }


    @FXML
    private void createLesson() {
        System.out.println("Navigating to Create Lesson screen...");
        Object controller = SceneManager.switchToScene(SceneManager.CREATE_LESSON);
        // No data needs to be passed here as CreateLessonController gets teacher from LoginManager
        // if (controller instanceof CreateLessonController) {
        //     ((CreateLessonController) controller).setTeacher(currentTeacher); // Not needed if using LoginManager
        // }
    }

    @FXML
    private void createQuiz() {
        // Decide if quiz is standalone or associated with a selected lesson
        Lesson selectedLesson = lessonsTableView.getSelectionModel().getSelectedItem();
        String lessonIdForQuiz = null;
        if (selectedLesson != null) {
            lessonIdForQuiz = selectedLesson.getLessonId();
            System.out.println("Navigating to Create Quiz screen for lesson: " + selectedLesson.getTitle());
        } else {
            System.out.println("Navigating to Create Quiz screen (standalone).");
            // Optionally ask user if they want to create a standalone quiz
        }

        Object controller = SceneManager.switchToScene(SceneManager.CREATE_QUIZ);
        if (controller instanceof CreateQuizController) {
            // Pass the teacher and optionally the selected lesson ID
            ((CreateQuizController) controller).setQuizContext(currentTeacher, lessonIdForQuiz);
        }
    }

    private void editSelectedLesson(Lesson lesson) {
        if (lesson == null) return;
        AlertMessage.showInformation("Not Implemented", "Editing lesson '" + lesson.getTitle() + "' is not yet implemented.");
        // 1. Navigate to CreateLesson scene (or a dedicated EditLesson scene)
        // 2. Pass the Lesson object to the controller
        // 3. The controller populates fields with existing data
        // 4. Save button updates the existing file instead of creating a new one
    }


    @FXML
    private void logout() {
        LoginManager.logout();
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }
}
