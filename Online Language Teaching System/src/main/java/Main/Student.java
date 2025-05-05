package Main;

import fileManager.FileManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Implement the Progress interface
public class Student extends User implements Progress {

    private String language;
    // Track progress internally
    private Map<String, Boolean> completedLessons;
    private Map<String, Integer> quizScores;

    // No need for static counters here unless tracking total student instances globally
    // private static int numberOfStudents = 0;

    public Student(String name, String email, String password, String ID, String language) {
        super(name, email, password, ID); // ID likely should be username
        this.language = (language != null && !language.trim().isEmpty()) ? language.trim() : "Unknown"; // Ensure language isn't empty
        this.completedLessons = new HashMap<>();
        this.quizScores = new HashMap<>();
        // numberOfStudents++; // If needed globally

        // TODO: Load progress data from a file specific to the student (e.g., progress_username.txt)
        // loadProgressData();
    }

    public String getLanguage() {
        return language;
    }

    // --- Progress Interface Implementation ---

    @Override
    public String getUserId() {
        return getID(); // Assuming User's ID is the unique identifier
    }

    @Override
    public void markLessonCompleted(String lessonId) {
        if (lessonId != null && !lessonId.trim().isEmpty()) {
            completedLessons.put(lessonId.trim(), true);
            System.out.println("Student " + getUsername() + " marked lesson " + lessonId + " as completed.");
            // TODO: Save progress data
            // saveProgressData();
        }
    }

    @Override
    public void recordQuizScore(String quizId, int score) {
        if (quizId != null && !quizId.trim().isEmpty()) {
            quizScores.put(quizId.trim(), score);
            System.out.println("Student " + getUsername() + " scored " + score + " on quiz " + quizId + ".");
            // TODO: Save progress data
            // saveProgressData();
        }
    }

    @Override
    public boolean isLessonCompleted(String lessonId) {
        if (lessonId == null || lessonId.trim().isEmpty()) return false;
        return completedLessons.getOrDefault(lessonId.trim(), false);
    }

    @Override
    public int getQuizScore(String quizId) {
        if (quizId == null || quizId.trim().isEmpty()) return -1; // Indicate not taken/found
        return quizScores.getOrDefault(quizId.trim(), -1); // Return -1 if score not recorded
    }

    @Override
    public Map<String, Boolean> getCompletedLessons() {
        return new HashMap<>(completedLessons); // Return a copy
    }

    @Override
    public Map<String, Integer> getQuizScores() {
        return new HashMap<>(quizScores); // Return a copy
    }

    @Override
    public double calculateOverallProgress() {
        // Basic example: progress based on number of completed lessons
        // Needs access to the total number of lessons for the student's language
        // This logic might be better placed in a service/manager class
        int totalLessons = 10; // Placeholder - Get this value dynamically!
        if (totalLessons == 0) return 0.0;
        return (double) completedLessons.size() / totalLessons * 100.0;
    }


    // --- TODO: Implement Progress Data Persistence ---
    private void loadProgressData() {
        // String progressFile = "src/main/resources/progress/progress_" + getUsername() + ".txt";
        // FileManager fm = new FileManager(progressFile);
        // HashMap<String, String> data = fm.loadData();
        // Parse data and populate completedLessons and quizScores maps
        System.out.println("Loading progress data for " + getUsername() + "...");
        // Placeholder: Add dummy data for now
        // completedLessons.put("L1020", true);
        // quizScores.put("Q001", 80);

    }

    private void saveProgressData() {
        // String progressFile = "src/main/resources/progress/progress_" + getUsername() + ".txt";
        // FileManager fm = new FileManager(progressFile);
        // HashMap<String, String> data = new HashMap<>();
        // // Convert maps to string format suitable for saving
        // // e.g., completedLessons: L1020=true,L1021=false
        // // e.g., quizScores: Q001=80,Q002=95
        // fm.saveDataOverwrite(data);
        System.out.println("Saving progress data for " + getUsername() + "...");

    }


    // --- Overrides ---

    @Override
    public String toString() {
        return "Student{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' + // Don't print password
                ", ID='" + getID() + '\'' +
                ", language='" + language + '\'' +
                ", completedLessons=" + completedLessons.size() +
                '}';
    }

    // Ensure equals/hashCode consider Student-specific fields if necessary,
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