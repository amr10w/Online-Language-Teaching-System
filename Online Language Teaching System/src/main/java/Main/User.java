package Main;

import java.util.Objects;

public abstract class User {
    private String username;
    private String email;
    private String password; // Plain text storage - VERY INSECURE! Use hashing in production.
    private String ID;       // Often the same as username, or a separate unique identifier

    public User(String username, String email, String password, String ID) {
        // Basic validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (email == null || email.trim().isEmpty()) { // Basic email format check could be added
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (password == null || password.isEmpty()) { // Allow potentially empty passwords? Usually not.
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (ID == null || ID.trim().isEmpty()) {
            // Default ID to username if not provided or empty
            System.out.println("Warning: ID is empty for user " + username + ". Using username as ID.");
            this.ID = username.trim();
        } else {
            this.ID = ID.trim();
        }

        this.username = username.trim();
        this.email = email.trim();
        this.password = password; // Store as is (insecure)

    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; } // Be careful exposing this directly
    public String getID() { return ID; }

    // Setter for password (e.g., for password change feature)
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
            // TODO: If implementing password change, ensure the change is saved persistently.
        } else {
            System.err.println("Attempted to set an empty password for user " + username);
        }
    }

    // --- equals() and hashCode() ---
    // Users should be considered equal if their IDs (or usernames) match.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; // Consider allowing comparison across Student/Teacher? If ID is unique, maybe.
        // If strict type matching is needed: if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Primarily compare by ID, fallback to username if ID logic changes
        return Objects.equals(ID, user.ID) || Objects.equals(username.toLowerCase(), user.username.toLowerCase());
    }

    @Override
    public int hashCode() {
        // Hash based on the primary identifier used in equals()
        return Objects.hash(ID != null ? ID : username.toLowerCase());
    }

    // Basic toString for debugging (avoid printing password)
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", ID='" + ID + '\'' +
                '}';
    }
}