package Main;

import fileManager.FileManager;

import java.util.HashMap;

public class Lesson implements Comparable<Lesson> {
    private String title;
    private String lessonId;
    private static int numberOfLessons;
    private String content;
    private FileManager fm;

    public Lesson(String filePath)
    {
        fm =new FileManager(filePath);
        loadFromFile();
    }

    public Lesson(String title, String content, String filePath) {
        this.title = title;
        this.content = content;
        this.lessonId = String.valueOf(numberOfLessons++);
        this.fm = new FileManager(filePath);
        saveToFile();
    }

    public void loadFromFile()
    {
        HashMap<String, String> lessonData = fm.loadLesson();
        if (lessonData != null) {
            this.title = lessonData.get("title" );
            this.lessonId = lessonData.get("lessonId");
            this.content = lessonData.get("content");
        } else {
            this.title = "Default Title";
            this.lessonId = "Default ID";
            this.content = "Default Content";
        }
    }

    public void saveToFile()
    {
        HashMap<String,String> lessonData =new HashMap<>();
        lessonData.put("title",title);
        lessonData.put("lessonId",lessonId);
        lessonData.put("content",content);
        fm.saveLesson(lessonData);
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

    @Override
    public int compareTo(Lesson other) {
        return this.title.compareTo(other.title);
    }

}
