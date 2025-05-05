package controllers;

import Main.*; // Import necessary classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import ProgressBar etc.
import javafx.scene.input.MouseEvent; // For ListView click


import java.io.File; // For file operations
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class StudentSceneController {

    @FXML private Label studentNameLabel;
    @FXML private Label languageLabel;
    @FXML private Label progressValue; // For percentage text
    @FXML private Label pointsValue; // Placeholder - need points system
    @FXML private Label rankValue; // Placeholder - need ranking system
    @FXML private Label currentLessonTitle; // Display title of selected/next lesson
    @FXML private Label nextLessonTitle; // Display language (as per FXML) or next lesson title
    @FXML private ListView<Lesson> lessonsListView; // Changed to ListView<Lesson>
    @FXML private ProgressBar progressBar; // fx:id for ProgressBar

    private Student currentStudent;
    private List<Lesson> availableLessons; // Lessons for the student's language

    @FXML
    public void initialize() {
        // Initial state before student data is set
        studentNameLabel.setText("Loading...");
        languageLabel.setText("");
        progressValue.setText("0%");
        progressBar.setProgress(0.0);
        pointsValue.setText("0");
        rankValue.setText("Beginner");
        currentLessonTitle.setText("Select a lesson");
        nextLessonTitle.setText("Available Lessons"); // Changed label text
       /lessonsListView.setPlaceholder(new Label("Loading lessons..."));

        // Add listener to ListView selection
        lessonsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        displayLessonDetails(newValue);
                    }
                }
        );

        // Add double-click listener to start lesson
        lessonsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Lesson selectedLesson = lessonsListView.getSelectionModel().getSelectedItem();
                if (selectedLesson != null) {
                    startSelectedLesson(selectedLesson);
                }
            }
        });
    }

    // Method to receive student data from LoginController
    public void setStudentData(Student student) {
        if (student == null) {
            AlertMessage.showError("Error", "Failed to load student data.");
            // Handle error - maybe navigate back to login
            SceneManager.switchToScene(SceneManager.LOGIN);
            return;
        }
        this.currentStudent = student;

        // Populate UI with student data
        studentNameLabel.setText(student.getUsername());
        languageLabel.setText(student.getLanguage());

        // Load lessons for the student's language
        loadAvailableLessons();

        // Update progress display
        updateProgressDisplay();

        // Update other stats (placeholders)
        pointsValue.setText("0"); // TODO: Implement points system
        rankValue.setText("Beginner"); // TODO: Implement ranking system

        // Set initial lesson display (e.g., first uncompleted lesson)
        Lesson firstLesson = findFirstUncompletedLesson();
        if (firstLesson != null) {
            lessonsListView.getSelectionModel().select(firstLesson);
            displayLessonDetails(firstLesson);
        } else if (!availableLessons.isEmpty()){
            lessonsListView.getSelectionModel().selectFirst();
            displayLessonDetails(availableLessons.get(0));
        } else {
            currentLessonTitle.setText("No lessons available");
            nextLessonTitle.setText("");
        }
    }


    private void loadAvailableLessons() {
        availableLessons = new ArrayList<>();
        String lang = currentStudent.getLanguage();
        if (lang == null || lang.isEmpty() || lang.equals("Unknown")) {
            lessonsListView.setPlaceholder(new Label("Please select a language via profile settings."));
            return;
        }

        // Correct path to lessons directory
        File lessonsDir = new File(ApplicationManager.getLessonsDirectory());
        System.out.println("Attempting to load lessons from: " + lessonsDir.getAbsolutePath()); // Debug path


        if (!lessonsDir.exists() || !lessonsDir.isDirectory()) {
            System.err.println("Lessons directory not found or not a directory: " + lessonsDir.getAbsolutePath());
            lessonsListView.setPlaceholder(new Label("Lessons directory not found."));
            return;
        }

        File[] lessonFiles = lessonsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt") && name.toLowerCase().startsWith("lesson_"));

        if (lessonFiles == null || lessonFiles.length == 0) {
            System.out.println("No lesson files found in directory.");
            lessonsListView.setPlaceholder(new Label("No lessons found for " + lang + "."));
            return;
        }

        System.out.println("Found " + lessonFiles.length + " potential lesson files.");

        for (File file : lessonFiles) {
            try {
                Lesson lesson = new Lesson(file.getAbsolutePath()); // Load lesson using its constructor
                // TODO: Filter lessons by language and proficiency level if needed
                // This requires Lesson objects to store language info, or file naming conventions.
                // Assuming all lessons in the folder are relevant for now.
                if (!lesson.getLessonId().equals("L_DEFAULT")) { // Check if load was successful
                    availableLessons.add(lesson);
                    System.out.println("Loaded lesson: " + lesson.getTitle());
                } else {
                    System.err.println("Skipping default/failed lesson load from: " + file.getName());
                }
            } catch (Exception e) {
                System.err.println("Error loading lesson from file " + file.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Sort lessons (e.g., by ID or title) - Lesson implements Comparable by title
        availableLessons.sort(null); // Use natural ordering

        // Populate ListView
        if (availableLessons.isEmpty()) {
            lessonsListView.setPlaceholder(new Label("No lessons found for " + lang + "."));
        } else {
            // Use a CellFactory for custom display
            lessonsListView.setCellFactory(lv -> new ListCell<Lesson>() {
                @Override
                protected void updateItem(Lesson item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle(""); // Reset style
                    } else {
                        // Show title and completion status
                        String status = currentStudent.isLessonCompleted(item.getLessonId()) ? " [Completed]" : "";
                        setText(item.getTitle() + status);
                        // Optional: Style completed items differently
                        if (currentStudent.isLessonCompleted(item.getLessonId())) {
                            setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
                        } else {
                            setStyle(""); // Reset style for non-completed items
                        }
                    }
                }
            });

            lessonsListView.setItems(FXCollections.observableArrayList(availableLessons));
            System.out.println("ListView populated with " + availableLessons.size() + " lessons.");

        }
    }

    // Find the first lesson in the list that the student hasn't completed
    private Lesson findFirstUncompletedLesson() {
        if (availableLessons == null || currentStudent == null) return null;
        for (Lesson lesson : availableLessons) {
            if (!currentStudent.isLessonCompleted(lesson.getLessonId())) {
                // TODO: Add prerequisite check here
                // if (arePrerequisitesMet(lesson)) {
                return lesson;
                // }
            }
        }
        return null; // All lessons completed or list is empty
    }

    // TODO: Implement prerequisite check
    // private boolean arePrerequisitesMet(Lesson lesson) {
    //     for (String prereqId : lesson.getPrerequisiteLessonIds()) {
    //         if (!prereqId.equalsIgnoreCase("none") && !currentStudent.isLessonCompleted(prereqId)) {
    //             return false; // Prerequisite not met
    //         }
    //     }
    //     return true; // All prerequisites met or none required
    // }


    private void displayLessonDetails(Lesson lesson) {
        if (lesson != null) {
            currentLessonTitle.setText(lesson.getTitle());
            // Optionally show next lesson title if logic exists
            // nextLessonTitle.setText(findNextLessonTitle(lesson));
            nextLessonTitle.setText(currentStudent.getLanguage() + " Curriculum"); // Keep it simple
        } else {
            currentLessonTitle.setText("Select a lesson");
            nextLessonTitle.setText("");
        }
    }

    private void updateProgressDisplay() {
        if (currentStudent == null || availableLessons == null || availableLessons.isEmpty()) {
            progressBar.setProgress(0.0);
            progressValue.setText("0%");
            return;
        }
        long completedCount = availableLessons.stream()
                .filter(l -> currentStudent.isLessonCompleted(l.getLessonId()))
                .count();
        double progress = (double) completedCount / availableLessons.size();
        progressBar.setProgress(progress);
        progressValue.setText(String.format("%.0f%%", progress * 100));
    }

    @FXML
    private void startLesson() {
        Lesson selectedLesson = lessonsListView.getSelectionModel().getSelectedItem();
        if (selectedLesson != null) {
            // TODO: Check prerequisites before starting
            // if (!arePrerequisitesMet(selectedLesson)) {
            //     AlertMessage.showWarning("Prerequisite Not Met", "You must complete previous lessons first.");
            //     return;
            // }
            startSelectedLesson(selectedLesson);
        } else {
            AlertMessage.showWarning("No Lesson Selected", "Please select a lesson from the list to start.");
        }
    }

    // Helper method to navigate to the lesson view
    private void startSelectedLesson(Lesson lesson) {
        System.out.println("Starting lesson: " + lesson.getTitle());
        Object controller = SceneManager.switchToScene(SceneManager.LESSON_VIEW);
        if (controller instanceof LessonController) {
            ((LessonController) controller).setLessonData(lesson); // Pass the selected lesson object
        } else {
            AlertMessage.showError("Navigation Error", "Could not load the lesson view controller.");
        }
    }

    @FXML
    private void previewNextLesson() {
        // Logic to preview the next lesson in sequence (if applicable)
        AlertMessage.showInformation("Not Implemented", "Lesson preview functionality is not yet available.");
    }

    @FXML
    private void logout() {
        LoginManager.logout(); // Clear the selected user session
        SceneManager.switchToScene(SceneManager.MAIN_SCENE); // Go to welcome/main screen
    }
}