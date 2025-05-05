// src/main/java/Main/Quiz.java
package Main;

import fileManager.FileManager;
import java.io.*; // For potential custom file reading
import java.nio.file.*; // Use NIO for file operations
import java.util.*; // Use List, Map etc.
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public class Quiz implements Comparable<Quiz> {

    private String title;
    private String quizId; // Unique ID (e.g., Q1, Q101)
    private List<Question> questions;
    private ProficiencyLevel proficiencyLevel;
    private String filePath; // Absolute path to the quiz file
    private String associatedLessonId; // Optional ID of the lesson this quiz belongs to
    private String teacherId; // Optional: ID of the teacher who created it

    // Static counter for generating unique IDs
    private static AtomicInteger quizCounter = new AtomicInteger(initializeQuizCounter());

    /**
     * Constructor for LOADING an existing quiz from its file path.
     * @param absoluteFilePath The absolute path to the quiz file.
     */
    public Quiz(String absoluteFilePath) {
        Objects.requireNonNull(absoluteFilePath, "Quiz file path cannot be null");
        this.filePath = absoluteFilePath;
        this.questions = new ArrayList<>(); // Initialize list
        this.proficiencyLevel = new ProficiencyLevel(); // Default level initially

        if (!loadFromFile()) {
            System.err.println("Failed to load quiz data from: " + filePath + ". Setting default values.");
            setDefaultValuesOnLoadFailure();
        } else {
            System.out.println("Quiz loaded successfully from: " + filePath);
        }
    }

    /**
     * Constructor for CREATING a NEW quiz. Generates ID and saves to file.
     * This version assumes level is provided.
     *
     * @param title The title of the quiz.
     * @param level The proficiency level string (e.g., "Beginner").
     * @param questions List of Question objects for the quiz.
     * @param associatedLessonId The ID of the lesson this quiz relates to (can be null or empty).
     * @param teacherId The ID of the teacher creating the quiz.
     * @throws IllegalArgumentException if required fields are invalid.
     * @throws RuntimeException if saving the new quiz fails.
     */
    public Quiz(String title, String level, List<Question> questions, String associatedLessonId, String teacherId) {
        // --- Validation ---
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Quiz title cannot be empty.");
        if (level == null || level.trim().isEmpty()) throw new IllegalArgumentException("Proficiency level cannot be empty.");
        if (questions == null || questions.isEmpty()) throw new IllegalArgumentException("Quiz must have at least one question.");
        if (teacherId == null || teacherId.trim().isEmpty()) throw new IllegalArgumentException("Teacher ID cannot be empty.");

        // --- Initialization ---
        this.title = title.trim();
        this.proficiencyLevel = new ProficiencyLevel(level); // Validates level
        this.questions = new ArrayList<>(questions); // Create a copy of the questions list
        this.teacherId = teacherId.trim();
        // Allow null or empty associatedLessonId
        this.associatedLessonId = (associatedLessonId != null && !associatedLessonId.trim().isEmpty()) ? associatedLessonId.trim() : null;
        this.quizId = "Q" + quizCounter.getAndIncrement(); // Generate unique ID (e.g., Q1, Q2...)

        // --- File Path Generation ---
        String quizzesDir = ApplicationManager.getQuizzesDirectory();
        if (quizzesDir == null) {
            throw new RuntimeException("Cannot create quiz: Quizzes directory path is not configured.");
        }
        this.filePath = Paths.get(quizzesDir, "quiz_" + this.quizId + ".txt").toString();

        // --- Save to File ---
        if (!saveToFile()) {
            System.err.println("CRITICAL: Failed to save newly created quiz to: " + this.filePath);
            throw new RuntimeException("Failed to save new quiz with ID " + this.quizId);
        } else {
            System.out.println("Successfully created and saved new quiz: " + this.quizId + " at " + this.filePath);
        }
    }


    /** Initializes the static quiz counter by finding the highest existing quiz ID (numeric part). */
    private static int initializeQuizCounter() {
        int maxId = 0; // Default starting point (so first ID is Q1)
        String quizzesDir = ApplicationManager.getQuizzesDirectory();

        if (quizzesDir != null) {
            Path dirPath = Paths.get(quizzesDir);
            if (Files.isDirectory(dirPath)) {
                try (Stream<Path> stream = Files.list(dirPath)) {
                    maxId = stream
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .filter(name -> name.toLowerCase().startsWith("quiz_q") && name.toLowerCase().endsWith(".txt"))
                            .map(name -> name.substring(6, name.length() - 4)) // Extract ID part (e.g., "1", "101")
                            .mapToInt(idStr -> {
                                try { return Integer.parseInt(idStr); } catch (NumberFormatException e) { return -1; }
                            })
                            .filter(idNum -> idNum > 0)
                            .max()
                            .orElse(maxId);
                    System.out.println("Initialized quiz counter. Max existing ID found: " + maxId);
                } catch (IOException e) {
                    System.err.println("Error reading quizzes directory ("+ quizzesDir +") to initialize counter: " + e.getMessage());
                }
            } else {
                System.err.println("Quizzes directory path is not a directory: " + quizzesDir);
            }
        } else {
            System.err.println("Quizzes directory path is null during counter initialization.");
        }
        return maxId + 1; // Start from next available ID
    }

    /** Sets default values when loading from file fails. */
    private void setDefaultValuesOnLoadFailure() {
        this.title = "Load Failed";
        this.quizId = "Q_DEFAULT"; // Special ID indicating failure
        this.proficiencyLevel = new ProficiencyLevel("Beginner");
        this.questions = new ArrayList<>(); // Empty list
        this.associatedLessonId = null;
        this.teacherId = "Unknown";
        // Keep filePath, but data is default
    }


    /** Loads quiz data from the file specified in `this.filePath`. */
    public boolean loadFromFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot load quiz: File path is not set.");
            return false;
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.err.println("Cannot load quiz: File does not exist at " + filePath);
            return false;
        }

        // Use FileManager's generic loadData for key-value pairs first
        FileManager fm = new FileManager(filePath);
        HashMap<String, String> quizData = fm.loadData(); // Loads simple key:value pairs

        if (quizData == null || quizData.isEmpty()) {
            System.err.println("Failed to load or empty data in quiz file: " + filePath);
            return false; // Indicates load failure or empty file
        }

        // Populate metadata
        this.title = quizData.getOrDefault("title", "Default Title");
        this.quizId = quizData.getOrDefault("id", "Q_DEFAULT"); // Use 'id' as key based on example
        this.proficiencyLevel.setLevel(quizData.getOrDefault("level", "Beginner"));
        this.associatedLessonId = quizData.getOrDefault("lessonId", null); // Can be null
        this.teacherId = quizData.getOrDefault("teacherId", "Unknown");

        // --- Load Questions ---
        // This requires parsing specific keys like "question1", "q1_option1", "q1_correct"
        this.questions.clear(); // Clear existing before loading new ones
        int questionCount = 1; // Start checking from question1

        try {
            while (true) {
                String qKey = "question" + questionCount;
                String qText = quizData.get(qKey);

                // Stop if no more question text is found
                if (qText == null) {
                    break;
                }

                // Assuming exactly 4 options per question based on FXML/Save logic
                final int NUM_OPTIONS = 4;
                String[] oTexts = new String[NUM_OPTIONS];
                boolean optionsComplete = true;
                for (int j = 0; j < NUM_OPTIONS; j++) {
                    String oKey = "q" + questionCount + "_option" + (j + 1);
                    oTexts[j] = quizData.get(oKey);
                    if (oTexts[j] == null) {
                        optionsComplete = false;
                        System.err.println("Warning: Missing option " + (j + 1) + " for question " + questionCount + " in quiz " + this.quizId + " (" + filePath + ")");
                        break; // Stop processing options for this question
                    }
                }

                // If options were incomplete, skip this question and try the next
                if (!optionsComplete) {
                    System.err.println("Skipping question " + questionCount + " due to missing options.");
                    questionCount++;
                    continue;
                }

                // Get the correct answer TEXT
                String cKey = "q" + questionCount + "_correct";
                String cText = quizData.get(cKey);
                if (cText == null) {
                    System.err.println("Warning: Missing correct answer key '" + cKey + "' for question " + questionCount + " in quiz " + this.quizId + " (" + filePath + "). Skipping question.");
                    questionCount++;
                    continue;
                }

                // Create the Question object (constructor handles validation)
                try {
                    Question newQuestion = new Question(qText, oTexts, cText);
                    questions.add(newQuestion);
                } catch (IllegalArgumentException e) {
                    // Catch validation errors from Question constructor (e.g., correct answer not in options)
                    System.err.println("Error creating Question " + questionCount + " for quiz " + this.quizId + " (" + filePath + "): " + e.getMessage() + ". Skipping question.");
                }

                questionCount++; // Move to the next potential question
            }

            if (questions.isEmpty() && questionCount > 1) {
                System.err.println("Warning: Loaded quiz metadata but failed to load any valid questions for quiz " + this.quizId + " (" + filePath + ")");
            } else {
                System.out.println("Loaded " + questions.size() + " questions for quiz " + this.quizId);
            }
            return true; // Load attempted, even if no questions were found/valid

        } catch (Exception e) {
            System.err.println("Unexpected error loading questions for quiz " + this.quizId + " (" + filePath + "): " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate a more severe loading error
        }
    }

    /** Saves the current quiz data to the file specified in `this.filePath`. */
    public boolean saveToFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot save quiz: File path is not set.");
            return false;
        }
        if (quizId == null || quizId.isEmpty() || quizId.equals("Q_DEFAULT")) {
            System.err.println("Cannot save quiz: Invalid or default Quiz ID.");
            return false;
        }
        if (title == null || title.isEmpty()) {
            System.err.println("Cannot save quiz: Title is empty.");
            return false;
        }
        if (teacherId == null || teacherId.isEmpty()) {
            System.err.println("Cannot save quiz: Teacher ID is empty.");
            return false;
        }
        if (questions == null || questions.isEmpty()){
            System.err.println("Cannot save quiz: Quiz has no questions.");
            return false;
        }

        // Prepare data map for FileManager
        HashMap<String, String> quizData = new HashMap<>();
        quizData.put("id", quizId);
        quizData.put("title", title);
        quizData.put("teacherId", teacherId);
        quizData.put("level", proficiencyLevel.getLevelInternal()); // Save internal lowercase level
        if (associatedLessonId != null) { // Only save lessonId if it's present
            quizData.put("lessonId", associatedLessonId);
        }

        // Add questions data
        int qNum = 1;
        final int EXPECTED_OPTIONS = 4; // Based on create/load logic

        for (Question q : questions) {
            quizData.put("question" + qNum, q.getQuestionText());
            String[] options = q.getOptions();

            if (options.length != EXPECTED_OPTIONS) {
                System.err.println("Warning: Question " + qNum + " in quiz " + quizId + " does not have exactly " + EXPECTED_OPTIONS + " options. Saving might be incompatible with loader.");
                // Pad or truncate if necessary, but current loader expects exactly 4
            }

            for (int i = 0; i < options.length; i++) {
                // Ensure we don't try to save more options than expected
                if (i < EXPECTED_OPTIONS) {
                    quizData.put("q" + qNum + "_option" + (i + 1), options[i]);
                } else {
                    break; // Stop if more options than expected
                }
            }
            // Pad with empty strings if fewer than expected options (though loader might fail)
            for (int i = options.length; i < EXPECTED_OPTIONS; i++){
                quizData.put("q" + qNum + "_option" + (i + 1), ""); // Add empty option
                System.err.println("Warning: Padding Question " + qNum + " with empty option " + (i+1));
            }

            // Save the TEXT of the correct answer
            quizData.put("q" + qNum + "_correct", q.getCorrectAnswer());
            qNum++;
        }

        // Use FileManager to save (overwrite mode)
        FileManager fm = new FileManager(filePath);
        if (fm.saveDataOverwrite(quizData)) {
            System.out.println("Quiz data saved successfully to: " + filePath);
            return true;
        } else {
            System.err.println("Failed to save quiz data using FileManager for: " + filePath);
            return false;
        }
    }


    // --- Getters ---
    public String getTitle() { return title; }
    public String getQuizId() { return quizId; }
    public List<Question> getQuestions() { return new ArrayList<>(questions); } // Return copy
    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public String getLevel() { // Convenience getter for level string
        return proficiencyLevel != null ? proficiencyLevel.getLevel() : "Unknown";
    }
    public String getFilePath() { return filePath; }
    public String getAssociatedLessonId() { return associatedLessonId; }
    public String getTeacherId() { return teacherId; }


    // --- Overrides ---

    @Override
    public int compareTo(Quiz other) {
        // Compare primarily by ID for consistent ordering
        if (this.quizId == null && other.quizId == null) return 0;
        if (this.quizId == null) return -1; // Nulls first
        if (other.quizId == null) return 1;

        // Extract numeric parts for comparison if possible
        try {
            int thisNum = Integer.parseInt(this.quizId.substring(1)); // Skip 'Q'
            int otherNum = Integer.parseInt(other.quizId.substring(1));
            return Integer.compare(thisNum, otherNum);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // Fallback to string comparison if ID format is unexpected
            return this.quizId.compareTo(other.quizId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        // Quizzes are equal if their IDs are the same (case-sensitive)
        return Objects.equals(quizId, quiz.quizId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId); // Hash based on the unique ID
    }

    @Override
    public String toString() {
        // Concise representation for logs and debugging
        return "Quiz[ID=" + quizId + ", Title='" + title + "', Level=" + getLevel() + ", Questions=" + (questions != null ? questions.size() : 0) + "]";
    }
}