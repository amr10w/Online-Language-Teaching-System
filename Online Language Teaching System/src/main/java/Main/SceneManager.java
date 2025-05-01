package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }
    
    public void testAllScenes() {
    String[] sceneFiles = {
        "StudentScene.fxml",
        "about.fxml",
        "createlesson.fxml",
        "createquiz.fxml",
        "lesson.fxml",
        "login.fxml",
        "mainScene.fxml",
        "quiz.fxml",
        "signup.fxml",
        "teacherScene.fxml"
    };
    for (String file : sceneFiles) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/" + file));
            loader.load(); // Just loading the file to check for errors
            System.out.println(file + " loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading: " + file);
            e.printStackTrace();
        }
    }
}

    public void switchToMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/about.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    public void switchToStudentScene() throws IOException {
    }

    public void switchToTeacherScene() throws IOException {

    }
}