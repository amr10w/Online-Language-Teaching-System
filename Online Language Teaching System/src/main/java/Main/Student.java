
package Main;

import fileManager.FileManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Represents a student user in the LinguaLearn system.
 * Extends User and implements the Progress interface to track learning progress.
 */
public class Student extends User implements Progress {



    private String language; // The primary language the student is learning
    // Internal maps to track progress
    private final Map<String, Boolean> completedLessons;
    private final Map<String, Integer> quizScores;

    /**
     * Creates a new Student object.
     *
     * @param username The student's username (used as ID).
     * @param email    The student's email address.
     * @param password The student's password (should be hashed in production).
     * @param ID       The unique ID for the student (typically same as username).
     * @param language The language the student is learning.
     * @throws IllegalArgumentException if User constructor constraints are violated or language is empty.
     */
    public Student(String username, String email, String password, String ID, String language) {
        super(username, email, password, ID); // Call User constructor

        if (language == null || language.trim().isEmpty()) {
            // throw new IllegalArgumentException("Student language cannot be empty.");
            System.err.println("Warning: Student language is empty for user " + username + ". Defaulting to 'English'.");
            this.language = "English"; // Default language
        } else {
            this.language = language.trim();
        }

        // Initialize progress tracking maps
        this.completedLessons = new HashMap<>();
        this.quizScores = new HashMap<>();

        // Attempt to load progress data when the student object is created
        // Note: This happens *after* the user is loaded by ApplicationManager
        // loadProgressData(); // Moved loading to ApplicationManager.loadAllStudentProgress()
    }

    /**
     * Gets the language the student is learning.
     * @return The language name.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language the student is learning.
     * Note: Need to consider how this affects progress tracking and data persistence.
     * @param language The new language name.
     */
    public void setLanguage(String language) {
        if (language != null && !language.trim().isEmpty()) {
            this.language = language.trim();
            // TODO: Consider if progress should be reset or handled differently when language changes.
            // TODO: Need to save the updated language in the main users.txt file (requires more complex FileManager logic).
            System.out.println("Student " + getUsername() + " language set to: " + this.language);
        } else {
            System.err.println("Attempted to set empty language for student " + getUsername());
        }
    }


    // --- Progress Interface Implementation ---

    @Override
    public String getUserId() {
        return getUsername(); // Use username as the unique identifier for progress files etc.
    }

    @Override
    public synchronized void markLessonCompleted(String lessonId) { // Added synchronized
        if (lessonId != null && !lessonId.trim().isEmpty()) {
            String trimmedId = lessonId.trim();
            if (!completedLessons.containsKey(trimmedId)) { // Only mark if not already completed
                completedLessons.put(trimmedId, true);
                System.out.println("Student " + getUsername() + " marked lesson '" + trimmedId + "' as completed.");
                saveProgressData(); // Save progress immediately after change
            } else {
                // Optionally log or ignore if already completed
                // System.out.println("Lesson '" + trimmedId + "' was already marked complete for " + getUsername());
            }
        } else {
            System.err.println("Attempted to mark null or empty lesson ID as complete for " + getUsername());
        }
    }

    @Override
    public synchronized void recordQuizScore(String quizId, int score) { // Added synchronized
        if (quizId != null && !quizId.trim().isEmpty()) {
            String trimmedId = quizId.trim();
            // Allow updating scores (e.g., if quiz is retaken)
            quizScores.put(trimmedId, score);
            System.out.println("Student " + getUsername() + " recorded score " + score + " for quiz '" + trimmedId + "'.");
            saveProgressData(); // Save progress immediately after change
        } else {
            System.err.println("Attempted to record score for null or empty quiz ID for " + getUsername());
        }
    }

    @Override
    public boolean isLessonCompleted(String lessonId) {
        if (lessonId == null || lessonId.trim().isEmpty()) return false;
        // getOrDefault is safe for null keys, but check avoids unnecessary lookup
        return completedLessons.getOrDefault(lessonId.trim(), false);
    }

    @Override
    public int getQuizScore(String quizId) {
        if (quizId == null || quizId.trim().isEmpty()) return -1; // Indicate not taken/found
        // Returns score or -1 if not found in the map
        return quizScores.getOrDefault(quizId.trim(), -1);
    }

    @Override
    public Map<String, Boolean> getCompletedLessons() {
        return new HashMap<>(completedLessons); // Return a defensive copy
    }

    @Override
    public Map<String, Integer> getQuizScores() {
        return new HashMap<>(quizScores); // Return a defensive copy
    }

