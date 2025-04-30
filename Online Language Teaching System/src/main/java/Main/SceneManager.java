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

    public void switchToMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


    public void switchToStudentScene() throws IOException {
    }

    public void switchToTeacherScene() throws IOException {

    }
}