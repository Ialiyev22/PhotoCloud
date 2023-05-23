package Users;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DD extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JPanel imagePanel;
    private String username;
    private HashMap<String, Integer> likeCounts;
    private HashMap<String, Integer> dislikeCounts;
    private ArrayList<String> likedImages;
    private ArrayList<String> dislikedImages;
    private HashMap<String, String> profilePictures;

    public DD(String username) {
        this.username = username;

        // Set frame properties
        setTitle("Discover Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());

        // Initialize image panel
        imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));

        // Initialize likeCounts and dislikeCounts maps
        likeCounts = new HashMap<>();
        dislikeCounts = new HashMap<>();

        // Load user's liked and disliked images
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

                // Create image label
                ImageIcon imageIcon = new ImageIcon("src\\images\\" + tokens[1]);
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                JLabel userLabel = new JLabel(tokens[0] + "  " + tokens[5]);
                if (tokens[5].equals("Administrator")) {
                    userLabel = new JLabel(tokens[0] + "  " + "Professional");
                }

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

                // Set like and dislike counts on the buttons
                int likeCount = likeCounts.getOrDefault(imageId, 0);
                int dislikeCount = dislikeCounts.getOrDefault(imageId, 0);
                likeButton.setText("Like (" + likeCount + ")");
                dislikeButton.setText("Dislike (" + dislikeCount + ")");

                // Add action listeners to the like and dislike buttons
                likeButton.addActionListener(e -> {
                    if (likeButton.isSelected()) {
                        // User likes the image
                        likedImages.add(imageId);
                        likeButton.setText("Unlike");
                        dislikeButton.setSelected(false);
                        dislikeButton.setText("Dislike (" + dislikeCount + ")");
                        updateActionsFile(username);
                    } else {
                        // User unlikes the image
                        likedImages.remove(imageId);
                        likeButton.setText("Like (" + likeCount + ")");
                        updateActionsFile(username);
                    }
                });

                dislikeButton.addActionListener(e -> {
                    if (dislikeButton.isSelected()) {
                        // User dislikes the image
                        dislikedImages.add(imageId);
                        dislikeButton.setText("Undislike");
                        likeButton.setSelected(false);
                        likeButton.setText("Like (" + likeCount + ")");
                        updateActionsFile(username);
                    } else {
                        // User undislikes the image
                        dislikedImages.remove(imageId);
                        dislikeButton.setText("Dislike (" + dislikeCount + ")");
                        updateActionsFile(username);
                    }
                });

                // Create a panel for the buttons
                JPanel buttonsPanel = new JPanel();
                buttonsPanel.add(likeButton);
                buttonsPanel.add(dislikeButton);

                // Create a panel for the image label and buttons
                JPanel imagePanelInner = new JPanel(new BorderLayout());
                imagePanelInner.add(imageLabel, BorderLayout.CENTER);
                imagePanelInner.add(userLabel, BorderLayout.NORTH);
                imagePanelInner.add(buttonsPanel, BorderLayout.SOUTH);
                imagePanel.add(imagePanelInner);
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

    // Rest of the code...

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle other action events
        // ...
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DD("username"));
    }
}

