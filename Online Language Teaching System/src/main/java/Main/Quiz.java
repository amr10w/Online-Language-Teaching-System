package Main;



import java.util.ArrayList;
import java.util.HashMap;

public class Quiz implements Comparable<Quiz>{

    private String title;
    private String quizId;
    private ArrayList<Question> questions;
    private ProficiencyLevel proficiencyLevel;


    public Quiz(String filePath)
    {

        questions=new ArrayList<>();
        proficiencyLevel=new ProficiencyLevel();

    }





    public static Quiz setQuiz(String file)
    {
        return new Quiz(file);
    }

    public String getTitle()
    {
        return title;
    }
    public String getQuizId()
    {
        return quizId;
    }
    public ArrayList<Question> getQuestions()
    {
        return questions;
    }

    @Override
    public int compareTo(Quiz other) {
        return this.title.compareTo(other.title);
    }

}





