package Main;

import fileManager.FileManager;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationManager {

    private static ArrayList<Student> students=new ArrayList<>();
    private static ArrayList<Teacher> teachers=new ArrayList<>();
    private static ArrayList<User> users=new ArrayList<>();
    public ApplicationManager() {

        loadUsers();
    }

    private void loadUsers() {
        FileManager fm = new FileManager("src/main/resources/Database");
        HashMap<String, ArrayList<String>> data = fm.loadStudentData();
        if (data == null) {
            System.out.println("No users data loaded.");
            return;
        }

        ArrayList<String> usernames = data.getOrDefault("username", new ArrayList<>());
        ArrayList<String> emails = data.getOrDefault("email", new ArrayList<>());
        ArrayList<String> passwords = data.getOrDefault("password", new ArrayList<>());
        ArrayList<String> roles = data.getOrDefault("role", new ArrayList<>());
        ArrayList<String> languages = data.getOrDefault("language", new ArrayList<>());

//        int maxEntries = Math.max(usernames.size(), Math.max(emails.size(), passwords.size()));
        for (int i = 0; i < usernames.size(); i++) {
//            if (i >= usernames.size()) {
//                continue;
//            }


            String username = usernames.get(i);
            String email = i < emails.size() ? emails.get(i) : "default@example.com";
            String password = i < passwords.size() ? passwords.get(i) : "default";
            String role = i < roles.size() ? roles.get(i) : "";
            String language = i < languages.size() ? languages.get(i) : "";
            String id = username;

            if (role.equalsIgnoreCase("student")) {
                Student student = new Student(username, email, password, id, language);
                students.add(student);
                users.add(student);
                System.out.println(student.toString());

            }
            else {
                Teacher teacher = new Teacher(username, email, password, id, language);
                teachers.add(teacher);
                users.add(teacher);
                System.out.println(teacher.toString());


            }
        }
    }

    public static void addStudent(Student student) {
        students.add(student);
        System.out.println("Added student: " + student.toString());
        users.add(student);
        System.out.println("Added user: " + student.toString());
    }

    public static void addTeachers(Teacher teacher) {
        teachers.add(teacher);
        System.out.println("Added teacher: " + teacher.toString());
        users.add(teacher);
        System.out.println("Added user: " + teacher.toString());
    }



    public static ArrayList<Student> getStudents() {
        return students;
    }


    public static ArrayList<User> getUsers() {
        return users;
    }
}