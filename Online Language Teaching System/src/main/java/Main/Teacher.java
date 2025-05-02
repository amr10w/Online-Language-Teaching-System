package Main;
public class Teacher extends User{
    private Language language;
    private double balance;
    private int numberOfCreatedLessons=0;

    Teacher(String name,String email,String password,String ID){
        super(name,email,password,ID);

    }


    public void createLesson(){
        numberOfCreatedLessons+=1;
    }
    public void createQuestion(){

    }
    public void editLesson(Lesson lesson){

    }
    public void editQuiz(Quiz quiz){

    }
    public String getLanguage(){
        return this.language.getLanguageName();
    }
    public double getBalance(){
        return this.balance;
    }

    public int getNumberOfCreatedLessons() {
        return numberOfCreatedLessons;
    }


}
