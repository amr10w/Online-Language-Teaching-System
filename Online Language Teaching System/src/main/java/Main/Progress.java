package Main;

public interface Progress {
    String getUserId();
    int makeLessonCompleted(String title);
    void recordQuizScore(int score);
    boolean isLessonCompleted(String title);
    int getQuizScore(String quizId);

}
