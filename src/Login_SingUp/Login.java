package Login_SingUp;

import Logs.BaseLogger;
import Users.DiscoverPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Login extends JFrame implements ActionListener {
    private JLabel usernameLabel, passwordLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    /**
     * creates a login page for users to enter the PhotoCloud application
     */

    public Login() {
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false);
        this.setLayout(null);
        setLocationRelativeTo(null);
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(250, 200, 80, 25);
        this.add(usernameLabel);
        setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());

        usernameTextField = new JTextField(20);
        usernameTextField.setBounds(350, 200, 190, 25);
        this.add(usernameTextField);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(250, 250, 80, 25);
        this.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(350, 250, 190, 25);
        this.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(355, 300, 80, 25);
        loginButton.addActionListener(this);
        this.add(loginButton);

        signupButton = new JButton("Signup");
        signupButton.setBounds(455, 300, 80, 25);
        signupButton.addActionListener(this);
        this.add(signupButton);

        this.setVisible(true);
    }

    /**
     *
     * @param e the event to be processed is determined by comparing, and the according processes are done such as logging in, or being directed to signup page
     */

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {
            String username = usernameTextField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Login Successful");
                this.dispose();
                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.info().log(String.format("User %s logged in successfully. took : %d ms.", username, duration));
                new DiscoverPage(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.info().log(String.format("Password do not match the passwords for user %s. took : %d ms.", username, duration));
            }
        } else if (e.getSource() == signupButton) {
            this.dispose();
            new SignUp();
        }
    }

    /**
     * encrypts a user's password with a specific algorithm to keep it secure
     * @param password to be encrypted
     * @return a string that is made of bytes of the encrypted string
     * @throws NoSuchAlgorithmException if the corresponding algorithm is not found
     */

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * compares the values entered and the ones in the corresponding txt file and determines if they match
     * @param username tries to log in
     * @param password the user entered
     * @return result of the comparison
     */

    private boolean authenticateUser(String username, String password) {
        long startTime = System.currentTimeMillis();
        try {
            Scanner scanner = new Scanner(new File("src\\Users\\Passwords.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[4].equals(username)) {
                    String encryptedPassword = parts[5];
                    String inputEncryptedPassword = encryptPassword(password);
                    if (encryptedPassword.equals(inputEncryptedPassword)) {
                        scanner.close();
                        return true;
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException ex) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(ex.getMessage() + " took " + duration + "ms.");
            //ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(ex.getMessage() + " took " + duration + "ms.");
        }
        return false;
    }

    public static void main(String[] args) {
        new Login();
    }
}
