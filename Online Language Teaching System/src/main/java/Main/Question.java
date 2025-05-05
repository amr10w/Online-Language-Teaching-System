package Main;

import java.util.Arrays;

public class Question {

    private String questionText; // Renamed for clarity
    private String[] options;
    private String correctAnswer; // Store the exact text of the correct option

    public Question(String questionText, String[] options, String correctAnswer) {
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("Question must have options.");
        }
        // Validate options are not empty
        for(String option : options) {
            if (option == null || option.trim().isEmpty()) {
                throw new IllegalArgumentException("Question options cannot be empty.");
            }
        }
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Correct answer cannot be empty.");
        }
        // Validate that the correct answer is one of the provided options
        boolean found = false;
        for (String option : options) {
            if (option.equals(correctAnswer)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Correct answer must be one of the provided options.");
        }

        this.questionText = questionText.trim();
        // Create a copy of the options array
        this.options = Arrays.copyOf(options, options.length);
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String[] getOptions() {
        // Return a copy to prevent external modification
        return Arrays.copyOf(options, options.length);
    }

    @Override
    public String toString() {
        return "Question: " + questionText + "\n" +
                "Options: " + Arrays.toString(options) + "\n" +
                "Correct: " + correctAnswer;
    }
}