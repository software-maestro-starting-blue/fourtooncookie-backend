package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Converter
@Component
public class OneBufferedImageToFourSubImagesConverter implements AttributeConverter<BufferedImage, List<BufferedImage>> {

    @Override
    public List<BufferedImage> convertToDatabaseColumn(BufferedImage attribute) {
        return splitImageToSubImages(attribute);
    }

    @Override
    public BufferedImage convertToEntityAttribute(List<BufferedImage> dbData) {
        return combineSubImagesToImage(dbData);
    }

    public List<BufferedImage> splitImageToSubImages(BufferedImage image) {
        if (image == null) {
            throw new ConversionException("BufferedImage attribute is null", null);
        }

        try {
            return extractSubImages(image);
        } catch (Exception e) {
            throw new ConversionException("Error splitting BufferedImage into subimages", e);
        }
    }

    private List<BufferedImage> extractSubImages(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<BufferedImage> subImages = new ArrayList<>();

        subImages.add(image.getSubimage(0, 0, width / 2, height / 2));                  // top-left
        subImages.add(image.getSubimage(width / 2, 0, width / 2, height / 2));          // top-right
        subImages.add(image.getSubimage(0, height / 2, width / 2, height / 2));         // bottom-left
        subImages.add(image.getSubimage(width / 2, height / 2, width / 2, height / 2)); // bottom-right

        return subImages;
    }

    public BufferedImage combineSubImagesToImage(List<BufferedImage> subImages) {
        if (subImages == null || subImages.size() != 4) {
            throw new ConversionException("List<BufferedImage> dbData is null or does not contain exactly 4 subimages", null);
        }

        try {
            return reconstructImage(subImages);
        } catch (Exception e) {
            throw new ConversionException("Error reconstructing BufferedImage from subimages", e);
        }
    }

    private BufferedImage reconstructImage(List<BufferedImage> subImages) {
        int subImageWidth = subImages.get(0).getWidth();
        int subImageHeight = subImages.get(0).getHeight();
        int originalWidth = subImageWidth * 2;
        int originalHeight = subImageHeight * 2;

        BufferedImage reconstructedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = reconstructedImage.createGraphics();

        drawSubImages(reconstructedImage, g2d, subImages, subImageWidth, subImageHeight);

        g2d.dispose();

        return reconstructedImage;
    }

    private void drawSubImages(BufferedImage reconstructedImage, Graphics2D g2d, List<BufferedImage> subImages, int subImageWidth, int subImageHeight) {
        g2d.drawImage(subImages.get(0), 0, 0, null);                     // Top-left
        g2d.drawImage(subImages.get(1), subImageWidth, 0, null);         // Top-right
        g2d.drawImage(subImages.get(2), 0, subImageHeight, null);        // Bottom-left
        g2d.drawImage(subImages.get(3), subImageWidth, subImageHeight, null);  // Bottom-right
    }
}
