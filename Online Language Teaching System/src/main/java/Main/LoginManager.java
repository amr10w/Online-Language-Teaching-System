package Main;

import fileManager.FileManager; // FileManager might not be needed here directly if ApplicationManager handles loading
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginManager {

    // No need for FileManager here if ApplicationManager loads users at startup
    // private FileManager fm;
    // private List<String> users; // Don't store raw lists here
    // private List<String> passwords; // Don't store raw lists here

    private static User selectedUser; // Keep track of the currently logged-in user

    public LoginManager() {
        // Constructor might not need to do anything if users are loaded by ApplicationManager
        // fm = new FileManager(ApplicationManager.getUsersFilePath());
        // users = new ArrayList<>();
        // passwords = new ArrayList<>();
    }

    /**
     * Registers a new user.
     * Adds the user to the runtime list and saves to the persistent store.
     * @param user The User object to register.
     * @return true if signup was successful (user added and saved), false otherwise.
     */
    public boolean signup(User user) {
        if (user == null) {
            System.err.println("Signup failed: User object is null.");
            return false;
        }
        // 1. Check if username already exists in the runtime list
        if (ApplicationManager.findUserByUsername(user.getUsername()) != null) {
            System.err.println("Signup failed: Username '" + user.getUsername() + "' already exists.");
            return false; // Username already taken
        }

        // 2. Add user to runtime lists via ApplicationManager
        ApplicationManager.addUser(user);

        // 3. Prepare data for saving
        HashMap<String, String> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("password", user.getPassword()); // Consider hashing passwords!
        userData.put("email", user.getEmail());

        if (user instanceof Student) {
            userData.put("role", "student");
            userData.put("language", ((Student) user).getLanguage());
        } else if (user instanceof Teacher) {
            userData.put("role", "teacher");
            userData.put("language", ((Teacher) user).getLanguage());
        } else {
            System.err.println("Signup Warning: Unknown user type for " + user.getUsername());
            userData.put("role", "unknown"); // Or handle differently
            userData.put("language", "");
        }

        // 4. Save data using FileManager (append mode)
        FileManager fm = new FileManager(ApplicationManager.getUsersFilePath());
        if (fm.saveDataAppend(userData)) { // Use append method
            System.out.println("User '" + user.getUsername() + "' signed up and saved successfully.");
            // Optionally, log the user in immediately after signup
            // setSelectedUser(user);
            return true;
        } else {
            System.err.println("Signup failed: Could not save user data for '" + user.getUsername() + "'.");
            // Consider rolling back the addition to the runtime list if save fails critically
            // ApplicationManager.removeUser(user); // Need a removeUser method
            return false;
        }
    }

    /**
     * Attempts to log in a user.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return Status code: 1 for student login, 2 for teacher login, -1 for user not found, -2 for incorrect password.
     */
    public int login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            System.err.println("Login attempt with empty username or password.");
            return -1; // Or a different error code for invalid input
        }

        User user = ApplicationManager.findUserByUsername(username);

        if (user == null) {
            System.out.println("Login failed: Username '" + username + "' not found.");
            return -1; // User not found
        }

        // IMPORTANT: Compare passwords securely. This is a plain text comparison (INSECURE).
        // In a real application, hash the entered password and compare with the stored hash.
        if (!user.getPassword().equals(password)) {
            System.out.println("Login failed: Incorrect password for username '" + username + "'.");
            return -2; // Incorrect password
        }

        // Login successful
        setSelectedUser(user); // Set the currently logged-in user
        System.out.println("Login successful for user: " + username);

        if (user instanceof Student) {
            return 1; // Student logged in
        } else if (user instanceof Teacher) {
            return 2; // Teacher logged in
        } else {
            System.err.println("Login Warning: Logged in user '" + username + "' has an unknown role.");
            return 0; // Or another code for unknown role logged in
        }
    }

    /**
     * Checks if a username is already taken.
     * @param username The username to check.
     * @return true if the username is available, false if it's already taken.
     */
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false; // Cannot register empty username
        }
        return ApplicationManager.findUserByUsername(username) == null;
    }

    // Static methods to manage the selected user session
    public static void setSelectedUser(User user) {
        selectedUser = user;
        if (user != null) {
            System.out.println("Current user set to: " + user.getUsername());
        } else {
            System.out.println("Current user session cleared (logout).");
        }
    }

    public static User getSelectedUser() {
        return selectedUser;
    }

    public static void logout() {
        setSelectedUser(null);
    }
}