package Main;

import java.util.Objects;

/**
 * Abstract base class for users in the LinguaLearn system.
 * Provides common properties like username, email, password, and ID.
 */
public abstract class User {
    private String username; // Unique identifier, often used for login
    private String email;
    private String password; // WARNING: Stored as plain text - Highly insecure! Use hashing.
    private String ID;       // Another unique identifier, potentially the same as username.

    /**
     * Constructs a new User object.
     *
     * @param username The user's username. Cannot be null or empty.
     * @param email    The user's email address. Cannot be null or empty. Basic format check recommended.
     * @param password The user's password. Cannot be null or empty. Should be hashed before storing.
     * @param ID       The unique identifier for the user. If null or empty, defaults to the username.
     * @throws IllegalArgumentException if username, email, or password is null or empty.
     */
    public User(String username, String email, String password, String ID) {
        // --- Validation ---
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        // Basic email check (presence of @ and .) - can be improved with regex
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        // Password check - ensure it's not null or empty
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // --- Initialization ---
        this.username = username.trim();
        this.email = email.trim();
        this.password = password; // Store plain text (INSECURE)

        // Set ID: Use provided ID if valid, otherwise default to username
        if (ID == null || ID.trim().isEmpty()) {
            System.out.println("Warning: Provided ID is empty for user '" + this.username + "'. Using username as ID.");
            this.ID = this.username; // Default ID to username
        } else {
            this.ID = ID.trim();
        }
    }

    // --- Getters ---

    public String getUsername() { return username; }
    public String getEmail() { return email; }

    /**
     * Gets the user's password.
     * WARNING: Returning plain text password is insecure. Avoid using this if possible.
     * @return The plain text password.
     */
    public String getPassword() { return password; }

    public String getID() { return ID; }

    // --- Setters ---

    /**
     * Sets the user's password.
     * WARNING: This method accepts plain text. Hashing should occur before calling this,
     * or ideally, password changes should go through a dedicated, secure process.
     *
     * @param password The new plain text password. Cannot be null or empty.
     * @throws IllegalArgumentException if the new password is null or empty.
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty for user " + username);
        }
        this.password = password;
        // TODO: If implementing password change, ensure this change is saved persistently
        // This likely involves updating the users.txt file, which requires careful handling.
        System.out.println("Password updated in memory for user " + username + ". (Persistence NOT implemented)");
    }

    /**
     * Sets the user's email address.
     * @param email The new email address.
     * @throws IllegalArgumentException if the email is invalid.
     */
    public void setEmail(String email) {
        // Add validation similar to constructor
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email address provided for update.");
        }
        this.email = email.trim();
        // TODO: Persist email change to users.txt
        System.out.println("Email updated in memory for user " + username + ". (Persistence NOT implemented)");
    }

    // --- equals() and hashCode() ---

    /**
     * Compares this User object to another object for equality.
     * Two users are considered equal if their IDs match (case-sensitive).
     * If IDs are null, it falls back to comparing usernames (case-insensitive).
     *
     * @param o The object to compare with.
     * @return true if the objects represent the same user, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Allow comparison even if o is a subclass (Student or Teacher)
        if (o == null || !(o instanceof User)) return false;
        User user = (User) o;

        // Primary comparison using ID (case-sensitive)
        if (this.ID != null && user.ID != null) {
            return this.ID.equals(user.ID);
        }
        // Fallback to username (case-insensitive) if IDs are involved in nulls
        // Note: Username should generally not be null due to constructor validation
        if (this.username != null && user.username != null){
            return this.username.equalsIgnoreCase(user.username);
        }
        // If both ID and username comparisons fail (e.g., nulls involved unexpectedly)
        return false;
    }

    /**
     * Generates a hash code for the User object.
     * Based primarily on the ID, falling back to the lowercase username if ID is null.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        // Use ID if available, otherwise use lowercase username
        if (this.ID != null) {
            return Objects.hash(this.ID);
        } else if (this.username != null) {
            return Objects.hash(this.username.toLowerCase());
        } else {
            return Objects.hash((Object) null); // Should not happen with validation
        }
    }

    /**
     * Returns a string representation of the User object (excluding password).
     * @return A string representation for debugging.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[ID='" + ID + "', Username='" + username + "', Email='" + email + "']";
    }
}