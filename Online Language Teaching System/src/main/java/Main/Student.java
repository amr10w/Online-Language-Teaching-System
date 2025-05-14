package Main;


import java.util.ArrayList;
import java.util.HashMap;

public class Student extends User implements Progress {

    private String language;
    private static int numberOfStudents = 0;
    private double progress = 0;
    private ArrayList<Lesson> lessons;
    private ArrayList<Boolean> compeleteLessons;
    private ProficiencyLevel proficiencyLevel;

    public Student(String name, String email, String password, String ID, String language) {
        super(name, email, password, ID);
        this.language = language != null ? language : "";
        numberOfStudents += 1;
        lessons=new ArrayList<>();
        proficiencyLevel=new ProficiencyLevel();
        compeleteLessons=new ArrayList<>();

    }

    public String getLanguage() {
        return language;
    }

    public void updateProgress() {
        this.progress += 1;

    }

    public String getProficiencyLevel()
    {
        return proficiencyLevel.getLevel();
    }

    public void addLesson(Lesson lesson)
    {
        lessons.add(lesson);
        compeleteLessons.add(false);
    }



    public double getProgress() {
        return progress;
    }



    @Override
    public String toString()
    {
        return "Student: "+ getUsername()+" "+getPassword()+" "+getID()+" "+getEmail()+" "+getLanguage();
    }

    @Override
    public String getUserId() {
        return super.getID();
    }

    @Override
    public int makeLessonCompleted(String title) {

        if(isLessonCompleted(title))
        {
            return 50;
        }
        return 0;

    }

    @Override
    public void recordQuizScore(int score) {

    }

    @Override
    public boolean isLessonCompleted(String title) {
        for(int i=0;i<lessons.size();i++)
        {
            if(lessons.get(i).getTitle().equals(title))
            {
                compeleteLessons.set(i,true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getQuizScore(String quizId) {
        return 0;
    }

    public ArrayList<Lesson> getLessons()
    {
        return lessons;
    }
}