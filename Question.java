package EPICC;

public class Question {
    private String category;
    private String difficulty;
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private boolean answeredCorrectly;

    public Question(String category, String difficulty, String questionText, String[] options, String correctAnswer) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.answeredCorrectly = false;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean isAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }
}