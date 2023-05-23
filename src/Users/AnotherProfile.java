package Users;

import Logs.BaseLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class AnotherProfile extends JFrame {

    private String username, directedUser;
    private JLabel nameLabel, usernameLabel, ageLabel;
    private JPanel headerPanel, userInfoPanel, photosPanel;
    private JButton profileButton, discoverButton;

    /**
     * displays the public profile of a user
     * @param username of the user who
     * @param directedUser is in the profile of the username
     */

    public AnotherProfile(String username, String directedUser) {

        super("Profile Page");
        this.username = username;
        this.directedUser = directedUser;

        // Set the size and layout of the frame
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());

        // Initialize the header panel
        headerPanel = new JPanel();

        // Initialize the upload button and add it to the header panel
        profileButton = new JButton("Profile");
        discoverButton = new JButton("Back to Discover");


        // Initialize the user info panel
        userInfoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Read the user's personal info from the file
        String[] userInfo = readUserInfo(username);

        // Initialize the name, username, and age labels
        nameLabel = new JLabel(userInfo[0] + " " + userInfo[1]);
        usernameLabel = new JLabel("Username: " + username);
        ageLabel = new JLabel("Age: " + userInfo[2]);

        // Add the labels to the user info panel
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(ageLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));

        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(profileButton, BorderLayout.EAST);
        headerPanel.add(discoverButton, BorderLayout.WEST);

        // Initialize the photo panel
        photosPanel = new JPanel(new GridLayout(0, 3));
        photosPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



        String[] photos = getPhotos(username);
        for (String photo : photos) {
            ImageIcon img = new ImageIcon("src\\images\\" + photo);
            Image Img = img.getImage();
            Image newImg = Img.getScaledInstance(150,150, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(newImg));
            if (userInfo[4].equals("Administrator")){
            photoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handlePhotoClick(username, photo);
                }
            });}
            photosPanel.add(photoLabel);
        }

        // Read the user's profile photo from the file
        String profilePhoto = userInfo[3];

        // Initialize the profile photo label
        ImageIcon profileImage = new ImageIcon("src\\images\\" + profilePhoto);
        Image img = profileImage.getImage().getScaledInstance(150,150, Image.SCALE_SMOOTH);
        JLabel profilePhotoLabel = new JLabel(new ImageIcon(img));

        // Add the profile photo label to the user info panel
        userInfoPanel.add(profilePhotoLabel);

        // Add the user info panel and photo panel to the frame
        add(userInfoPanel, BorderLayout.WEST);
        add(new JScrollPane(photosPanel), BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        /**
         * directs the user to their discover page
         */

        discoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DiscoverPage(directedUser);
                BaseLogger.info().log(String.format("User %s is directed to their Discover Page.", directedUser));
            }
        });

        /**
         * directs the user to their own profile page
         */
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ProfilePage(directedUser);
                BaseLogger.info().log(String.format("User %s is directed to their Profile Page.", directedUser));
            }});
                // Display the frame
                setVisible(true);
            }

        long startTime = System.currentTimeMillis();
        // Method to read the user's personal info from the file
        private String[] readUserInfo(String username) {
        long startTime = System.currentTimeMillis();
        String[] userInfo = new String[5];
            try {
                BufferedReader reader = new BufferedReader(new FileReader("src\\Users\\Passwords.txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (fields[4].equals(username)) {
                        userInfo[0] = fields[0];
                        userInfo[1] = fields[1];
                        userInfo[2] = fields[3];
                        userInfo[3] = fields[6];
                        userInfo[4] = fields[7];
                        break;
                    }
                }
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.error().log(ex.getMessage() + " took " + duration + "ms.");
            }
            return userInfo;
        }

    /**
     * gives a string array consisting of specified user's public images' directories
     * @param user that we want to get the photos of
     * @return the user's public images' directories
     */

    public String[] getPhotos(String user) {
            String[] profile = new String[50];
            long startTime = System.currentTimeMillis();
            try {
                File f = new File("src\\Users\\PublicImages.txt");
                Scanner scan = new Scanner(f);
                int i = 0;
                while (scan.hasNextLine()) {
                    String photo = scan.nextLine();
                    String[] photoinfo = photo.split(",");
                    if (photoinfo[0].equals(username)) {
                        profile[i] = photoinfo[1];
                        i++;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.error().log(e.getMessage() + " took " + duration + "ms.");
            }
            return profile;
        }

    /**
     * refreshes the photos panel after an image is deleted or edited
      * @param username whose panel is to be refreshed
     */

    private void refreshPhotosPanel(String username) {
        // Clear the current photo labels
        photosPanel.removeAll();

        // Load updated photos
        String[] photos = getPhotos(username);
        for (String photo : photos) {
            ImageIcon img = new ImageIcon("src/images/" + photo);
            Image imgScaled = img.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(imgScaled));

            // Add mouse listener to the updated photo label
            photoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handlePhotoClick(username, photo);
                }
            });

            photosPanel.add(photoLabel);
        }

        // Refresh the photos panel
        photosPanel.revalidate();
        photosPanel.repaint();
    }

    /**
     * handles processes to be done on photos when they are clicked on
     * @param username whose photo is clicked on
     * @param photo that is clicked on
     */

    private void handlePhotoClick(String username, String photo) {
        long startTime =  System.currentTimeMillis();
        String[] options = {"Delete", "Edit"};
        int selectedOption = JOptionPane.showConfirmDialog(AnotherProfile.this, "Do you want to delete image", "Delete Image", JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            deleteImageInfoFromFile("src\\Users\\Images.txt", username, photo);
            try {
                File inputFile = new File("src\\Users\\PublicImages.txt");
                ;
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] imageInfo = line.split(",");
                    String existingUsername = imageInfo[0];
                    String existingImageName = imageInfo[1];

                    // Check if the line contains the image information to delete
                    if (existingUsername.equals(username) && existingImageName.equals(photo)) {
                        deleteImageInfoFromFile("src\\Users\\PublicImages.txt", username, photo);
                    }
                    ;
                }
            } catch (IOException e) {
                e.printStackTrace();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                BaseLogger.error().log(e.getMessage() + " took: " + duration + " ms.");
            }
            ImageIcon img = new ImageIcon("src\\images\\" + photo);
            Image Img = img.getImage();
            Image newImg = Img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(newImg));
            photosPanel.remove(photoLabel);

            refreshPhotosPanel(username);
        }
    }

    /**
     * deletes a specific image from the filepath it is stored
     * @param filePath where the image is stored
     * @param username of the user who posted the image
     * @param imageName that is to be deleted
     */

    private void deleteImageInfoFromFile(String filePath, String username, String imageName) {
        long startTime = System.currentTimeMillis();
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
            BaseLogger.info().log(String.format("Image %s that %s posted is deleted from %s", imageName, username, filePath));
        } catch (IOException ex) {
            ex.printStackTrace();
            long endTime=  System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(ex.getMessage() + " took: " + duration + " ms.");
        }
    }
}