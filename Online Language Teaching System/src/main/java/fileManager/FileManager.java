package fileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; // Use List interface
import java.util.Map;   // Use Map interface

public class FileManager {

    private final String filePath;
    private final Path pathObject;

    public FileManager(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        this.filePath = filePath.trim();
        this.pathObject = Paths.get(this.filePath);
        // Ensure parent directory exists, especially when saving new files
        ensureDirectoryExists(pathObject.getParent());
    }

    private void ensureDirectoryExists(Path directoryPath) {
        if (directoryPath != null && !Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println("Created directory: " + directoryPath);
            } catch (IOException e) {
                System.err.println("Error creating directory " + directoryPath + ": " + e.getMessage());
                // Depending on severity, maybe throw a runtime exception
            }
        }
    }

    /**
     * Saves data to the file, overwriting existing content.
     * Each key-value pair is written on a new line in "key:value" format.
     *
     * @param data The HashMap containing data to save.
     * @return true if saving was successful, false otherwise.
     */
    public boolean saveDataOverwrite(Map<String, String> data) {
        ensureDirectoryExists(pathObject.getParent()); // Ensure directory exists before writing
        try (BufferedWriter writer = Files.newBufferedWriter(pathObject)) { // Uses try-with-resources, handles closing
            for (Map.Entry<String, String> entry : data.entrySet()) {
                // Basic check for multi-line values - this format doesn't handle them well!
                // If content spans multiple lines, it needs special handling during save/load.
                String value = entry.getValue();
                if (value.contains("\n")) {
                    System.err.println("Warning: Value for key '" + entry.getKey() + "' contains newlines. Saving might corrupt format in: " + filePath);
                    // Replace newlines or use a different saving strategy for multi-line content.
                    // For now, we save it as is, which loadLesson might handle (or break).
                }
                writer.write(entry.getKey() + ":" + value);
                writer.newLine(); // Use platform-independent newline
            }
            System.out.println("Data successfully saved (overwrite) to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data (overwrite) to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Appends data to the file.
     * Each key-value pair is appended on a new line in "key:value" format.
     * Useful for adding new users without reloading and overwriting the whole file.
     *
     * @param data The HashMap containing data to append.
     * @return true if appending was successful, false otherwise.
     */
    public boolean saveDataAppend(Map<String, String> data) {
        ensureDirectoryExists(pathObject.getParent());
        // Use FileWriter with append=true flag
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter writer = new BufferedWriter(fw)) {

            // Add a separator if the file is not empty and doesn't end with newline (optional)
            // if (Files.size(pathObject) > 0) { writer.newLine(); }

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String value = entry.getValue();
                if (value.contains("\n")) {
                    System.err.println("Warning: Value for key '" + entry.getKey() + "' contains newlines. Appending might corrupt format in: " + filePath);
                }
                writer.write(entry.getKey() + ":" + value);
                writer.newLine();
            }
            System.out.println("Data successfully appended to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error appending data to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Loads data from a file where each line is expected to be "key:value".
     * Handles potential multi-line values ONLY if the key "content" is encountered.
     *
     * @return A HashMap containing the loaded data, or null if an error occurs.
     * @deprecated Use loadData() for general key-value or loadSpecific formats. This is too specific.
     */
    @Deprecated
    public HashMap<String, String> loadLesson() {
        System.err.println("Warning: loadLesson() is deprecated. Use generic loadData() or specific loaders.");
        return loadData(); // Delegate to the more generic loader for now
    }


    /**
     * Generic method to load data from a file with "key:value" lines.
     * Reads the entire file line by line. Splits based on the *first* colon.
     * Handles simple key-value pairs. Does NOT specifically handle multi-line 'content'.
     *
     * @return A HashMap containing the loaded key-value pairs, or an empty map if file not found/empty, or null on error.
     */
    public HashMap<String, String> loadData() {
        HashMap<String, String> data = new HashMap<>();
        if (!Files.exists(pathObject)) {
            System.err.println("Warning: File not found for loading: " + filePath);
            return data; // Return empty map if file doesn't exist
        }

        try (BufferedReader reader = Files.newBufferedReader(pathObject)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.contains(":")) {
                    int colonIndex = line.indexOf(':');
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim(); // Value is everything after the first colon
                    if (!key.isEmpty()) { // Avoid adding entries with empty keys
                        data.put(key, value);
                    } else {
                        System.err.println("Warning: Skipped line with empty key in " + filePath + ": " + line);
                    }
                } else if (!line.isEmpty()) {
                    System.err.println("Warning: Skipped malformed line (no colon or empty) in " + filePath + ": " + line);
                }
            }
            System.out.println("Data loaded successfully from: " + filePath);
            return data;
        } catch (IOException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on critical IO error
        }
    }


    /**
     * Loads user data specifically, grouping values by key.
     * Assumes the user file repeats keys (username, password, email, role, language).
     *
     * @return A HashMap where keys are field names (e.g., "username") and values are ArrayLists of corresponding data. Returns null on error.
     */
    public HashMap<String, ArrayList<String>> loadGroupedUserData() {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        if (!Files.exists(pathObject)) {
            System.err.println("Error: User data file not found: " + filePath);
            return null; // Indicate file not found error
        }

        try (BufferedReader reader = Files.newBufferedReader(pathObject)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.contains(":")) {
                    int colonIndex = line.indexOf(':');
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    if (!key.isEmpty()) {
                        // Get the list for the key, or create it if it doesn't exist
                        data.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    } else {
                        System.err.println("Warning: Skipped user data line with empty key in " + filePath + ": " + line);
                    }
                } else if (!line.isEmpty()) {
                    System.err.println("Warning: Skipped malformed user data line (no colon or empty) in " + filePath + ": " + line);
                }
            }
            System.out.println("Grouped user data loaded successfully from: " + filePath);
            return data;
        } catch (IOException e) {
            System.err.println("Error loading grouped user data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on critical IO error
        }
    }

    /**
     * @deprecated Use loadGroupedUserData() which is more robust. This method is prone to errors if file format isn't perfect.
     */
    @Deprecated
    public void loadUsersData(ArrayList<String> users, ArrayList<String> passwords) {
        System.err.println("Warning: loadUsersData() is deprecated. Use loadGroupedUserData().");
        // This old method is fragile. If you *must* use it:
        HashMap<String, ArrayList<String>> groupedData = loadGroupedUserData();
        if (groupedData != null) {
            users.clear();
            passwords.clear();
            users.addAll(groupedData.getOrDefault("username", new ArrayList<>()));
            passwords.addAll(groupedData.getOrDefault("password", new ArrayList<>()));
        }
    }


// --- Other potential methods ---
// public boolean deleteFile() { ... }
// public static List<String> listFilesInDirectory(String directoryPath) { ... }
}