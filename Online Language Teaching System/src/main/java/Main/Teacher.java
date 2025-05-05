package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Teacher extends User {
    private String language; // Language the teacher primarily teaches
    private double balance; // Placeholder for potential payment features
    private List<String> createdLessonIds; // Keep track of lessons created by this teacher
    private List<String> createdQuizIds;   // Keep track of quizzes created by this teacher

    public Teacher(String name, String email, String password, String ID, String language) {
        super(name, email, password, ID); // ID likely should be username
        this.language = (language != null && !language.trim().isEmpty()) ? language.trim() : "Unknown";
        this.balance = 0.0; // Initialize balance
        this.createdLessonIds = new ArrayList<>();
        this.createdQuizIds = new ArrayList<>();
        // TODO: Load teacher-specific data if stored separately (e.g., created content lists)
    }

    public String getLanguage() {
        return language;
    }

    public double getBalance() {
        return balance;
    }

    // Method to be called when a lesson is successfully created and saved
    public void addCreatedLesson(String lessonId) {
        if (lessonId != null && !lessonId.isEmpty() && !createdLessonIds.contains(lessonId)) {
            createdLessonIds.add(lessonId);
            // TODO: Persist this change if needed
            System.out.println("Teacher " + getUsername() + " added created lesson: " + lessonId);
        }
    }

    // Method to be called when a quiz is successfully created and saved
    public void addCreatedQuiz(String quizId) {
        if (quizId != null && !quizId.isEmpty() && !createdQuizIds.contains(quizId)) {
            createdQuizIds.add(quizId);
            // TODO: Persist this change if needed
            System.out.println("Teacher " + getUsername() + " added created quiz: " + quizId);
        }
    }


    public List<String> getCreatedLessonIds() {
        return new ArrayList<>(createdLessonIds); // Return copy
    }

    public List<String> getCreatedQuizIds() {
        return new ArrayList<>(createdQuizIds); // Return copy
    }

    public int getNumberOfCreatedLessons() {
        return createdLessonIds.size();
    }
    public int getNumberOfCreatedQuizzes() {
        return createdQuizIds.size();
    }


    // --- Placeholders for Edit Functionality (Require more implementation) ---

    public boolean editLesson(String lessonId, String newTitle, String newContent, String newLevel) {
        // 1. Find the lesson file (e.g., using lessonId)
        // 2. Load the Lesson object
        // 3. Modify its properties
        // 4. Save the Lesson object back to the file
        // 5. Return true if successful, false otherwise
        System.out.println("Attempting to edit lesson: " + lessonId + " (Not fully implemented)");
        // Lesson lessonToEdit = findLessonById(lessonId); // Need a way to find/load the lesson
        // if (lessonToEdit != null && lessonToEdit.getTeacherId().equals(this.getID())) { // Check ownership
        //    lessonToEdit.setTitle(newTitle); ...
        //    return lessonToEdit.saveToFile();
        // }
        return false;
    }

    public boolean editQuiz(String quizId /*, Updated quiz data */) {
        // Similar logic to editLesson for quizzes
        System.out.println("Attempting to edit quiz: " + quizId + " (Not fully implemented)");
        return false;
    }

    // --- Overrides ---

    @Override
    public String toString() {
        return "Teacher{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", ID='" + getID() + '\'' +
                ", language='" + language + '\'' +
                ", lessonsCreated=" + createdLessonIds.size() +
                '}';
    }

    // Ensure equals/hashCode consider Teacher-specific fields if necessary,
    // but User's implementation based on ID/username might be sufficient.
    @Override
    public boolean equals(Object o) {
        return super.equals(o); // Relies on User's equals
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // Relies on User's hashCode
    }
}