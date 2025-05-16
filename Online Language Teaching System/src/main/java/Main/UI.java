package Main;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UI {
    private static ArrayList<Student> allStudents = new ArrayList<>();
    private static ArrayList<Teacher> allTeachers = new ArrayList<>();
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static HashMap<String, Language> languages = new HashMap<>();
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);

    private static void initializeLanguagesAndContent() {
        // Initialize languages and content
        //for testing
        English english = new English();
        Lesson engLesson1 = new Lesson("English Introduction", "ENG101_ID_IGNORED", "Content_IGNORED_English_Intro", "beginner");
        engLesson1.setQuiz("english_quiz1.txt");
        Lesson engLesson2 = new Lesson("English Grammar Basics", "ENG102_ID_IGNORED", "Content_IGNORED_English_Grammar", "beginner");
        english.addLesson(engLesson1);
        english.addLesson(engLesson2);
        languages.put("English", english);

        German german = new German();
        Lesson gerLesson1 = new Lesson("German Greetings", "GER101_ID_IGNORED", "Content_IGNORED_German_Greetings", "beginner");
        gerLesson1.setQuiz("german_quiz1.txt");
        Lesson gerLesson2 = new Lesson("German Numbers", "GER102_ID_IGNORED", "Content_IGNORED_German_Numbers", "beginner");
        german.addLesson(gerLesson1);
        german.addLesson(gerLesson2);
        languages.put("German", german);

        French french = new French();
        Lesson frLesson1 = new Lesson("French Pronunciation", "FRE101_ID_IGNORED", "Content_IGNORED_French_Pronunciation", "beginner");
        Lesson frLesson2 = new Lesson("Basic French Phrases", "FRE102_ID_IGNORED", "Content_IGNORED_French_Phrases", "beginner");
        french.addLesson(frLesson1);
        french.addLesson(frLesson2);
        languages.put("French", french);

        Student testStudent = new Student("Test Student", "student@test.com", "pass", "S001", "English");
        Language studentLang = languages.get(testStudent.getLanguage());
        if (studentLang != null) {
            for (Lesson l : studentLang.getLessons()) {
                testStudent.addLesson(l);
            }
        }
        allStudents.add(testStudent);
        allUsers.add(testStudent);

        Teacher testTeacher = new Teacher("Test Teacher", "teacher@test.com", "pass", "T001", "German");
        allTeachers.add(testTeacher);
        allUsers.add(testTeacher);
        German.addTeachers(testTeacher);
    }

    public static void main(String[] args) {
        // Start JavaFX application and run terminal UI
        ApplicationController.launch(ApplicationController.class, args);
        initializeLanguagesAndContent();
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else if (currentUser instanceof Student) {
                showStudentDashboard((Student) currentUser);
            } else if (currentUser instanceof Teacher) {
                showTeacherDashboard((Teacher) currentUser);
            }
        }
    }

    private static void showMainMenu() {
        // Display main menu
        System.out.println("\n--- Language Learning Platform ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = getIntInput();
        scanner.nextLine();
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleSignUp();
                break;
            case 3:
                System.out.println("Exiting application...");
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleLogin() {
        // Handle user login
        System.out.println("\n--- Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        for (Student s : allStudents) {
            if (s.getEmail().equals(email) && s.getPassword().equals(password)) {
                currentUser = s;
                System.out.println("Login successful. Welcome " + s.getUsername() + "!");
                return;
            }
        }
        for (Teacher t : allTeachers) {
            if (t.getEmail().equals(email) && t.getPassword().equals(password)) {
                currentUser = t;
                System.out.println("Login successful. Welcome " + t.getUsername() + "!");
                return;
            }
        }
        System.out.println("Invalid email or password.");
    }

    private static boolean isEmailTaken(String email) {
        // Check if email is already used
        for (Student s : allStudents) if (s.getEmail().equals(email)) return true;
        for (Teacher t : allTeachers) if (t.getEmail().equals(email)) return true;
        return false;
    }

    private static boolean isIdTaken(String id) {
        // Check if ID is already used
        for (Student s : allStudents) if (s.getID().equals(id)) return true;
        for (Teacher t : allTeachers) if (t.getID().equals(id)) return true;
        return false;
    }

    private static void handleSignUp() {
        // Handle user signup
        System.out.println("\n--- Sign Up ---");
        System.out.print("Are you a (1) Student or (2) Teacher? ");
        int type = getIntInput();
        scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        if (isEmailTaken(email)) {
            System.out.println("This email is already registered. Please try logging in or use a different email.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter ID (must be unique): ");
        String id = scanner.nextLine();
        if (isIdTaken(id)) {
            System.out.println("This ID is already taken. Please choose a different ID.");
            return;
        }
        System.out.println("Available languages:");
        int langIndex = 1;
        ArrayList<String> langNames = new ArrayList<>(languages.keySet());
        for (String langName : langNames) {
            System.out.println(langIndex++ + ". " + langName);
        }
        System.out.print("Choose a language (number): ");
        int langChoice = getIntInput();
        scanner.nextLine();
        String chosenLanguageName = "";
        if (langChoice > 0 && langChoice <= langNames.size()) {
            chosenLanguageName = langNames.get(langChoice - 1);
        } else {
            System.out.println("Invalid language choice. Defaulting to English (if available).");
            chosenLanguageName = languages.containsKey("English") ? "English" : (langNames.isEmpty() ? "" : langNames.get(0));
            if (chosenLanguageName.isEmpty()) {
                System.out.println("Error: No languages available in the system. Cannot complete signup.");
                return;
            }
        }
        if (type == 1) {
            Student newStudent = new Student(name, email, password, id, chosenLanguageName);
            Language studentLang = languages.get(chosenLanguageName);
            if (studentLang != null) {
                for (Lesson lesson : studentLang.getLessons()) {
                    newStudent.addLesson(lesson);
                }
            }
            allStudents.add(newStudent);
            allUsers.add(newStudent);
            if (chosenLanguageName.equals("German")) German.addStudents(newStudent);
            System.out.println("Student registration successful! Please login.");
        } else if (type == 2) {
            Teacher newTeacher = new Teacher(name, email, password, id, chosenLanguageName);
            allTeachers.add(newTeacher);
            allUsers.add(newTeacher);
            if (chosenLanguageName.equals("German")) German.addTeachers(newTeacher);
            System.out.println("Teacher registration successful! Please login.");
        } else {
            System.out.println("Invalid user type choice.");
        }
    }

    private static void showStudentDashboard(Student student) {
        // Display student dashboard
//        System.out.println("\n--- Student Dashboard: " + student.getUsername() + " ---");
//        System.out.println("Email: " + student.getEmail());
//        System.out.println("ID: " + student.getID());
//        System.out.println("Language: " + student.getLanguage());
//        System.out.println("Proficiency: " + student.getProficiencyLevel());
//
        System.out.println("\n"+student.toString()+"\n");

        int totalLessonsInList = student.getLessons().size();
        double completedLessonsCount = student.getProgress();
        double progressPercentage = (totalLessonsInList > 0) ? (completedLessonsCount / totalLessonsInList) * 100 : 0;
        System.out.printf("Progress: %.0f / %d lessons completed (%.2f%%)\n", completedLessonsCount, totalLessonsInList, progressPercentage);
        System.out.println("\nOptions:");
        System.out.println("1. View/Attend Lessons");
        System.out.println("2. View My Detailed Progress");
        System.out.println("3. Logout");
        System.out.print("Choose an option: ");
        int choice = getIntInput();
        scanner.nextLine();
        switch (choice) {
            case 1:
                handleViewAttendLessons(student);
                break;
            case 2:
                viewStudentDetailedProgress(student);
                break;
            case 3:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void handleViewAttendLessons(Student student) {
        // Handle viewing and attending lessons
        System.out.println("\n--- Lessons for " + student.getLanguage() + " ---");
        Language langObj = languages.get(student.getLanguage());
        if (student.getLessons().isEmpty() && langObj != null && !langObj.getLessons().isEmpty()) {
            System.out.println("Your lesson list was empty, repopulating...");
            for (Lesson l : langObj.getLessons()) {
                student.addLesson(l);
            }
        }
        ArrayList<Lesson> studentLessons = student.getLessons();
        if (studentLessons.isEmpty()) {
            System.out.println("No lessons currently available or assigned for your language: " + student.getLanguage());
            return;
        }
        for (int i = 0; i < studentLessons.size(); i++) {
            Lesson lesson = studentLessons.get(i);
            System.out.printf("%d. %s (ID: %s) - %s\n",
                    i + 1,
                    lesson.getTitle(),
                    lesson.getLessonId(),
                    student.isLessonCompleted(i) ? "Completed" : "Not Started");
        }
        System.out.print("Enter lesson number to attend (or 0 to go back): ");
        int lessonChoice = getIntInput();
        scanner.nextLine();
        if (lessonChoice > 0 && lessonChoice <= studentLessons.size()) {
            int lessonIndexInStudentList = lessonChoice - 1;
            if (student.isLessonCompleted(lessonIndexInStudentList)) {
                System.out.println("You have already completed this lesson. Do you want to review it? (y/n)");
                String reviewChoice = scanner.nextLine().trim().toLowerCase();
                if (!reviewChoice.equals("y")) {
                    return;
                }
            }
            attendLesson(student, lessonIndexInStudentList);
        } else if (lessonChoice != 0) {
            System.out.println("Invalid lesson number.");
        }
    }

    private static void attendLesson(Student student, int lessonIndex) {
        // Attend a lesson
        Lesson lesson = student.getLessons().get(lessonIndex);
        System.out.println("\n--- Attending Lesson: " + lesson.getTitle() + " ---");
        System.out.println("Lesson ID: " + lesson.getLessonId());
        System.out.println("Content: " + lesson.getContent());
        System.out.println("\nOptions:");
        int optionNum = 1;
        boolean hasQuiz = lesson.getQuiz() != null && lesson.getQuiz().getQuestions() != null && !lesson.getQuiz().getQuestions().isEmpty();
        if (hasQuiz) {
            System.out.println(optionNum++ + ". Take Quiz");
        }
        if (!student.isLessonCompleted(lessonIndex)) {
            System.out.println(optionNum++ + ". Mark as Complete");
        } else {
            System.out.println(optionNum++ + ". (Lesson Already Completed)");
        }
        System.out.println(optionNum++ + ". Back to Dashboard");
        System.out.print("Choose an option: ");
        int choice = getIntInput();
        scanner.nextLine();
        optionNum = 1;
        if (hasQuiz) {
            if (choice == optionNum++) {
                takeQuiz(student, lesson, lessonIndex);
                return;
            }
        }
        if (!student.isLessonCompleted(lessonIndex) && choice == optionNum++) {
            student.makeLessonCompleted(lessonIndex);
            System.out.println("Lesson '" + lesson.getTitle() + "' marked as complete.");
        } else if (student.isLessonCompleted(lessonIndex) && choice == optionNum++) {
            System.out.println("Lesson was already marked complete.");
        } else if (choice == optionNum++) {
            System.out.println("Returning to dashboard.");
        } else {
            System.out.println("Invalid option or action.");
        }
    }

    private static void takeQuiz(Student student, Lesson lesson, int lessonIndex) {
        // Take a quiz for a lesson
        Quiz quiz = lesson.getQuiz();
        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            System.out.println("No quiz available for this lesson, or quiz data is missing/empty.");
            if (!student.isLessonCompleted(lessonIndex)) {
                System.out.println("Do you want to mark the lesson as complete anyway? (y/n)");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    student.makeLessonCompleted(lessonIndex);
                    System.out.println("Lesson '" + lesson.getTitle() + "' marked as complete.");
                }
            }
            return;
        }
        System.out.println("\n--- Quiz: " + quiz.getTitle() + " ---");
        ArrayList<Question> questions = quiz.getQuestions();
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q == null) {
                System.out.println("Warning: Found a null question at index " + i + ", skipping.");
                continue;
            }
            System.out.println("\nQuestion " + (i + 1) + ": " + q.getQuestion());
            String[] options = q.getOptions();
            if (options == null || options.length == 0) {
                System.out.println("Warning: Question has no options, skipping.");
                continue;
            }
            for (int j = 0; j < options.length; j++) {
                System.out.println((j + 1) + ". " + options[j]);
            }
            System.out.print("Your answer (number): ");
            int ansChoice = getIntInput();
            scanner.nextLine();
            if (ansChoice > 0 && ansChoice <= options.length) {
                q.setUserAnswer(options[ansChoice - 1]);
                if (q.isCorrect()) {
                    score++;
                }
            } else {
                System.out.println("Invalid option. Question marked incorrect.");
                q.setUserAnswer(null);
            }
        }
        System.out.println("\n--- Quiz Results ---");
        System.out.println("You scored " + score + " out of " + questions.size() + ".");
        double percentage = questions.size() > 0 ? (double) score / questions.size() * 100 : 0;
        System.out.printf("Percentage: %.2f%%\n", percentage);
        if (percentage >= 50.0 && !student.isLessonCompleted(lessonIndex)) {
            System.out.println("Congratulations, you passed the quiz!");
            student.makeLessonCompleted(lessonIndex);
            System.out.println("Lesson '" + lesson.getTitle() + "' marked as complete.");
        } else if (percentage < 50.0) {
            System.out.println("You did not pass the quiz. Please try again later.");
            if (!student.isLessonCompleted(lessonIndex)) {
                System.out.println("Lesson '" + lesson.getTitle() + "' is not marked as complete yet.");
            }
        } else {
            System.out.println("Quiz completed. Lesson status remains unchanged as it was already complete or no score threshold met for change.");
        }
    }

    private static void viewStudentDetailedProgress(Student student) {
        // Display detailed student progress
        System.out.println("\n--- Detailed Progress for " + student.getUsername() + " ---");
        ArrayList<Lesson> studentLessons = student.getLessons();
        if (studentLessons.isEmpty()) {
            System.out.println("No lessons assigned to you yet.");
            return;
        }
        System.out.println("Status of your lessons:");
        int actualCompletedCount = 0;
        for (int i = 0; i < studentLessons.size(); i++) {
            Lesson lesson = studentLessons.get(i);
            boolean isCompleted = student.isLessonCompleted(i);
            System.out.printf("- %s (ID: %s): %s\n",
                    lesson.getTitle(),
                    lesson.getLessonId(),
                    isCompleted ? "Completed" : "Not Completed");
            if (isCompleted) {
                actualCompletedCount++;
            }
        }
        if (student.getProgress() != actualCompletedCount) {
            System.out.println("Progress sync issue detected. Recalculating...");
            student.setProgress(0);
            for (int i = 0; i < student.getLessons().size(); i++) {
                if (student.isLessonCompleted(i)) {
                    student.updateProgress();
                }
            }
            actualCompletedCount = (int) student.getProgress();
        }
        double progressPercentage = (studentLessons.size() > 0) ? ((double) actualCompletedCount / studentLessons.size()) * 100 : 0;
        System.out.printf("\nOverall: %d / %d lessons completed (%.2f%%).\n",
                actualCompletedCount, studentLessons.size(), progressPercentage);
        System.out.println("Current Proficiency Level: " + student.getProficiencyLevel());
    }

    private static void showTeacherDashboard(Teacher teacher) {
        // Display teacher dashboard
        System.out.println("\n--- Teacher Dashboard: " + teacher.getUsername() + " ---");
        System.out.println("Email: " + teacher.getEmail());
        System.out.println("ID: " + teacher.getID());
        System.out.println("Teaches: " + teacher.getLanguage());
        System.out.println("Number of Lessons Created (this session via UI): " + teacher.getNumberOfCreatedLessons());
        System.out.println("Balance: $" + teacher.getBalance());

        System.out.println("\n"+teacher.toString()+"\n");

        System.out.println("\nOptions:");
        System.out.println("1. Create a New Lesson");
        System.out.println("2. View Lessons for My Language");
        System.out.println("3. Logout");
        System.out.print("Choose an option: ");



        int choice = getIntInput();
        scanner.nextLine();
        switch (choice) {
            case 1:
                handleTeacherCreateLesson(teacher);
                break;
            case 2:
                viewTeacherLanguageLessons(teacher);
                break;
            case 3:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void handleTeacherCreateLesson(Teacher teacher) {
        // Create a new lesson
        System.out.println("\n--- Create New Lesson for " + teacher.getLanguage() + " ---");
        System.out.print("Enter lesson title: ");
        String title = scanner.nextLine();
        System.out.print("Enter lesson ID (e.g., " + teacher.getLanguage().substring(0, 3).toUpperCase() + "201 - will be overridden by title in current Lesson class): ");
        String lessonIdInput = scanner.nextLine();
        System.out.print("Enter lesson content (will be overridden by title in current Lesson class): ");
        String contentInput = scanner.nextLine();
        System.out.print("Enter proficiency level (beginner, intermediate, advanced): ");
        String level = scanner.nextLine();
        Language lang = languages.get(teacher.getLanguage());
        if (lang != null) {
            for (Lesson l : lang.getLessons()) {
                if (l.getLessonId().equals(title)) {
                    System.out.println("Error: A lesson with title (acting as ID) '" + title + "' already exists for " + teacher.getLanguage() + ".");
                    return;
                }
            }
        }
        Lesson newLesson = new Lesson(title, lessonIdInput, contentInput, level);
        System.out.print("Does this lesson have a quiz? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter path to quiz data file (e.g., quizzes/" + newLesson.getLessonId() + "_quiz.txt): ");
            String quizPath = scanner.nextLine();
            newLesson.setQuiz(quizPath);
            if (newLesson.getQuiz() != null && "Default Title".equals(newLesson.getQuiz().getTitle()) && !quizPath.isEmpty()) {
                System.out.println("Warning: Quiz loaded with default data. Check path or file content: " + quizPath);
            } else if (newLesson.getQuiz() != null) {
                System.out.println("Quiz '" + newLesson.getQuiz().getTitle() + "' associated with the lesson.");
            }
        }
        if (lang != null) {
            lang.addLesson(newLesson);
            teacher.addLesson(newLesson);
            System.out.println("Lesson '" + newLesson.getTitle() + "' (ID: " + newLesson.getLessonId() + ") created successfully for " + teacher.getLanguage() + ".");
            for (Student s : allStudents) {
                if (s.getLanguage().equals(teacher.getLanguage())) {
                    boolean alreadyHas = false;
                    for (Lesson studentLesson : s.getLessons()) {
                        if (studentLesson.getLessonId().equals(newLesson.getLessonId())) {
                            alreadyHas = true;
                            break;
                        }
                    }
                    if (!alreadyHas) {
                        s.addLesson(newLesson);
                    }
                }
            }
            System.out.println("Lesson also made available to current students of " + teacher.getLanguage() + ".");
        } else {
            System.out.println("Error: Language '" + teacher.getLanguage() + "' not found. Lesson not saved globally.");
        }
    }

    private static void viewTeacherLanguageLessons(Teacher teacher) {
        // View lessons for teacher's language
        System.out.println("\n--- All Lessons for " + teacher.getLanguage() + " ---");
        Language lang = languages.get(teacher.getLanguage());
        if (lang == null || lang.getLessons().isEmpty()) {
            System.out.println("No lessons found for " + teacher.getLanguage() + " in the system.");
            return;
        }
        ArrayList<Lesson> lessonsInLanguage = lang.getLessons();
        for (int i = 0; i < lessonsInLanguage.size(); i++) {
            Lesson lesson = lessonsInLanguage.get(i);
            System.out.printf("%d. %s (ID: %s)\n",
                    i + 1,
                    lesson.getTitle(),
                    lesson.getLessonId());
        }
    }

    private static int getIntInput() {
        // Get valid integer input
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }
}