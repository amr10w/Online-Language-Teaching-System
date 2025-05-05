// src/main/java/Main/Language.java
package Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for languages.
 * Manages lessons and quizzes associated with a specific language.
 * Note: Lesson/Quiz loading is currently handled elsewhere (e.g., StudentSceneController).
 * This class structure might be better if it actively loaded its content.
 */
public abstract class Language {
    // Instance variables to hold lessons and quizzes for this language instance
    // These lists might not be actively used if loading happens directly in controllers.
    private final List<Lesson> lessons = new ArrayList<>();
    private final List<Quiz> quizzes = new ArrayList<>();

    /**
     * Gets the name of the language (e.g., "English", "French").
     * @return The language name.
     */
    abstract public String getLanguageName();

    // --- Lesson Management ---

    /**
     * Gets a sorted list of lessons associated with this language.
     * Note: This method currently returns the internal list. Dynamic loading logic
     * based on getLanguageName() would be needed here for it to be truly useful.
     * @return A sorted list of lessons.
     */
    public List<Lesson> getLessons() {
        // Consider loading lessons dynamically here if not done elsewhere
        // loadLessonsForLanguage(getLanguageName()); // Example placeholder
        Collections.sort(lessons); // Sorts by Lesson's compareTo (title)
        return new ArrayList<>(lessons); // Return copy
    }

    /**
     * Adds a lesson to this language's collection.
     * @param lesson The lesson to add.
     * @throws IllegalArgumentException if the lesson is null.
     */
    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }
        // Check for duplicates based on ID before adding
        if (findLessonById(lesson.getLessonId()) == null) {
            this.lessons.add(lesson);
        }
    }

    /**
     * Finds a lesson by its ID within this language's loaded lessons.
     * @param lessonId The ID of the lesson to find.
     * @return The Lesson object if found, otherwise null.
     */
    public Lesson findLessonById(String lessonId) {
        if (lessonId == null || lessonId.isEmpty()) return null;
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    // --- Quiz Management ---

    /**
     * Gets a sorted list of quizzes associated with this language.
     * Note: Similar to getLessons(), dynamic loading would be needed.
     * @return A sorted list of quizzes.
     */
    public List<Quiz> getQuizzes() {
        // Consider loading quizzes dynamically here
        // loadQuizzesForLanguage(getLanguageName()); // Example placeholder
        Collections.sort(quizzes); // Sorts by Quiz's compareTo (title)
        return new ArrayList<>(quizzes); // Return copy
    }

    /**
     * Adds a quiz to this language's collection.
     * @param quiz The quiz to add.
     * @throws IllegalArgumentException if the quiz is null.
     */
    public void addQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        // Check for duplicates based on ID
        if (findQuizById(quiz.getQuizId()) == null) {
            this.quizzes.add(quiz);
        }
    }

    /**
     * Finds a quiz by its ID within this language's loaded quizzes.
     * @param quizId The ID of the quiz to find.
     * @return The Quiz object if found, otherwise null.
     */
    public Quiz findQuizById(String quizId) {
        if (quizId == null || quizId.isEmpty()) return null;
        for (Quiz quiz : quizzes) {
            if (quiz.getQuizId().equals(quizId)) {
                return quiz;
            }
        }
        return null;
    }

    // --- Dynamic Loading Placeholders (Example) ---

    // private void loadLessonsForLanguage(String languageName) {
    //     this.lessons.clear();
    //     File lessonsDir = new File(ApplicationManager.getLessonsDirectory());
    //     // Filter files based on languageName (e.g., file naming convention or metadata inside file)
    //     // Load matching files into this.lessons
    //     System.out.println("Dynamically loading lessons for " + languageName);
    // }
    //
    // private void loadQuizzesForLanguage(String languageName) {
    //     this.quizzes.clear();
    //     File quizzesDir = new File(ApplicationManager.getQuizzesDirectory());
    //     // Filter files based on languageName
    //     // Load matching files into this.quizzes
    //      System.out.println("Dynamically loading quizzes for " + languageName);
    // }


    @Override
    public String toString() {
        return getLanguageName();
    }

    // equals() and hashCode() based on language name for uniqueness if needed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return java.util.Objects.equals(getLanguageName(), language.getLanguageName());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getLanguageName());
    }
}