package Main;



import java.util.ArrayList;
import java.util.HashMap;

public class Lesson implements Comparable<Lesson> {
    private String title;
    private String lessonId;
    private static int numberOfLessons=1020;
    private String content;
    private ProficiencyLevel proficiencyLevel;
    private ArrayList<String> prerequisiteLessonIds = new ArrayList<>();
    private Quiz quiz;


    public Lesson()
    {

    }
    public Lesson(String title,String lessonId,String content)
    {
        this.title=title;
        this.lessonId=lessonId;
        this.content=content;
    }
    public Lesson(String title,String lessonId,String content,String level)
    {
        this.title=title;
        this.lessonId=lessonId;
        this.content=content;
        proficiencyLevel=new ProficiencyLevel(level);
    }







    public void setLesson(String title, String lessonId, String content)
    {
        this.title=title;
        this.lessonId=lessonId;
        this.content=content;
    }



    public String getTitle() {
        return title;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getPrerequisiteLessonIds() {
        return prerequisiteLessonIds;
    }

    @Override
    public int compareTo(Lesson other) {
        return this.title.compareTo(other.title);
    }

    public Quiz getQuiz()
    {
        return quiz;
    }

    public void setQuiz(String pathfile)
    {
        this.quiz=new Quiz(pathfile);
    }

    public boolean getAllFieldsNotEmpty()
    {
        return !getTitle().isEmpty() | !getContent().isEmpty() | !getLessonId().isEmpty();
    }

}
