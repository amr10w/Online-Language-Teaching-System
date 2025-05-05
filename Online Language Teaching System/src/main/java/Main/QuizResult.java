/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.util.ArrayList;

/**
 *
 * @author ahmed
 */
public class QuizResult {
    private int score;
    private ArrayList<String>  userAnswers = new ArrayList<>(Quiz.getNumberOfQuestions());

    
    public int getScore()
    {
        return score;
    }
    public ArrayList<String> getUserAnswers()
    {
        return userAnswers;
    }
    public void setUserAnswers(int index, String userAnswer)
    {
        this.userAnswers.set(index, userAnswer);
    }

    
}
