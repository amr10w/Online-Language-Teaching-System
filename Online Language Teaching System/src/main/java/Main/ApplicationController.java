// src/main/java/Main/ApplicationController.java
package Main;

import controllers.AlertMessage; // Import AlertMessage
import Main.LoginManager; // Ensure LoginManager is imported if used directly (it isn't here)
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

    // Use constants for resource paths relative to the classpath root
    private static final String ICON_PATH = "/images/lovebird.png";
    private static final String WELCOME_AUDIO_PATH = "/audios/welcome.mp3";

    private MediaPlayer mediaPlayer; // Keep a reference to stop it later if needed

    @Override
    public void start(Stage stage) {
        stage.setTitle("LinguaLearn - Online Language Teaching System");

        // Set Application Icon
        try {
            URL iconUrl = getClass().getResource(ICON_PATH);
            if (iconUrl == null) {
                System.err.println("Error: Icon resource not found at " + ICON_PATH);
                // Optionally load a default icon or proceed without one
            } else {
                Image icon = new Image(iconUrl.toExternalForm());
                stage.getIcons().add(icon);
                System.out.println("Application icon loaded successfully from: " + iconUrl.toExternalForm());
            }
        } catch (Exception e) { // Catch broader exceptions during loading
            System.err.println("Error loading application icon: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
        }

        // Initialize Application Manager (resolves paths and loads users)
        // This static block in ApplicationManager will run when the class is loaded.
        // Trigger class loading explicitly if needed, but accessing its static methods will do it.
        System.out.println("Initializing ApplicationManager...");
        ApplicationManager.getUsersFilePath(); // Access a static method to ensure initialization
        System.out.println("ApplicationManager initialized.");

        // Initialize Scene Manager
        SceneManager sceneManager = new SceneManager(stage);

        // Start with the Main Scene (Welcome/Home)
        sceneManager.switchToScene(SceneManager.MAIN_SCENE); // Use constant

        // Play Welcome Music
        playWelcomeMusic();

        stage.show();
    }

    private void playWelcomeMusic() {
        try {
            URL audioUrl = getClass().getResource(WELCOME_AUDIO_PATH);
            if (audioUrl == null) {
                System.err.println("Error: Welcome audio resource not found at " + WELCOME_AUDIO_PATH);
                return;
            }
            String audioExternalForm = audioUrl.toExternalForm();
            System.out.println("Attempting to play audio from: " + audioExternalForm);

            Media media = new Media(audioExternalForm);
            mediaPlayer = new MediaPlayer(media);

            // Add error handling for MediaPlayer
            mediaPlayer.setOnError(() -> {
                System.err.println("MediaPlayer Error: " + mediaPlayer.getError());
                AlertMessage.showError("Audio Playback Error", "Could not play the welcome audio.");
            });

            mediaPlayer.setOnReady(() -> {
                System.out.println("MediaPlayer Ready. Playing audio.");
                mediaPlayer.play();
            });

            // Optional: Loop the audio
            // mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        } catch (Exception e) {
            System.err.println("Error initializing or playing MP3 file: " + e.getMessage());
            e.printStackTrace();
            AlertMessage.showError("Audio Error", "An error occurred while trying to play the welcome audio.");
        }
    }

    @Override
    public void stop() throws Exception {
        // Stop the music when the application closes
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            System.out.println("Media player stopped.");
        }
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}