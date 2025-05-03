package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage stage;
    private String[] sceneFiles;
    private  static Scene[] scenes=new Scene[10];

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
        for (int i = 0; i < scenes.length; i++) {
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



    


    public static void switchToMainScene(int i)   {
        stage.setScene(scenes[i]);
    }


    public void switchToStudentScene() throws IOException {
    }

    public void switchToTeacherScene() throws IOException {

    }
}