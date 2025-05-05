//package controllers;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.layout.VBox;
//import javafx.scene.shape.Circle;
//import Main.Quiz;
//import Main.Question;
//import Main.QuizResult;
//import Main.SceneManager;
//import java.net.URL;
//import java.util.ResourceBundle;
//import javafx.fxml.Initializable;
//
//
//public class QuizGradeController implements Initializable {
//    @FXML private Label scoreLabel;
//    @FXML private Label scoreText;
//    @FXML private VBox questionsContainer;
//    @FXML private Label q1;
//    @FXML private Label Q1Sol;
//    private Quiz quiz;
//    private QuizResult quizResult;
//    
//    public void setQuizGrade() {
//        // Initialize the scene
//        quiz=Quiz.setQuiz("src/main/resources/quiz1.txt");
//        q1.setText(quiz.getQuestions().get(0).getQuestion());
//        Q1Sol.setText(quiz.getQuestions().get(0).getCorrectAnswer());
//        
//    }
//    
//    public void setQuizData(Quiz quiz, QuizResult result) {
//        this.quiz = quiz;
//        this.quizResult = result;
//        
//        updateScore();
//        displayQuestions();
//    }
//    
//    private void updateScore() {
//        int percentage = quizResult.getScore() / Quiz.getNumberOfQuestions() * 100;
//        scoreLabel.setText(String.format("%.0f%%", percentage));
//        scoreText.setText(String.format("%d out of %d correct", 
//        quizResult.getScore(), Quiz.getNumberOfQuestions()));       
//    }
//    
//    private void displayQuestions() {
//        questionsContainer.getChildren().clear();
//        
//        for (int i = 0; i < Quiz.getNumberOfQuestions(); i++) {
//            Question question = quiz.getQuestions().get(i);
//            String userAnswer = quizResult.getUserAnswers().get(i);
//            
//            VBox questionCard = createQuestionCard(question, userAnswer, i + 1);
//            questionsContainer.getChildren().add(questionCard);
//        }
//    }
//    
//    private VBox createQuestionCard(Question question, String userAnswer, int questionNumber) {
//        VBox card = new VBox(10);
//        card.getStyleClass().add("question-card");
//        
//        Label questionLabel = new Label("Question " + questionNumber + ": " + question.getQuestion());
//        questionLabel.getStyleClass().add("question-header");
//        
//        Label answerLabel = new Label("Your answer: " + userAnswer);
//        answerLabel.getStyleClass().add(
//            userAnswer.equals(question.getCorrectAnswer()) ? "correct-answer" : "incorrect-answer"
//        );
//        
//        Label correctAnswerLabel = new Label("Correct answer: " + question.getCorrectAnswer());
//        
//        card.getChildren().addAll(questionLabel, answerLabel, correctAnswerLabel);
//        return card;
//    }
//    
//    @FXML
//    private void navigateToStudentScene() {
//        // Navigate back to dashboard
//        SceneManager.switchToMainScene(0);
//
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        setQuizGrade();
//    }
//}