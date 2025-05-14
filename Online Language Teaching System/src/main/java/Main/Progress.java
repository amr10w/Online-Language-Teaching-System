package Main;

public interface Progress {
    String getUserId();
    int makeLessonCompleted(int i);
    void recordQuizScore(int score);
    boolean isLessonCompleted(int i);
    int getQuizScore(String quizId);

}
