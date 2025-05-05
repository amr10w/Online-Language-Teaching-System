package controllers;

import Main.*; // Import Main package classes
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink; // Import Hyperlink
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the 'login.fxml' view.
 * Handles user authentication and navigation upon successful login.
 */
public class LoginController {

    // --- FXML Elements ---
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button navigateToHome;    // Side menu button
    @FXML private Button navigateToSignup;  // Side menu button
    @FXML private Button navigateToAbout;   // Side menu button
    @FXML private Button loginButton;       // Main login button in the center pane
    @FXML private Hyperlink signupLink;     // "Sign Up" hyperlink below login button

    // --- Controller Dependencies ---
    private LoginManager loginManager; // Instance to handle login logic

    /**
     * Initializes the controller class. Called after FXML fields are injected.
     * Creates a LoginManager instance.
     */
    @FXML
    public void initialize() {
        System.out.println("LoginController initializing...");
        loginManager = new LoginManager(); // Create instance to use its methods
    }

    // --- Navigation Handlers (Side Menu and Links) ---

    @FXML
    private void navigateToHome() {
        System.out.println("Navigating to Home scene...");
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    /** Handles both the side menu Signup button and the Hyperlink */
    @FXML
    private void navigateToSignup() {
        System.out.println("Navigating to Signup scene...");
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }

    @FXML
    private void navigateToAbout() {
        System.out.println("Navigating to About scene...");
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    // --- Login Action Handler ---

    /**
     * Handles the action event for the main login button.
     * Retrieves username and password, validates them, attempts login via LoginManager,
     * and navigates to the appropriate dashboard upon success.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText(); // Get password (plain text)

        // --- Basic Input Validation ---
        if (username.isEmpty()) {
            AlertMessage.showWarning("Login Failed", "Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            AlertMessage.showWarning("Login Failed", "Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        System.out.println("Attempting login for user: " + username);
        // --- Attempt Login ---
        int loginStatus = loginManager.login(username, password);

        // --- Handle Login Result ---
        switch (loginStatus) {
            case 1: // Student login successful
                System.out.println("Student login successful: " + username);
                User loggedInStudent = LoginManager.getSelectedUser(); // Get the logged-in user object
                // Navigate to Student Dashboard and pass data
                Object studentController = SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
                if (studentController instanceof StudentSceneController && loggedInStudent instanceof Student) {
                    ((StudentSceneController) studentController).setStudentData((Student) loggedInStudent);
                    clearFields(); // Clear fields after successful login
                } else {
                    System.err.println("Error: Could not get StudentSceneController or logged-in user is not a Student.");
                    AlertMessage.showError("Navigation Error", "Could not load the student dashboard properly.");
                    // Optionally navigate back to login or show a generic error dashboard
                    SceneManager.switchToScene(SceneManager.LOGIN);
                }
                break;

            case 2: // Teacher login successful
                System.out.println("Teacher login successful: " + username);
                User loggedInTeacher = LoginManager.getSelectedUser();
                // Navigate to Teacher Dashboard and pass data
                Object teacherController = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
                if (teacherController instanceof TeacherSceneController && loggedInTeacher instanceof Teacher) {
                    ((TeacherSceneController) teacherController).setTeacherData((Teacher) loggedInTeacher);
                    clearFields(); // Clear fields after successful login
                } else {
                    System.err.println("Error: Could not get TeacherSceneController or logged-in user is not a Teacher.");
                    AlertMessage.showError("Navigation Error", "Could not load the teacher dashboard properly.");
                    SceneManager.switchToScene(SceneManager.LOGIN);
                }
                break;

            case -1: // Username not found
                AlertMessage.showError("Login Failed", "Username '" + username + "' not found.\nPlease check your username or click 'Sign Up'.");
                usernameField.requestFocus();
                break;

            case -2: // Incorrect password
                AlertMessage.showError("Login Failed", "Incorrect password. Please try again.");
                passwordField.clear(); // Clear only the password field
                passwordField.requestFocus();
                break;

            case 0: // Login successful, but unknown role (should ideally not happen)
                AlertMessage.showWarning("Login Warning", "Login successful, but your user role is unrecognized. Please contact support.");
                // Decide where to navigate - maybe a generic error page or back to login?
                LoginManager.logout(); // Log out the user with the unknown role
                SceneManager.switchToScene(SceneManager.LOGIN);
                break;

            default: // Other unexpected errors
                AlertMessage.showError("Login Failed", "An unexpected error occurred during login. Please try again later.");
                break;
        }
    }

    /** Clears the username and password fields. */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    /**
     * @deprecated Kept for compatibility if FXML `onAction="#navigateToDashboard"` exists. Use `handleLogin` instead.
     */
    @Deprecated
    @FXML
    private void navigateToDashboard() {
        System.out.println("Warning: Deprecated method navigateToDashboard() called in LoginController. Using handleLogin().");
        handleLogin();
    }
}