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

    public static byte[] transformImageToSquare(byte[] imageContent, int sourceSice, int targetSize, ImageShift sourceImageShift) {
        return writeImage(expandImage(setMaxDimension(readImage(imageContent), sourceSice), targetSize, targetSize, sourceImageShift));
    }

    public static byte[] transformImageMaxDimension(byte[] imageContent, int maxDimension) {
        return writeImage(setMaxDimension(readImage(imageContent), maxDimension));
    }

    public static int mediumARGB(byte[] imageContent) {
        BufferedImage image = readImage(imageContent);
        int width = image.getWidth();
        int height = image.getHeight();
        int pixels = 0;
        int sr = 0;
        int sg = 0;
        int sb = 0;
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                int rgb = image.getRGB(x, y);
                if(rgb == 0) {
                    continue;
                }
                pixels++;
                sr += (rgb >>> 16) & 0x000000ff;
                sg += (rgb >>> 8) & 0x000000ff;
                sb += rgb & 0x000000ff;
            }
        }
        if(pixels == 0) {
            return 0;
        }
        return 0xff000000 | (sr/pixels << 16) | (sg/pixels << 8) | sb/pixels;
    }

}
