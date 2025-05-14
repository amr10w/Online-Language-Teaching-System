package fileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private String filePath;

    public FileManager(String filePath)
    {
        this.filePath=filePath;
    }
    public void saveData(HashMap<String, String> Data)
    {
        try(FileWriter writer =new FileWriter(filePath,true))
        {
            for(String key : Data.keySet())
            {
                writer.write(key+":"+Data.get(key)+"\n");
            }

        } catch (IOException e) {
            System.err.println("Error saving lesson to " + filePath + ": " + e.getMessage());
        }
    }


    public void saveQuiz()
    {

    }


    public HashMap<String, String> loadData() {
        HashMap<String, String> Data = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(":",2); // Split on first colon
                    if (parts.length == 2) {
                        Data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
            return Data;
        } catch (IOException e) {
            System.out.println("Error loading quiz from " + filePath + ": " + e.getMessage());
            return null;
        }
    }


}