// src/main/java/Main/ApplicationManager.java
package Main;

import fileManager.FileManager;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;


public class ApplicationManager {

    // --- Constants for Resource Paths (relative to classpath root) ---
    private static final String DATABASE_DIR = "/Database/Database"; // Directory containing users.txt
    private static final String USERS_FILE_NAME = "users.txt";
    private static final String LESSONS_RESOURCE_DIR = "/lessons";
    private static final String QUIZZES_RESOURCE_DIR = "/quizzes";
    private static final String PROGRESS_RESOURCE_DIR = "/progress"; // Added for student progress

    // --- Resolved Runtime Paths (will be set in static block) ---
    private static Path runtimeBaseResourcePath = null; // Base directory where resources are found
    private static Path runtimeUsersFilePath = null;
    private static Path runtimeLessonsDirPath = null;
    private static Path runtimeQuizzesDirPath = null;
    private static Path runtimeProgressDirPath = null; // Added for student progress

    // --- In-Memory Data Storage ---
    private static final Map<String, Student> students = new HashMap<>(); // Use Map for faster lookup by username
    private static final Map<String, Teacher> teachers = new HashMap<>(); // Use Map
    private static final Map<String, User> users = new HashMap<>();      // Combined map for easy lookup

    // Static initializer block: Resolve paths and load data when the class loads
    static {
        System.out.println("--- ApplicationManager Initialization Start ---");
        try {
            resolveResourcePaths();
            if (runtimeUsersFilePath != null) {
                loadUsers();
                // Load progress data after users are loaded
                if(runtimeProgressDirPath != null) {
                    loadAllStudentProgress();
                }
            } else {
                System.err.println("CRITICAL: User file path not resolved. Cannot load users.");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Exception during ApplicationManager initialization: " + e.getMessage());
            e.printStackTrace();
            // Consider exiting if core data cannot be loaded
        }
        System.out.println("--- ApplicationManager Initialization End ---");
    }

    // Private constructor to prevent instantiation (static utility class)
    private ApplicationManager() {}

    /**
     * Resolves the runtime paths for resource directories (data, lessons, quizzes).
     * This is crucial for accessing files correctly whether running from IDE or JAR.
     */
    private static void resolveResourcePaths() {
        try {
            // Find a reliable anchor resource (e.g., the users.txt file itself)
            URL anchorUrl = ApplicationManager.class.getResource(DATABASE_DIR + "/" + USERS_FILE_NAME);
            if (anchorUrl == null) {
                // Fallback: Try finding just the directory
                anchorUrl = ApplicationManager.class.getResource(DATABASE_DIR);
                if (anchorUrl == null) {
                    System.err.println("CRITICAL: Could not find anchor resource directory: " + DATABASE_DIR);
                    throw new RuntimeException("Cannot locate resource directory: " + DATABASE_DIR);
                }
            }

            // Determine the base path from the anchor
            File anchorFile = new File(anchorUrl.toURI());
            // The base path might be the directory containing the anchor, or its parent, depending on structure
            // If anchor is users.txt, base is its parent's parent directory relative to resource root
            // If anchor is the directory /Database/Database, base is its parent directory /Database
            // If anchor is just /Database, base is its parent (likely target/classes or JAR root)

            if (anchorFile.isFile()) {
                // Anchor is users.txt, base is parent of parent (/Database)
                runtimeBaseResourcePath = anchorFile.getParentFile().getParentFile().toPath();
            } else { // Anchor is directory like /Database/Database or /Database
                runtimeBaseResourcePath = anchorFile.getParentFile().toPath(); // Parent should be the root resource dir
            }

            // A simpler approach: Assume standard Maven structure (target/classes)
            if (runtimeBaseResourcePath == null) {
                URL classesRootUrl = ApplicationManager.class.getProtectionDomain().getCodeSource().getLocation();
                if (classesRootUrl != null) {
                    File classesRootFile = new File(classesRootUrl.toURI());
                    if (classesRootFile.isDirectory()) { // Running from IDE/exploded archive
                        runtimeBaseResourcePath = classesRootFile.toPath();
                    } else { // Running from JAR
                        runtimeBaseResourcePath = classesRootFile.getParentFile().toPath();
                    }
                    System.out.println("Using fallback base resource path: " + runtimeBaseResourcePath);
                } else {
                    throw new RuntimeException("Could not determine classpath root.");
                }
            }

            System.out.println("Resolved Base Resource Path: " + runtimeBaseResourcePath.toAbsolutePath());


            // Resolve specific paths relative to the base
            runtimeUsersFilePath = runtimeBaseResourcePath.resolve(DATABASE_DIR.substring(1)).resolve(USERS_FILE_NAME); // Remove leading '/'
            runtimeLessonsDirPath = runtimeBaseResourcePath.resolve(LESSONS_RESOURCE_DIR.substring(1));
            runtimeQuizzesDirPath = runtimeBaseResourcePath.resolve(QUIZZES_RESOURCE_DIR.substring(1));
            runtimeProgressDirPath = runtimeBaseResourcePath.resolve(PROGRESS_RESOURCE_DIR.substring(1)); // Added


            // Ensure directories exist at runtime (especially important when running from JAR)
            ensureDirectoryExists(runtimeLessonsDirPath);
            ensureDirectoryExists(runtimeQuizzesDirPath);
            ensureDirectoryExists(runtimeProgressDirPath); // Added


            // Verify users file exists (it should if anchor was found)
            if (!Files.exists(runtimeUsersFilePath)) {
                // Try creating a default/empty users file?
                System.err.println("Warning: Users file does not exist at resolved path: " + runtimeUsersFilePath.toAbsolutePath());
                try {
                    ensureDirectoryExists(runtimeUsersFilePath.getParent());
                    Files.createFile(runtimeUsersFilePath);
                    System.out.println("Created empty users file at: " + runtimeUsersFilePath.toAbsolutePath());
                } catch (IOException e) {
                    System.err.println("CRITICAL: Failed to create users file: " + e.getMessage());
                    runtimeUsersFilePath = null; // Mark as unusable
                }

            } else {
                System.out.println("Resolved Users File Path: " + runtimeUsersFilePath.toAbsolutePath());
            }

            System.out.println("Resolved Lessons Directory Path: " + runtimeLessonsDirPath.toAbsolutePath());
            System.out.println("Resolved Quizzes Directory Path: " + runtimeQuizzesDirPath.toAbsolutePath());
            System.out.println("Resolved Progress Directory Path: " + runtimeProgressDirPath.toAbsolutePath()); // Added


        } catch (URISyntaxException e) {
            System.err.println("CRITICAL: Error converting resource URL to URI: " + e);
            throw new RuntimeException("Failed to resolve resource paths (URI Syntax).", e);
        } catch (Exception e) {
            System.err.println("CRITICAL: Unexpected error resolving resource paths: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to resolve resource paths.", e);
        }
    }

    /** Ensures the given directory path exists, creating it if necessary. */
    private static void ensureDirectoryExists(Path directoryPath) {
        if (directoryPath != null && !Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println("Ensured directory exists: " + directoryPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating directory " + directoryPath.toAbsolutePath() + ": " + e.getMessage());
                // Depending on severity, could throw exception
            }
        }
    }

    /**
     * Loads user data from the resolved users file path.
     */
    private static void loadUsers() {
        // Clear existing maps before loading
        users.clear();
        students.clear();
        teachers.clear();

        if (runtimeUsersFilePath == null || !Files.exists(runtimeUsersFilePath)) {
            System.err.println("Cannot load users: File path not resolved or file does not exist.");
            return;
        }

        FileManager fm = new FileManager(runtimeUsersFilePath.toString());
        HashMap<String, ArrayList<String>> data = fm.loadGroupedUserData();

        if (data == null || data.isEmpty()) {
            System.out.println("No users data found or failed to load from " + runtimeUsersFilePath);
            return;
        }

        // Check for key presence
        if (!data.containsKey("username") || !data.containsKey("password") ||
                !data.containsKey("email") || !data.containsKey("role")) {
            System.err.println("Error: User data file is missing required keys (username, password, email, role). Cannot load users.");
            return;
        }

        List<String> usernames = data.get("username");
        List<String> emails = data.getOrDefault("email", new ArrayList<>());
        List<String> passwords = data.get("password");
        List<String> roles = data.get("role");
        List<String> languages = data.getOrDefault("language", new ArrayList<>()); // Language might be optional

        int numberOfUsers = usernames.size();
        // Validate consistency: all essential lists must have the same size
        if (emails.size() != numberOfUsers || passwords.size() != numberOfUsers || roles.size() != numberOfUsers) {
            System.err.println("Error: User data lists have inconsistent sizes. Aborting user load.");
            System.err.printf("Sizes: usernames=%d, emails=%d, passwords=%d, roles=%d%n",
                    numberOfUsers, emails.size(), passwords.size(), roles.size());
            return;
        }
        // Language list size check (optional, allow default)
        if (languages.size() != numberOfUsers && !languages.isEmpty()) {
            System.err.println("Warning: Language list size ("+ languages.size() +") differs from username count ("+ numberOfUsers +"). Using defaults where missing.");
        }


        System.out.println("Loading " + numberOfUsers + " user entries...");

        for (int i = 0; i < numberOfUsers; i++) {
            String username = usernames.get(i);
            String email = emails.get(i);
            String password = passwords.get(i); // Load plain text (INSECURE!)
            String role = roles.get(i);
            // Handle potentially missing language entry
            String language = (i < languages.size() && languages.get(i) != null && !languages.get(i).trim().isEmpty())
                    ? languages.get(i).trim()
                    : "English"; // Default language if missing or empty

            // Use username as the default ID
            String id = username;

            try {
                if (username == null || username.trim().isEmpty()) {
                    System.err.println("Warning: Skipping user entry at index " + i + " due to empty username.");
                    continue;
                }
                if (users.containsKey(username.toLowerCase())) { // Check if username already added (case-insensitive)
                    System.err.println("Warning: Duplicate username '" + username + "' found during loading. Skipping duplicate entry at index " + i + ".");
                    continue;
                }

                User newUser = null;
                if ("student".equalsIgnoreCase(role)) {
                    Student student = new Student(username, email, password, id, language);
                    students.put(username.toLowerCase(), student); // Use lowercase key for lookup
                    newUser = student;
                    System.out.println("Loaded Student: " + student.getUsername() + " ("+ language +")");
                } else if ("teacher".equalsIgnoreCase(role)) {
                    Teacher teacher = new Teacher(username, email, password, id, language);
                    teachers.put(username.toLowerCase(), teacher); // Use lowercase key
                    newUser = teacher;
                    System.out.println("Loaded Teacher: " + teacher.getUsername() + " ("+ language +")");
                } else {
                    System.err.println("Warning: Unknown role '" + role + "' for user '" + username + "'. Skipping user.");
                    continue; // Skip this user
                }

                if (newUser != null) {
                    users.put(username.toLowerCase(), newUser); // Add to the combined map (lowercase key)
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Error creating user object for username '" + username + "' at index " + i + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error processing user entry for username '" + username + "' at index " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Finished loading users. Total users loaded: " + users.size());
    }

    /**
     * Adds a new user to the runtime lists and triggers saving.
     * Should be called AFTER successful validation (e.g., username available).
     * @param user The User object to add.
     */
    public static synchronized void addUser(User user) { // Added synchronized for thread safety
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.err.println("Attempted to add null or invalid user.");
            return;
        }
        String usernameLower = user.getUsername().toLowerCase();

        // Double-check for duplicates in runtime map before adding
        if (users.containsKey(usernameLower)) {
            System.err.println("Attempted to add duplicate user (runtime check): " + user.getUsername());
            return;
        }

        // Add to the specific map and the general map
        if (user instanceof Student) {
            students.put(usernameLower, (Student) user);
            System.out.println("Added student to runtime map: " + user.getUsername());
        } else if (user instanceof Teacher) {
            teachers.put(usernameLower, (Teacher) user);
            System.out.println("Added teacher to runtime map: " + user.getUsername());
        } else {
            System.err.println("Warning: Adding user with unknown type: " + user.getUsername());
        }
        users.put(usernameLower, user);
        System.out.println("Added user to main runtime map: " + user.getUsername());

        // Persist the new user data (Append to file)
        if (!saveSpecificUser(user)) {
            System.err.println("CRITICAL: Failed to save new user " + user.getUsername() + " to persistent storage!");
            // Consider rolling back the runtime addition if saving fails critically
            // removeUser(user.getUsername()); // Requires a removeUser method
        }
    }

    /**
     * Saves a single user's data by appending it to the users file.
     * @param user The user to save.
     * @return true if saving was successful, false otherwise.
     */
    private static boolean saveSpecificUser(User user) {
        if (runtimeUsersFilePath == null) {
            System.err.println("Cannot save user: users file path not set.");
            return false;
        }
        FileManager fm = new FileManager(runtimeUsersFilePath.toString());
        HashMap<String, String> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("password", user.getPassword()); // INSECURE! Needs hashing.
        userData.put("email", user.getEmail());

        if (user instanceof Student) {
            userData.put("role", "student");
            userData.put("language", ((Student) user).getLanguage());
        } else if (user instanceof Teacher) {
            userData.put("role", "teacher");
            userData.put("language", ((Teacher) user).getLanguage());
        } else {
            userData.put("role", "unknown");
            userData.put("language", "");
        }

        return fm.saveDataAppend(userData); // Use append mode
    }

    // --- Getters for User Lists (Return Copies) ---

    public static List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    public static List<Teacher> getTeachers() {
        return new ArrayList<>(teachers.values());
    }

    public static List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Finds a user by username (case-insensitive).
     * @param username The username to search for.
     * @return The User object if found, otherwise null.
     */
    public static User findUserByUsername(String username) {
        if (username == null) return null;
        return users.get(username.toLowerCase()); // Use lowercase key for lookup
    }

    // --- Getters for Resolved Paths (Return String representation) ---

    public static String getUsersFilePath() {
        if (runtimeUsersFilePath == null) {
            System.err.println("Warning: Users file path accessed before resolution or resolution failed.");
            // Optionally attempt re-resolution or return null/throw exception
            // resolveResourcePaths(); // Be careful about re-calling this
            return null;
        }
        return runtimeUsersFilePath.toString();
    }

    public static String getLessonsDirectory() {
        if (runtimeLessonsDirPath == null) {
            System.err.println("Warning: Lessons directory path accessed before resolution or resolution failed.");
            return null;
        }
        return runtimeLessonsDirPath.toString();
    }

    public static String getQuizzesDirectory() {
        if (runtimeQuizzesDirPath == null) {
            System.err.println("Warning: Quizzes directory path accessed before resolution or resolution failed.");
            return null;
        }
        return runtimeQuizzesDirPath.toString();
    }

    public static String getProgressDirectory() {
        if (runtimeProgressDirPath == null) {
            System.err.println("Warning: Progress directory path accessed before resolution or resolution failed.");
            return null;
        }
        return runtimeProgressDirPath.toString();
    }

    // --- Progress Loading/Saving ---

    /** Loads progress for all students found in the user list. */
    private static void loadAllStudentProgress() {
        System.out.println("Loading progress data for all students...");
        for (Student student : students.values()) {
            student.loadProgressData(); // Call the load method on each student instance
        }
        System.out.println("Finished loading student progress.");
    }

    /** Saves progress for a specific student. */
    public static void saveStudentProgress(Student student) {
        if (student != null) {
            student.saveProgressData(); // Call the save method on the student instance
        }
    }

    // Method to load a specific lesson by its ID
    public static Lesson findLessonById(String lessonId) {
        if (lessonId == null || lessonId.isEmpty() || getLessonsDirectory() == null) {
            return null;
        }
        // Construct the expected file path
        String lessonFilePath = getLessonsDirectory() + File.separator + "lesson_" + lessonId + ".txt";
        File lessonFile = new File(lessonFilePath);

        if (lessonFile.exists()) {
            try {
                Lesson lesson = new Lesson(lessonFilePath); // Load using the file path constructor
                if (!lesson.getLessonId().equals("L_DEFAULT")) { // Check if loading succeeded
                    return lesson;
                } else {
                    System.err.println("Failed to properly load lesson with ID " + lessonId + " from " + lessonFilePath);
                }
            } catch (Exception e) {
                System.err.println("Error loading lesson ID " + lessonId + " from " + lessonFilePath + ": " + e.getMessage());
            }
        } else {
            System.err.println("Lesson file not found for ID " + lessonId + " at " + lessonFilePath);
        }
        return null;
    }

    // Method to load a specific quiz by its ID
    public static Quiz findQuizById(String quizId) {
        if (quizId == null || quizId.isEmpty() || getQuizzesDirectory() == null) {
            return null;
        }
        // Construct the expected file path
        String quizFilePath = getQuizzesDirectory() + File.separator + "quiz_" + quizId + ".txt";
        File quizFile = new File(quizFilePath);

        if (quizFile.exists()) {
            try {
                Quiz quiz = new Quiz(quizFilePath); // Load using the file path constructor
                if (!quiz.getQuizId().equals("Q_DEFAULT")) { // Check if loading succeeded
                    return quiz;
                } else {
                    System.err.println("Failed to properly load quiz with ID " + quizId + " from " + quizFilePath);
                }
            } catch (Exception e) {
                System.err.println("Error loading quiz ID " + quizId + " from " + quizFilePath + ": " + e.getMessage());
            }
        } else {
            System.err.println("Quiz file not found for ID " + quizId + " at " + quizFilePath);
        }
        return null;
    }
}