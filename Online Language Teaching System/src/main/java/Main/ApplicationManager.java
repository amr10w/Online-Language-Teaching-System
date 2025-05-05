package Main;

import fileManager.FileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationManager {

    // Use constants for file paths/directories
    private static final String DATABASE_DIR = "src/main/resources/Database"; // Or adjust as needed
    private static final String USERS_FILE = DATABASE_DIR + "/Database/users.txt";
    public static final String LESSONS_DIR = "src/main/resources/lessons";
    public static final String QUIZZES_DIR = "src/main/resources/quizzes";

    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<User> users = new ArrayList<>();

    public ApplicationManager() {
        loadUsers();
    }

    private void loadUsers() {
        FileManager fm = new FileManager(USERS_FILE); // Use specific file path
        HashMap<String, ArrayList<String>> data = fm.loadGroupedUserData(); // Use the correct method
        if (data == null || data.isEmpty()) {
            System.out.println("No users data found or failed to load from " + USERS_FILE);
            return;
        }

        // Extract data, providing defaults if a key is missing entirely
        List<String> usernames = data.getOrDefault("username", new ArrayList<>());
        List<String> emails = data.getOrDefault("email", new ArrayList<>());
        List<String> passwords = data.getOrDefault("password", new ArrayList<>());
        List<String> roles = data.getOrDefault("role", new ArrayList<>());
        List<String> languages = data.getOrDefault("language", new ArrayList<>());

        // Determine the minimum size to avoid IndexOutOfBounds
        int numberOfUsers = usernames.size();
        if (numberOfUsers == 0) {
            System.out.println("User data loaded but username list is empty.");
            return;
        }
        if (emails.size() != numberOfUsers || passwords.size() != numberOfUsers ||
                roles.size() != numberOfUsers || languages.size() != numberOfUsers) {
            System.err.println("Warning: Data inconsistency in " + USERS_FILE + ". Lists have different sizes.");
            // Optionally, you could stop loading or try to load only consistent entries
            // For now, we proceed but might encounter issues or use defaults.
        }


        System.out.println("Loading " + numberOfUsers + " user entries...");

        for (int i = 0; i < numberOfUsers; i++) {
            // Safely get data for each user, using defaults if lists are shorter than usernames list
            String username = usernames.get(i);
            String email = (i < emails.size()) ? emails.get(i) : "default@example.com";
            String password = (i < passwords.size()) ? passwords.get(i) : "defaultPassword";
            String role = (i < roles.size()) ? roles.get(i) : "student"; // Default to student
            String language = (i < languages.size()) ? languages.get(i) : ""; // Default to empty language
            String id = username; // Using username as ID

            try {
                if ("student".equalsIgnoreCase(role)) {
                    Student student = new Student(username, email, password, id, language);
                    students.add(student);
                    users.add(student);
                    System.out.println("Loaded Student: " + student);
                } else if ("teacher".equalsIgnoreCase(role)) {
                    Teacher teacher = new Teacher(username, email, password, id, language);
                    teachers.add(teacher);
                    users.add(teacher);
                    System.out.println("Loaded Teacher: " + teacher);
                } else {
                    System.err.println("Warning: Unknown role '" + role + "' for user '" + username + "'. Skipping.");
                }
            } catch (Exception e) {
                System.err.println("Error creating user object for username '" + username + "': " + e.getMessage());
            }
        }
        System.out.println("Finished loading users. Total users: " + users.size());
    }

    public static void addUser(User user) {
        if (user instanceof Student) {
            if (!students.contains(user)) {
                students.add((Student) user);
                System.out.println("Added student to runtime list: " + user.getUsername());
            }
        } else if (user instanceof Teacher) {
            if (!teachers.contains(user)) {
                teachers.add((Teacher) user);
                System.out.println("Added teacher to runtime list: " + user.getUsername());
            }
        }
        if (!users.contains(user)) {
            users.add(user);
            System.out.println("Added user to main runtime list: " + user.getUsername());
        }
    }

    // Consider removing addStudent/addTeachers if addUser handles both
    // public static void addStudent(Student student) {
    //     if (!students.contains(student)) {
    //         students.add(student);
    //         System.out.println("Added student: " + student);
    //     }
    //     if (!users.contains(student)) {
    //         users.add(student);
    //     }
    // }

    // public static void addTeachers(Teacher teacher) {
    //     if (!teachers.contains(teacher)) {
    //         teachers.add(teacher);
    //         System.out.println("Added teacher: " + teacher);
    //     }
    //      if (!users.contains(teacher)) {
    //         users.add(teacher);
    //     }
    // }

    public static List<Student> getStudents() {
        return new ArrayList<>(students); // Return a copy
    }

    public static List<Teacher> getTeachers() {
        return new ArrayList<>(teachers); // Return a copy
    }

    public static List<User> getUsers() {
        return new ArrayList<>(users); // Return a copy
    }

    // Helper method to find a user by username
    public static User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null; // Not found
    }

    // Method to get the path for saving/loading user data
    public static String getUsersFilePath() {
        return USERS_FILE;
    }

    // Method to get the directory for lessons
    public static String getLessonsDirectory() {
        return LESSONS_DIR;
    }

    // Method to get the directory for quizzes
    public static String getQuizzesDirectory() {
        return QUIZZES_DIR;
    }
}