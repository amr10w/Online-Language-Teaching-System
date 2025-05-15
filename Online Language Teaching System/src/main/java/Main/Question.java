package Main;

public class Question {

    private String question;
    private String[] options;
    private String correctAnswer;
    private String userAnswer; // Add this to store the selected answer


    public Question (String question,String [] options ,String correctAnswer)
    {
        this.question=question;
        this.options=options;
        this.correctAnswer=correctAnswer;
    }

    public String getQuestion()
    {
        return question;
    }

    public String getCorrectAnswer()
    {
        return correctAnswer;
    }
    public String[] getOptions()
    {
        return options;
    }

    @Override
    public String toString()
    {
        return "Question: "+question+"\nOptions\n"+String.join("\n", options)+"\nCorrect: " + correctAnswer;
    }
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public boolean isCorrect() {
        return userAnswer != null && userAnswer.equals(correctAnswer);
    }

}
