package Main;

import java.util.ArrayList;

public class German extends Language {
    private static ArrayList<Student> students=new ArrayList<>();
    private static ArrayList<Teacher> teachers=new ArrayList<>();

    public static void addStudents(Student student)
    {
        students.add(student);
    }

    public static void addTeachers(Teacher teacher)
    {
        teachers.add(teacher);
    }
    @Override
    public String getLanguageName() {
        return "German";
    }
}