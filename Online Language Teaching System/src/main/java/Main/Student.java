package Main;


import java.util.ArrayList;
import java.util.HashMap;

public class Student extends User {

    private String language;
    private static int numberOfStudents = 0;
    private double progress = 0;
    private ArrayList<Lesson> lessons;
    private ProficiencyLevel proficiencyLevel;

    public Student(String name, String email, String password, String ID, String language) {
        super(name, email, password, ID);
        this.language = language != null ? language : "";
        numberOfStudents += 1;
        lessons=new ArrayList<>();
        proficiencyLevel=new ProficiencyLevel();

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



    public double getProgress() {
        return progress;
    }



    @Override
    public String toString()
    {
        return "Student: "+ getUsername()+" "+getPassword()+" "+getID()+" "+getEmail()+" "+getLanguage();
    }

}