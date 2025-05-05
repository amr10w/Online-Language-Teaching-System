// src/main/java/Main/Question.java
package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single question within a Quiz.
 * Contains the question text, multiple-choice options, and the correct answer.
 */
public class Question {

    private final String questionText;
    private final String[] options; // Array to hold the options (e.g., 4 options)
    private final String correctAnswer; // Store the exact text of the correct option

    /**
     * Creates a new Question object.
     *
     * @param questionText The text of the question. Cannot be null or empty.
     * @param options An array of strings representing the answer options. Cannot be null or empty, and options cannot be empty strings.
     * @param correctAnswer The exact string text of the correct answer, which must be one of the provided options. Cannot be null or empty.
     * @throws IllegalArgumentException if any input parameters are invalid.
     */
    public Question(String questionText, String[] options, String correctAnswer) {
        // --- Input Validation ---
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("Question must have options.");
        }
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Correct answer cannot be empty.");
        }

        // Validate individual options and check if the correct answer exists within options
        boolean foundCorrect = false;
        List<String> trimmedOptions = new ArrayList<>(options.length);
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                throw new IllegalArgumentException("Question options cannot be null or empty strings.");
            }
            String trimmedOption = option.trim();
            trimmedOptions.add(trimmedOption);
            if (trimmedOption.equals(correctAnswer.trim())) {
                foundCorrect = true;
            }
        }

        if (!foundCorrect) {
            throw new IllegalArgumentException("Correct answer ('" + correctAnswer.trim() + "') must exactly match one of the provided options: " + Arrays.toString(options));
        }

        // --- Initialization ---
        this.questionText = questionText.trim();
        // Store a copy of the trimmed options
        this.options = trimmedOptions.toArray(new String[0]);
        this.correctAnswer = correctAnswer.trim(); // Store trimmed correct answer
    }

    // --- Getters ---

    /**
     * Gets the text of the question.
     * @return The question text.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Gets the text of the correct answer option.
     * @return The correct answer string.
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Gets the available answer options for this question.
     * @return A copy of the options array to prevent external modification.
     */
    public String[] getOptions() {
        // Return a copy to maintain encapsulation
        return Arrays.copyOf(options, options.length);
    }

    // --- Overrides ---

    @Override
    public String toString() {
        return "Question[Text='" + questionText + "', Options=" + Arrays.toString(options) + ", Correct='" + correctAnswer + "']";
    }

    // Optional: equals() and hashCode() if questions need to be compared/stored in sets/maps
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        // Questions are equal if text, options, and correct answer are the same
        return Objects.equals(questionText, question.questionText) &&
                Arrays.equals(options, question.options) && // Order matters here
                Objects.equals(correctAnswer, question.correctAnswer);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(questionText, correctAnswer);
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }
}