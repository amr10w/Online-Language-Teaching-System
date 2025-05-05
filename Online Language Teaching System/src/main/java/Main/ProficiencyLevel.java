// src/main/java/Main/ProficiencyLevel.java
package Main;

import java.util.Objects;

/**
 * Represents the proficiency level (Beginner, Intermediate, Advanced).
 * Ensures valid levels are used. Stores level internally as lowercase.
 */
public class ProficiencyLevel {
    private String level; // Store as lowercase internally for consistency

    // Define constants for valid levels (internal representation)
    public static final String BEGINNER = "beginner";
    public static final String INTERMEDIATE = "intermediate";
    public static final String ADVANCED = "advanced";

    private static final String DEFAULT_LEVEL = BEGINNER;

    /**
     * Default constructor, initializes level to Beginner.
     */
    public ProficiencyLevel() {
        this.level = DEFAULT_LEVEL;
    }

    /**
     * Constructor that initializes the level with validation.
     * @param levelString The level name (e.g., "Beginner", "intermediate"). Case-insensitive.
     */
    public ProficiencyLevel(String levelString) {
        setLevel(levelString); // Use setter for validation logic
    }

    /**
     * Gets the proficiency level formatted for display (e.g., "Beginner").
     * @return The formatted level string, or "Unknown" if the internal level is invalid (shouldn't happen with validation).
     */
    public String getLevel() {
        if (level == null || level.isEmpty()) {
            return "Unknown"; // Should ideally not happen due to validation
        }
        // Capitalize the first letter for display
        return level.substring(0, 1).toUpperCase() + level.substring(1);
    }

    /**
     * Gets the internal lowercase representation of the level.
     * Useful for saving or comparisons.
     * @return The level string in lowercase (e.g., "beginner").
     */
    public String getLevelInternal() {
        return level;
    }


    /**
     * Sets the proficiency level with validation.
     * Accepts "Beginner", "Intermediate", or "Advanced" (case-insensitive).
     * Defaults to Beginner if the input is invalid or null/empty.
     * @param levelString The level name to set.
     */
    public void setLevel(String levelString) {
        if (levelString == null || levelString.trim().isEmpty()) {
            System.err.println("Warning: ProficiencyLevel cannot be set to null or empty. Using default '" + getFormattedLevel(DEFAULT_LEVEL) + "'.");
            this.level = DEFAULT_LEVEL;
            return;
        }

        String lowerCaseLevel = levelString.trim().toLowerCase();

        switch (lowerCaseLevel) {
            case BEGINNER:
            case INTERMEDIATE:
            case ADVANCED:
                this.level = lowerCaseLevel;
                break;
            default:
                System.err.println("Error: Invalid ProficiencyLevel '" + levelString + "'. Must be Beginner, Intermediate, or Advanced. Using default '" + getFormattedLevel(DEFAULT_LEVEL) + "'.");
                this.level = DEFAULT_LEVEL; // Default to beginner on invalid input
                break;
        }
    }

    // Helper to format internal constants for messages
    private static String getFormattedLevel(String internalLevel) {
        return internalLevel.substring(0, 1).toUpperCase() + internalLevel.substring(1);
    }

    @Override
    public String toString() {
        return getLevel(); // Return display-formatted level
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProficiencyLevel that = (ProficiencyLevel) o;
        // Levels are equal if their internal lowercase representations match
        return Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }
}