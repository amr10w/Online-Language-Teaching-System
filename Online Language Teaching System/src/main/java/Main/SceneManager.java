package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static Stage stage;
    // Use constants for scene indices/names for better readability
    public static final int STUDENT_DASHBOARD = 0;
    public static final int ABOUT = 1;
    public static final int CREATE_LESSON = 2;
    public static final int CREATE_QUIZ = 3;
    public static final int LESSON_VIEW = 4;
    public static final int LOGIN = 5;
    public static final int MAIN_SCENE = 6; // Welcome/Home
    public static final int QUIZ_VIEW = 7;
    public static final int SIGNUP = 8;
    public static final int TEACHER_DASHBOARD = 9;

    private static final Map<Integer, String> sceneFiles = new HashMap<>();

    static {
        sceneFiles.put(STUDENT_DASHBOARD, "StudentScene.fxml");
        sceneFiles.put(ABOUT, "about.fxml");
        sceneFiles.put(CREATE_LESSON, "createlesson.fxml");
        sceneFiles.put(CREATE_QUIZ, "createquiz.fxml");
        sceneFiles.put(LESSON_VIEW, "lesson.fxml");
        sceneFiles.put(LOGIN, "login.fxml");
        sceneFiles.put(MAIN_SCENE, "mainScene.fxml");
        sceneFiles.put(QUIZ_VIEW, "quiz.fxml");
        sceneFiles.put(SIGNUP, "signup.fxml");
        sceneFiles.put(TEACHER_DASHBOARD, "teacherScene.fxml");
    }

    // Cache loaded scenes? Maybe not necessary unless performance is an issue.
    // private static final Scene[] scenes = new Scene[sceneFiles.size()];

    public SceneManager(Stage stage) {
        if (SceneManager.stage != null) {
            System.err.println("Warning: SceneManager already initialized with a Stage.");
        }
        SceneManager.stage = stage;
        // Pre-loading scenes can be complex with controllers needing data. Load on demand.
    }

    /**
     * Switches the primary stage to the specified scene and returns its controller.
     *
     * @param sceneIndex The index (use constants like SceneManager.LOGIN) of the scene to switch to.
     * @return The controller instance associated with the loaded FXML, or null if loading fails.
     */
    public static Object switchToScene(int sceneIndex) {
        if (stage == null) {
            System.err.println("Error: Stage is not initialized in SceneManager.");
            return null;
        }

        String fxmlFile = sceneFiles.get(sceneIndex);
        if (fxmlFile == null) {
            System.err.println("Error: No FXML file defined for scene index " + sceneIndex);
            return null;
        }

        try {
            String fxmlPath = "/scenes/" + fxmlFile;
            URL fxmlUrl = SceneManager.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Error: Cannot find FXML resource at path: " + fxmlPath);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root); // Create new scene each time

            stage.setScene(scene);
            stage.setTitle("LinguaLearn - " + getSceneTitle(sceneIndex)); // Set title based on scene
            System.out.println(fxmlFile + " loaded and scene switched successfully.");
            return loader.getController(); // Return the controller instance

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlFile);
            e.printStackTrace(); // Print stack trace for detailed debugging
            // Optionally show an error alert to the user here
            controllers.AlertMessage.showError("Scene Load Error", "Could not load the requested screen (" + fxmlFile + "). Please check logs.");

        } catch (Exception e) {
            System.err.println("An unexpected error occurred while switching scenes: " + e.getMessage());
            e.printStackTrace();
            controllers.AlertMessage.showError("Scene Load Error", "An unexpected error occurred while loading the screen.");
        }

        return null; // Return null if switching failed
    }

    // Helper method to get a title for the window based on the scene
    private static String getSceneTitle(int sceneIndex) {
        switch (sceneIndex) {
            case STUDENT_DASHBOARD: return "Student Dashboard";
            case ABOUT: return "About LinguaLearn";
            case CREATE_LESSON: return "Create Lesson";
            case CREATE_QUIZ: return "Create Quiz";
            case LESSON_VIEW: return "Lesson View";
            case LOGIN: return "Login";
            case MAIN_SCENE: return "Welcome";
            case QUIZ_VIEW: return "Quiz";
            case SIGNUP: return "Sign Up";
            case TEACHER_DASHBOARD: return "Teacher Dashboard";
            default: return "LinguaLearn";
        }
    }

    // Method kept for compatibility if old code uses it, but switchToScene is preferred
    @Deprecated
    public static Object switchToMainScene(int i) {
        return switchToScene(i);
    }

    // Method kept for compatibility if old code uses it
    @Deprecated
    public static String getSceneFile(int i) {
        return sceneFiles.getOrDefault(i, "unknown.fxml");
    }
}