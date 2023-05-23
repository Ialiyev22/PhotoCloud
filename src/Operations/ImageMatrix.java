package Operations;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.UUID;

/**
 * ImageMatrix is used to represent images.
 *
 * @author osman.yasal
 *
 */
public class ImageMatrix {

    private String id;
    private int[][] img;

    private int width;
    private int height;

    /**
     * Builds up ImageMatrix from BufferedImage
     *
     * @see BufferedImage
     */
    public ImageMatrix(BufferedImage image) {
        this.id = UUID.randomUUID().toString();
        this.img = convertImageToPixelArray(image);
        this.width = img.length;
        this.height = img[0].length;
    }

    public ImageMatrix(int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.img = generateEmptyImageArray(width, height);
        this.width = img.length;
        this.height = img[0].length;

    }

    private int[][] generateEmptyImageArray(int width, int height) {
        return new int[width][height];
    }

    private int[][] convertImageToPixelArray(BufferedImage image) {
        int[][] im = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++) {
                im[i][j] = image.getRGB(i, j);
            }
        return im;
    }

    public BufferedImage getBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, img[i][j]);
            }
        }
        return bufferedImage;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRGB(int x, int y) {
        return img[x][y];
    }

    public int setRGB(int x, int y, int rgb) {
        img[x][y] = rgb;
        return rgb;
    }

    public int setARGB(int x, int y, int argb) {
        img[x][y] = argb;
        return argb;
    }

    public int getRed(int x, int y) {
        return (img[x][y] >> 16) & 0xFF;
    }

    public int getGreen(int x, int y) {
        return (img[x][y] >> 8) & 0xFF;
    }

    public int getBlue(int x, int y) {
        return img[x][y] & 0xFF;
    }

    /**
     * gets alpha value of a pixel for brigtness changing
     * @param x
     * @param y
     * @return alpha value of a pixel
     */

    public int getAlpha(int x, int y) {
        return img[x][y] >> 24 & 0xFF;
    }


    public static int convertRGB(int red, int green, int blue) {
        return (red << 16 | green << 8 | blue);
    }
    public static int convertARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24 | red << 16 | green << 8 | blue);
    }

    /**
     * takes a specific amount of blur, and adjusts image's blur accordingly
     * @param amount of blur
     * @return blurred BufferedImage
     */


    public BufferedImage blurImage(int amount) {
        BufferedImage blurredImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        if (amount <=25) {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int Red = 0, Green = 0, Blue = 0, Alpha = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color color = new Color(getRGB(x + i, y + j));
                        Red += color.getRed();
                        Green += color.getGreen();
                        Blue += color.getBlue();
                        Alpha += color.getAlpha();
                    }
                }
                Red /= 9;
                Blue /= 9;
                Green /= 9;
                int ARGB = convertARGB(Alpha,Red, Green, Blue);
                blurredImage.setRGB( x,  y, ARGB);
            }
        }
        }

        if (amount >25 && amount<=50) {
            for (int x = 2; x < width - 2; x++) {
                for (int y = 2; y < height - 2; y++) {
                    int Red = 0, Green = 0, Blue = 0, Alpha = 0;
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            Color color = new Color(getRGB(x + i, y + j));
                            Red += color.getRed();
                            Green += color.getGreen();
                            Blue += color.getBlue();
                            Alpha += color.getAlpha();
                        }
                    }
                    Red /= 25;
                    Blue /= 25;
                    Green /= 25;
                    int ARGB = convertARGB(Alpha,Red, Green, Blue);
                    blurredImage.setRGB( x,  y, ARGB);
                }
            }
        }

        if (amount >50 && amount<=75) {
            for (int x = 3; x < width - 3; x++) {
                for (int y = 3; y < height - 3; y++) {
                    int Red = 0, Green = 0, Blue = 0, Alpha = 0;
                    for (int i = -3; i <= 3; i++) {
                        for (int j = -3; j <= 3; j++) {
                            Color color = new Color(getRGB(x + i, y + j));
                            Red += color.getRed();
                            Green += color.getGreen();
                            Blue += color.getBlue();
                            Alpha += color.getAlpha();
                        }
                    }
                    Red /= 49;
                    Blue /= 49;
                    Green /= 49;
                    int ARGB = convertARGB(Alpha,Red, Green, Blue);
                    blurredImage.setRGB( x,  y, ARGB);
                }
            }
        }

        if (amount >75) {
            for (int x = 4; x < width - 4; x++) {
                for (int y = 4; y < height - 4; y++) {
                    int Red = 0, Green = 0, Blue = 0, Alpha = 0;
                    for (int i = -4; i <= 4; i++) {
                        for (int j = -4; j <= 4; j++) {
                            Color color = new Color(getRGB(x + i, y + j));
                            Red += color.getRed();
                            Green += color.getGreen();
                            Blue += color.getBlue();
                            Alpha += color.getAlpha();
                        }
                    }
                    Red /= 81;
                    Blue /= 81;
                    Green /= 81;
                    int ARGB = convertARGB(Alpha,Red, Green, Blue);
                    blurredImage.setRGB( x,  y, ARGB);
                }
            }
        }
        return blurredImage;
    }

    /**
     * takes a specific amount of sharpness, and adjusts image's sharpness accordingly
     * @param amount of sharpening
     * @return
     */

    public BufferedImage sharpenImage(int amount) {
        BufferedImage blurredImage = blurImage(amount);
        int[][] blurredMatrix = convertImageToPixelArray(blurredImage);
        BufferedImage sharpenedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int [][] detailsMatrix = generateEmptyImageArray(getWidth(), getHeight());
        int[][] sharpenedMatrix = generateEmptyImageArray(getWidth(), getHeight());

        for (int x = 0; x<width; x++) {
            for (int y =0; y<width; y++) {
                detailsMatrix[x][y] = img[x][y];
            }
        }

        if (amount <=25) {
            for (int x = 1; x<detailsMatrix.length-1; x++) {
                for (int y = 1; y<detailsMatrix[x].length-1; y++) {
                    detailsMatrix[x][y] = img[x][y] - blurredMatrix[x-1][y-1];
                }
            }
        }

        if (amount >25 && amount<=50) {
            for (int x = 2; x<detailsMatrix.length-2; x++) {
                for (int y = 2; y<detailsMatrix[x].length-2; y++) {
                    detailsMatrix[x][y] = img[x][y] - blurredMatrix[x-2][y-2];
                }
            }
        }

        if (amount >50 && amount<=75) {
            for (int x = 3 ; x<detailsMatrix.length-3; x++) {
                for (int y = 3; y<detailsMatrix[x].length-3; y++) {
                    detailsMatrix[x][y] = img[x][y] - blurredMatrix[x-3][y-3];
                }
            }
        }

        if (amount >75) {
            for (int x = 4; x<detailsMatrix.length-4; x++) {
                for (int y = 4; y<detailsMatrix[x].length-4; y++) {
                    detailsMatrix[x][y] = img[x][y] - blurredMatrix[x-4][y-4];
                }
            }
        }


        for (int x = 0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                sharpenedMatrix[x][y] = img[x][y] + detailsMatrix[x][y];
            }
        }


        for (int x=0; x<width; x++) {
            for (int y =0; y<height; y++) {
                int Red = 0, Green = 0, Blue = 0;
                Color color = new Color(getRGB(x, y));
                Red += color.getRed();
                Green += color.getGreen();
                Blue += color.getBlue();
                int RGB = convertRGB(Red, Green, Blue);
                sharpenedImage.setRGB(x, y, RGB);
            }
        }
        return sharpenedImage;
    }

    /**
     * implements grayscaling on image
     * @return grayscaled version of image
     */

    public BufferedImage grayScaleImage() {

        BufferedImage grayScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int Red = 0, Green = 0, Blue = 0;
                Color color = new Color(getRGB(x,y));
                int avg = (color.getRed() + color.getBlue() + color.getGreen())/3;
                int RGB = convertRGB(avg, avg, avg);
                grayScaledImage.setRGB(x, y, RGB);
            }
        }

        return grayScaledImage;
    }

    /**
     * takes an amount, and changes image's brightness accordingly
     * @param amount of brightness
     * @return image with changed brightness
     */

    public BufferedImage changeBrightness(int amount) {

        BufferedImage brightenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                Color color = new Color(getRGB(x,y));
                int Red = color.getRed(), Green = color.getGreen(), Blue = color.getBlue();
                Red += amount; Green += amount; Blue += amount;
                Red = Math.min(Math.max(Red, 0), 255);
                Green = Math.min(Math.max(Green, 0), 255);
                Blue = Math.min(Math.max(Blue, 0), 255);
                int RGB = convertRGB(Red, Green, Blue);
                brightenedImage.setRGB(x,y, RGB);
            }
        }

        return brightenedImage;
    }

    /**
     * takes a specific amount, and changes image's contrast accordingly
     * @param amount of contrast to add
     * @return image with changed contrast
     */

    public BufferedImage changeContrast(int amount) {

        BufferedImage contrastedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int x = 0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                Color color = new Color(getRGB(x,y));
                int Red = color.getRed(), Green = color.getGreen(), Blue = color.getBlue();
                double luminance = (0.2126 * Red + 0.7152 * Green + 0.0722 * Blue) / 255.0;

                // Apply the contrast adjustment
                luminance = (luminance - 0.5) * amount + 0.5;

                // Convert the luminance back to RGB values
                Red = (int) (luminance * 255.0);
                Green = (int) (luminance * 255.0);
                Blue = (int) (luminance * 255.0);

                // Clamp the values to the range [0, 255]
                Red = Math.min(Math.max(Red, 0), 255);
                Green = Math.min(Math.max(Green, 0), 255);
                Blue = Math.min(Math.max(Blue, 0), 255);

                int rgb = convertRGB(Red, Green, Blue);
                contrastedImage.setRGB(x,y,rgb);

            }
        }

        return contrastedImage;
    }

    /**
     * implements edgedetection filter on image using sobel operator
     * @return edgedetected image
     */

    public BufferedImage edgeDetectImage() {

        BufferedImage blurredImage = blurImage(30);
        BufferedImage grayscaleImage = new ImageMatrix(blurredImage).grayScaleImage();

        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();

        // Create a new image to store the edge detection result
        BufferedImage edgeImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Define the Sobel operator kernels
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        // Apply the Sobel operator to each pixel in the image
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int pixelX = 0;
                int pixelY = 0;

                // Apply the Sobel operator kernels to the pixel and its neighbors
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int rgb = grayscaleImage.getRGB(x + i, y + j);
                        int gray = (int) ((rgb & 0xff) * 0.299 + ((rgb >> 8) & 0xff) * 0.587 + ((rgb >> 16) & 0xff) * 0.114); // Convert RGB to grayscale
                        pixelX += gray * sobelX[j + 1][i + 1];
                        pixelY += gray * sobelY[j + 1][i + 1];
                    }
                }

                // Compute the gradient magnitude and set the pixel value in the edge image
                int magnitude = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY);
                edgeImage.setRGB(x, y, (magnitude << 16) | (magnitude << 8) | magnitude);
            }
        }

        return edgeImage;
    }

}


