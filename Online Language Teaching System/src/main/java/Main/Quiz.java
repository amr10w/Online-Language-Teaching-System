package Main;

import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Quiz implements Comparable<Quiz> {

    private String title;
    private String quizId; // Should be unique, e.g., Q001
    private List<Question> questions;
    private ProficiencyLevel proficiencyLevel;
    private String filePath; // Store the file path
    private String associatedLessonId; // Optional: Link quiz to a lesson

    private static AtomicInteger quizCounter = new AtomicInteger(loadInitialQuizCounter()); // Thread-safe counter

    // Constructor for loading an existing quiz
    public Quiz(String filePath) {
        this.filePath = filePath;
        this.questions = new ArrayList<>();
        this.proficiencyLevel = new ProficiencyLevel(); // Default level initially
        if (!loadFromFile()) {
            System.err.println("Failed to load quiz from: " + filePath);
            setDefaultValues();
        }
    }

    // Constructor for creating a NEW quiz (likely called from CreateQuizController)
    public Quiz(String title, String level, List<Question> questions, String associatedLessonId, String teacherId) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Quiz title cannot be empty.");
        if (level == null || level.trim().isEmpty()) throw new IllegalArgumentException("Proficiency level cannot be empty.");
        if (questions == null || questions.isEmpty()) throw new IllegalArgumentException("Quiz must have questions.");

        this.title = title.trim();
        this.proficiencyLevel = new ProficiencyLevel(level);
        this.questions = new ArrayList<>(questions); // Copy list
        this.quizId = "Q" + quizCounter.getAndIncrement(); // Generate unique ID
        this.associatedLessonId = associatedLessonId; // Store link to lesson
        // Store teacherId if needed

        // Define file path
        this.filePath = ApplicationManager.getQuizzesDirectory() + File.separator + "quiz_" + this.quizId + ".txt";

        if (!saveToFile()) {
            System.err.println("Failed to save newly created quiz to: " + filePath);
            // Handle error - maybe throw exception
        } else {
            System.out.println("Successfully saved new quiz: " + this.quizId + " to " + this.filePath);
        }
    }


    private static int loadInitialQuizCounter() {
        // Similar to Lesson: Implement logic to find the highest existing quiz ID + 1
        // Or start from a base number.
        return 1; // Default start e.g., Q1, Q2...
    }

    private void setDefaultValues() {
        this.title = "Default Quiz";
        this.quizId = "Q_DEFAULT";
        this.proficiencyLevel = new ProficiencyLevel("Beginner");
        this.questions = new ArrayList<>(); // Empty list
        this.filePath = "";
        // Add default questions for testing if needed
        // questions.add(new Question("Default Q1?", new String[]{"A", "B"}, "A"));
    }

    public boolean loadFromFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot load quiz: File path is not set.");
            return false;
        }
        FileManager fm = new FileManager(filePath);
        HashMap<String, String> quizData = fm.loadData();

        if (quizData == null || quizData.isEmpty()) {
            System.err.println("Failed to load or empty data in quiz file: " + filePath);
            return false;
        }

        this.title = quizData.getOrDefault("title", "Default Title");
        this.quizId = quizData.getOrDefault("id", "Default ID"); // Key is 'id' in sample
        this.proficiencyLevel.setLevel(quizData.getOrDefault("level", "Beginner"));
        this.associatedLessonId = quizData.getOrDefault("lessonId", null); // Load associated lesson ID if present
        this.questions.clear(); // Clear existing before loading

        try {
            // Assuming a fixed number of questions (e.g., 4) based on sample - make dynamic if needed
            int questionCount = 0;
            while(true) {
                questionCount++;
                String qKey = "question" + questionCount;
                String qText = quizData.get(qKey);
                if (qText == null) break; // Stop if no more questions found

                String[] oTexts = new String[4]; // Assuming 4 options
                boolean optionsComplete = true;
                for (int j = 0; j < 4; j++) {
                    String oKey = "q" + questionCount + "_option" + (j + 1);
                    oTexts[j] = quizData.get(oKey);
                    if (oTexts[j] == null) {
                        optionsComplete = false;
                        System.err.println("Warning: Missing option " + (j+1) + " for question " + questionCount + " in " + filePath);
                        break;
                    }
                }
                if (!optionsComplete) continue; // Skip this question if options are missing

                String cKey = "q" + questionCount + "_correct";
                String cText = quizData.get(cKey); // This is the *text* of the correct answer in the sample
                if (cText == null) {
                    System.err.println("Warning: Missing correct answer for question " + questionCount + " in " + filePath);
                    continue; // Skip this question
                }

                // Find which option matches the correct answer text
                // String correctOptionValue = null;
                // for(String opt : oTexts) {
                //     if (opt.equals(cText)) { // Compare text directly
                //         correctOptionValue = opt;
                //         break;
                //     }
                // }
                // if (correctOptionValue == null) {
                //      System.err.println("Warning: Correct answer text '" + cText + "' not found in options for question " + questionCount + " in " + filePath);
                //     continue; // Skip
                // }

                // Create the question object
                questions.add(new Question(qText, oTexts, cText)); // Pass the correct answer text
            }
            System.out.println("Quiz loaded successfully from: " + filePath + " with " + questions.size() + " questions.");
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error creating question while loading quiz " + quizId + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error loading questions for quiz " + quizId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveToFile() {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Cannot save quiz: File path is not set.");
            return false;
        }
        if (quizId == null || quizId.isEmpty() || quizId.equals("Q_DEFAULT")) {
            System.err.println("Cannot save quiz: Invalid or default Quiz ID.");
            return false;
        }

        FileManager fm = new FileManager(filePath);
        HashMap<String, String> quizData = new HashMap<>();
        quizData.put("title", title);
        quizData.put("id", quizId);
        quizData.put("level", proficiencyLevel.getLevel());
        if (associatedLessonId != null && !associatedLessonId.isEmpty()) {
            quizData.put("lessonId", associatedLessonId);
        }
        // Add teacherId if needed
        // quizData.put("teacherId", teacherId);

        int qNum = 1;
        for (Question q : questions) {
            quizData.put("question" + qNum, q.getQuestionText());
            String[] options = q.getOptions();
            for (int i = 0; i < options.length; i++) {
                // Ensure options array is not too large for the assumed format (max 4)
                if (i < 4) {
                    quizData.put("q" + qNum + "_option" + (i + 1), options[i]);
                } else {
                    System.err.println("Warning: Quiz save format only supports 4 options. Skipping extra options for Q" + qNum);
                    break;
                }
            }
            // Ensure number of options matches the expected format (4 options)
            if (options.length < 4) {
                System.err.println("Warning: Question " + qNum + " has fewer than 4 options. Saving might be incomplete for standard format.");
                // Optionally pad with empty strings if needed, but the loader assumes 4 exist
            }

            quizData.put("q" + qNum + "_correct", q.getCorrectAnswer()); // Save the text of the correct answer
            qNum++;
        }

        return fm.saveDataOverwrite(quizData); // Use overwrite
    }


    // --- Static factory method (optional, constructor loading is fine too) ---
    // public static Quiz loadQuizFromFile(String file) {
    //     return new Quiz(file);
    // }

    // --- Getters ---
    public String getTitle() { return title; }
    public String getQuizId() { return quizId; }
    public List<Question> getQuestions() { return new ArrayList<>(questions); } // Return copy
    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public String getFilePath() { return filePath; }
    public String getAssociatedLessonId() { return associatedLessonId; }

    @Override
    public int compareTo(Quiz other) {
        int titleComparison = this.title.compareToIgnoreCase(other.title);
        if (titleComparison != 0) {
            return titleComparison;
        }
        return this.quizId.compareTo(other.quizId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return java.util.Objects.equals(quizId, quiz.quizId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(quizId);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "title='" + title + '\'' +
                ", quizId='" + quizId + '\'' +
                ", level=" + proficiencyLevel.getLevel() +
                ", questions=" + questions.size() +
                '}';
    }
}