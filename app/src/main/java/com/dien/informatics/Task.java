package com.dien.informatics;

import java.util.List;

public class Task {
    private String Question;
    private List<String> Answers;
    private int Correct;

    public Task() {
    }

    public Task(String question, List<String> answers, int correct) {
        Question = question;
        Answers = answers;
        Correct = correct;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public List<String> getAnswers() {
        return Answers;
    }

    public void setAnswers(List<String> answers) {
        Answers = answers;
    }

    public int getCorrect() {
        return Correct;
    }

    public void setCorrect(int correct) {
        Correct = correct;
    }
}
