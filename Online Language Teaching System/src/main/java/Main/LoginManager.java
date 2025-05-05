// src/main/java/Main/LoginManager.java
package Main;

// No need for FileManager here directly if ApplicationManager handles loading/saving users
// import fileManager.FileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages user login, signup, and the current user session.
 * Relies on ApplicationManager for accessing user data and persistence.
 */
public class LoginManager {

    // Static field to hold the currently logged-in user
    private static User selectedUser = null;

    /**
     * Constructor - currently does nothing as functionality is static or relies on ApplicationManager.
     */
    public LoginManager() {
        // Initialization logic can go here if needed in the future
    }

    /**
     * Registers a new user.
     * Performs validation, adds the user via ApplicationManager (which handles persistence).
     *
     * @param user The User object representing the new user.
     * @return true if signup was successful (user validated and added), false otherwise.
     */
    public boolean signup(User user) {
        if (user == null) {
            System.err.println("Signup failed: User object is null.");
            return false;
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.err.println("Signup failed: Username cannot be empty.");
            return false;
        }

        // 1. Check if username already exists (case-insensitive) via ApplicationManager
        if (ApplicationManager.findUserByUsername(user.getUsername()) != null) {
            System.err.println("Signup failed: Username '" + user.getUsername() + "' already exists.");
            return false; // Username already taken
        }

        // 2. Add user via ApplicationManager (this handles runtime addition and saving)
        // Wrap in try-catch in case ApplicationManager.addUser throws an exception (e.g., save fails)
        try {
            ApplicationManager.addUser(user);
            System.out.println("User '" + user.getUsername() + "' signed up successfully.");
            // Optionally, log the user in immediately after signup:
            // setSelectedUser(user);
            return true;
        } catch (Exception e) {
            System.err.println("Signup failed during user addition/saving for '" + user.getUsername() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to log in a user using username and password.
     * Performs validation and checks credentials against data loaded by ApplicationManager.
     * Updates the currently selected user on success.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return Status code: 1 for student login, 2 for teacher login, -1 for user not found, -2 for incorrect password.
     */
    public int login(String username, String password) {
        // Basic input validation
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Login failed: Username is empty.");
            return -1; // Indicate user not found (or invalid input)
        }
        if (password == null || password.isEmpty()) {
            System.err.println("Login failed: Password is empty.");
            return -2; // Indicate incorrect password (or invalid input)
        }

        // Find user via ApplicationManager (case-insensitive lookup)
        User user = ApplicationManager.findUserByUsername(username);

        if (user == null) {
            System.out.println("Login failed: Username '" + username + "' not found.");
            return -1; // User not found
        }

        // --- Password Verification ---
        // !!! VERY INSECURE - PLAIN TEXT COMPARISON !!!
        // In a real application, you MUST hash the stored password and compare
        // the hash of the entered password with the stored hash.
        // Example using simple equals (replace with secure comparison):
        if (!user.getPassword().equals(password)) {
            System.out.println("Login failed: Incorrect password for username '" + username + "'.");
            // Consider adding slight delay here to mitigate timing attacks
            return -2; // Incorrect password
        }
        // --- End Password Verification ---


        // Login successful!
        setSelectedUser(user); // Set the currently logged-in user session
        System.out.println("Login successful for user: " + username + " (Role: " + user.getClass().getSimpleName() + ")");

        // Return code based on user type
        if (user instanceof Student) {
            return 1; // Student logged in
        } else if (user instanceof Teacher) {
            return 2; // Teacher logged in
        } else {
            System.err.println("Login Warning: Logged in user '" + username + "' has an unknown role.");
            // Logged in successfully, but role is unexpected
            return 0; // Or another code indicating successful login but unknown role
        }
    }

    /**
     * Checks if a username is available (not already registered).
     * Performs a case-insensitive check.
     * @param username The username to check.
     * @return true if the username is available, false if it's already taken or invalid.
     */
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false; // Empty username is not available for registration
        }
        // Check against the user list loaded by ApplicationManager
        return ApplicationManager.findUserByUsername(username) == null;
    }

    // --- Static Session Management Methods ---

    /**
     * Sets the currently logged-in user. Use `null` to clear the session (logout).
     * @param user The user who logged in, or null to log out.
     */
    public static synchronized void setSelectedUser(User user) { // Added synchronized
        selectedUser = user;
        if (user != null) {
            System.out.println("Current user session set to: " + user.getUsername());
        } else {
            System.out.println("Current user session cleared (logout).");
        }
    }

    /**
     * Gets the currently logged-in user.
     * @return The logged-in User object, or null if no one is logged in.
     */
    public static User getSelectedUser() {
        return selectedUser;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public static void logout() {
        setSelectedUser(null);
    }
}