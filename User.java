package EPICC;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class User {
    private String username;
    private List<Integer> quizScores;  // Scores of quizzes taken by the user

    public User(String username) {
        this.username = username;
        this.quizScores = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void addQuizScore(int points) {
        quizScores.add(points);
    }

    public List<Integer> getQuizScores() {
        return quizScores;
    }
    
    
    
    // STATISTICS

    // Method to calculate mean of quiz scores
    public double calculateMean() {
        if (quizScores.isEmpty()) return 0;

        int sum = 0;
        for (int score : quizScores) {
            sum += score;
        }
        return (double) sum / quizScores.size();
    }

    // Method to calculate median of quiz scores
    public double calculateMedian() {
        if (quizScores.isEmpty()) return 0;

        int n = quizScores.size();
        quizScores.sort(Integer::compareTo);

        if (n % 2 == 0) {
            return (double) (quizScores.get(n / 2 - 1) + quizScores.get(n / 2)) / 2;
        } else {
            return quizScores.get(n / 2);
        }
    }

    // Method to calculate standard deviation of quiz scores
    public double calculateStandardDeviation() {
        if (quizScores.isEmpty()) return 0;

        double mean = calculateMean();
        double sumOfSquares = 0;

        for (int score : quizScores) {
            sumOfSquares += Math.pow(score - mean, 2);
        }

        return Math.sqrt(sumOfSquares / quizScores.size());
    }
    
    
    
    
    
    public void displayStatistics() {
        StringBuilder message = new StringBuilder("Statistics for " + getUsername() + ":\n");
        message.append("Quiz Scores: ").append(getQuizScores()).append("\n");
        message.append("Mean: ").append(calculateMean()).append("\n");
        message.append("Median: ").append(calculateMedian()).append("\n");
        message.append("Standard Deviation: ").append(calculateStandardDeviation()).append("\n");
        //package EPICC;

}
}