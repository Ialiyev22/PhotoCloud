package Users;

import Login_SingUp.Login;
import Logs.BaseLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DiscoverPage extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel imagePanel;
    private JButton profileButton, loginButton;
    private String username;
    private HashMap<String, Integer> likeCounts;
    private HashMap<String, Integer> dislikeCounts;
    private ArrayList<String> likedImages;
    private ArrayList<String> dislikedImages;
    private HashMap<String, String> profilePictures;

    /**
     * shows other users' photos and their info such as dislike and like count, description, let the user dislike or like, or go to other users' profiles.
     * @param username of whose discover page is to be set up
     */

    public DiscoverPage(String username) {
        this.username = username;

        // Set frame properties
        setTitle("Discover Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());

        // Initialize header panel
        headerPanel = new JPanel(new BorderLayout());
        profileButton = new JButton("Profile");
        profileButton.addActionListener(this);
        loginButton = new JButton("Back to Login");
        loginButton.addActionListener(this);
        headerPanel.add(profileButton, BorderLayout.EAST);
        headerPanel.add(loginButton, BorderLayout.WEST);

        // Add header panel to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        likeCounts = new HashMap<>();
        dislikeCounts = new HashMap<>();

        // Initialize image panel
        imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));

        // Initialize search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            /**
             * this button l
             * @param e search event
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Read passwords from file and search for username
                long startTime = System.currentTimeMillis();
                try {
                    File file = new File("src\\Users\\Passwords.txt");
                    Scanner scanner = new Scanner(file);

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] tokens = line.split(",");

                        if (tokens[4].equals(searchField.getText())) {
                            // Redirect to user's profile
                            dispose(); // Close the current frame
                            new AnotherProfile(tokens[4], username);
                            BaseLogger.info().log(String.format("User %s is directed to user %s's Profile Page.", username, tokens[4]));
                            return;
                        }
                    }

                    // If no match is found, show error message
                    JOptionPane.showMessageDialog(null, "User not found.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    BaseLogger.info().log(ex.getMessage() + " took " + duration + "ms.");
                }
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

