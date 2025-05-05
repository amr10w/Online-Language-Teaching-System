package Main;

// Removed static lists, these should be instance members in Language
// or managed by a central service. The Language class already has instance lists.
public class German extends Language {
    // private static ArrayList<Student> students=new ArrayList<>(); // Remove static lists here
    // private static ArrayList<Teacher> teachers=new ArrayList<>(); // Remove static lists here

    // // These methods don't belong here, user management is in ApplicationManager
    // public static void addStudents(Student student) { students.add(student); }
    // public static void addTeachers(Teacher teacher) { teachers.add(teacher); }

    @Override
    public String getLanguageName() {
        return "German";
    }
}