package Main;

import controllers.QuizController;
import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.image.Image;
public class ApplicationController extends Application {
    @Override
    public void start(Stage stage)  {
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

        ApplicationManager am=new ApplicationManager();


        SceneManager sceneManager = new SceneManager(stage);

        sceneManager.switchToMainScene(6);





        playMusic();
        stage.show();


    }

    private void playMusic() {
        try {
            String filePath = getClass().getResource("/audios/welcome.mp3").toExternalForm();
            Media media = new Media(filePath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loops the audio
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing MP3 file. Ensure the file exists at the specified path.");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        launch();
    }
}