// Add search panel to center of header panel
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        loadUserActions(username);

        // Read images from file and create image labels
        try {
            File file = new File("src\\Users\\PublicImages.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(",");

                String description = "";
                if (tokens.length > 4) {
                    description = tokens[4];
                }

                // Load user's profile picture
                File profileFile = new File("src\\Users\\ProfilePics.txt");
                Scanner profileScanner = new Scanner(profileFile);

                while (profileScanner.hasNextLine()) {
                    String profileLine = profileScanner.nextLine();
                    String[] profileTokens = profileLine.split(",");

                    if (profileTokens[0].equals(tokens[0]) && !tokens[0].equals(username)) {
                        // Create image label
                        ImageIcon imageIcon = new ImageIcon("src\\images\\" + tokens[1]);
                        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(image));
                        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                        JLabel userLabel = new JLabel(tokens[0] + "  " + tokens[5]);
                        if (tokens[5].equals("Administrator")) {
                            userLabel = new JLabel(tokens[0] + "  " + "Professional");
                        }
                        userLabel.setHorizontalAlignment(JLabel.CENTER);

                        // Load user's profile picture
                        ImageIcon profileIcon = new ImageIcon("src\\images\\" + profileTokens[1]);
                        Image profileImage = profileIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                        JLabel profileLabel = new JLabel(new ImageIcon(profileImage));

                        profileLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                new AnotherProfile(tokens[0], username);
                                BaseLogger.info().log(String.format("User %s is directed to %s's Profile Page.", username, tokens[0]));
                            }
                        });
                        userLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                new AnotherProfile(tokens[0], username);
                                BaseLogger.info().log(String.format("User %s is directed to %s's Profile Page.", username, tokens[0]));
                            }
                        });

                        // Create panel to hold image label and user label
                        JPanel panel = new JPanel(new BorderLayout());
                        panel.add(profileLabel, BorderLayout.WEST);
                        panel.add(userLabel, BorderLayout.CENTER);
                        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

                        if (!description.equals("")) {
                            JLabel descriptionLabel = new JLabel(description);
                            descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
                            panel.add(descriptionLabel, BorderLayout.NORTH);
                        }

                        // Add panel and image label to image panel
                        JPanel imagePanelInner = new JPanel(new BorderLayout());
                        imagePanelInner.add(imageLabel, BorderLayout.CENTER);
                        imagePanelInner.add(panel, BorderLayout.SOUTH);
                        imagePanel.add(imagePanelInner);

                        imageLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                // Create new frame
                                JFrame frame = new JFrame();
                                frame.setTitle("Image Details");
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                frame.setSize(800, 600);
                                frame.setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());
                                frame.setLocationRelativeTo(null);

                                // Create panel for image and details
                                JPanel panel = new JPanel(new BorderLayout());

                                // Add full-sized image to panel
                                ImageIcon fullSizeIcon = new ImageIcon("src\\images\\" + tokens[1]);
                                Image fullSizeImage = fullSizeIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
                                JLabel fullSizeLabel = new JLabel(new ImageIcon(fullSizeImage));
                                panel.add(fullSizeLabel, BorderLayout.CENTER);


                                // Add details to panel
                                JPanel detailsPanel = new JPanel(new GridLayout(0, 2));
                                detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                                // Add owner's profile picture and username to details panel
                                JLabel ownerLabel = new JLabel("Owner:");
                                ownerLabel.setHorizontalAlignment(JLabel.RIGHT);
                                detailsPanel.add(ownerLabel);
                                JPanel ownerPanel = new JPanel(new FlowLayout());
                                ownerPanel.add(new JLabel(new ImageIcon(profileImage)));
                                ownerPanel.add(new JLabel(tokens[0]));
                                ownerPanel.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        dispose();
                                        frame.dispose();
                                        new AnotherProfile(tokens[0], username);
                                        BaseLogger.info().log(String.format("User %s is directed to %s's Profile Page", username, tokens[0]));
                                    }
                                });
                                detailsPanel.add(ownerPanel);

                                // Add likes and dislikes to details panel
                                JLabel likesLabel = new JLabel("Likes:");
                                likesLabel.setHorizontalAlignment(JLabel.RIGHT);
                                detailsPanel.add(likesLabel);
                                JLabel likesCountLabel = new JLabel(tokens[2] + " Like(s)");
                                detailsPanel.add(likesCountLabel);

                                JLabel dislikesLabel = new JLabel("Dislikes:");
                                dislikesLabel.setHorizontalAlignment(JLabel.RIGHT);
                                detailsPanel.add(dislikesLabel);
                                JLabel dislikesCountLabel = new JLabel(tokens[3] + " Dislike(s)");
                                detailsPanel.add(dislikesCountLabel);

                                JButton backButton = new JButton("Back");

                                backButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        frame.dispose();
                                        new DiscoverPage(username);
                                    }
                                });

                                if (readUserInfo(username)[1].equals("Administrator")) {

                                JButton deleteButton = new JButton("Delete");
                                deleteButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        deleteImageInfoFromFile("src\\Users\\Images.txt", tokens[0], tokens[1]);

                                        // Delete from "PublicImages.txt" file
                                        deleteImageInfoFromFile("src\\Users\\PublicImages.txt", tokens[0], tokens[1]);

                                        // Close the frame and display a message
                                        frame.dispose();
                                        JOptionPane.showMessageDialog(null, "Image deleted successfully!");
                                        new DiscoverPage(username);
                                    }
                                });
                                detailsPanel.add(deleteButton);}
                                detailsPanel.add(backButton);

                                panel.add(detailsPanel, BorderLayout.SOUTH);

                                // Add panel to frame and make it visible
                                frame.add(panel);
                                frame.setVisible(true);
                            }
                        });


                        JPanel likeDislikePanel = new JPanel(new FlowLayout());
                        // Create like and dislike buttons
                        JToggleButton likeButton = new JToggleButton("Like");
                        JToggleButton dislikeButton = new JToggleButton("Dislike");

                        // Check if the user has already liked or disliked the image
                        String imageId = tokens[1];
                        if (likedImages.contains(imageId)) {
                            likeButton.setSelected(true);
                            likeButton.setText("Unlike");
                        } else if (dislikedImages.contains(imageId)) {
                            dislikeButton.setSelected(true);
                            dislikeButton.setText("Undislike");
                        }

                        // Add action listeners to the like and dislike buttons
                        int likeCount = likeCounts.getOrDefault(imageId, 0);
                        int dislikeCount = dislikeCounts.getOrDefault(imageId, 0);
                        likeButton.setText("Like (" + likeCount + ")");
                        dislikeButton.setText("Dislike (" + dislikeCount + ")");

