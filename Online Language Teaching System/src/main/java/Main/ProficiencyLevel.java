package Main;

// Consider using an Enum for fixed levels for type safety
// public enum ProficiencyLevel { BEGINNER, INTERMEDIATE, ADVANCED }

import javafx.beans.value.ObservableValue;

public class ProficiencyLevel {
    private String level; // Store as lowercase internally for consistency

    private static final String BEGINNER = "beginner";
    private static final String INTERMEDIATE = "intermediate";
    private static final String ADVANCED = "advanced";

    public ProficiencyLevel() {
        this.level = BEGINNER; // Default level
    }

    public ProficiencyLevel(String level) {
        setLevel(level); // Use setter for validation
    }

    public String getLevel() {
        // Return with first letter capitalized for display perhaps
        if (level == null || level.isEmpty()) return "";
        return level.substring(0, 1).toUpperCase() + level.substring(1);
    }

    // Internal getter for consistent checks
    private String getLevelInternal() {
        return level;
    }

    public void setLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            System.err.println("Error: ProficiencyLevel cannot be set to null or empty. Using default 'Beginner'.");
            this.level = BEGINNER;
            return;
        }

        String lowerCaseLevel = level.trim().toLowerCase();

        if (lowerCaseLevel.equals(BEGINNER) || lowerCaseLevel.equals(INTERMEDIATE) || lowerCaseLevel.equals(ADVANCED)) {
            this.level = lowerCaseLevel;
        } else {
            System.err.println("Error: Invalid ProficiencyLevel '" + level + "'. Must be Beginner, Intermediate, or Advanced. Using default 'Beginner'.");
            this.level = BEGINNER; // Default to beginner on invalid input
        }
    }

    @Override
    public String toString() {
        return getLevel(); // Return formatted level
    }


}