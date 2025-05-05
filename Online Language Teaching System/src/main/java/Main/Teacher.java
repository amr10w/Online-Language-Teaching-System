package Main;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a teacher user in the LinguaLearn system.
 * Extends User and adds properties/methods specific to teachers,
 * like the language taught and lists of created content.
 */
public class Teacher extends User {
    private String language; // Language the teacher primarily teaches
    private double balance; // Placeholder for potential payment features
    // Store IDs of content created by this teacher. Loaded/updated from Lesson/Quiz objects.
    private final List<String> createdLessonIds;
    private final List<String> createdQuizIds;

    /**
     * Creates a new Teacher object.
     *
     * @param username The teacher's username (used as ID).
     * @param email    The teacher's email address.
     * @param password The teacher's password (should be hashed in production).
     * @param ID       The unique ID for the teacher (typically same as username).
     * @param language The primary language the teacher teaches.
     * @throws IllegalArgumentException if User constructor constraints are violated or language is empty.
     */
    public Teacher(String username, String email, String password, String ID, String language) {
        super(username, email, password, ID); // Call User constructor

        if (language == null || language.trim().isEmpty()) {
            // throw new IllegalArgumentException("Teacher language cannot be empty.");
            System.err.println("Warning: Teacher language is empty for user " + username + ". Defaulting to 'English'.");
            this.language = "English"; // Default language
        } else {
            this.language = language.trim();
        }

        this.balance = 0.0; // Initialize balance
        // Initialize lists - These should ideally be populated by scanning content files on load
        this.createdLessonIds = new ArrayList<>();
        this.createdQuizIds = new ArrayList<>();

        // TODO: Populate createdLessonIds and createdQuizIds upon loading the teacher.
        // This would involve scanning the lessons/quizzes directories and checking the
        // teacherId field within each file, then adding the corresponding lesson/quiz ID
        // to these lists. This is more robust than relying on addCreated... methods alone.
        // Example placeholder call:
        // populateCreatedContentLists();
    }


    // --- Getters and Setters ---

    /**
     * Gets the primary language taught by the teacher.
     * @return The language name.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the primary language taught by the teacher.
     * @param language The new language name.
     */
    public void setLanguage(String language) {
        if (language != null && !language.trim().isEmpty()) {
            this.language = language.trim();
            // TODO: Save updated language to users.txt
            System.out.println("Teacher " + getUsername() + " language set to: " + this.language);
        } else {
            System.err.println("Attempted to set empty language for teacher " + getUsername());
        }
    }


    /**
     * Gets the teacher's current balance (placeholder).
     * @return The balance amount.
     */
    public double getBalance() {
        return balance;
    }

    // Method to add a lesson ID when a lesson is confirmed created by this teacher
    // Note: Less reliable than populating from file scan during load.
    public synchronized void addCreatedLesson(String lessonId) { // Added synchronized
        if (lessonId != null && !lessonId.isEmpty() && !createdLessonIds.contains(lessonId)) {
            createdLessonIds.add(lessonId);
            // TODO: Persist this change? Not strictly necessary if lists are populated on load.
            System.out.println("Teacher " + getUsername() + " tracked created lesson: " + lessonId);
        }
    }

    // Method to add a quiz ID when a quiz is confirmed created by this teacher
    public synchronized void addCreatedQuiz(String quizId) { // Added synchronized
        if (quizId != null && !quizId.isEmpty() && !createdQuizIds.contains(quizId)) {
            createdQuizIds.add(quizId);
            // TODO: Persist?
            System.out.println("Teacher " + getUsername() + " tracked created quiz: " + quizId);
        }
    }

    /**
     * Gets a list of IDs for lessons created by this teacher.
     * Best practice is to populate this list by scanning lesson files on load.
     * @return A copy of the list of created lesson IDs.
     */
    public List<String> getCreatedLessonIds() {
        // Consider calling populateCreatedContentLists() here if not done on load
        return new ArrayList<>(createdLessonIds); // Return copy
    }

    /**
     * Gets a list of IDs for quizzes created by this teacher.
     * @return A copy of the list of created quiz IDs.
     */
    public List<String> getCreatedQuizIds() {
        // Consider calling populateCreatedContentLists() here
        return new ArrayList<>(createdQuizIds); // Return copy
    }

