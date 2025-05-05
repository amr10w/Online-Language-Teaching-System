package controllers;

import Main.LoginManager;
import Main.SceneManager;
import Main.Student;
import Main.Teacher;
import Main.User; // Import User
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button navigateToHome;
    @FXML private Button loginButton; // Renamed from 'login' to avoid conflict
    @FXML private Button navigateToSignup;
    @FXML private Button navigateToAbout;

    private LoginManager loginManager; // Instance of LoginManager

    @FXML
    public void initialize() {
        loginManager = new LoginManager(); // Create instance
    }


    @FXML
    private void navigateToHome() {
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    @FXML
    private void navigateToSignup() {
        SceneManager.switchToScene(SceneManager.SIGNUP);
    }

    @FXML
    private void navigateToAbout() {
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    @FXML
    private void handleLogin() { // Renamed method tied to login button's onAction
        String username = usernameField.getText().trim();
        String password = passwordField.getText(); // Get password as is

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

        int loginStatus = loginManager.login(username, password);

        switch (loginStatus) {
            case 1: // Student login successful
                System.out.println("Student login successful for: " + username);
                User loggedInStudent = LoginManager.getSelectedUser();
                Object studentController = SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
                if (studentController instanceof StudentSceneController && loggedInStudent instanceof Student) {
                    ((StudentSceneController) studentController).setStudentData((Student) loggedInStudent);
                } else {
                    System.err.println("Error: Could not set student data in StudentSceneController.");
                    // Maybe navigate back to login?
                    SceneManager.switchToScene(SceneManager.LOGIN);
                }
                break;

            case 2: // Teacher login successful
                System.out.println("Teacher login successful for: " + username);
                User loggedInTeacher = LoginManager.getSelectedUser();
                Object teacherController = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
                if (teacherController instanceof TeacherSceneController && loggedInTeacher instanceof Teacher) {
                    ((TeacherSceneController) teacherController).setTeacherData((Teacher) loggedInTeacher);
                } else {
                    System.err.println("Error: Could not set teacher data in TeacherSceneController.");
                    // Maybe navigate back to login?
                    SceneManager.switchToScene(SceneManager.LOGIN);
                }
                break;

            case -1: // Username not found
                AlertMessage.showError("Login Failed", "Username '" + username + "' not found. Please check your username or sign up.");
                usernameField.requestFocus();
                break;

            case -2: // Incorrect password
                AlertMessage.showError("Login Failed", "Incorrect password. Please try again.");
                passwordField.requestFocus();
                break;

            default: // Other errors (e.g., unknown role)
                AlertMessage.showError("Login Failed", "An unexpected error occurred during login.");
                break;
        }
    }

    // Method kept for compatibility if FXML still calls navigateToDashboard
    @Deprecated
    @FXML
    private void navigateToDashboard() {
        handleLogin();
    }
}