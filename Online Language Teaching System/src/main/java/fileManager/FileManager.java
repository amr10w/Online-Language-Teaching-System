package fileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private String filePath;

    public FileManager(String filePath)
    {
        this.filePath=filePath;
    }
    public void saveLesson(HashMap<String, String> lessonData)
    {
        try(FileWriter writer =new FileWriter(filePath))
        {
            for(String key : lessonData.keySet())
            {
                writer.write(lessonData.get(key)+":"+key+"\n");
            }

        } catch (IOException e) {
            System.err.println("Error saving lesson to " + filePath + ": " + e.getMessage());
        }
    }

    public HashMap<String,String> loadLesson()
    {
        HashMap<String, String> lessonData = new HashMap<>();
        String keyContent="content";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean contentFound = false;
            StringBuilder allContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(":", 2);
                    if (parts.length != 2) {
                        continue;
                    }
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (key.equals(keyContent)) {
                        contentFound = true;
                        allContent.append(value); // Start with the first line of content
                        break; // Exit the loop to start collecting remaining lines
                    } else {
                        lessonData.put(key, value); // Store title, lessonId, etc.
                    }
                }
            }
            if(contentFound)
            {
                while((line=reader.readLine() )!=null)
                {
                    if(!line.trim().isEmpty())
                    {
                        if(allContent.length()>0)
                        {
                            allContent.append("\n");
                        }
                        allContent.append(line.trim());
                    }
                }
                lessonData.put(keyContent,allContent.toString());
            }


            return lessonData;
        }catch (IOException e) {
            System.err.println("Error loading lesson from " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    public void saveQuiz()
    {

    }

    public HashMap<String, String> loadQuiz() {
        HashMap<String, String> quizData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(":", 2); // Split on first colon
                    if (parts.length == 2) {
                        quizData.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
            return quizData;
        } catch (IOException e) {
            System.err.println("Error loading quiz from " + filePath + ": " + e.getMessage());
            return null;
        }
    }

}
