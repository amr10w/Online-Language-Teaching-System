package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;
    private String[] sceneFiles;
    private Scene[] scenes=new Scene[9];

    public SceneManager(Stage stage) {
        this.stage=stage;
        this.sceneFiles= new String[]{
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
        for (int i=0;i<9;i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/" + sceneFiles[i]));
                Scene scene = new Scene(loader.load());
                System.out.println(sceneFiles[i] + " loaded successfully.");
                scenes[i]=scene;
            } catch (IOException e) {
                System.out.println("Error loading: " + sceneFiles[i]);
                e.printStackTrace();
            }
        }


    }
    


    public void switchToMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/mainScene.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    public void switchToStudentScene() throws IOException {
    }

    public void switchToTeacherScene() throws IOException {

    }
}