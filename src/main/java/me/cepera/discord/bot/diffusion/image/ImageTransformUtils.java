package me.cepera.discord.bot.diffusion.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTransformUtils {

    public static BufferedImage readImage(byte[] imageContent) {
        try {
            return ImageIO.read(new ByteArrayInputStream(imageContent));
        }catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static byte[] writeImage(BufferedImage image) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bos);
            return bos.toByteArray();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage setMaxDimension(BufferedImage image, int maxDimension) {
        int targetWidth;
        int targetHeight;
        if(image.getHeight() == image.getWidth()) {
            targetHeight = maxDimension;
            targetWidth = maxDimension;
        }else if(image.getHeight() > image.getWidth()) {
            targetHeight = maxDimension;
            targetWidth = (int) (1.0D * image.getWidth() * targetHeight / image.getHeight());
        }else {
            targetWidth = maxDimension;
            targetHeight = (int) (1.0D * image.getHeight() * targetWidth / image.getWidth());
        }

        Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

        return resizedImage;
    }

    public static BufferedImage expandImage(BufferedImage image, int newWidth, int newHeight, ImageShift sourceImageShift) {

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        int x = sourceImageShift.x(image.getWidth(), newWidth, image.getHeight(), newHeight);
        int y = sourceImageShift.y(image.getWidth(), newWidth, image.getHeight(), newHeight);

        resizedImage.getGraphics().drawImage(image, x, y, null);

        return resizedImage;
    }

    public static byte[] transformImageByDefault(byte[] imageContent, ImageShift sourceImageShift) {
        return writeImage(expandImage(setMaxDimension(readImage(imageContent), 512), 768, 768, sourceImageShift));
    }

}
