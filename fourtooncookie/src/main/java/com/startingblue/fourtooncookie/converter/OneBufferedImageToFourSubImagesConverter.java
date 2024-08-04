package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Converter
@Component
public class OneBufferedImageToFourSubImagesConverter {

    public List<BufferedImage> splitImageToSubImages(BufferedImage image) {
        if (image == null) {
            throw new ConversionException("BufferedImage attribute is null", null);
        }

        try {
            int width = image.getWidth();
            int height = image.getHeight();
            List<BufferedImage> subImages = new ArrayList<>();

            subImages.add(image.getSubimage(0, 0, width / 2, height / 2));                  // top-left
            subImages.add(image.getSubimage(width / 2, 0, width / 2, height / 2));          // top-right
            subImages.add(image.getSubimage(0, height / 2, width / 2, height / 2));         // bottom-left
            subImages.add(image.getSubimage(width / 2, height / 2, width / 2, height / 2)); // bottom-right

            return subImages;
        } catch (Exception e) {
            throw new ConversionException("Error splitting BufferedImage into subimages", e);
        }
    }

    public BufferedImage combineSubImagesToImage(List<BufferedImage> subImages) {
        if (subImages == null || subImages.size() != 4) {
            throw new ConversionException("List<BufferedImage> dbData is null or does not contain exactly 4 subimages", null);
        }

        int subImageWidth = subImages.get(0).getWidth();
        int subImageHeight = subImages.get(0).getHeight();
        int originalWidth = subImageWidth * 2;
        int originalHeight = subImageHeight * 2;
        try {
            BufferedImage reconstructedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = reconstructedImage.createGraphics();

            drawSubImages(g2d, subImages, subImageWidth, subImageHeight);
            g2d.dispose();
            return reconstructedImage;
        } catch (Exception e) {
            throw new ConversionException("Error reconstructing BufferedImage from subimages", e);
        }
    }

    private void drawSubImages(Graphics2D g2d, List<BufferedImage> subImages, int subImageWidth, int subImageHeight) {
        g2d.drawImage(subImages.get(0), 0, 0, null);                     // Top-left
        g2d.drawImage(subImages.get(1), subImageWidth, 0, null);         // Top-right
        g2d.drawImage(subImages.get(2), 0, subImageHeight, null);        // Bottom-left
        g2d.drawImage(subImages.get(3), subImageWidth, subImageHeight, null);  // Bottom-right
    }
}
