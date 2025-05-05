// src/main/java/fileManager/FileManager.java
package fileManager;

import java.io.*;
import java.nio.charset.StandardCharsets; // Specify Charset
import java.nio.file.*; // Use NIO
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles reading from and writing to files, particularly for application data
 * using simple key:value formats. Includes methods for overwriting, appending,
 * and loading different data structures.
 */
public class FileManager {

    private final Path pathObject; // Use Path object for modern file operations
    private final String filePathString; // Keep original string for messages

    /**
     * Creates a FileManager instance for a specific file path.
     * @param filePath The absolute or relative path to the file. Cannot be null or empty.
     * @throws IllegalArgumentException if filePath is null or empty.
     */
    public FileManager(String filePath) {
        Objects.requireNonNull(filePath, "File path cannot be null.");
        if (filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }
        this.filePathString = filePath;
        this.pathObject = Paths.get(filePath.trim()).toAbsolutePath(); // Work with absolute paths
        // Parent directory is ensured before writing in save methods.
    }

    /** Ensures the parent directory of the file exists, creating it if necessary. */
    private void ensureParentDirectoryExists() {
        Path parentDir = pathObject.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
                System.out.println("Created directory: " + parentDir);
            } catch (IOException e) {
                // Wrap IOException in a RuntimeException if directory creation is critical
                throw new UncheckedIOException("CRITICAL: Error creating directory " + parentDir + ": " + e.getMessage(), e);
            }
        } else if (parentDir != null && !Files.isDirectory(parentDir)) {
            throw new UncheckedIOException("CRITICAL: Path intended for directory is not a directory: " + parentDir, new IOException());
        }
    }

    /**
     * Saves data to the file, overwriting any existing content.
     * Each key-value pair is written on a new line in "key:value" format.
     * Handles potential multi-line values for a key named "content" by writing them
     * directly after the "content:" line. Other multi-line values will cause issues.
     *
     * @param data The Map containing data to save. Keys and values should not be null.
     * @return true if saving was successful, false otherwise.
     */
    public boolean saveDataOverwrite(Map<String, String> data) {
        Objects.requireNonNull(data, "Data map cannot be null for saving.");
        ensureParentDirectoryExists(); // Ensure directory exists before writing

        // Use try-with-resources for automatic closing, specify UTF-8 encoding
        try (BufferedWriter writer = Files.newBufferedWriter(pathObject, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, // Create file if it doesn't exist
                StandardOpenOption.TRUNCATE_EXISTING, // Overwrite content if it exists
                StandardOpenOption.WRITE))
        {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key == null || value == null) {
                    System.err.println("Warning: Skipping null key or value during save to " + filePathString);
                    continue;
                }

                // Special handling for multi-line 'content' key (simple approach)
                if ("content".equals(key) && value.contains("\n")) {
                    writer.write(key + ":"); // Write key and colon on one line
                    writer.newLine();        // Start content on the next line
                    writer.write(value);     // Write potentially multi-line content directly
                    writer.newLine();        // Add a newline after the content block
                } else {
                    // For single-line values or keys other than 'content'
                    if (value.contains("\n")) {
                        System.err.println("Warning: Value for key '" + key + "' contains newlines but is not 'content'. Saving on single line might corrupt format in: " + filePathString);
                        // Replace newlines or log error, saving as single line here:
                        value = value.replace("\n", "\\n"); // Basic escaping (loader needs to handle this)
                    }
                    writer.write(key + ":" + value);
                    writer.newLine();
                }
            }
            // System.out.println("Data successfully saved (overwrite) to: " + pathObject);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data (overwrite) to " + pathObject + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Appends data to the file.
     * Each key-value pair is appended on a new line in "key:value" format.
     * Useful for adding new entries (like users) without reading and rewriting the whole file.
     * Does NOT handle multi-line values well in append mode.
     *
     * @param data The Map containing data to append. Keys/values should not be null.
     * @return true if appending was successful, false otherwise.
     */
    public boolean saveDataAppend(Map<String, String> data) {
        Objects.requireNonNull(data, "Data map cannot be null for appending.");
        ensureParentDirectoryExists();

        // Use try-with-resources with append and create options
        try (BufferedWriter writer = Files.newBufferedWriter(pathObject, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, // Create if doesn't exist
                StandardOpenOption.APPEND, // Append to existing file
                StandardOpenOption.WRITE))
        {
            // Optional: Add a newline before appending if the file isn't empty and doesn't end with one
            if (Files.exists(pathObject) && Files.size(pathObject) > 0) {
                // Reading last byte is complex; safer to just add newline unconditionally if appending non-empty data
                // writer.newLine(); // Might add extra blank lines if file already ends with one
            }

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key == null || value == null) {
                    System.err.println("Warning: Skipping null key or value during append to " + filePathString);
                    continue;
                }
                if (value.contains("\n")) {
                    System.err.println("Warning: Value for key '" + key + "' contains newlines. Appending multi-line data is not recommended with this format in: " + filePathString);
                    value = value.replace("\n", "\\n"); // Basic escaping
                }
                writer.write(key + ":" + value);
                writer.newLine();
            }
            // System.out.println("Data successfully appended to: " + pathObject);
            return true;
        } catch (IOException e) {
            System.err.println("Error appending data to " + pathObject + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Generic method to load data from a file with "key:value" lines.
     * Reads the entire file line by line. Splits based on the *first* colon.
     * Handles a simple multi-line case ONLY for the key "content".
     *
     * @return A HashMap containing the loaded key-value pairs. Returns an empty map if file not found or empty. Returns null on critical read error.
     */
    public HashMap<String, String> loadData() {
        HashMap<String, String> data = new HashMap<>();
        if (!Files.exists(pathObject)) {
            System.err.println("Warning: File not found for loading: " + pathObject);
            return data; // Return empty map, not null, if file simply doesn't exist
        }
        if (!Files.isReadable(pathObject)) {
            System.err.println("Error: Cannot read file (check permissions): " + pathObject);
            return null; // Indicate read error
        }


        StringBuilder contentBuilder = null; // Used only when reading 'content'
        String currentKeyForContent = null;

        // Use try-with-resources and specify UTF-8
        try (BufferedReader reader = Files.newBufferedReader(pathObject, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                // Check if we are currently building multi-line content
                if (contentBuilder != null) {
                    // Does this line look like a new key:value pair?
                    if (trimmedLine.contains(":") && !isLikelyPartOfContent(trimmedLine)) {
                        // Finish the previous content block
                        data.put(currentKeyForContent, contentBuilder.toString().trim());
                        contentBuilder = null; // Stop content mode
                        currentKeyForContent = null;
                        // Process the current line as a new key:value
                        processLine(line, data);
                    } else {
                        // Append to the current content block
                        contentBuilder.append(line).append("\n"); // Append raw line with newline
                    }
                } else {
                    // Not currently reading content, process line normally
                    processLine(line, data);
                    // Check if this line starts a new content block
                    if (data.containsKey("content") && contentBuilder == null && line.trim().startsWith("content:")) {
                        // Special case: If processLine just added content and it was empty, start multi-line mode
                        String initialContent = data.get("content");
                        if(initialContent.isEmpty()){
                            contentBuilder = new StringBuilder();
                            currentKeyForContent = "content";
                            // Remove the empty entry put by processLine
                            // data.remove("content"); // Keep it simple: let the final put overwrite
                        }
                    }
                }
            } // End while loop

            // If we finished reading the file while still in content mode, save the last block
            if (contentBuilder != null && currentKeyForContent != null) {
                data.put(currentKeyForContent, contentBuilder.toString().trim());
            }

            // System.out.println("Data loaded successfully from: " + pathObject);
            return data;
        } catch (IOException e) {
            System.err.println("Error loading data from " + pathObject + ": " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on critical IO error
        }
    }

    /** Helper to process a single line for loadData(). */
    private void processLine(String line, Map<String, String> data) {
        int colonIndex = line.indexOf(':');
        if (colonIndex > 0) { // Ensure colon exists and is not the first character
            String key = line.substring(0, colonIndex).trim();
            // Value is everything after the *first* colon
            String value = (colonIndex == line.length() - 1) ? "" : line.substring(colonIndex + 1);
            // Don't trim value here yet, especially if it's the start of content
            data.put(key, value);
        } else if (!line.trim().isEmpty()) {
            // Handle lines without a colon (could be part of multi-line value or malformed)
            System.err.println("Warning: Malformed line (no colon) in " + pathObject + ": " + line);
        }
    }

    /** Heuristic check if a line is likely a new key:value pair vs part of content. */
    private boolean isLikelyPartOfContent(String line) {
        // If the line starts with common patterns that are unlikely keys, assume it's content
        // E.g., indentation, list markers, special characters etc.
        // This is a basic example, can be refined.
        return line.startsWith(" ") || line.startsWith("\t") || line.startsWith("*") || line.startsWith("-");
    }


    /**
     * Loads user data specifically, grouping values by key.
     * Assumes the user file format repeats keys (username, password, email, role, language)
     * for each user, with each key:value on its own line.
     *
     * @return A HashMap where keys are field names (e.g., "username") and values are ArrayLists
     *         of corresponding data in the order they appeared. Returns null on critical error or file not found.
     */
    public HashMap<String, ArrayList<String>> loadGroupedUserData() {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        if (!Files.exists(pathObject)) {
            System.err.println("Error: User data file not found: " + pathObject);
            // Create an empty file? Or just return null? Returning null signals the caller file is missing.
            return null;
        }
        if (!Files.isReadable(pathObject)) {
            System.err.println("Error: Cannot read user data file (check permissions): " + pathObject);
            return null;
        }

        // Use try-with-resources and specify UTF-8
        try (BufferedReader reader = Files.newBufferedReader(pathObject, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) { // Ensure colon exists and is not the first character
                    String key = line.substring(0, colonIndex).trim();
                    String value = (colonIndex == line.length() - 1) ? "" : line.substring(colonIndex + 1).trim(); // Trim value here for user data

                    // Get the list for the key, or create it if it doesn't exist, then add value
                    data.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

                } else if (!line.trim().isEmpty()) {
                    System.err.println("Warning: Skipped malformed user data line (no colon or empty) in " + pathObject + ": " + line);
                }
            }
            // System.out.println("Grouped user data loaded successfully from: " + pathObject);
            return data;
        } catch (IOException e) {
            System.err.println("Error loading grouped user data from " + pathObject + ": " + e.getMessage());
            e.printStackTrace();
            return null; // Return null on critical IO error
        }
    }

    /**
     * @deprecated Use {@link #loadGroupedUserData()} which is more robust and returns all fields.
     * This method only loads usernames and passwords and is prone to errors if file format isn't perfect.
     */
    @Deprecated
    public void loadUsersData(ArrayList<String> users, ArrayList<String> passwords) {
        System.err.println("Warning: FileManager.loadUsersData() is deprecated. Use loadGroupedUserData().");
        Objects.requireNonNull(users, "Users list cannot be null");
        Objects.requireNonNull(passwords, "Passwords list cannot be null");

        HashMap<String, ArrayList<String>> groupedData = loadGroupedUserData();
        users.clear(); // Clear output lists before populating
        passwords.clear();

        if (groupedData != null) {
            users.addAll(groupedData.getOrDefault("username", new ArrayList<>()));
            passwords.addAll(groupedData.getOrDefault("password", new ArrayList<>()));
            if (users.size() != passwords.size()) {
                System.err.println("CRITICAL WARNING (Deprecated Method): Loaded user/password counts mismatch in " + pathObject + ". Data may be corrupt.");
                // Attempt to truncate to the shorter list size? Or leave as is? Leaving as is.
            }
        } else {
            System.err.println("Error (Deprecated Method): Failed to load grouped data to populate users/passwords lists from " + pathObject);
        }
    }
}