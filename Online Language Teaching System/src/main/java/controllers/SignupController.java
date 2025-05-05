package controllers;

import Main.*; // Import necessary classes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class SignupController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton teacherRadio;
    @FXML private ToggleGroup userTypeGroup; // Make sure fx:id is set in FXML
    @FXML private ComboBox<String> languageComboBox;
    @FXML private Button signUpButton; // Renamed from signUp
    @FXML private Button navigateToHome;
    @FXML private Button navigateToLogin;
    @FXML private Button navigateToAbout;

    private LoginManager loginManager; // Use LoginManager for signup logic

    @FXML
    public void initialize() {
        loginManager = new LoginManager(); // Initialize LoginManager

        // Populate language choices
        ObservableList<String> languages = FXCollections.observableArrayList("English", "German", "French"); // Add more as needed
        languageComboBox.setItems(languages);
        languageComboBox.setPromptText("Select language..."); // More informative prompt

        // Ensure at least one radio button is selected by default (optional)
        // studentRadio.setSelected(true); // Default to student
    }

    @FXML
    private void navigateToHome() {
        SceneManager.switchToScene(SceneManager.MAIN_SCENE);
    }

    @FXML
    private void navigateToLogin() {
        SceneManager.switchToScene(SceneManager.LOGIN);
    }

    @FXML
    private void navigateToAbout() {
        SceneManager.switchToScene(SceneManager.ABOUT);
    }

    @FXML
    private void handleSignup() { // Renamed method connected to signup button
        // --- 1. Get Input ---
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText(); // Get password
        String selectedLanguage = languageComboBox.getValue();
        RadioButton selectedRoleRadio = (RadioButton) userTypeGroup.getSelectedToggle();

        // --- 2. Validate Input ---
        if (username.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a username.");
            usernameField.requestFocus();
            return;
        }
        // Basic email validation (contains @)
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            AlertMessage.showWarning("Input Required", "Please enter a valid email address.");
            emailField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            AlertMessage.showWarning("Input Required", "Please enter a password.");
            passwordField.requestFocus();
            return;
        }
        // Basic password length check (example)
        if (password.length() < 6) {
            AlertMessage.showWarning("Password Too Short", "Password must be at least 6 characters long.");
            passwordField.requestFocus();
            return;
        }
        if (selectedLanguage == null) {
            AlertMessage.showWarning("Input Required", "Please select a language.");
            languageComboBox.requestFocus();
            return;
        }
        if (selectedRoleRadio == null) {
            AlertMessage.showWarning("Input Required", "Please select whether you are a Student or a Teacher.");
            studentRadio.requestFocus(); // Focus on the first radio button
            return;
        }

        // Check username availability BEFORE creating user object
        if (!loginManager.isUsernameAvailable(username)) {
            AlertMessage.showError("Username Taken", "The username '" + username + "' is already taken. Please choose another one.");
            usernameField.requestFocus();
            return;
        }


        // --- 3. Create User Object ---
        User newUser = null;
        String role = selectedRoleRadio.getText(); // Get text ("Student" or "Teacher")

        // ID generation - using username is simple, but could use UUID or sequence
        String userId = username;

        try {
            if ("Student".equalsIgnoreCase(role)) {
                newUser = new Student(username, email, password, userId, selectedLanguage);
            } else if ("Teacher".equalsIgnoreCase(role)) {
                newUser = new Teacher(username, email, password, userId, selectedLanguage);
            } else {
                AlertMessage.showError("Internal Error", "Invalid role selected.");
                return; // Should not happen if RadioButtons are set up correctly
            }
        } catch (IllegalArgumentException e) {
            AlertMessage.showError("Invalid Data", "Error creating user: " + e.getMessage());
            return;
        }


        // --- 4. Perform Signup using LoginManager ---
        boolean signupSuccess = loginManager.signup(newUser);

        // --- 5. Handle Result ---
        if (signupSuccess) {
            AlertMessage.showInformation("Signup Successful", "Welcome, " + username + "! Your account has been created.");

            // Automatically log the user in and navigate to dashboard
            // LoginManager automatically sets the selected user on successful signup if desired,
            // otherwise, call login explicitly here. For now, just navigate.
            LoginManager.setSelectedUser(newUser); // Set the session user

            if (newUser instanceof Student) {
                Object controller = SceneManager.switchToScene(SceneManager.STUDENT_DASHBOARD);
                if (controller instanceof StudentSceneController) {
                    ((StudentSceneController) controller).setStudentData((Student) newUser);
                }
            } else { // Must be Teacher
                Object controller = SceneManager.switchToScene(SceneManager.TEACHER_DASHBOARD);
                if (controller instanceof TeacherSceneController) {
                    ((TeacherSceneController) controller).setTeacherData((Teacher) newUser);
                }
            }
        } else {
            // Specific error should have been shown by LoginManager or caught earlier
            AlertMessage.showError("Signup Failed", "Could not create your account. Please check the details or try again later.");
        }
    }


    // Method kept for compatibility if FXML calls navigateToDashboard
    @Deprecated
    @FXML
    private void navigateToDashboard() {
        handleSignup();
    }
}