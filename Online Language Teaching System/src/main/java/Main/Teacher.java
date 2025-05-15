package Main;

import java.util.ArrayList;

public class Teacher extends User{
    private String language;
    private double balance;
    private int numberOfCreatedLessons=0;
    private ArrayList<Lesson> createdLessons;
    private static int numberOfTeachers = 0;
//    private ArrayList<Question> questions = new ArrayList<>();


    public Teacher(String name, String email, String password, String ID, String language) {
        super(name, email, password, ID);
        createdLessons=new ArrayList<>();
        if (language == null || language.trim().isEmpty()) {
            System.out.println("Warning: Teacher language is empty for user " + name + ", Defaulting to 'English'.");
            this.language = "English";
        } else {
            this.language = language.trim();
        }
        this.balance = 0.0;
        numberOfTeachers++;

    }

    public Lesson createLesson(String title,String lessonId,String content,String level) {
        Lesson lesson =new Lesson(title,lessonId,content,level);
        createdLessons.add(lesson);
        numberOfCreatedLessons++;
        return lesson;
    }

    public void addLesson(Lesson lesson)
    {
        createdLessons.add(lesson);
        numberOfCreatedLessons++;
    }
    public static int getNumberOfTeachers() {
        return numberOfTeachers;
    }

    public String getLanguage() {
        return language;
    }
    public double getBalance(){
        return this.balance;
    }

    public int getNumberOfCreatedLessons() {
        return numberOfCreatedLessons;

    }

    public ArrayList<Lesson> getCreatedLessonIds() { return createdLessons;}


    @Override
    public String toString()
    {
        return "Teacher: "+ getUsername()+" "+getPassword()+" "+getID()+" "+getEmail()+" "+getLanguage();
    }


}
