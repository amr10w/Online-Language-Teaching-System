package controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType; // Import ButtonType
import java.util.Optional; // Import Optional

/**
 * Utility class for displaying common JavaFX Alert dialogs.
 */
public class AlertMessage {

    /**
     * Shows a standard information alert dialog.
     * Useful for confirming actions or providing neutral information.
     * @param title The title of the alert window.
     * @param message The main message content.
     */
    public static void showInformation(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Usually null for simple messages
        alert.setContentText(message);
        alert.showAndWait(); // Blocks until the user closes the alert
    }

    /**
     * Shows a warning alert dialog.
     * Useful for alerting the user about potential issues or non-critical errors.
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
     * Useful for indicating that an operation failed or a critical error occurred.
     * @param title The title of the alert window.
     * @param message The main message content describing the error.
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Header can be used for a brief error summary if needed
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation alert dialog with OK and Cancel buttons.
     * Useful for asking the user to confirm potentially destructive or irreversible actions.
     * @param title The title of the alert window.
     * @param message The confirmation question (e.g., "Are you sure you want to delete...?").
     * @return true if the user clicked the OK button, false if they clicked Cancel or closed the dialog.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the result is present and if it's the OK button
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * @deprecated Use specific methods like {@link #showWarning(String, String)} or {@link #showError(String, String)} instead.
     */
    @Deprecated
    public static void alertMessage(String title, String message) {
        System.err.println("Warning: Deprecated method AlertMessage.alertMessage() called. Use showWarning() or showError().");
        showWarning(title, message); // Default to warning for the old method
    }
}