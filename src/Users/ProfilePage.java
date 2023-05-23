package Users;

import Logs.BaseLogger;
import Operations.ImageMatrix;
import Operations.ImageSecretary;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class ProfilePage extends JFrame {

    private String username, name, surname, age, email, password;
    private JTextField nameField, surnameField, ageField, emailField, passwordField;
    private JLabel usernameLabel;
    private JPanel headerPanel, userInfoPanel, photosPanel, inputPanel;
    private JButton uploadButton, discoverButton, editButton, saveButton, changePasswordButton, changeProfilePictureButton;

    /**
     * displays a user's profile page
     * @param username whose profile page is to be showed
     */

    public ProfilePage(String username) {
        super("Profile Page");
        this.username = username;

        // Set the size and layout of the frame
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("src\\images\\PhotoCloud.png").getImage());

        // Initialize the header panel
        headerPanel = new JPanel();

        // Initialize the upload button and add it to the header panel
        uploadButton = new JButton("Upload");
        discoverButton = new JButton("Back to Discover");

        // Initialize the user info panel
        userInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Read the user's personal info from the file
        String[] userInfo = readUserInfo(username);

        usernameLabel = new JLabel("Username: " + username);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(userInfo[0]);
        nameField.setEditable(false);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameField = new JTextField(userInfo[1]);
        surnameField.setEditable(false);

        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField(userInfo[2]);
        ageField.setEditable(false);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(userInfo[5]);
        emailField.setEditable(false);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField("fiwei!fpewjf");
        passwordField.setEditable(false);

        editButton = new JButton("Edit");
        saveButton = new JButton("Save Changes");
        changePasswordButton = new JButton("Change Password");
        changeProfilePictureButton = new JButton("Change Profile Picture");
        saveButton.setEnabled(false);

        // Add the labels to the user info panel
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createVerticalStrut(10));

        userInfoPanel.add(nameLabel);
        userInfoPanel.add(nameField);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(surnameLabel);
        userInfoPanel.add(surnameField);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(ageLabel);
        userInfoPanel.add(ageField);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(emailLabel);
        userInfoPanel.add(emailField);
        userInfoPanel.add(Box.createVerticalStrut(10));
        userInfoPanel.add(passwordLabel);
        userInfoPanel.add(passwordField);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(editButton);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(saveButton);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(changePasswordButton);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(changeProfilePictureButton);
        userInfoPanel.add(Box.createVerticalStrut(5));

        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(uploadButton, BorderLayout.EAST);
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
            photosPanel.add(photoLabel);
            // Add mouse listener to the photo label


            photoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Delete the image info from Images.txt
                    photoLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            handlePhotoClick(username, photo);
                        }
                    });

                    photosPanel.add(photoLabel);
                }
            });
        }

        // Read the user's profile photo from the file
        String profilePhoto = userInfo[4];

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

        password = userInfo[6];

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.setEditable(true);
                nameField.setEditable(false);
                surnameField.setEditable(false);
                ageField.setEditable(false);
                emailField.setEditable(false);
                saveButton.setEnabled(true);
                editButton.setEnabled(false);
                password = passwordField.getText();
                while (password.length() < 8 || !password.matches(".*[!@#$%^&*()_+=?/\\|{}\\[\\]~-].*")) {
                    JOptionPane.showMessageDialog(ProfilePage.this, "Password must be at least 8 characters long and contain at least one special character.");
                    return;
                }
            }
        });

        changeProfilePictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));


                // Show the file chooser dialog
                int result = fileChooser.showOpenDialog(ProfilePage.this);

                // Process the selected file
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    // Update the profile photo label with the new image
                    ImageIcon newProfileImage = new ImageIcon(selectedFile.getPath());
                    Image scaledImage = newProfileImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    profilePhotoLabel.setIcon(new ImageIcon(scaledImage));

                    // Save the new profile photo file and update the file path in the user's data
                    saveNewProfilePhoto(selectedFile);
                }
            }
        });



        discoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DiscoverPage(username);
                BaseLogger.info().log(String.format("User %s is directed to their Discover Page", username));
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setEditable(true);
                surnameField.setEditable(true);
                ageField.setEditable(true);
                emailField.setEditable(true);
                saveButton.setEnabled(true);
            }
        });

        // Add ActionListener for Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update personal info
                name = nameField.getText();
                surname = surnameField.getText();
                age = ageField.getText();
                email = emailField.getText();

                while (name.isEmpty() || surname.isEmpty() || email.isEmpty() || age.isEmpty()|| password.isEmpty()) {
                    JOptionPane.showMessageDialog(ProfilePage.this, "Please fill in all fields.");
                    return;
                }

                // Save changes
                savePersonalInfo();

                // Disable editing and save button
                nameField.setEditable(false);
                surnameField.setEditable(false);
                ageField.setEditable(false);
                passwordField.setEditable(false);
                emailField.setEditable(false);
                saveButton.setEnabled(false);
                editButton.setEnabled(true);
            }
        });

        // Add action listener to the upload button
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(ProfilePage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String imageName = selectedFile.getName();
                    String description = "";
                    dispose();

                    int option = JOptionPane.showConfirmDialog(ProfilePage.this, "Do you want to edit the image?", "Edit Image", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        editImage(username, selectedFile);
                    }
                    else {
                        description = JOptionPane.showInputDialog(ProfilePage.this, "Enter a description for the photo:", "");
                        writeImageName(username, imageName, description);
                    }

                    new ProfilePage(username);
                    int option2 = JOptionPane.showConfirmDialog(ProfilePage.this, "Do you want to make the image public to other users?", "Make Image Public", JOptionPane.YES_NO_OPTION);
                    if (option2 == JOptionPane.YES_OPTION) {
                    BaseLogger.info().log(String.format("Image %s is added to %s's Profile Page Publicly", imageName, username));
                    writePublicImageName(username, imageName, description);
                }
                else {
                    BaseLogger.info().log(String.format("Image %s is added to %s's Profile Page Privately", imageName, username));
                }
                }
                photosPanel.revalidate();
                photosPanel.repaint();
            }

        });

        // Display the frame
        setVisible(true);
    }

    /**
     * hashes a password with SHA-256 algorithm
     * @param password to be hashed
     * @return hashed password
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
            BaseLogger.error().log(e.getMessage() + ". took: " + duration + " ms.");
            return null;
        }
    }

    /**
     * saves the new profile photo of the user to the corresponding directory
     * @param selectedFile new profile photo's filepath
     */

    private void saveNewProfilePhoto(File selectedFile) {
        String newProfilePhotoPath = selectedFile.getName();

        try {
            // Copy the selected file to the new profile photo path
            InputStream inputStream = new FileInputStream(selectedFile);
            OutputStream outputStream = new FileOutputStream(newProfilePhotoPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            // Update the user's data with the new profile photo path
            updateProfilePhotoPath(newProfilePhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param newProfilePhotoPath
     */

    private void updateProfilePhotoPath(String newProfilePhotoPath) {
        // Update the user's data file with the new profile photo path
        try {
            File userDataFile = new File("src\\Users\\Passwords.txt");
            File tempFile = new File(userDataFile.getAbsolutePath() + ".tmp");
            BufferedReader reader = new BufferedReader(new FileReader(userDataFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[4].equals(username)) {
                    fields[6] = newProfilePhotoPath;
                    line = String.join(",", fields);
                }
                writer.write(line + System.lineSeparator());
            }

            reader.close();
            writer.close();

            // Replace the user's data file with the updated file
            userDataFile.delete();
            tempFile.renameTo(userDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * saves the personal data after changes are made
     */

    private void savePersonalInfo() {
        try {
            File file = new File("src\\Users\\Passwords.txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));


            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens[4].equals(username)) {
                    line = name + "," + surname + "," +email + ","+ age + "," + username + "," + hashPassword(password) + "," + readUserInfo(username)[4] + "," + readUserInfo(username)[3];
                }
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();
            passwordField = new JPasswordField("wdfeqewfwe");
            // Rename the temp file to the original file
            file.delete();
            tempFile.renameTo(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gives user's datas in a String array to be showed in the personal info panel
     * @param username of the user whose data is read
     * @return String array made up from user's datas
     */
    private String[] readUserInfo(String username) {
        String[] userInfo = new String[7];
        long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src\\Users\\Passwords.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[4].equals(username)) {
                    userInfo[0] = fields[0];
                    userInfo[1] = fields[1];
                    userInfo[2] = fields[3];
                    userInfo[3] = fields[7];
                    userInfo[4] = fields[6];
                    userInfo[5] = fields[2];
                    userInfo[6] = fields[5];
                    break;
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(ex.getMessage() + ". took: " + duration + " ms.");
        }
        return userInfo;
    }

    /**
     * handles the processes each time an image is clicked on
     * @param username whose photo is clicked on
     * @param photo to delete or edit when clicked on
     */

    private void handlePhotoClick(String username, String photo) {
        String[] options = {"Delete", "Edit"};
        int selectedOption = JOptionPane.showOptionDialog(ProfilePage.this, "Do you want to delete or edit the image", "Delete or Edit", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selectedOption == 0) {
            deleteImageInfoFromFile("src\\Users\\Images.txt", username, photo);
            try {
                File inputFile = new File("src\\Users\\PublicImages.txt");;
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
                }}
            catch (IOException e) {
                e.printStackTrace();
            }
            ImageIcon img = new ImageIcon("src\\images\\" + photo);
            Image Img = img.getImage();
            Image newImg = Img.getScaledInstance(150,150, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(newImg));
            photosPanel.remove(photoLabel);
        }
        else if(selectedOption == 1) {
            editImage(username, new File("src\\images\\" + photo));
            String description = "";
            int option2 = JOptionPane.showConfirmDialog(ProfilePage.this, "Do you want to make the image public to other users?", "Make Image Public", JOptionPane.YES_NO_OPTION);
            if (option2 == JOptionPane.YES_OPTION) {
                BaseLogger.info().log(String.format("Image %s is added to %s's Profile Page Publicly", photo, username));
                writePublicImageName(username, photo, description);
            }
            else {
                BaseLogger.info().log(String.format("Image %s is added to %s's Profile Page Privately", photo, username));
            }}
        refreshPhotosPanel(username);}

    /**
     * refreshes a user's photos panel each time an image is deleted or edit
     * @param username whose photos panel to be refreshed
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
     * writes the username, image's name and description in the corresponding txt file if the user made the photo public
     * @param user who shares the picture
     * @param name of edited or original photo
     * @param description of image
     */

    public void writePublicImageName(String user, String name, String description) {
        long startTime = System.currentTimeMillis();
        try {
            FileWriter f = new FileWriter("src\\Users\\PublicImages.txt", true);
            f.write(user + "," + name +"," + 0 + "," + 0 + "," + description + ","+ readUserInfo(username)[3] + "\n");
            f.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(e.getMessage() + ". took: " + duration + " ms.");
        }
    }

    /**
     * writes the username, image's name and description in the txt file where all posted images' data is saved
     * @param user who shares the picture
     * @param name of edited or original photo
     * @param description of image
     */

    public void writeImageName(String user, String name, String description) {
        long startTime = System.currentTimeMillis();
        try {
            FileWriter f = new FileWriter("src\\Users\\Images.txt", true);
            f.write(user + "," + name +"," + 0 + "," + 0 + "," + description  + ","+ readUserInfo(username)[3] + "\n");
            f.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            BaseLogger.error().log(e.getMessage() + ". took: " + duration + " ms.");
        }
    }


    /**
     * gives user's all photos' paths in a string array to show them in the profile page
     * @param user whose photos are shown in the profile page
     * @return String array consisting of paths of the user's all photos
     */

    public String[] getPhotos(String user) {
            long startTime = System.currentTimeMillis();
            String[] profile = new String[50];
            try {
                File f = new File("src\\Users\\Images.txt");
                Scanner scan = new Scanner(f);
                int i =0;
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
                BaseLogger.error().log(e.getMessage() + ". took: " + duration + " ms.");
            }
            return profile;
    }

    /**
     * takes information related to a photo, and matches it to the lines in the filepath, and if finds a match, updates the file by excluding photo to be deleted
     * @param filePath that photos are stored in
     * @param username whose photo is to be deleted
     * @param imageName of the image to be deleted
     */

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
     * takes a user and offers editing processes according to their tier
     * @param user whose photo is to be edited
     * @param file filepath of the image to be edited
     */

    public void editImage(String user, File file) {
        String[] options;
        String[] userInfo = readUserInfo(username);
        String description = "";
        String imageName = file.getName();

        if (userInfo[3].equals("Free")) {
            options = new String[]{"Blur", "Sharpen"};
        } else if (userInfo[3].equals("Hobbyist")) {
            options = new String[]{"Blur", "Sharpen", "Brightness", "Contrast"};
        }
        else if (userInfo[3].equals("Professional") || userInfo[3].equals("Administrator") ) {
            options = new String[]{"Blur", "Sharpen", "Brightness", "Contrast", "GrayScale", "EdgeDetection"};
        } else {
            throw new IllegalArgumentException("Invalid user tier: " + userInfo[3]);
        }

        int selectedOption = JOptionPane.showOptionDialog(ProfilePage.this, "Choose an editing process", "Edit Image", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedOption == 0 || selectedOption == 1 || selectedOption == 2) {
            // Prompt user to enter filter intensity
            String intensityString = JOptionPane.showInputDialog(ProfilePage.this, "Enter the filter intensity (0-100):", "50");
            int intensity = Integer.parseInt(intensityString);
            if (intensity < 0 || intensity > 100) {
                JOptionPane.showMessageDialog(ProfilePage.this, "Please enter a valid intensity value between 0 and 100.");
                return;
            }

            try {
                BufferedImage image = ImageIO.read(file);
                BufferedImage editedImage = image;
                switch (selectedOption) {
                    case 0:
                        // Blur image
                        BaseLogger.info().log(String.format("Blurring filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_blur";
                        ImageMatrix im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.blurImage(intensity)), imageName, "jpg");
                        break;
                    case 1:
                        // Sharpen image
                        BaseLogger.info().log(String.format("Sharpening filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_sharpen";
                        im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.sharpenImage(intensity)), imageName, "jpg");
                        break;
                    case 2:
                        // Brighten image
                        BaseLogger.info().log(String.format("Brightness Changing filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_brightness";
                        im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.changeBrightness(intensity)), imageName, "jpg");
                        break;
                    case 3:
                        // Contrast image
                        BaseLogger.info().log(String.format("Contrast Changing filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_contrast";
                        im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.changeContrast(intensity)), imageName, "jpg");
                        break;
                    case 5:
                        // Grayscale image
                        BaseLogger.info().log(String.format("Gray Scaling filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_grayScale";
                        im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.grayScaleImage()), imageName, "jpg");
                        break;
                    case 6:
                        // Grayscale image
                        BaseLogger.info().log(String.format("Edge Detecting filter applied to image %s.", imageName));
                        imageName = imageName.substring(0, imageName.lastIndexOf('.')) + "_edgeDetect";
                        im = new ImageMatrix(editedImage);
                        ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.edgeDetectImage()), imageName, "jpg");
                        break;
                }
                description = JOptionPane.showInputDialog(ProfilePage.this, "Enter a description for the photo:", "");
                writeImageName(username, imageName + ".jpg", description);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}