import java.util.ArrayList;
import java.util.Collections;

public abstract class Language
{
    private ArrayList<Lesson> lessons = new ArrayList<>();
    private ArrayList<Quiz> quizzes = new ArrayList<>();

    abstract public String getLanguageName();

    public ArrayList<Lesson> getLessons() {
        Collections.sort(lessons);
        return lessons;
    }

    public ArrayList<Quiz> getQuizzes() {
        Collections.sort(quizzes);
        return quizzes;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void setQuizzes(ArrayList<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        lessons.add(lesson);
    }

    public void addQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        quizzes.add(quiz);
    }
}
/*
abstract class Language {
  -lessons: ArrayList<Lesson>
  -quizzes: ArrayList<Quiz>
  +getLanguageName(): String {abstract}
  +getLessons(): ArrayList<Lesson>
  +getQuizzes(): ArrayList<Quiz>
  +setLessons(lessons: ArrayList<Lesson>): void
  +setQuizzes(quizzes: ArrayList<Quiz>): void
  +addLesson(lesson: Lesson): void
  +addQuiz(quiz: Quiz): void
}
 */