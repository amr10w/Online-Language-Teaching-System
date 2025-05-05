// src/main/java/Main/SceneManager.java
package Main;

import controllers.AlertMessage; // Import AlertMessage
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages switching between different scenes (FXML views) in the JavaFX application.
 */
public class SceneManager {
    private static Stage stage; // The primary stage of the application

    // Constants for scene identification (use these instead of magic numbers)
    public static final int STUDENT_DASHBOARD = 0;
    public static final int ABOUT = 1;
    public static final int CREATE_LESSON = 2;
    public static final int CREATE_QUIZ = 3;
    public static final int LESSON_VIEW = 4;
    public static final int LOGIN = 5;
    public static final int MAIN_SCENE = 6; // Welcome/Home screen
    public static final int QUIZ_VIEW = 7;
    public static final int SIGNUP = 8;
    public static final int TEACHER_DASHBOARD = 9;

    // Map scene indices to their corresponding FXML file names (relative to resources/scenes/)
    private static final Map<Integer, String> sceneFiles = new HashMap<>();
    // Map scene indices to their desired window titles
    private static final Map<Integer, String> sceneTitles = new HashMap<>();

    static {
        // Populate scene file mapping
        sceneFiles.put(STUDENT_DASHBOARD, "StudentScene.fxml");
        sceneFiles.put(ABOUT, "about.fxml");
        sceneFiles.put(CREATE_LESSON, "createlesson.fxml");
        sceneFiles.put(CREATE_QUIZ, "createquiz.fxml");
        sceneFiles.put(LESSON_VIEW, "lesson.fxml");
        sceneFiles.put(LOGIN, "login.fxml");
        sceneFiles.put(MAIN_SCENE, "mainScene.fxml"); // Welcome/Home
        sceneFiles.put(QUIZ_VIEW, "quiz.fxml");
        sceneFiles.put(SIGNUP, "signup.fxml");
        sceneFiles.put(TEACHER_DASHBOARD, "teacherScene.fxml");

        // Populate scene title mapping
        sceneTitles.put(STUDENT_DASHBOARD, "Student Dashboard");
        sceneTitles.put(ABOUT, "About LinguaLearn");
        sceneTitles.put(CREATE_LESSON, "Create Lesson");
        sceneTitles.put(CREATE_QUIZ, "Create Quiz");
        sceneTitles.put(LESSON_VIEW, "Lesson View");
        sceneTitles.put(LOGIN, "Login");
        sceneTitles.put(MAIN_SCENE, "Welcome to LinguaLearn"); // Welcome/Home title
        sceneTitles.put(QUIZ_VIEW, "Quiz");
        sceneTitles.put(SIGNUP, "Sign Up");
        sceneTitles.put(TEACHER_DASHBOARD, "Teacher Dashboard");
    }

    // Scene cache (optional, can improve performance slightly but uses more memory)
    // private static final Map<Integer, Scene> sceneCache = new HashMap<>();

    /**
     * Initializes the SceneManager with the primary application stage.
     * This should be called once at application startup.
     * @param stage The main stage provided by JavaFX Application.start().
     */
    public SceneManager(Stage stage) {
        Objects.requireNonNull(stage, "Stage cannot be null for SceneManager");
        if (SceneManager.stage != null) {
            System.err.println("Warning: SceneManager is being re-initialized. This might indicate an issue.");
            // Optionally throw an exception or log more severely
        }
        SceneManager.stage = stage;
        System.out.println("SceneManager initialized with Stage.");
    }

    /**
     * Switches the primary stage to display the specified scene.
     * Loads the FXML file, creates a new Scene object, sets it on the stage,
     * updates the title, and returns the controller instance.
     *
     * @param sceneIndex The index (use constants like SceneManager.LOGIN) of the scene to switch to.
     * @return The controller instance associated with the loaded FXML, or null if loading fails or the stage is not set.
     */
    public static Object switchToScene(int sceneIndex) {
        if (stage == null) {
            System.err.println("Error switching scene: Stage is not initialized in SceneManager.");
            AlertMessage.showError("Navigation Error", "Application window is not available.");
            return null;
        }

        String fxmlFile = sceneFiles.get(sceneIndex);
        if (fxmlFile == null) {
            System.err.println("Error switching scene: No FXML file defined for scene index " + sceneIndex);
            AlertMessage.showError("Navigation Error", "The requested screen mapping is missing.");
            return null;
        }

        String sceneTitle = sceneTitles.getOrDefault(sceneIndex, "LinguaLearn"); // Get title or default

        try {
            // Construct the full resource path (assuming FXMLs are in resources/scenes/)
            String fxmlPath = "/scenes/" + fxmlFile;
            URL fxmlUrl = SceneManager.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Error switching scene: Cannot find FXML resource at path: " + fxmlPath);
                // Attempt alternative path if structure might differ (e.g., direct resources root)
                fxmlPath = "/" + fxmlFile;
                fxmlUrl = SceneManager.class.getResource(fxmlPath);
                if(fxmlUrl == null){
                    AlertMessage.showError("Resource Error", "Could not find the screen file: " + fxmlFile);
                    return null;
                } else {
                    System.out.println("Found FXML at alternative path: " + fxmlPath);
                }
            }

            System.out.println("Loading FXML: " + fxmlUrl.toExternalForm());
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load(); // Load the FXML hierarchy

            // Create a new Scene object each time (or use caching if implemented)
            Scene scene = new Scene(root);
            // Optional Caching:
            // Scene scene = sceneCache.computeIfAbsent(sceneIndex, k -> new Scene(root));
            // scene.setRoot(root); // Ensure root is updated if cached

            stage.setScene(scene); // Set the new scene on the stage
            stage.setTitle(sceneTitle); // Update the window title
            stage.sizeToScene(); // Adjust stage size to fit the new scene
            // stage.centerOnScreen(); // Optionally re-center the window

            System.out.println("Successfully switched to scene: " + fxmlFile);
            return loader.getController(); // Return the controller instance for potential data passing

        } catch (IOException e) {
            System.err.println("Error loading FXML file '" + fxmlFile + "': " + e.getMessage());
            e.printStackTrace(); // Print detailed stack trace
            AlertMessage.showError("Scene Load Error", "Could not load the screen (" + fxmlFile + "). Please check logs.\nError: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Error during FXML loading (likely controller issue or missing element): " + e.getMessage());
            e.printStackTrace();
            AlertMessage.showError("Scene Load Error", "An internal error occurred while preparing the screen.\nError: " + e.getMessage());
        }
        catch (Exception e) { // Catch any other unexpected errors
            System.err.println("An unexpected error occurred while switching scenes: " + e.getMessage());
            e.printStackTrace();
            AlertMessage.showError("Unexpected Error", "An unexpected error occurred while loading the screen.\nError: " + e.getMessage());
        }

        return null; // Return null if switching failed for any reason
    }


    /**
     * @deprecated Use the constant-based {@link #switchToScene(int)} method instead.
     */
    @Deprecated
    public static Object switchToMainScene(int i) {
        System.out.println("Warning: switchToMainScene(int) is deprecated. Use switchToScene(int).");
        return switchToScene(i);
    }

    /**
     * @deprecated This method is generally not needed externally.
     */
    @Deprecated
    public static String getSceneFile(int i) {
        System.out.println("Warning: getSceneFile(int) is deprecated.");
        return sceneFiles.getOrDefault(i, "unknown.fxml");
    }
}