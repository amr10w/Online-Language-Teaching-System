package Main;

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