package Main;

import fileManager.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Lesson implements Comparable<Lesson> {
    private String title;
    private String lessonId;
    private static int numberOfLessons=1020;
    private String content;
    private ProficiencyLevel proficiencyLevel;
    private ArrayList<String> prerequisiteLessonIds = new ArrayList<>();
    private final FileManager fm;

    public Lesson(String filePath)
    {
        fm =new FileManager(filePath);
        loadFromFile();
    }

    public Lesson(String title, String content,String level, ArrayList<String> prerequisiteLessonIds,String filePath) {
        this.title = title;
        this.content = content;
        this.proficiencyLevel = new ProficiencyLevel(level);
        this.prerequisiteLessonIds = new ArrayList<>(prerequisiteLessonIds);
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
            String prerequisites = lessonData.get("prerequisites");
            if(prerequisites.isEmpty())
            {
                prerequisites="no prerequisites";
            }else
            {
                String [] prerequisiteIds=prerequisites.split(",");
                for(String e:prerequisiteIds)
                {
                    this.prerequisiteLessonIds.add(e);
                }
            }

            this.proficiencyLevel=new ProficiencyLevel(lessonData.get("level"));
        } else {
            this.title = "Default Title";
            this.lessonId = "Default ID";
            this.content = "Default Content";
            this.proficiencyLevel=new ProficiencyLevel("Beginner");

        }
    }

    public void saveToFile()
    {
        HashMap<String,String> lessonData =new HashMap<>();
        lessonData.put("title",title);
        lessonData.put("lessonId",lessonId);
        lessonData.put("content",content);
        StringBuilder ids= new StringBuilder();
        if (prerequisiteLessonIds.isEmpty()) {
            ids.append("no prerequisites");
        }
        else if(prerequisiteLessonIds.get(0).equals("no prerequisites"))
        {
            ids.append("no prerequisites");
        }
        else
        {
            for(String e:prerequisiteLessonIds)
            {
                ids.append(e);
                ids.append(",");

            }
        }
        lessonData.put("prerequisite", ids.toString() );
        lessonData.put("level",proficiencyLevel.getLevel());
        fm.saveData(lessonData);
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

}
