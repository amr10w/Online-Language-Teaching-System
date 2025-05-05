package Main;

import fileManager.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import Main.Quiz;
import Main.Question;

public class Quiz implements Comparable<Quiz>{

    private String title;
    private String quizId;
    private ArrayList<Question> questions;
    private ProficiencyLevel proficiencyLevel;
    private FileManager fm;
    private static int numberOfQuestions =4;
    private QuizResult result;

    public Quiz(String filePath)
    {
        fm = new FileManager(filePath);
        questions=new ArrayList<>();
        proficiencyLevel=new ProficiencyLevel();
        loadQuizData();
    }


    public void loadQuizData()
    {

        HashMap<String,String> quizData = fm.loadData();
        if (quizData != null) {
            this.title=quizData.get("title");
            System.out.println(quizData.get("title"));
            this.quizId=quizData.get("id");
            this.proficiencyLevel.setLevel(quizData.get("level"));
            for(int i=0;i<4;i++) {
                String qKey = "question" + (i + 1);
                String qText = quizData.get(qKey);
                String[] oTexts = new String[4];
                for (int j = 0; j < 4; j++) {
                    String oKey = "q" + (i + 1) + "_option" + (j + 1);
                    oTexts[j] = quizData.get(oKey);
                }

                String cKey = "q" + (i + 1) + "_correct";
                String cText = quizData.get(cKey);

                questions.add(new Question(qText, oTexts, cText));
            }


        }
        else
        {
            // Fallback if loading fails
            this.title = "Default Title";
            this.quizId = "Default ID";
            for (int i = 1; i <= 4; i++) {
                questions.add(new Question(
                        "Default Question " + i,
                        new String[]{"Option 1", "Option 2", "Option 3", "Option 4"},
                        "Option 1"
                ));
            }
        }
    }

    public static Quiz setQuiz(String file)
    {
        return new Quiz(file);
    }

    public String getTitle()
    {
        return title;
    }
    
    public static int getNumberOfQuestions()
    {
        return numberOfQuestions;
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





