package Main;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LessonTest {

    Lesson lesson;

    @BeforeAll
    public static void setupAll() {
        System.out.println("Lessons tests");
    }


    @BeforeEach
    void setUp()
    {
        lesson=  new Lesson("title","lessonId","content");
    }

    @Test
    @DisplayName("get right title")
    public void testGetTitle()
    {
        lesson.setLesson("hello world","lesson001","how to print hello world");
        assertEquals("hello world",lesson.getTitle());
    }

    @DisplayName("Repeat check Test 5 Times")
    @RepeatedTest(value = 5,
            name = "Repeating LessonID Test {currentRepetition} of {totalRepetitions}")
    public void testGetLessonId() {
        lesson.setLesson("hello world","lesson001","how to print hello world");
        assertEquals("lesson001", lesson.getLessonId());
    }


    @DisplayName("Lesson ID should accept phone number format")
    @ParameterizedTest
    @ValueSource(strings = {"0123456789", "0123456798", "0123456897"})
    public void shouldAcceptLessonsId(String phoneNumber) {
        lesson.setLesson("Title", phoneNumber, "Some content");
        assertEquals(phoneNumber, lesson.getLessonId());
    }



}