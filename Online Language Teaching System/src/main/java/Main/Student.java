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

    @Override
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

    public void setProgress(double progress){
        this.progress = progress;
    }



    @Override
    public String toString()
    {
        return "Student: "+ getUsername()+" "+getPassword()+" "+getID()+" "+getEmail()+" "+getLanguage();
    }


    @Override
    public int makeLessonCompleted(int i) {
        if (i >= 0 && i < lessons.size()) {
            compeleteLessons.set(i, true); // Mark lesson as completed
            updateProgress(); // Update progress when lesson is completed
            return 1; // Success
        }
        return 0; // Failure (invalid index)
    }



    public boolean isLessonCompleted(int i) {
        if (i >= 0 && i < compeleteLessons.size()) {
            return compeleteLessons.get(i);
        }
        return false;
    }



    public ArrayList<Lesson> getLessons()
    {
        return lessons;
    }
}