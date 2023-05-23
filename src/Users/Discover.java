package Users;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Discover extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JPanel imagePanel;
    private String username;
    private Map<String, Integer> likeCounts;
    private Map<String, Integer> dislikeCounts;
    private Set<String> likedImages;
    private Set<String> dislikedImages;

    public Discover(String username) {
        this.username = username;
        likeCounts = new HashMap<>();
        dislikeCounts = new HashMap<>();
        likedImages = new HashSet<>();
        dislikedImages = new HashSet<>();

        // Set frame properties
        setTitle("Discover Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());

        // Initialize image panel
        imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));

        // Read images from file and create image labels
        try {
            File file = new File("src\\Users\\PublicImages.txt");
            java.util.List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (String line : lines) {
                String[] tokens = line.split(",");

                String description = "";
                if (tokens.length > 4) {
                    description = tokens[4];
                }

                if (tokens[0].equals(username)) {
                    continue; // Skip own images
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

                loadUserActions(username);
                // Load like and dislike counts
                int likeCount = Integer.parseInt(tokens[2]);
                int dislikeCount = Integer.parseInt(tokens[3]);


                // Create mutable wrapper classes for like and dislike counts
                AtomicInteger likeCountWrapper = new AtomicInteger(likeCount);
                AtomicInteger dislikeCountWrapper = new AtomicInteger(dislikeCount);

                // Add action listeners to like and dislike buttons
                JToggleButton likeButton = new JToggleButton("Like");
                JToggleButton dislikeButton = new JToggleButton("Dislike");

                // Set initial like and dislike counts on buttons
                likeButton.setText("Like (" + likeCount + ")");
                dislikeButton.setText("Dislike (" + dislikeCount + ")");

// Check if the current image is already liked or disliked by the user
                if (likedImages.contains(tokens[1])) {
                    likeButton.setSelected(true);
                } else if (dislikedImages.contains(tokens[1])) {
                    dislikeButton.setSelected(true);
                }

// Add action listeners to like and dislike toggle buttons
                likeButton.addActionListener(e -> {
                    if (likeButton.isSelected()) {
                        // User clicked on like button
                        if (dislikeButton.isSelected()) {
                            // If dislike was previously selected, unselect it
                            dislikeButton.setSelected(false);
                            dislikedImages.remove(tokens[1]);
                            dislikeCountWrapper.decrementAndGet();
                            dislikeButton.setText("Dislike (" + dislikeCountWrapper.get() + ")");
                        }

                        // Add like
                        likedImages.add(tokens[1]);
                        likeCountWrapper.incrementAndGet();
                        likeButton.setText("Unlike (" + likeCountWrapper.get() + ")");
                    } else {
                        // User unselected the like button
                        likedImages.remove(tokens[1]);
                        likeCountWrapper.decrementAndGet();
                        likeButton.setText("Like (" + likeCountWrapper.get() + ")");
                    }

                    updateLikeDislikeCounts(tokens[1], likeCountWrapper.get(), dislikeCountWrapper.get());
                });

                dislikeButton.addActionListener(e -> {
                    if (dislikeButton.isSelected()) {
                        // User clicked on dislike button
                        if (likeButton.isSelected()) {
                            // If like was previously selected, unselect it
                            likeButton.setSelected(false);
                            likedImages.remove(tokens[1]);
                            likeCountWrapper.decrementAndGet();
                            likeButton.setText("Like (" + likeCountWrapper.get() + ")");
                        }

                        // Add dislike
                        dislikedImages.add(tokens[1]);
                        dislikeCountWrapper.incrementAndGet();
                        dislikeButton.setText("Undislike (" + dislikeCountWrapper.get() + ")");
                    } else {
                        // User unselected the dislike button
                        dislikedImages.remove(tokens[1]);
                        dislikeCountWrapper.decrementAndGet();
                        dislikeButton.setText("Dislike (" + dislikeCountWrapper.get() + ")");
                    }

                    updateLikeDislikeCounts(tokens[1], likeCountWrapper.get(), dislikeCountWrapper.get());
                });

                // Add image label, like button, and dislike button to the image panel
                JPanel imagePanelInner = new JPanel(new BorderLayout());
                JPanel likeDislikePanel = new JPanel();
                likeDislikePanel.add(likeButton);
                likeDislikePanel.add(dislikeButton);
                imagePanelInner.add(imageLabel, BorderLayout.CENTER);
                imagePanelInner.add(likeDislikePanel, BorderLayout.SOUTH);
                imagePanel.add(imagePanelInner);
            }
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

    private void readUserActions(String username) {
        String likedImagesFile = "src\\Users\\" + username + "_liked_images.txt";
        likedImages.clear();

        try {
            File file = new File("src\\Users\\UserActions.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(",");

                if (tokens[0].equals(username)) {
                    if (tokens[1].equals("like")) {
                        likedImages.add(tokens[2]);
                    } else if (tokens[1].equals("dislike")) {
                        dislikedImages.add(tokens[2]);
                    }
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadUserActions(String username) {
        // Load user's liked images
        String likedImagesFile = "src\\Users\\" + username + "_liked_images.txt";
        likedImages.clear();
        File likedFile = new File(likedImagesFile);
        try {
            if (!likedFile.exists()) {
                likedFile.createNewFile();
            } else {
                Scanner scanner = new Scanner(likedFile);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    likedImages.add(line);
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load user's disliked images
        String dislikedImagesFile = "src\\Users\\" + username + "_disliked_images.txt";
        dislikedImages.clear();
        File dislikedFile = new File(dislikedImagesFile);
        try {
            if (!dislikedFile.exists()) {
                dislikedFile.createNewFile();
            } else {
                Scanner scanner = new Scanner(dislikedFile);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    dislikedImages.add(line);
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ...
    }



    private void updateLikeDislikeCounts(String imageName, int likeCount, int dislikeCount) {
        try {
            // Update like and dislike counts in the file
            File file = new File("src\\Users\\PublicImages.txt");
            java.util.List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            StringBuilder updatedFileContent = new StringBuilder();

            for (String line : lines) {
                String[] tokens = line.split(",");
                String currentImageName = tokens[1];

                if (currentImageName.equals(imageName)) {
                    tokens[2] = String.valueOf(likeCount);
                    tokens[3] = String.valueOf(dislikeCount);
                }

                updatedFileContent.append(String.join(",", tokens)).append("\n");
            }

            // Write the updated content back to the file
            FileWriter writer = new FileWriter(file);
            writer.write(updatedFileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle action events
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Discover("username"));
    }
}
