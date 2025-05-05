// src/main/java/Main/Lesson.java
package Main;

import fileManager.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger; // Using AtomicInteger
import java.util.stream.Stream;


public class Lesson implements Comparable<Lesson> {
    private String title;
    private String lessonId; // Should be unique (e.g., L1020)
    private String content; // Can be multi-line
    private ProficiencyLevel proficiencyLevel;
    private List<String> prerequisiteLessonIds = new ArrayList<>();
    private String filePath; // Store the absolute file path
    private String teacherId; // Added field to track creator

    // Static counter for generating unique IDs
    private static AtomicInteger lessonCounter = new AtomicInteger(initializeCounter());

    /**
     * Constructor for LOADING an existing lesson from its file path.
     * @param absoluteFilePath The absolute path to the lesson file.
     */
    public Lesson(String absoluteFilePath) {
        Objects.requireNonNull(absoluteFilePath, "Lesson file path cannot be null");
        this.filePath = absoluteFilePath;
        if (!loadFromFile()) {
            // Log error and set default values to indicate load failure
            System.err.println("Failed to load lesson data from: " + filePath + ". Setting default values.");
            setDefaultValuesOnLoadFailure();
        } else {
            System.out.println("Lesson loaded successfully from: " + filePath);
        }
    }

    /**
     * Constructor for CREATING a NEW lesson. Generates ID and saves to file.
     * @param title The title of the lesson.
     * @param content The lesson content.
     * @param level The proficiency level string (e.g., "Beginner").
     * @param prerequisiteLessonIds List of prerequisite lesson IDs.
     * @param teacherId The ID of the teacher creating the lesson.
     * @throws IllegalArgumentException if required fields are invalid.
     * @throws RuntimeException if saving the new lesson fails.
     */
    public Lesson(String title, String content, String level, List<String> prerequisiteLessonIds, String teacherId) {
        // --- Validation ---
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Lesson title cannot be empty.");
        if (content == null) throw new IllegalArgumentException("Lesson content cannot be null (can be empty)."); // Allow empty content?
        if (level == null || level.trim().isEmpty()) throw new IllegalArgumentException("Proficiency level cannot be empty.");
        if (teacherId == null || teacherId.trim().isEmpty()) throw new IllegalArgumentException("Teacher ID cannot be empty.");


        // --- Initialization ---
        this.title = title.trim();
        this.content = content; // Store raw content, including newlines
        this.proficiencyLevel = new ProficiencyLevel(level); // Handles level validation
        this.prerequisiteLessonIds = (prerequisiteLessonIds != null) ? new ArrayList<>(prerequisiteLessonIds) : new ArrayList<>();
        this.teacherId = teacherId.trim();
        this.lessonId = "L" + lessonCounter.getAndIncrement(); // Generate unique ID

        // --- File Path Generation ---
        String lessonsDir = ApplicationManager.getLessonsDirectory();
        if (lessonsDir == null) {
            throw new RuntimeException("Cannot create lesson: Lessons directory path is not configured.");
        }
        this.filePath = Paths.get(lessonsDir, "lesson_" + this.lessonId + ".txt").toString();

        // --- Save to File ---
        if (!saveToFile()) {
            // If saving fails, the object is created but represents an unsaved state.
            // Throwing an exception might be appropriate here.
            System.err.println("CRITICAL: Failed to save newly created lesson to: " + this.filePath);
            throw new RuntimeException("Failed to save new lesson with ID " + this.lessonId);
        } else {
            System.out.println("Successfully created and saved new lesson: " + this.lessonId + " at " + this.filePath);
        }
    }

