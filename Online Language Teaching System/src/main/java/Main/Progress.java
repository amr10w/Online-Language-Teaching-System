package Main;

import java.util.Map; // Added for potential return type

public interface Progress {
    String getUserId(); // Get the ID of the user this progress belongs to

    void markLessonCompleted(String lessonId); // Changed method name for clarity
    void recordQuizScore(String quizId, int score);

    boolean isLessonCompleted(String lessonId);
    int getQuizScore(String quizId); // Returns score, maybe -1 if not taken

    // Optional: Methods to get all progress data
    Map<String, Boolean> getCompletedLessons();
    Map<String, Integer> getQuizScores();

    double calculateOverallProgress(); // Example: Percentage based on completed lessons/quizzes
}