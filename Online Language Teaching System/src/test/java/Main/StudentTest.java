package Main;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    Student student;
    @BeforeAll
    public static void setupAll() {
        System.out.println("Student class tests");
    }


    @BeforeEach
    void setUp()
    {
        student=  new Student("Amr","amr@gmail.com","123456","s01234","English");


    }


    @DisplayName("Repeat check  Test 5 Times")
    @RepeatedTest(value = 5,
            name = "Repeating username Test {currentRepetition} of {totalRepetitions}")
    public void testGetUserName() {

        assertEquals("Amr", student.getUsername());
    }
    @DisplayName("Repeat check  Test 5 Times")
    @RepeatedTest(value = 5,
            name = "Repeating testGetPassword Test {currentRepetition} of {totalRepetitions}")
    public void testGetPassword() {

        assertEquals("123456", student.getPassword());
    }
    @DisplayName("Repeat check  Test 5 Times")
    @RepeatedTest(value = 5,
            name = "Repeating testEmail Test {currentRepetition} of {totalRepetitions}")
    public void testEmail() {

        assertEquals("amr@gmail.com", student.getEmail());
    }



}