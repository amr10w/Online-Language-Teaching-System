package Main;

public interface Progress {
    String getUserId();
    void makeLessonCompleted(String lessonId);
    void recordQuizScore(String quizId, int score);
    boolean isLessonCompleted(String lessonId);
    int getQuizScore(String quizId);

}