// Add action listeners to the like and dislike buttons
                        likeButton.addActionListener(e -> {
                            if (likeButton.isSelected()) {
                                // User likes the image
                                likedImages.add(imageId);
                                likeCounts.put(imageId, likeCount + 1);
                                likeButton.setText("Unlike (" + likeCounts.get(imageId) + ")");
                                dislikeButton.setSelected(false);
                                dislikeCounts.put(imageId, dislikeCount);
                                dislikeButton.setText("Dislike (" + dislikeCount + ")");
                                updateActionsFile(username);
                            } else {
                                // User unlikes the image
                                likedImages.remove(imageId);
                                likeCounts.put(imageId, likeCount);
                                likeButton.setText("Like (" + likeCounts.get(imageId) + ")");
                                updateActionsFile(username);
                            }
                        });

                        dislikeButton.addActionListener(e -> {
                            if (dislikeButton.isSelected()) {
                                // User dislikes the image
                                dislikedImages.add(imageId);
                                dislikeCounts.put(imageId, dislikeCount + 1);
                                dislikeButton.setText("Undislike (" + dislikeCounts.get(imageId) + ")");
                                likeButton.setSelected(false);
                                likeCounts.put(imageId, likeCount);
                                likeButton.setText("Like (" + likeCount + ")");
                                updateActionsFile(username);
                            } else {
                                // User undislikes the image
                                dislikedImages.remove(imageId);
                                dislikeCounts.put(imageId, dislikeCount);
                                dislikeButton.setText("Dislike (" + dislikeCounts.get(imageId) + ")");
                                updateActionsFile(username);
                            }
                        });
                        likeDislikePanel.add(likeButton);
                        likeDislikePanel.add(dislikeButton);


                        panel.add(likeDislikePanel, BorderLayout.SOUTH);
                    }

                }
                profileScanner.close();
            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create scroll pane and add image panel to it
        JScrollPane scrollPane = new JScrollPane(imagePanel);

        // Add scroll pane to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Set frame to be visible
        setVisible(true);

    }

    /**
     * gives a user's interactions with photos in the system
     * @param username whose interactions is to be taken
     */

    private void loadUserActions(String username) {
        likedImages = new ArrayList<>();
        dislikedImages = new ArrayList<>();

        // Load user's actions from the file
        try {
            File file = new File("src\\Users\\UserActions.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] tokens = line.split(",");
                    if (tokens.length == 3) {
                        String user = tokens[0];
                        String imageId = tokens[1];
                        String action = tokens[2];
                        if (user.equals(username)) {
                            if (action.equals("like")) {
                                likedImages.add(imageId);
                            } else if (action.equals("dislike")) {
                                dislikedImages.add(imageId);
                            }
                        }
                    }
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * updates actiomns file after a user likes or dislikes an image
     * @param username of the user whose actions are  to be updated
     */

    private void updateActionsFile(String username) {
        String actionsFile = "src\\Users\\UserActions.txt";
        try {
            FileWriter writer = new FileWriter(actionsFile);
            for (String imageId : likedImages) {
                writer.write(username + "," + imageId + ",like\n");
            }
            for (String imageId : dislikedImages) {
                writer.write(username + "," + imageId + ",dislike\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteImageInfoFromFile(String filePath, String username, String imageName) {
        try {
            File inputFile = new File(filePath);
            File tempFile = new File("src\\Users\\temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] imageInfo = line.split(",");
                String existingUsername = imageInfo[0];
                String existingImageName = imageInfo[1];

                // Check if the line contains the image information to delete
                if (existingUsername.equals(username) && existingImageName.equals(imageName)) {
                    // Skip this line to exclude it from the new file
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();

            // Replace the original file with the temporary file
            inputFile.delete();
            tempFile.renameTo(inputFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * provides a user's personal information in a String array
     * @param username of the user whose data is to be read
     * @return String array that is made of user's personal info
     */

    public String[] readUserInfo(String username) {
        String[] info = new String[2];
        long startTime = System.currentTimeMillis();
        try {
            File f = new File("src\\Users\\Passwords.txt");
            Scanner scan = new Scanner(f);
            while (scan.hasNextLine()) {
                String userinfo = scan.nextLine();
                String[] infos = userinfo.split(",");
                if (infos[4].equals(username)) {
                    info[0] = infos[4];
                    info[1] = infos[7];
                    break;
                }
            }
        }
        catch (IOException e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(e.getMessage() + ". took: " + duration + "ms.");
        }
        return info;
    }


    /**
     * handles different buttons' events
     * @param e the event to be processed
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == profileButton) {
            dispose();
            new ProfilePage(username);
            BaseLogger.info().log(String.format("User %s is directed to their Profile Page.", username));
        }
        else if (e.getSource() == loginButton) {
            dispose();
            new Login();
            BaseLogger.info().log(String.format("Back to Login Page."));
        }
    }
}
