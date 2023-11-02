package EPICC;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Quiz {
	//initialize instances
	private static QuizManager quizManager = new QuizManager();
	private static UsersAll usersAll = new UsersAll();

	//get a list of users
	public static List<User> getUsers() {
	    return usersAll.getAllUsers();
	}

	//get selected user (both this and a list of users are used later in statistics)
	public static User getSelectedUser() {
	    return quizManager.getSelectedUser();
	}


	

    public static void main(String[] args) {
    	//String testPath = "./src/EPICC/questionsNew.json";
    	
    	
    
        while (true) {
        	//design 
        	Color darkBlueColor = new Color(0, 0, 128); // Dark Blue
            Color whiteColor = Color.WHITE; 
            
            UIManager.put("OptionPane.background", darkBlueColor); 
            UIManager.put("OptionPane.messageForeground", darkBlueColor); 
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14)); 
            UIManager.put("Button.background", whiteColor); 
            UIManager.put("Button.foreground", Color.BLACK); 
            UIManager.put("Button.border", BorderFactory.createLineBorder(darkBlueColor, 2)); 
        	
        	
        	UIManager.put("OptionPane.minimumSize", new Dimension(50, 200));
        	UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
        	
        	//options on display
        	 String[] options = {"Create New User", "Display User Statistics", "Start Quiz", "Exit"};
             int choice = JOptionPane.showOptionDialog(null,
                     "Choose an option:", "Quiz Application", JOptionPane.DEFAULT_OPTION,
                     JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
             
             //depending on selected choice, a method is called
             switch (choice) {
                 case 0:
                     createUser(); //creating a user
                     break;
                 case 1:
                     List<User> allUsers = usersAll.getAllUsers(); // get all users from UsersAll
                     if (!allUsers.isEmpty()) {
                         quizManager.handleUserStatistics(); //displaying statistics
                     } else {
                         JOptionPane.showMessageDialog(null, "Error: There are no users logged into the system",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                     }
                     break;
                 case 2:
                     List<User> allUsersForQuiz = usersAll.getAllUsers(); // get all users from UsersAll
                     if (!allUsersForQuiz.isEmpty()) {
                         User selectedUser = selectUser(); //select user that will start the quiz
                         if (selectedUser != null) {
                        	 quizManager.startQuiz(selectedUser); //start the quiz
                         }
                     } else {
                         JOptionPane.showMessageDialog(null, "Error: There are no users logged into the system",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                     }
                     break;
                 case 3:
                     return;
             }
         }
     }
    
    
    
   
//select user from the list (the one who will start the quiz)
    private static User selectUser() {
        List<User> allUsers = usersAll.getAllUsers();
        String[] usernames = new String[allUsers.size()];
        
        for (int i = 0; i < allUsers.size(); i++) {
            usernames[i] = allUsers.get(i).getUsername();
        }

        String selectedUsername = (String) JOptionPane.showInputDialog(null,
                "Select a user to start the quiz:", "Select User",
                JOptionPane.QUESTION_MESSAGE, null, usernames, usernames[0]);

        if (selectedUsername != null) {
            for (User user : allUsers) {
                if (user.getUsername().equals(selectedUsername)) {
                    return user;
                }
            }
        }
        return null; // User not found
    }

//creating a new user
    private static void createUser() {
        String username = JOptionPane.showInputDialog(null, "Enter username:");

    	if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(null, "Username is already taken. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            User user = new User(username);
            usersAll.addUser(user);
            JOptionPane.showMessageDialog(null, "User created successfully.", "User Created", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    //checking if username is taken
    private static boolean isUsernameTaken(String username) {
        for (User user : usersAll.getAllUsers()) {
            if (user.getUsername().equals(username)) {
                return true; // Username is already taken
            }
        }
        return false; // Username is available
    }
}