package Main;
import java.util.ArrayList;

public class Student extends User implements Progress{




    private static int numberOfStudents=0;
    //we make it static to be shared over all objects
    private double progress=0;
    ArrayList<Language> language = new ArrayList<>();



    Student(String name,String email,String password,String ID){
        super(name,email,password,ID);
                numberOfStudents+=1;
    }



    public void startQuiz(){
        //initially empty
    }
    public void enrollInLanguage(Language language){

        this.language.add(language);
    }
    public Lesson[] getAvailableLessons(Language language){

        return this.Lesson;
    }

    public ArrayList<Language> getLanguage() {
        return language;
    }

    public void updateProgress(){
        this.progress+=1;

    }
    public double getProgress(){
        return this.progress;
    }
    public double ShowCurriculum(){

     return language.size();
    }


}
