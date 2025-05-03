package Main;
public class Teacher extends User{
    private Language language;
    private double balance;
    private int numberOfCreatedLessons=0;
    private ArrayList<Question> questions = new ArrayList<>();

    public Teacher(String name, String email, String password, String ID){
        super(name,email,password,ID);

    }


    public void createLesson(){
        numberOfCreatedLessons+=1;
    }
    public void createQuestion(String question,String [] options ,String correctAnswer){
        Question Q = new Question (String question,String [] options ,String correctAnswer);
        questions.add(Q);
        //each teacher can have multiple questions created, they can be accessed via the array list named questions
    }
    public void editLesson(Lesson lesson){
//the editLesson method is not found in the Lesson class, waiting until it get implemented...
    }
    public void editQuiz(Quiz quiz){
//the editQuiz method is not found in the Quiz class, waiting until it get implemented...
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
