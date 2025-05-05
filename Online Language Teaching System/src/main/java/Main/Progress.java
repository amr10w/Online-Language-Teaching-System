// src/main/java/Main/Progress.java
package Main;

import java.util.Map;

/**
 * Interface defining methods for tracking user progress (lessons completed, quiz scores).
 * Typically implemented by the Student class.
 */
public interface Progress {

    /**
     * Gets the unique identifier of the user whose progress is being tracked.
     * @return The user ID (e.g., username).
     */
    String getUserId();

    /**
     * Marks a specific lesson as completed for the user.
     * Implementations should handle potential duplicates and persistence.
     * @param lessonId The unique ID of the lesson completed.
     */
    void markLessonCompleted(String lessonId);

    /**
     * Records the score obtained by the user on a specific quiz.
     * Implementations should handle potential updates to scores and persistence.
     * @param quizId The unique ID of the quiz taken.
     * @param score The score achieved (e.g., percentage).
     */
    void recordQuizScore(String quizId, int score);

    /**
     * Checks if a specific lesson has been marked as completed by the user.
     * @param lessonId The unique ID of the lesson to check.
     * @return true if the lesson is completed, false otherwise.
     */
    boolean isLessonCompleted(String lessonId);

    /**
     * Retrieves the score recorded for a specific quiz taken by the user.
     * @param quizId The unique ID of the quiz.
     * @return The recorded score, or a specific value (e.g., -1) if the quiz hasn't been taken or scored.
     */
    int getQuizScore(String quizId);

    /**
     * Gets a map containing all lessons marked as completed.
     * The key is the lesson ID, the value indicates completion (typically true).
     * @return A map of completed lesson IDs. Should return a copy to prevent external modification.
     */
    Map<String, Boolean> getCompletedLessons();

    /**
     * Gets a map containing all recorded quiz scores.
     * The key is the quiz ID, the value is the score.
     * @return A map of quiz IDs and their scores. Should return a copy.
     */
    Map<String, Integer> getQuizScores();

    /**
     * Calculates an overall progress metric for the user.
     * The calculation method depends on the application's logic (e.g., percentage of completed lessons/quizzes).
     * @return A double representing the overall progress (e.g., 75.5 for 75.5%).
     */
    double calculateOverallProgress();

    // --- Added Methods for Persistence ---

    /**
     * Loads the user's progress data from persistent storage (e.g., a file).
     * Should be called when the user object is created or logged in.
     */
    void loadProgressData();

    /**
     * Saves the user's current progress data to persistent storage.
     * Should be called whenever progress is updated (lesson completed, quiz scored).
     * @return true if saving was successful, false otherwise.
     */
    boolean saveProgressData();
}