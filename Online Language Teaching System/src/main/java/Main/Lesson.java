package Main;

import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; // For thread-safe unique IDs

public class Lesson implements Comparable<Lesson> {
    private String title;
    private String lessonId; // Should be unique
    private String content;
    private ProficiencyLevel proficiencyLevel;
    private List<String> prerequisiteLessonIds = new ArrayList<>();
    private String filePath; // Store the file path

    // Use AtomicInteger for potentially safer ID generation if multi-threaded (though unlikely here)
    // Or load the last used ID from a config file. Simple approach for now:
    private static int lessonCounter = loadInitialCounter(); // Start from a base or load last


    // Constructor for loading an existing lesson
    public Lesson(String filePath) {
        this.filePath = filePath;
        if (!loadFromFile()) {
            // Handle error - perhaps throw exception or set default values
            System.err.println("Failed to load lesson from: " + filePath);
            setDefaultValues(); // Set defaults if load fails
        }
    }

    // Constructor for creating a NEW lesson
    public Lesson(String title, String content, String level, List<String> prerequisiteLessonIds, String teacherId) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Lesson title cannot be empty.");
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("Lesson content cannot be empty.");
        if (level == null || level.trim().isEmpty()) throw new IllegalArgumentException("Proficiency level cannot be empty.");


        this.title = title.trim();
        this.content = content; // Keep original formatting potentially
        this.proficiencyLevel = new ProficiencyLevel(level);
        this.prerequisiteLessonIds = (prerequisiteLessonIds != null) ? new ArrayList<>(prerequisiteLessonIds) : new ArrayList<>();
        this.lessonId = "L" + lessonCounter++; // Generate unique ID (simple approach)
        // teacherId should be stored if needed for tracking creators

        // Define file path based on ID and configured directory
        this.filePath = ApplicationManager.getLessonsDirectory() + File.separator + "lesson_" + this.lessonId + ".txt";

        if (!saveToFile()) {
            System.err.println("Failed to save newly created lesson to: " + filePath);
            // Maybe throw an exception here?
        } else {
            System.out.println("Successfully saved new lesson: " + this.lessonId + " to " + this.filePath);
        }
    }

    // Helper to load the initial counter (e.g., find the highest existing ID)
    private static int loadInitialCounter() {
        // Basic implementation: Start from a fixed number.
        // Better: Scan the lessons directory, parse IDs, find max + 1.
        return 1020; // Default start
    }

    private void setDefaultValues() {
        this.title = "Default Title";
        this.lessonId = "L_DEFAULT";
        this.content = "Default Content - Load Failed";
        this.proficiencyLevel = new ProficiencyLevel("Beginner");
        this.prerequisiteLessonIds = new ArrayList<>();
        this.filePath = ""; // Indicate it's not associated with a valid file
    }


    public boolean loadFromFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot load lesson: File path is not set.");
            return false;
        }
        FileManager fm = new FileManager(filePath);
        HashMap<String, String> lessonData = fm.loadData(); // Use generic loadData

        if (lessonData != null && !lessonData.isEmpty()) {
            this.title = lessonData.getOrDefault("title", "Default Title");
            this.lessonId = lessonData.getOrDefault("lessonId", "Default ID");
            this.content = lessonData.getOrDefault("content", "Default Content"); // Assumes content is single line or handled by FileManager correctly

            // Handle prerequisites more robustly
            String prerequisitesStr = lessonData.getOrDefault("prerequisites", ""); // Changed key name for clarity
            this.prerequisiteLessonIds.clear(); // Clear existing before loading
            if (prerequisitesStr != null && !prerequisitesStr.isEmpty() && !prerequisitesStr.equalsIgnoreCase("none")) {
                String[] prerequisiteIds = prerequisitesStr.split(",");
                for (String id : prerequisiteIds) {
                    String trimmedId = id.trim();
                    if (!trimmedId.isEmpty()) {
                        this.prerequisiteLessonIds.add(trimmedId);
                    }
                }
            }

            this.proficiencyLevel = new ProficiencyLevel(lessonData.getOrDefault("level", "Beginner"));
            System.out.println("Lesson loaded successfully from: " + filePath);
            return true;
        } else {
            System.err.println("Failed to load or empty data in lesson file: " + filePath);
            return false;
        }
    }

    public boolean saveToFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot save lesson: File path is not set.");
            return false;
        }
        if (lessonId == null || lessonId.isEmpty() || lessonId.equals("L_DEFAULT")) {
            System.err.println("Cannot save lesson: Invalid or default Lesson ID.");
            return false;
        }

        FileManager fm = new FileManager(filePath);
        HashMap<String, String> lessonData = new HashMap<>();
        lessonData.put("title", title);
        lessonData.put("lessonId", lessonId);
        lessonData.put("level", proficiencyLevel.getLevel());
        lessonData.put("content", content); // Assumes content is saved correctly by FileManager

        // Format prerequisites
        StringBuilder ids = new StringBuilder();
        if (prerequisiteLessonIds.isEmpty()) {
            ids.append("none"); // Use "none" for clarity
        } else {
            for (int i = 0; i < prerequisiteLessonIds.size(); i++) {
                ids.append(prerequisiteLessonIds.get(i).trim());
                if (i < prerequisiteLessonIds.size() - 1) {
                    ids.append(",");
                }
            }
        }
        lessonData.put("prerequisites", ids.toString()); // Key name consistent with load

        // Add teacherId if tracking is implemented
        // lessonData.put("teacherId", this.teacherId);

        return fm.saveDataOverwrite(lessonData); // Use overwrite to ensure file consistency
    }

    // Getters
    public String getTitle() { return title; }
    public String getLessonId() { return lessonId; }
    public String getContent() { return content; }
    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public List<String> getPrerequisiteLessonIds() { return new ArrayList<>(prerequisiteLessonIds); } // Return copy
    public String getFilePath() { return filePath; }


    @Override
    public int compareTo(Lesson other) {
        // Compare by title first, then maybe ID for uniqueness
        int titleComparison = this.title.compareToIgnoreCase(other.title);
        if (titleComparison != 0) {
            return titleComparison;
        }
        return this.lessonId.compareTo(other.lessonId); // Fallback comparison
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return java.util.Objects.equals(lessonId, lesson.lessonId); // ID is the primary key
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(lessonId);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "title='" + title + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", level=" + proficiencyLevel.getLevel() +
                ", prerequisites=" + prerequisiteLessonIds +
                '}';
    }
}