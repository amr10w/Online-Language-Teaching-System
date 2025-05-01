package Main;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationController extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchToMainScene();
        stage.setTitle("Online Language Teaching System");
        stage.show();
        sceneManager.testAllScenes();
    }

    public static void main(String[] args) {

        launch();
    }
}
