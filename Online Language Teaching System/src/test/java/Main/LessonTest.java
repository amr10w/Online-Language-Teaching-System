package Main;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

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

    //@Test
    //@DisplayName("get right title")
    /*public void testGetTitle()
    {
        lesson.setLesson("hello world","lesson001","how to print hello world");
        assertEquals("hello world",lesson.getTitle());
    }*/



}