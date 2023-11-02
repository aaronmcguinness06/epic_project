package EPICC;
import java.sql.Connection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.IntSummaryStatistics;

import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuizManager {
	private List<Question> questions = new ArrayList<>();
    private User selectedUser;

    // Access the static UsersAll instance from QuizManager
    public static UsersAll usersAll = new UsersAll();
    
    public User getSelectedUser() {
        return selectedUser;
    }



    public QuizManager() {
        // Read and parse the JSON file to load questions
        try {
        	
            String jsonFilePath = "./src/EPICC/questionsNew.json";  
            FileReader fileReader = new FileReader(jsonFilePath);
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(fileReader, JsonArray.class);

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject questionObject = jsonArray.get(i).getAsJsonObject();
                // Create Question objects from questionObject and add them to the questions list
                Question question = new Question(
                	    questionObject.get("category").getAsString(),
                	    questionObject.get("difficulty").getAsString(),
                	    questionObject.get("question").getAsString(),
                	    parseOptions(questionObject.getAsJsonArray("options")),
                	    questionObject.get("correctOption").getAsString()
                	);
                questions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private String[] parseOptions(JsonArray optionsArray) {
        String[] options = new String[optionsArray.size()];
        for (int i = 0; i < optionsArray.size(); i++) {
            options[i] = optionsArray.get(i).getAsString();
        }
        return options;
    }

   
    public void startQuiz(User user) {
        selectedUser = user;
         
        usersAll.addUser(user);

        String[] options = { "Random Draw", "Increasing Difficulty", "Select categories manually" };
        
        int choice = showCustom(
                "Select the quiz format",
                "Quiz Format",
                options,
                "Choose an Option"
        );

        if (choice == 0) {
            // Start the quiz using the random draw format
            startRandomDrawQuiz();
        } else if (choice == 1) {
            // Start the quiz using the increasing difficulty format
            startIncreasingDifficultyQuiz();
        } else if (choice == 2) {
        	// Start the quiz using the select category format
        	selectCategory();
        }
    }
    
    private int showCustom(String message, String title, String[] buttonLabels, String buttonPrompt) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Design
        int buttonWidth = 200;
        int buttonHeight = 90;
        Font buttonFont = new Font("Arial", Font.PLAIN, 20);
        UIManager.put("OptionPane.minimumSize", new Dimension(50, 200));
    	UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);

        // Add buttons with equal spacing between them (using for loop)
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(30)); 
            panel.add(button);
        }
        
        

        int choice = JOptionPane.showOptionDialog(
                null, message, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, buttonLabels, buttonLabels[0]);

            return choice;
        
    }
    
    
    private void startRandomDrawQuiz() {
        List<Question> allQuestions = new ArrayList<>(questions);
        List<Question> selectedQuestions = new ArrayList<>();

        // Shuffle the list of all questions
        Collections.shuffle(allQuestions);
        

        // Select 6 questions (2 from each category)
        int questionsPerCategory = 2;
        for (String category : new String[] { "COMP SCIENCE", "COMP ORG", "DISCRETE MATHS" }) {
            List<Question> categoryQuestions = new ArrayList<>(allQuestions);
            categoryQuestions.removeIf(question -> !question.getCategory().equals(category));
            Collections.shuffle(categoryQuestions);
            selectedQuestions.addAll(categoryQuestions.subList(0, questionsPerCategory));
        }

       

        // Ask selected questions
        for (Question question : selectedQuestions) {
            askQuestion(question);
        }
    }
    
    private void selectCategory() {
        String[] categoryOptions = {"Novice", "Intermediate", "Expert", "All questions"};
        String choice = (String) JOptionPane.showInputDialog(
                null, "Select a category:", "Category Selection",
                JOptionPane.QUESTION_MESSAGE, null, categoryOptions, categoryOptions[0]);

        if (choice != null) {
            List<Question> allQuestions = new ArrayList<>(questions);
            List<Question> selectedQuestions = new ArrayList<>();

            if ("All questions".equals(choice)) {
                // If "All questions" is selected, add all the questions
                selectedQuestions.addAll(allQuestions);
            } else {
                // Filter questions based on the selected category (difficulty)
                for (Question question : allQuestions) {
                    if (question.getDifficulty().equals(choice)) {
                        selectedQuestions.add(question);
                    }
                }
            }

            // Ask questions from the selected category
            for (Question question : selectedQuestions) {
                askQuestion(question);
            }
        }
    }
 
    

    private void startIncreasingDifficultyQuiz() {
        List<Question> allQuestions = new ArrayList<>(questions);
        List<Question> selectedQuestions = new ArrayList<>();

        // Sort all questions by difficulty (NOVICE, INTERMEDIATE, EXPERT)
        Collections.sort(allQuestions, (q1, q2) -> q1.getDifficulty().compareTo(q2.getDifficulty()));

        // Select 6 questions (2 from each category, increasing difficulty)
        int questionsPerCategory = 2;
        for (String category : new String[] { "COMP SCIENCE", "COMP ORG", "DISCRETE MATHS" }) {
            List<Question> categoryQuestions = new ArrayList<>(allQuestions);
            categoryQuestions.removeIf(question -> !question.getCategory().equals(category));
            selectedQuestions.addAll(categoryQuestions.subList(0, questionsPerCategory));
        }

        // Ask selected questions
        for (Question question : selectedQuestions) {
            askQuestion(question);
        }
    }
    
    //method for a good looking pop up window after answering a question
    private void nicePopUp(Component parentComponent, String message, String title, int messageType, int size) {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JLabel(message));

        int width = size;
        int height = size;

        JOptionPane optionPane = new JOptionPane(panel, messageType);
        JDialog dialog = optionPane.createDialog(parentComponent, title);

        dialog.setSize(new Dimension(width, height));
        dialog.setVisible(true);
    }


    
    private void askQuestion(Question question) {
        // Create an array of options with labels A, B, and C
        String[] optionLabels = { "A", "B", "C" };

        // Create an array of radio buttons for options
        JRadioButton[] radioButtons = new JRadioButton[3];

        // Create a button group to ensure only one option can be selected
        ButtonGroup buttonGroup = new ButtonGroup();

        // Create a panel to hold the radio buttons and options
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Create a label to display the question
        JLabel questionLabel = new JLabel(question.getQuestionText());
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Set a custom font
        panel.add(questionLabel);

        // Add radio buttons and options to the panel
        for (int i = 0; i < 3; i++) {
            radioButtons[i] = new JRadioButton(optionLabels[i] + ": " + question.getOptions()[i]);
            panel.add(radioButtons[i]);
            buttonGroup.add(radioButtons[i]);
        }

        int option = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            for (int i = 0; i < 3; i++) {
                if (radioButtons[i].isSelected()) {
                    if (optionLabels[i].equalsIgnoreCase(question.getCorrectAnswer())) {
                        // Correct answer
                        int points = 0;

                        // Assign points based on the difficulty level
                        if (question.getDifficulty().equals("Novice")) {
                            points = 1;
                        } else if (question.getDifficulty().equals("Intermediate")) {
                            points = 2;
                        } else if (question.getDifficulty().equals("Expert")) {
                            points = 3;
                        }

                        selectedUser.addQuizScore(points); // Update user's score with the assigned points
                        question.setAnsweredCorrectly(true); // Mark the question as answered correctly

                        nicePopUp(null, "Correct!", "Result", JOptionPane.INFORMATION_MESSAGE, 300);
                        

                    } else {
                        // Incorrect answer
                    	nicePopUp(null, "Incorrect. The correct answer is: " + question.getCorrectAnswer(), "Result", JOptionPane.ERROR_MESSAGE, 300);
                    }
                }
            }
        }



    }
    
   
    
    public void displayUserStatistics(User selectedUser) {
        if (selectedUser != null) {
            // Retrieve and display statistics for the selected user
            String username = selectedUser.getUsername();
            List<Integer> quizScores = selectedUser.getQuizScores();
            double mean = selectedUser.calculateMean();
            double median = selectedUser.calculateMedian();
            double standardDeviation = selectedUser.calculateStandardDeviation();
            

            // Display the statistics using JOptionPane or any other preferred method
            StringBuilder message = new StringBuilder("Statistics for " + username + ":\n");
            message.append("Quiz Scores: ").append(quizScores).append("\n");
            message.append("Mean: ").append(mean).append("\n");
            message.append("Median: ").append(median).append("\n");
            message.append("Standard Deviation: ").append(standardDeviation).append("\n");

            JOptionPane.showMessageDialog(null, message.toString(), "User Statistics", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    public void displayAllUserStatistics() {
    	// Retrieve and display statistics for all users
        List<User> users = usersAll.getAllUsers();

        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No users have taken the quiz.", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("Statistics for all users:\n");

            for (User user : users) {
                message.append(user.getUsername()).append(":\n");
                message.append("Quiz Scores: ").append(user.getQuizScores()).append("\n");
                message.append("Mean: ").append(user.calculateMean()).append("\n");
                message.append("Median: ").append(user.calculateMedian()).append("\n");
                message.append("Standard Deviation: ").append(user.calculateStandardDeviation()).append("\n\n");
            }

            JOptionPane.showMessageDialog(null, message.toString(), "All Users Statistics", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
 // Method to handle when to display what statistics
    void handleUserStatistics() {
        List<User> users = usersAll.getAllUsers(); // Get the users list from the Quiz class

        String[] options = { "View User Statistics", "View All User Statistics" };
        int choice = showCustom("Select an option", "Statistics Options", options, "Choose an Option");

        if (choice == 0) { // Opening statistics for a selected user only
            if (!users.isEmpty()) {
                String[] usernames = users.stream().map(User::getUsername).toArray(String[]::new);
                String selectedUsername = (String) JOptionPane.showInputDialog(
                    null,
                    "Select a user to view their statistics:",
                    "Select User",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    usernames,
                    usernames[0]
                );

                if (selectedUsername != null) {
                    User selectedUser = users.stream()
                        .filter(user -> user.getUsername().equals(selectedUsername))
                        .findFirst()
                        .orElse(null);

                    if (selectedUser != null) {
                        displayUserStatistics(selectedUser);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: There are no users logged into the system", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error: There are no users logged into the system", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) { // displaying statistics for all users 
            displayAllUserStatistics();
        }
    }

    

 
}