    /** Initializes the static counter by finding the highest existing lesson ID. */
    private static int initializeCounter() {
        int maxId = 1019; // Default starting point (so first ID is L1020)
        String lessonsDir = ApplicationManager.getLessonsDirectory(); // Get path AFTER it's resolved

        if (lessonsDir != null) {
            Path dirPath = Paths.get(lessonsDir);
            if (Files.isDirectory(dirPath)) {
                try (Stream<Path> stream = Files.list(dirPath)) {
                    maxId = stream
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .filter(name -> name.toLowerCase().startsWith("lesson_l") && name.toLowerCase().endsWith(".txt"))
                            .map(name -> name.substring(8, name.length() - 4)) // Extract ID part (e.g., "1020")
                            .mapToInt(idStr -> {
                                try { return Integer.parseInt(idStr); } catch (NumberFormatException e) { return -1; }
                            })
                            .filter(idNum -> idNum > 0)
                            .max()
                            .orElse(maxId); // Use default if no valid IDs found
                    System.out.println("Initialized lesson counter. Max existing ID found: " + maxId);
                } catch (IOException e) {
                    System.err.println("Error reading lessons directory ("+ lessonsDir +") to initialize counter: " + e.getMessage());
                }
            } else {
                System.err.println("Lessons directory path is not a directory: " + lessonsDir);
            }
        } else {
            System.err.println("Lessons directory path is null during counter initialization.");
        }
        // Return the next available ID number
        return maxId + 1;
    }

    /** Sets default values when loading from file fails. */
    private void setDefaultValuesOnLoadFailure() {
        this.title = "Load Failed";
        this.lessonId = "L_DEFAULT"; // Special ID indicating failure
        this.content = "Failed to load content from " + (filePath != null ? filePath : "unknown file");
        this.proficiencyLevel = new ProficiencyLevel("Beginner"); // Default level
        this.prerequisiteLessonIds = new ArrayList<>();
        this.teacherId = "Unknown";
        // Keep filePath as it was, but the data is default
    }


