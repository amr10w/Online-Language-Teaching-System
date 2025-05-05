package controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertMessage {

    /**
     * Shows a standard information alert dialog.
     * @param title The title of the alert window.
     * @param message The main message content.
     */
    public static void showInformation(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert dialog.
     * @param title The title of the alert window.
     * @param message The main message content.
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog.
     * @param title The title of the alert window.
     * @param message The main message content.
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation alert dialog.
     * @param title The title of the alert window.
     * @param message The confirmation question.
     * @return true if the user clicked OK, false otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Show the dialog and wait for user response
        return alert.showAndWait()
                .map(response -> response == javafx.scene.control.ButtonType.OK)
                .orElse(false); // Default to false if dialog is closed otherwise
    }

    // Kept for backward compatibility if needed
    @Deprecated
    public static void alertMessage(String title, String message) {
        showWarning(title, message); // Default to warning for the old method
    }
}