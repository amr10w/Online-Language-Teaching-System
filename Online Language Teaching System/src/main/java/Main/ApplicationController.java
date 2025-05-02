package Main;

import controllers.QuizController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;
public class ApplicationController extends Application {

   
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Online Language Teaching System");
        try {

            Image icon = new Image(getClass().getResourceAsStream("/images/lovebird.png"));
            stage.getIcons().add(icon);
        }
        catch (NullPointerException e) {
            System.err.println("Error: Could not load image . Verify the file exists in src/main/resources/images/");
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid image format or corrupted file.");
            System.out.println(e.getMessage());
        }


        SceneManager sceneManager = new SceneManager(stage);

        sceneManager.switchToMainScene(7);






        stage.show();


    }

    public static void main(String[] args) {

        launch();
    }
}
