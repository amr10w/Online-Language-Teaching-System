package Main;

public class Question {

    private String question;
    private String[] options;
    private String correctAnswer;

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

}
