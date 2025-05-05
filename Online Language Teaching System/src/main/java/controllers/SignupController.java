// src/main/java/controllers/SignupController.java
package controllers;

import Main.*; // Import Main package classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.events.MouseEvent;

import java.io.File;

/**
 * Controller for the 'signup.fxml' view.
 * Handles new user registration.
 */
public class SignupController {

    // --- FXML Elements ---
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField; // Consider adding confirm password field
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton teacherRadio;
    @FXML private ToggleGroup userTypeGroup;    // Ensure fx:id="userTypeGroup" in FXML
    @FXML private ComboBox<String> languageComboBox;
    @FXML private Button signUpButton;          // Ensure fx:id="signUpButton" in FXML for the main button
    @FXML private Hyperlink loginLink;          // Ensure fx:id="loginLink" for the hyperlink

    // Side Menu Buttons
    @FXML private Button navigateToHome;
    @FXML private Button navigateToLogin; // This button in the side menu
    @FXML private Button navigateToAbout;

    // --- Controller Dependencies ---
    private LoginManager loginManager; // Use LoginManager for signup logic and checks

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Sets up language choices and default selections.
     */
    @FXML
    public void initialize() {
        System.out.println("SignupController initializing...");
        loginManager = new LoginManager(); // Initialize manager

        // Populate language choices in the ComboBox
        // TODO: Get language list dynamically if possible (e.g., from Language subclasses)
        ObservableList<String> languages = FXCollections.observableArrayList(
                "English", "French", "German" // Add other supported languages
        );
        languageComboBox.setItems(languages);
        languageComboBox.setPromptText("Select language..."); // Set a helpful prompt

        // Default selection (optional, but good practice)
        studentRadio.setSelected(true); // Default to student role
        languageComboBox.setValue("English"); // Default to English
    }

    // --- Navigation Handlers ---

    @FXML
    private void navigateToHome() {
        System.out.println("Navigating to Home scene...");
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    /** Handles both the side menu Login button and the Hyperlink */
    @FXML
    private void navigateToLogin() {
        System.out.println("Navigating to Login scene...");
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    @FXML
    private void navigateToAbout() {
        System.out.println("Navigating to About scene...");
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    // --- Signup Action Handler ---

    /**
     * Handles the action for the main "Sign Up" button.
     * Validates input, checks username availability, creates the appropriate User object,
     * attempts signup via LoginManager, and navigates to the dashboard on success.
     */
    @FXML
    private void handleSignup() { // Renamed from navigateToDashboard which was confusing
        // --- 1. Get Input Data ---
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText(); // Get password (plain text)
        String selectedLanguage = languageComboBox.getValue();
        RadioButton selectedRoleRadio = (RadioButton) userTypeGroup.getSelectedToggle();

        // --- 2. Input Validation ---
        if (username.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a username.");
            usernameField.requestFocus();
            return;
        }
        // Improved basic email validation
        if (email.isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            AlertMessage.showWarning("Input Required", "Please enter a valid email address (e.g., user@example.com).");
            emailField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a password.");
            passwordField.requestFocus();
            return;
        }
        // Example: Basic password complexity check (minimum length)
        final int MIN_PASSWORD_LENGTH = 6;
        if (password.length() < MIN_PASSWORD_LENGTH) {
            AlertMessage.showWarning("Password Too Short", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            passwordField.requestFocus();
            return;
        }
        // TODO: Add password confirmation field and check if they match

        if (selectedLanguage == null || selectedLanguage.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please select a language.");
            languageComboBox.requestFocus();
            return;
        }
        if (selectedRoleRadio == null) {
            // Should not happen if one is selected by default, but check anyway
            AlertMessage.showWarning("Input Required", "Please select your role (Student or Teacher).");
            studentRadio.requestFocus(); // Focus the first radio
            return;
        }

        // --- 3. Check Username Availability (Case-Insensitive) ---
        if (!loginManager.isUsernameAvailable(username)) {
            AlertMessage.showError("Username Taken", "The username '" + username + "' is already taken. Please choose a different username.");
            usernameField.requestFocus();
            return;
        }

        // --- 4. Create User Object ---
        User newUser = null;
        String role = selectedRoleRadio.getText(); // "Student" or "Teacher"
        String userId = username; // Use username as the unique ID

        try {
            if ("Student".equalsIgnoreCase(role)) {
                newUser = new Student(username, email, password, userId, selectedLanguage);
            } else if ("Teacher".equalsIgnoreCase(role)) {
                newUser = new Teacher(username, email, password, userId, selectedLanguage);
            } else {
                // Should not happen with RadioButtons
                AlertMessage.showError("Internal Error", "Invalid role selected: " + role);
                return;
            }
        } catch (IllegalArgumentException e) {
            // Catch validation errors from User/Student/Teacher constructors
            AlertMessage.showError("Invalid Data", "Could not create user profile: " + e.getMessage());
            return;
        }

        // --- 5. Perform Signup via LoginManager ---
        System.out.println("Attempting signup for user: " + username + " as " + role);
        boolean signupSuccess = loginManager.signup(newUser); // signup method handles persistence

        // --- 6. Handle Signup Result ---
        if (signupSuccess) {
            AlertMessage.showInformation("Signup Successful", "Welcome, " + username + "! Your account has been created successfully.");

            // --- Auto-Login and Navigate to Dashboard ---
            // Set the current session user
            LoginManager.setSelectedUser(newUser);

            // Navigate to the appropriate dashboard and pass user data
            if (newUser instanceof Student) {
                Object controller = SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
                if (controller instanceof StudentSceneController) {
                    ((StudentSceneController) controller).setStudentData((Student) newUser);
                } else { handleDashboardLoadError("Student"); }
            } else { // Must be Teacher
                Object controller = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
                if (controller instanceof TeacherSceneController) {
                    ((TeacherSceneController) controller).setTeacherData((Teacher) newUser);
                } else { handleDashboardLoadError("Teacher"); }
            }
            clearFields(); // Clear form after successful signup and navigation

        } else {
            // Specific error should have been shown by LoginManager or caught earlier
            AlertMessage.showError("Signup Failed", "Could not create your account.\nPlease check the details provided or try a different username.");
        }
    }

    /** Helper method to show error and navigate back to login if dashboard fails to load. */
    private void handleDashboardLoadError(String userType) {
        System.err.println("Error: Could not get " + userType + "SceneController or pass data after signup.");
        AlertMessage.showError("Navigation Error", "Account created, but could not load the " + userType.toLowerCase() + " dashboard. Please try logging in.");
        SceneManager.switchToScene(SceneManager.LOGIN); // Go to login screen
    }

    /** Clears all input fields and resets selections. */
    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        // Clear confirm password field if added
        studentRadio.setSelected(true); // Reset role to default
        languageComboBox.setValue("English"); // Reset language to default
    }

    /**
     * @deprecated Kept for compatibility if FXML uses onAction="#navigateToDashboard". Use handleSignup instead.
     */
    @Deprecated
    @FXML
    private void navigateToDashboard() {
        System.out.println("Warning: Deprecated method navigateToDashboard() called in SignupController. Using handleSignup().");
        handleSignup();
    }
}