package Login_SingUp;

import Logs.BaseLogger;
import Users.Tier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUp extends JFrame implements ActionListener {
    private JTextField nameField, surnameField, emailField, ageField, usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton, uploadButton, loginButton;
    private JComboBox<Tier> tierComboBox;
    private JLabel profilePhotoLabel;
    private File selectedFile = null;
    private ImageIcon profilePhoto;

    /**
     * creates a signup page that gets a user's personal info, profile picture, and tier data and writes it in a txt file to be used later
     */

    public SignUp() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11, 1, 10, 10));

        JLabel nameLabel = new JLabel("Name: ");
        nameField = new JTextField(20);

        JLabel surnameLabel = new JLabel("Surname: ");
        surnameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email: ");
        emailField = new JTextField(20);

        JLabel ageLabel = new JLabel("Age: ");
        ageField = new JTextField(20);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(20);

        JLabel tierLabel = new JLabel("Tier: ");
        tierComboBox = new JComboBox<>(Tier.values());

        // Create and add the profile photo label to the frame
        profilePhotoLabel = new JLabel();
        profilePhotoLabel.setBounds(220, 50, 100, 100);
        long startTime = System.currentTimeMillis();
        try {
            profilePhoto = new ImageIcon(ImageIO.read(new File("src\\images\\Default.jpg")));
        }
        catch (IOException e) {
            e.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.info().log(e.getMessage() + " took " + duration + "ms.");
        }
        Image image = profilePhoto.getImage().getScaledInstance(profilePhotoLabel.getWidth(), profilePhotoLabel.getHeight(), Image.SCALE_SMOOTH);
        profilePhoto = new ImageIcon(image);
        profilePhotoLabel.setIcon(profilePhoto);

        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(this);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this);

        add(nameLabel);
        add(nameField);

        add(surnameLabel);
        add(surnameField);

        add(emailLabel);
        add(emailField);

        add(ageLabel);
        add(ageField);

        add(usernameLabel);
        add(usernameField);

        add(passwordLabel);
        add(passwordField);

        add(tierLabel);
        add(tierComboBox);


        add(uploadButton);
        add(loginButton);

        add(new JLabel(""));
        add(signUpButton);

        setVisible(true);
    };

    /**
     * hashes a password to write in a txt file securely
     * @param password to be hashed
     * @return string made up from the hashed bytes
     */

    private String hashPassword(String password) {
        long startTime = System.currentTimeMillis();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(e.getMessage() + " took " + duration + "ms.");
            return null;
        }
    }

    private boolean isUsernameUsed(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\Users\\Passwords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userInfo = line.split(",");
                String existingUsername = userInfo[4];
                if (existingUsername.equals(username)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean isAdminTierAssigned() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src\\Users\\Passwords.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String tier = fields[7].trim(); // Assuming tier information is in the 7th index
                if (tier.equals("Administrator")) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * process certain events
     * @param e the event to be processed is determined by comparing the buttons, and then according processes are done
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            // Create a file chooser for selecting the profile photo
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File file = fileChooser.getSelectedFile();
                profilePhotoLabel.setText(file.getName());
            }
        }
        else if(e.getSource() == loginButton) {
            dispose();
            new Login();
        }else if (e.getSource() == signUpButton) {
            long startTime = System.currentTimeMillis();

            // Get the input from the text fields
            String name = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            String age = ageField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String hashedPassword = hashPassword(password);

            while (name.isEmpty() || surname.isEmpty() || email.isEmpty() || age.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            while (password.length() < 8 || !password.matches(".*[!@#$%^&*()_+=?/\\|{}\\[\\]~-].*")) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long and contain at least one special character.");
                return;
            }

            while (isUsernameUsed(username)) {
                JOptionPane.showMessageDialog(this, "This username is already in use. Please choose a different username.");
                return;
            }

            String tier = tierComboBox.getSelectedItem().toString();

            while (tier.equals("Administrator") && isAdminTierAssigned()) {
                JOptionPane.showMessageDialog(this, "Administrator tier is already assigned to another user. Please choose a different tier.");
                return;
            }

            // Set the default photo if no photo is selected
            String photoName = profilePhotoLabel.getText();
            if (photoName.equals("")) {
                photoName = "Default.jpg";
            }
            // Write the input to a file
            try {
                FileWriter pp  = new FileWriter("src\\Users\\ProfilePics.txt", true);
                FileWriter writer = new FileWriter("src\\Users\\Passwords.txt", true);
                pp.write(username + "," + photoName + "\n");
                writer.write(name + "," + surname + "," + email + "," + age + "," + username + "," + hashedPassword + "," + photoName + "," + tier + "\n");
                writer.close();
                pp.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.info().log(ex.getMessage() + " took " + duration + "ms.");
            }

            // Display a message and close the frame
            JOptionPane.showMessageDialog(this, "Sign up successful!");
            dispose();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.info().log(String.format("User %s created successfully. took : %d ms.", username, duration));
            new Login();
        }


    }
}