
// src/main/java/controllers/StudentSceneController.java
package controllers;

import Main.*; // Import Main package classes
        import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.input.MouseEvent; // For ListView click events

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator; // For sorting
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for the 'StudentScene.fxml' view (Student Dashboard).
 * Displays student progress, available lessons, and allows lesson interaction.
 */
public class StudentSceneController {

    // --- FXML Elements ---
    @FXML private Label studentNameLabel;
    @FXML private Label languageLabel;
    @FXML private Label progressValue;      // Label showing progress percentage text
    @FXML private ProgressBar progressBar;  // ProgressBar visualization
    @FXML private Label pointsValue;        // Placeholder for points
    @FXML private Label rankValue;          // Placeholder for rank/level
    @FXML private Label currentLessonTitle; // Label to show selected lesson title
    @FXML private Label nextLessonTitle;    // Label (used for "Available Lessons" header)
    @FXML private ListView<Lesson> lessonsListView; // ListView to display Lesson objects
    @FXML private Button startLessonButton; // Button to start the selected lesson

    // --- Controller State ---
    private Student currentStudent;
    private List<Lesson> allAvailableLessons; // All lessons for the student's language
    private ObservableList<Lesson> lessonListForView = FXCollections.observableArrayList(); // List bound to ListView

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Sets initial UI state and configures the lessons ListView.
     */
    @FXML
    public void initialize() {
        System.out.println("StudentSceneController initializing...");
        // Set initial placeholder text
        studentNameLabel.setText("Loading...");
        languageLabel.setText("");
        progressValue.setText("0%");
        progressBar.setProgress(0.0);
        pointsValue.setText("0");   // Placeholder value
        rankValue.setText("N/A"); // Placeholder value
        currentLessonTitle.setText("Select a lesson below");
        nextLessonTitle.setText("Available Lessons");

        // Configure the ListView
        lessonsListView.setItems(lessonListForView); // Bind the ObservableList
        lessonsListView.setPlaceholder(new Label("Loading lessons...")); // Placeholder until data is loaded

        // Customize how Lesson objects are displayed in the ListView cells
        lessonsListView.setCellFactory(lv -> new ListCell<Lesson>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                if (empty || lesson == null || lesson.getLessonId() == null) {
                    setText(null);
                    setGraphic(null); // Clear graphic too
                    setStyle("");     // Reset styles
                } else {
                    // Format: "Lesson Title [Status]"
                    String status;
                    if (currentStudent != null && currentStudent.isLessonCompleted(lesson.getLessonId())) {
                        status = "[Completed]";
                        // Apply style for completed lessons
                        setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
                    } else {
                        // Check prerequisites (only if student data is available)
                        // boolean prereqsMet = (currentStudent == null) || arePrerequisitesMet(lesson);
                        boolean prereqsMet = true; // Simplified: Assume met for now
                        status = prereqsMet ? "" : "[Locked]";
                        // Apply style for available/locked lessons
                        setStyle(prereqsMet ? "" : "-fx-text-fill: darkred;");
                        // Disable selection for locked lessons? (Optional, might be confusing)
                        // this.setDisable(!prereqsMet);
                    }
                    setText(lesson.getTitle() + " " + status);
                    // Optional: Add tooltip with more info (e.g., level, prerequisites)
                    // Tooltip tt = new Tooltip("ID: " + lesson.getLessonId() + "\nLevel: " + lesson.getLevel());
                    // setTooltip(tt);
                }
            }
        });

        // Add listener for selection changes in the ListView
        lessonsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    displaySelectedLessonDetails(newValue); // Update detail label when selection changes
                }
        );

        // Add listener for double-click to start lesson
        lessonsListView.setOnMouseClicked(this::handleListViewClick);

        // Ensure the start button is initially disabled until a lesson is selected
        startLessonButton.setDisable(true);
    }

    /**
     * Sets the student data after login/signup.
     * Populates the dashboard with student-specific information and loads relevant lessons.
     *
     * @param student The logged-in Student object.
     */
    public void setStudentData(Student student) {
        Objects.requireNonNull(student, "Student object cannot be null for setStudentData");
        this.currentStudent = student;
        System.out.println("Setting data for student: " + currentStudent.getUsername());

        // Populate header labels
        studentNameLabel.setText(student.getUsername());
        languageLabel.setText(student.getLanguage());

        // Load lessons relevant to the student
        loadAvailableLessons();

        // Update progress display based on loaded lessons and student progress
        updateProgressDisplay();

        // Update other stats (placeholders)
        updateStatsPlaceholders();

        // Select the first available (uncompleted and prerequisites met) lesson by default
        selectDefaultLesson();
    }


    /** Loads all lesson files from the lessons directory. */
    private void loadAvailableLessons() {
        lessonListForView.clear(); // Clear the list bound to the view
        allAvailableLessons = new ArrayList<>(); // Reset the internal full list

        String lessonsDirPath = ApplicationManager.getLessonsDirectory();
        if (lessonsDirPath == null) {
            System.err.println("Failed to get lessons directory path. Cannot load lessons.");
            lessonsListView.setPlaceholder(new Label("Error: Lessons directory not configured."));
            return;
        }

        File lessonsDir = new File(lessonsDirPath);
        System.out.println("Loading lessons from: " + lessonsDir.getAbsolutePath());

        if (!lessonsDir.exists() || !lessonsDir.isDirectory()) {
            System.err.println("Lessons directory not found or is not a directory: " + lessonsDir.getAbsolutePath());
            lessonsListView.setPlaceholder(new Label("Lessons folder not found."));
            return;
        }

        File[] lessonFiles = lessonsDir.listFiles((dir, name) -> name.toLowerCase().startsWith("lesson_l") && name.toLowerCase().endsWith(".txt"));

        if (lessonFiles == null || lessonFiles.length == 0) {
            System.out.println("No lesson files found in the directory.");
            // Placeholder depends on whether the student has a language set
            String lang = (currentStudent != null) ? currentStudent.getLanguage() : "any language";
            lessonsListView.setPlaceholder(new Label("No lessons found for " + lang + "."));
            return;
        }

        System.out.println("Found " + lessonFiles.length + " potential lesson files.");
        for (File file : lessonFiles) {
            try {
                Lesson lesson = new Lesson(file.getAbsolutePath()); // Load using constructor
                // Basic check for successful load
                if (!"L_DEFAULT".equals(lesson.getLessonId())) {
                    // TODO: Add filtering based on student's language IF lessons have language metadata
                    // For now, add all loaded lessons
                    allAvailableLessons.add(lesson);
                } else {
                    System.err.println("Skipping lesson due to load failure: " + file.getName());
                }
            } catch (Exception e) {
                System.err.println("Error loading lesson from file " + file.getName() + ": " + e.getMessage());
                // e.printStackTrace(); // Optionally print stack trace
            }
        }

        // Sort lessons (using Lesson's compareTo, likely by ID or title)
        allAvailableLessons.sort(null); // Use natural order defined in Lesson.compareTo

        // Add loaded lessons to the ObservableList for the ListView
        lessonListForView.addAll(allAvailableLessons);

        if (lessonListForView.isEmpty()) {
            String lang = (currentStudent != null) ? currentStudent.getLanguage() : "any language";
            lessonsListView.setPlaceholder(new Label("No valid lessons found for " + lang + "."));
        }
        System.out.println("Finished loading lessons. Total valid lessons loaded: " + allAvailableLessons.size());
    }

    /** Updates the progress bar and percentage label. */
    private void updateProgressDisplay() {
        if (currentStudent == null || allAvailableLessons == null || allAvailableLessons.isEmpty()) {
            progressBar.setProgress(0.0);
            progressValue.setText("0%");
            return;
        }

        // Calculate completion based on the loaded available lessons
        long completedCount = allAvailableLessons.stream()
                .filter(l -> currentStudent.isLessonCompleted(l.getLessonId()))
                .count();

        double progress = (double) completedCount / allAvailableLessons.size();

        // Ensure progress is between 0.0 and 1.0
        progress = Math.max(0.0, Math.min(1.0, progress));

        progressBar.setProgress(progress);
        progressValue.setText(String.format("%.0f%%", progress * 100));
        System.out.println("Progress updated: " + completedCount + "/" + allAvailableLessons.size() + " (" + progressValue.getText() + ")");
    }

    /** Updates placeholder stats like points and rank. */
    private void updateStatsPlaceholders() {
        // TODO: Implement actual logic for points and ranking
        // Example: Calculate points based on completed lessons/quizzes
        int points = 0;
        if (currentStudent != null) {
            points += currentStudent.getCompletedLessons().size() * 10; // 10 points per lesson
            for (int score : currentStudent.getQuizScores().values()) {
                if (score >= 0) points += score / 10; // Points based on quiz score %
            }
        }
        pointsValue.setText(String.valueOf(points));

        // Example: Determine rank based on points or progress
        String rank = "Beginner";
        if (points > 500) rank = "Intermediate";
        if (points > 1500) rank = "Advanced";
        rankValue.setText(rank);
    }


    /** Selects the first uncompleted lesson in the list by default. */
    private void selectDefaultLesson() {
        Lesson firstAvailable = findFirstAvailableLesson();
        if (firstAvailable != null) {
            lessonsListView.getSelectionModel().select(firstAvailable);
            // Scroll to the selected item
            lessonsListView.scrollTo(firstAvailable);
        } else if (!lessonListForView.isEmpty()) {
            // If all are completed or locked, select the first one anyway
            lessonsListView.getSelectionModel().selectFirst();
        } else {
            // No lessons available
            currentLessonTitle.setText("No lessons available");
            startLessonButton.setDisable(true);
        }
    }

    /** Finds the first lesson that is not completed and has prerequisites met. */
    private Lesson findFirstAvailableLesson() {
        if (currentStudent == null || lessonListForView.isEmpty()) return null;
        for (Lesson lesson : lessonListForView) {
            // boolean prereqsMet = arePrerequisitesMet(lesson); // Check prerequisites
            boolean prereqsMet = true; // Simplified
            if (prereqsMet && !currentStudent.isLessonCompleted(lesson.getLessonId())) {
                return lesson; // Found the first available lesson
            }
        }
        return null; // All available lessons are completed or locked
    }

    /**
     * Checks if the prerequisites for a given lesson are met by the current student.
     * @param lesson The lesson to check.
     * @return true if all prerequisites are met or none exist, false otherwise.
     */
    private boolean arePrerequisitesMet(Lesson lesson) {
        if (currentStudent == null || lesson == null || lesson.getPrerequisiteLessonIds().isEmpty()) {
            return true; // No student, no lesson, or no prerequisites means they are met
        }
        for (String prereqId : lesson.getPrerequisiteLessonIds()) {
            if (prereqId != null && !prereqId.trim().isEmpty() && !prereqId.equalsIgnoreCase("none")) {
                if (!currentStudent.isLessonCompleted(prereqId.trim())) {
                    System.out.println("Prerequisite '" + prereqId + "' not met for lesson '" + lesson.getTitle() + "'");
                    return false; // Found an unmet prerequisite
                }
            }
        }
        return true; // All prerequisites are met
    }


    /** Updates the detail label based on the selected lesson in the ListView. */
    private void displaySelectedLessonDetails(Lesson selectedLesson) {
        if (selectedLesson != null) {
            currentLessonTitle.setText(selectedLesson.getTitle());
            // Enable/disable start button based on prerequisites
            // startLessonButton.setDisable(!arePrerequisitesMet(selectedLesson));
            startLessonButton.setDisable(false); // Simplified: Always enable if selected
        } else {
            currentLessonTitle.setText("Select a lesson below");
            startLessonButton.setDisable(true); // Disable if nothing selected
        }
    }

    /** Handles clicks on the ListView (specifically double-click). */
    private void handleListViewClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click detected
            Lesson selectedLesson = lessonsListView.getSelectionModel().getSelectedItem();
            if (selectedLesson != null) {
                System.out.println("ListView double-clicked on: " + selectedLesson.getTitle());
                startSelectedLesson(selectedLesson); // Start the lesson on double-click
            }
        }
    }

    /** Handles the action for the "Start Lesson" button. */
    @FXML
    private void startLesson() {
        Lesson selectedLesson = lessonsListView.getSelectionModel().getSelectedItem();
        if (selectedLesson != null) {
            startSelectedLesson(selectedLesson);
        } else {
            // Should not happen if button is disabled correctly, but as a safeguard:
            AlertMessage.showWarning("No Lesson Selected", "Please select a lesson from the list first.");
        }
    }

    /**
     * Navigates to the Lesson view for the given lesson, checking prerequisites first.
     * @param lesson The lesson to start.
     */
    private void startSelectedLesson(Lesson lesson) {
        Objects.requireNonNull(lesson, "Cannot start a null lesson.");

        // --- Prerequisite Check ---
        // boolean prereqsMet = arePrerequisitesMet(lesson);
        boolean prereqsMet = true; // Simplified
        if (!prereqsMet) {
            AlertMessage.showWarning("Lesson Locked", "You must complete the prerequisite lessons before starting '" + lesson.getTitle() + "'.");
            // Optionally list the missing prerequisites
            // List<String> missing = lesson.getPrerequisiteLessonIds().stream()
            //     .filter(id -> !id.equalsIgnoreCase("none") && !currentStudent.isLessonCompleted(id))
            //     .collect(Collectors.toList());
            // AlertMessage.showWarning("Prerequisites Missing", "Missing: " + String.join(", ", missing));
            return; // Do not proceed
        }
        // --- End Prerequisite Check ---


        System.out.println("Attempting to start lesson: " + lesson.getTitle() + " (ID: " + lesson.getLessonId() + ")");
        // Switch to the Lesson scene and pass the Lesson object
        Object controller = SceneManager.switchToScene(SceneManager.LESSON_VIEW);
        if (controller instanceof LessonController) {
            ((LessonController) controller).setLessonData(lesson);
        } else {
            System.err.println("Failed to get LessonController instance during scene switch.");
            AlertMessage.showError("Navigation Error", "Could not load the lesson view properly.");
            // Stay on dashboard or navigate back to login?
        }
    }


    /** Handles the logout action. */
    @FXML
    private void logout() {
        boolean confirm = AlertMessage.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirm) {
            System.out.println("Logging out student: " + (currentStudent != null ? currentStudent.getUsername() : "Unknown"));
            LoginManager.logout(); // Clear the selected user session
            SceneManager.switchToScene(SceneManager.LOGIN); // Go back to login screen
        }
    }
}