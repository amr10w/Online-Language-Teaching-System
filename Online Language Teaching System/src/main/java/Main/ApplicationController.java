package Main;

import controllers.MainSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

import javafx.scene.image.Image;

public class ApplicationController extends Application {

    private static final String ICON_PATH = "/images/lovebird.png";
    private static final String WELCOME_AUDIO_PATH = "/audios/welcome.mp3";

    @Override
    public void start(Stage stage) {
        stage.setTitle("LinguaLearn - Online Language Teaching System");

        // Set Application Icon
        try {
            URL iconUrl = getClass().getResource(ICON_PATH);
            if (iconUrl == null) {
                System.err.println("Error: Icon resource not found at " + ICON_PATH);
            } else {
                Image icon = new Image(iconUrl.toExternalForm());
                stage.getIcons().add(icon);
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Error loading application icon: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
        }

        // Initialize Application Manager (loads users)
        ApplicationManager am = new ApplicationManager();

        // Initialize Scene Manager
        SceneManager sceneManager = new SceneManager(stage);

        // Start with the Main Scene (Welcome/Home)
        sceneManager.switchToScene(SceneManager.MAIN_SCENE); // Use constant

        // Play Welcome Music
        playMusic();

        stage.show();
    }

    private void playMusic() {
        try {
            URL audioUrl = getClass().getResource(WELCOME_AUDIO_PATH);
            if (audioUrl == null) {
                System.err.println("Error: Welcome audio resource not found at " + WELCOME_AUDIO_PATH);
                return;
            }
            String filePath = audioUrl.toExternalForm();
            Media media = new Media(filePath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            // mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loops the audio - uncomment if needed
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing MP3 file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}