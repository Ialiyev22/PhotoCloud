package Operations;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class test {

    public static void main(String[] args) {
        try {
            ImageMatrix im = ImageSecretary.readResourceImage("cat", "jpg");
            ImageSecretary.writeFilteredImageToResources(new ImageMatrix(im.blurImage(50)), "doggray", "jpg");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