    @Override
    public double calculateOverallProgress() {
        // Basic example: Progress based on number of completed lessons relative to *available* lessons.
        // This needs access to the list of relevant lessons, which might be loaded elsewhere (e.g., StudentSceneController).
        // For a simple calculation here, we might need to pass the total lesson count.

        // Let's refine this: Calculate based *only* on the lessons the student has interacted with (completed).
        // This isn't ideal, as it doesn't reflect progress through the whole curriculum.
        // A better approach involves getting total lesson count from ApplicationManager or Language object.

        // Placeholder calculation (needs total lesson count for the language):
        int totalLessonsForLanguage = 50; // !!! HARDCODED PLACEHOLDER - REPLACE WITH DYNAMIC COUNT !!!
        if (totalLessonsForLanguage <= 0) {
            return completedLessons.isEmpty() ? 0.0 : 100.0; // Avoid division by zero; 100% if completed any with 0 total? Or 0%?
        }
        return Math.min(100.0, (double) completedLessons.size() / totalLessonsForLanguage * 100.0); // Cap at 100%
    }


    // --- Progress Data Persistence Implementation ---

    /** Returns the expected file path for this student's progress data. */
    private Path getProgressFilePath() {
        String progressDir = ApplicationManager.getProgressDirectory();
        if (progressDir == null) return null;
        // Sanitize username for use in filename if necessary (e.g., replace invalid chars)
        String safeUsername = getUsername().replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return Paths.get(progressDir, "progress_" + safeUsername + ".txt");
    }

    @Override
    public synchronized void loadProgressData() { // Added synchronized
        Path progressFile = getProgressFilePath();
        if (progressFile == null) {
            System.err.println("Could not determine progress file path for student " + getUsername() + ". Progress not loaded.");
            return;
        }

        // Clear current progress before loading
        this.completedLessons.clear();
        this.quizScores.clear();

        if (!Files.exists(progressFile)) {
            System.out.println("No progress file found for student " + getUsername() + " at " + progressFile + ". Starting fresh.");
            return; // No data to load
        }

        System.out.println("Loading progress data for " + getUsername() + " from " + progressFile);
        try (BufferedReader reader = Files.newBufferedReader(progressFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) continue; // Use '=' as separator

                int separatorIndex = line.indexOf('=');
                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).trim();

                if (key.startsWith("lesson_")) {
                    String lessonId = key.substring("lesson_".length());
                    if (Boolean.parseBoolean(value)) { // Only store completed lessons
                        completedLessons.put(lessonId, true);
                    }
                } else if (key.startsWith("quiz_")) {
                    String quizId = key.substring("quiz_".length());
                    try {
                        int score = Integer.parseInt(value);
                        quizScores.put(quizId, score);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid score format for quiz '" + quizId + "' in progress file " + progressFile + ". Value: " + value);
                    }
                } else {
                    System.err.println("Warning: Unknown key format in progress file " + progressFile + ". Key: " + key);
                }
            }
            System.out.println("Progress loaded for " + getUsername() + ": " + completedLessons.size() + " lessons, " + quizScores.size() + " quizzes.");
        } catch (IOException e) {
            System.err.println("Error loading progress data for " + getUsername() + " from " + progressFile + ": " + e.getMessage());
            e.printStackTrace();
            // Don't clear maps here, might have partial data loaded? Or clear for safety?
            this.completedLessons.clear();
            this.quizScores.clear();
        }
    }

    @Override
    public synchronized boolean saveProgressData() { // Added synchronized
        Path progressFile = getProgressFilePath();
        if (progressFile == null) {
            System.err.println("Could not determine progress file path for student " + getUsername() + ". Progress not saved.");
            return false;
        }

        System.out.println("Saving progress data for " + getUsername() + " to " + progressFile);
        try (BufferedWriter writer = Files.newBufferedWriter(progressFile)) { // Overwrites existing file
            // Save completed lessons
            for (Map.Entry<String, Boolean> entry : completedLessons.entrySet()) {
                // Only save if value is true (completed)
                if (Boolean.TRUE.equals(entry.getValue())) {
                    writer.write("lesson_" + entry.getKey() + "=true");
                    writer.newLine();
                }
            }

            // Save quiz scores
            for (Map.Entry<String, Integer> entry : quizScores.entrySet()) {
                writer.write("quiz_" + entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
            System.out.println("Progress saved successfully for " + getUsername());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving progress data for " + getUsername() + " to " + progressFile + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // --- Overrides ---

    @Override
    public String toString() {
        // More informative toString for debugging
        return "Student[Username='" + getUsername() + "', Email='" + getEmail() + "', ID='" + getID() + "', Language='" + language + "']";
    }

    // Equals and hashCode are inherited from User and are based on ID/username, which is usually sufficient.
    // If student-specific fields needed to affect equality, override them here.
    @Override
    public boolean equals(Object o) {
        return super.equals(o); // Relies on User's equals (ID/Username)
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // Relies on User's hashCode (ID/Username)
    }
}