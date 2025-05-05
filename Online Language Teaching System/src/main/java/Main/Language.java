// Main/Language.java
package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Language {
    private List<Lesson> lessons = new ArrayList<>();
    private List<Quiz> quizzes = new ArrayList<>();

    abstract public String getLanguageName();

    // Maybe add a constructor if needed

    public List<Lesson> getLessons() {
        // Consider loading lessons dynamically here if not done elsewhere
        Collections.sort(lessons); // Sorts by Lesson's compareTo (title)
        return new ArrayList<>(lessons); // Return copy
    }

    public List<Quiz> getQuizzes() {
        // Consider loading quizzes dynamically here if not done elsewhere
        Collections.sort(quizzes); // Sorts by Quiz's compareTo (title)
        return new ArrayList<>(quizzes); // Return copy
    }

    // Setters might not be the best approach if loading happens elsewhere.
    // Consider add methods more reliable.
    // public void setLessons(List<Lesson> lessons) {
    //     this.lessons = new ArrayList<>(lessons);
    // }

    // public void setQuizzes(List<Quiz> quizzes) {
    //     this.quizzes = new ArrayList<>(quizzes);
    // }

    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }
        if (!this.lessons.contains(lesson)) {
            this.lessons.add(lesson);
        }
    }

    public void addQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        if (!this.quizzes.contains(quiz)) {
            this.quizzes.add(quiz);
        }
    }

    // Method to find a lesson by ID within this language
    public Lesson findLessonById(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    // Method to find a quiz by ID within this language
    public Quiz findQuizById(String quizId) {
        for (Quiz quiz : quizzes) {
            if (quiz.getQuizId().equals(quizId)) {
                return quiz;
            }
        }
        return null;
    }
}
