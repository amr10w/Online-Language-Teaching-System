# Online Language Teaching System

The Online Language Teaching System, named **LinguaLearn**, is a Java and JavaFX-based application designed to facilitate language learning through an interactive and user-friendly platform. This README provides an overview of the project, highlighting the object-oriented programming (OOP) concepts, file management, graphical user interface (GUI), user interface (UI), and unit testing implemented in the system.

##  Table of Contents


1. [Introduction](#introduction)  
2. [Part 1: OOP Concepts and File Management](#part-1-oop-concepts-and-file-management)  
   - [OOP Concepts](#oop-concepts)  
   - [File Management](#file-management)  
3. [Part 2: GUI](#part-2-gui)  
   - [Graphical User Interface (GUI)](#graphical-user-interface-gui)  
4. [Part 3: UI](#part-3-ui)  
   - [User Interface (UI)](#user-interface-ui)  
5. [Part 4: JUnit Testing for Student and Lesson Classes](#part-4-junit-testing-for-student-and-lesson-classes)  
   - [Unit Testing](#unit-testing)  
6. [How to Run](#how-to-run)  
   - [Getting Started](#getting-started)  
7. [Video Demo Section](#video-demo-section)  
   - [Demo Overview](#demo-overview)  
8. [Team Members](#team-members)  
9. [Conclusion](#conclusion)



---

## Introduction
LinguaLearn is a comprehensive language learning platform developed to offer an engaging and effective learning experience. Built using Java and JavaFX, it incorporates modern OOP principles, a robust GUI, an intuitive UI, and rigorous testing to ensure quality and usability. This project aims to support learners in mastering various languages through interactive lessons, quizzes, and progress tracking.

---

## Part 1: OOP Concepts and File Management

### OOP Concepts
LinguaLearn leverages core OOP principles to ensure a modular, maintainable, and scalable codebase:

- **Encapsulation**: Data is protected by restricting direct access to object attributes. For example, the `Student` class encapsulates attributes like `username`, `password`, and `progress`, accessible only through public getter and setter methods, ensuring data integrity.
- **Inheritance**: The system uses inheritance to create specialized classes. The `Student` and `Teacher` classes extend the abstract `User` class, inheriting common attributes like `username` and `id` while adding specific functionalities.
- **Polymorphism**: Methods are overridden in derived classes to provide flexibility. For instance, lesson presentation varies based on user type (e.g., `Student` or `Teacher`), allowing customized behavior.
- **Abstraction**: Abstract classes like `User` define a blueprint for shared functionality, reducing code duplication and enhancing maintainability.

### File Management

The system manages data using local `.txt` files instead of a database due to limited knowledge about database systems at this stage. Despite this limitation, a creative approach was used to simulate persistent storage and allow interaction between sessions:

- **Lesson and Quiz Import**: Lessons and quizzes can be added to the system through structured `.txt` files, making content easy to modify or expand.
- **User Credentials Storage**: Usernames and passwords are saved in a local `.txt` file. When the application is closed and reopened, users can log in again using the saved credentials.
- **Persistence Without Database**: Although it lacks the security and scalability of a real database, this method demonstrates an inventive way to implement file-based persistence using only core Java features.

**Disadvantage:** This is not a secure method of storing credentials or data and is not recommended for real-world applications. It was implemented as a learning exercise and proof of concept.

---

## Part 2: GUI

### Graphical User Interface (GUI)
LinguaLearn's GUI, built using JavaFX, provides a visually appealing and interactive environment for language learning. Key features include:

- **Customizable Learning Plans**: Users can create personalized lessons, set goals, and track progress through intuitive interfaces.
- **Interactive Tools**: The GUI offers vocabulary flashcards, grammar exercises, and pronunciation guides to enhance learning.
- **Dynamic Navigation**: The `ApplicationController` class manages different dashboards (`showStudentDashboard` and `showTeacherDashboard`) based on user roles, ensuring a tailored experience.
- **Styling**: Contributions from team members like Anas Ayman Mohammed and Ahmed Ezzat ensure consistent and user-friendly GUI styling, with features like scene management and exception handling for robustness.


---

## Part 3: UI

### User Interface (UI)
The UI of LinguaLearn is designed to be user-friendly, focusing on accessibility and engagement. Key aspects include:

- **Interactive Elements**: The UI includes screens, buttons, icons, and text layouts that allow users to navigate lessons, quizzes, and progress tracking effortlessly.
- **Real-time Feedback**: Users receive immediate feedback on their performance, helping them identify areas for improvement.
- **Language Selection**: Users can choose from a variety of languages (e.g., English, German, French) and difficulty levels, making the platform versatile.
- **Progress Tracking**: The UI displays progress metrics, allowing users to monitor their learning journey over time.

The UI is implemented through the `Main` class, which initializes the JavaFX application and manages user interactions via methods like `showMainMenu()` and `initializeLanguagesAndContent()`.


---

## Part 4: JUnit Testing for Student and Lesson Classes

### Unit Testing
We have already implemented JUnit tests to ensure the reliability of the `Student` and `Lesson` classes. These tests verify core functionalities such as retrieving usernames, setting passwords, adding lessons, updating progress, and accessing lesson details.


---

## How to Run

### Getting Started
To run the LinguaLearn application, follow these steps:

1. Ensure you have Java Development Kit (JDK) and JavaFX installed on your system.
2. Clone the repository or download the source code.
3. Open the project in your preferred IDE (e.g., IntelliJ IDEA or Eclipse).
4. Navigate to the `src.main.java` directory.
5. Run the `UI.java` file to launch the application.
6. Follow the on-screen prompts to interact with the GUI and UI features.


---

## Video Demo Section

### Demo Overview
A video demonstration showcasing the LinguaLearn UI and its features is available. This demo highlights the language selection, interactive lessons, quizzes, and progress tracking functionalities.

- **Steps to View**: 
  1. Visit the provided link or insert the video file path here.
  2. Watch the demo to explore the user experience and capabilities of LinguaLearn.
- **Note**: Ensure you have a compatible media player to view the demo.


---
## Team Members
- Hamza Eldafrawy
- Amr Ahmed Wahidi
- Mostafa Abdelsattar
- Ahmed Ezzat
- Anas Elsaba

---
## Conclusion
LinguaLearn is a robust and user-centric platform for language learning, built with Java and JavaFX. By leveraging OOP principles, a well-designed GUI and UI, and rigorous unit testing, the system provides an effective and engaging learning experience. The contributions of Hamza Eldafrawy, Amr Ahmed Wahidi, Mostafa Abdelsattar, Ahmed Ezzat, and Anas Elsaba have been instrumental in developing this project. We hope this platform inspires and supports language learners worldwide.