    /** Loads lesson data from the file specified in `this.filePath`. */
    public boolean loadFromFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot load lesson: File path is not set.");
            return false;
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.err.println("Cannot load lesson: File does not exist at " + filePath);
            return false;
        }

        // Custom loading logic to handle multi-line content
        Map<String, String> lessonData = new HashMap<>();
        StringBuilder contentBuilder = new StringBuilder();
        boolean readingContent = false;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("content:")) {
                    readingContent = true;
                    // Capture potential content on the same line after "content:"
                    String firstLineContent = line.substring("content:".length()).trim();
                    if (!firstLineContent.isEmpty()) {
                        contentBuilder.append(firstLineContent).append("\n");
                    }
                } else if (readingContent && line.contains(":")) {
                    // Found a new key:value pair, stop reading content
                    readingContent = false;
                    // Process the new line normally
                    int colonIndex = line.indexOf(':');
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    if (!key.isEmpty()) lessonData.put(key, value);
                } else if (readingContent) {
                    // Append line to content
                    contentBuilder.append(line).append("\n");
                } else if (line.contains(":")) {
                    // Process regular key:value pairs
                    int colonIndex = line.indexOf(':');
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    if (!key.isEmpty()) lessonData.put(key, value);
                } else if (!line.trim().isEmpty()) {
                    System.err.println("Warning: Skipping malformed line in " + filePath + ": " + line);
                }
            }

            // Store the accumulated content
            if (contentBuilder.length() > 0) {
                // Remove the last newline character if it exists
                if (contentBuilder.charAt(contentBuilder.length() - 1) == '\n') {
                    contentBuilder.setLength(contentBuilder.length() - 1);
                }
                lessonData.put("content", contentBuilder.toString());
            }


            // Populate fields from loaded data
            this.title = lessonData.getOrDefault("title", "Default Title");
            this.lessonId = lessonData.getOrDefault("lessonId", "Default ID");
            this.content = lessonData.getOrDefault("content", "Default Content"); // Get potentially multi-line content
            this.teacherId = lessonData.getOrDefault("teacherId", "Unknown");

            // Handle prerequisites
            String prerequisitesStr = lessonData.getOrDefault("prerequisites", "");
            this.prerequisiteLessonIds.clear(); // Clear existing before loading
            if (!prerequisitesStr.isEmpty() && !prerequisitesStr.equalsIgnoreCase("none")) {
                String[] prerequisiteIds = prerequisitesStr.split(",");
                for (String id : prerequisiteIds) {
                    String trimmedId = id.trim();
                    if (!trimmedId.isEmpty()) {
                        this.prerequisiteLessonIds.add(trimmedId);
                    }
                }
            }

            this.proficiencyLevel = new ProficiencyLevel(lessonData.getOrDefault("level", "Beginner"));
            return true;

        } catch (IOException e) {
            System.err.println("Error reading lesson file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) { // Catch other potential errors during parsing
            System.err.println("Unexpected error parsing lesson file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Saves the current lesson data to the file specified in `this.filePath`. */
    public boolean saveToFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot save lesson: File path is not set.");
            return false;
        }
        // Validate essential data before saving
        if (lessonId == null || lessonId.isEmpty() || lessonId.equals("L_DEFAULT")) {
            System.err.println("Cannot save lesson: Invalid or default Lesson ID.");
            return false;
        }
        if (title == null || title.isEmpty()) {
            System.err.println("Cannot save lesson: Title is empty.");
            return false;
        }
        if (teacherId == null || teacherId.isEmpty()) {
            System.err.println("Cannot save lesson: Teacher ID is empty.");
            return false;
        }

        // Use try-with-resources for automatic closing
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write("lessonId:" + lessonId); writer.newLine();
            writer.write("title:" + title); writer.newLine();
            writer.write("teacherId:" + teacherId); writer.newLine();
            writer.write("level:" + proficiencyLevel.getLevelInternal()); writer.newLine(); // Save internal lowercase level

            // Format prerequisites
            String prerequisitesValue = prerequisiteLessonIds.isEmpty() ? "none" : String.join(",", prerequisiteLessonIds);
            writer.write("prerequisites:" + prerequisitesValue); writer.newLine();

            // Write content, handling potential null and ensuring the "content:" prefix
            writer.write("content:" + (content != null ? content : "")); // Write content potentially spanning multiple lines
            writer.newLine(); // Ensure a newline after content starts

            System.out.println("Lesson data saved successfully to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving lesson data to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Getters ---
    public String getTitle() { return title; }
    public String getLessonId() { return lessonId; }
    public String getContent() { return content; }
    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public String getLevel() { // Convenience getter for level string
        return proficiencyLevel != null ? proficiencyLevel.getLevel() : "Unknown";
    }
    public List<String> getPrerequisiteLessonIds() { return new ArrayList<>(prerequisiteLessonIds); } // Return copy
    public String getFilePath() { return filePath; }
    public String getTeacherId() { return teacherId; } // Getter for teacherId


    // --- Overrides ---

    @Override
    public int compareTo(Lesson other) {
        // Compare primarily by ID for consistent ordering
        if (this.lessonId == null && other.lessonId == null) return 0;
        if (this.lessonId == null) return -1; // Nulls first
        if (other.lessonId == null) return 1;

        // Extract numeric parts for comparison if possible
        try {
            int thisNum = Integer.parseInt(this.lessonId.substring(1)); // Skip 'L'
            int otherNum = Integer.parseInt(other.lessonId.substring(1));
            return Integer.compare(thisNum, otherNum);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // Fallback to string comparison if ID format is unexpected
            return this.lessonId.compareTo(other.lessonId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        // Lessons are equal if their IDs are the same (case-sensitive)
        return Objects.equals(lessonId, lesson.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId); // Hash based on the unique ID
    }

    @Override
    public String toString() {
        // Provides a concise representation, useful for logs and debugging
        return "Lesson[ID=" + lessonId + ", Title='" + title + "', Level=" + getLevel() + "]";
    }
}