    /**
     * Gets the number of lessons tracked as created by this teacher.
     * @return The count of created lessons.
     */
    public int getNumberOfCreatedLessons() {
        return createdLessonIds.size();
    }

    /**
     * Gets the number of quizzes tracked as created by this teacher.
     * @return The count of created quizzes.
     */
    public int getNumberOfCreatedQuizzes() {
        return createdQuizIds.size();
    }


    // --- Content Editing Placeholders ---
    // Full implementation requires loading, modifying, and saving Lesson/Quiz objects

    /**
     * Placeholder for editing an existing lesson.
     * Requires loading the lesson, checking ownership, modifying, and saving.
     * @param lessonId The ID of the lesson to edit.
     * @param newTitle New title.
     * @param newContent New content.
     * @param newLevel New level string.
     * @return true if edit was successful, false otherwise.
     */
    public boolean editLesson(String lessonId, String newTitle, String newContent, String newLevel /*, etc */) {
        System.out.println("Attempting to edit lesson: " + lessonId + " (Not fully implemented)");
        Lesson lessonToEdit = ApplicationManager.findLessonById(lessonId); // Use ApplicationManager to find/load

        if (lessonToEdit == null) {
            System.err.println("Edit failed: Lesson " + lessonId + " not found.");
            return false;
        }

        // Check if the current teacher is the creator of the lesson
        if (!this.getID().equals(lessonToEdit.getTeacherId())) {
            System.err.println("Edit failed: Teacher " + getUsername() + " does not own lesson " + lessonId);
            // controllers.AlertMessage.showError("Permission Denied", "You can only edit lessons you created.");
            return false;
        }

        // --- Apply Changes (Add setters to Lesson if needed) ---
        // Example: (Assuming Lesson has setters, which it currently doesn't directly)
        // lessonToEdit.setTitle(newTitle);
        // lessonToEdit.setContent(newContent);
        // lessonToEdit.getProficiencyLevel().setLevel(newLevel);
        // ---

        // Save the modified lesson
        boolean saveSuccess = lessonToEdit.saveToFile();
        if (saveSuccess) {
            System.out.println("Lesson " + lessonId + " edited and saved successfully.");
        } else {
            System.err.println("Edit failed: Could not save changes for lesson " + lessonId);
        }
        return saveSuccess;
    }

    /**
     * Placeholder for editing an existing quiz.
     * @param quizId The ID of the quiz to edit.
     * @param updatedQuizData Map or object containing updated quiz details.
     * @return true if edit was successful, false otherwise.
     */
    public boolean editQuiz(String quizId, Map<String, Object> updatedQuizData) {
        System.out.println("Attempting to edit quiz: " + quizId + " (Not fully implemented)");
        Quiz quizToEdit = ApplicationManager.findQuizById(quizId); // Use ApplicationManager

        if (quizToEdit == null) {
            System.err.println("Edit failed: Quiz " + quizId + " not found.");
            return false;
        }

        if (!this.getID().equals(quizToEdit.getTeacherId())) {
            System.err.println("Edit failed: Teacher " + getUsername() + " does not own quiz " + quizId);
            return false;
        }

        // --- Apply Changes (Requires setters or modification logic in Quiz) ---
        // Example:
        // quizToEdit.setTitle((String)updatedQuizData.get("title"));
        // quizToEdit.getProficiencyLevel().setLevel((String)updatedQuizData.get("level"));
        // quizToEdit.setQuestions((List<Question>)updatedQuizData.get("questions")); // Need a setter
        // ---

        boolean saveSuccess = quizToEdit.saveToFile();
        if (saveSuccess) {
            System.out.println("Quiz " + quizId + " edited and saved successfully.");
        } else {
            System.err.println("Edit failed: Could not save changes for quiz " + quizId);
        }
        return saveSuccess;
    }


    // --- Overrides ---

    @Override
    public String toString() {
        return "Teacher[Username='" + getUsername() + "', Email='" + getEmail() + "', ID='" + getID() + "', Language='" + language + "']";
    }

    // Equals and hashCode inherited from User (based on ID/username)
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}