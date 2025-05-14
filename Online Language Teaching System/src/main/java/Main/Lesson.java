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
        this.lessonId=title;
        this.content=title;
    }






    public Lesson(String filePath)
    {

    }

    public void setLesson(String title, String lessonId, String content)
    {
        this.title=title;
        this.lessonId=title;
        this.content=title;
    }

    public Lesson(String title, String content,String level, ArrayList<String> prerequisiteLessonIds,String filePath) {
        this.title = title;
        this.content = content;
        this.proficiencyLevel = new ProficiencyLevel(level);
        this.prerequisiteLessonIds = new ArrayList<>(prerequisiteLessonIds);
        this.lessonId = String.valueOf(numberOfLessons++);


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

}
