package com.startingblue.fourtooncookie.converter;

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
        int width = attribute.getWidth();
        int height = attribute.getHeight();
        List<BufferedImage> subImages = new ArrayList<>();

        subImages.add(attribute.getSubimage(0, 0, width / 2, height / 2)); // top-left
        subImages.add(attribute.getSubimage(width / 2, 0, width / 2, height / 2)); // top-right
        subImages.add(attribute.getSubimage(0, height / 2, width / 2, height / 2)); // bottom-left
        subImages.add(attribute.getSubimage(width / 2, height / 2, width / 2, height / 2));

        return subImages;
    }

    @Override
    public BufferedImage convertToEntityAttribute(List<BufferedImage> dbData) {
        int subImageWidth = dbData.get(0).getWidth();
        int subImageHeight = dbData.get(0).getHeight();
        int originalWidth = subImageWidth * 2;
        int originalHeight = subImageHeight * 2;

        BufferedImage reconstructedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = reconstructedImage.createGraphics();

        // Top-left
        g2d.drawImage(dbData.get(0), 0, 0, null);

        // Top-right
        g2d.drawImage(dbData.get(1), subImageWidth, 0, null);

        // Bottom-left
        g2d.drawImage(dbData.get(2), 0, subImageHeight, null);

        // Bottom-right
        g2d.drawImage(dbData.get(3), subImageWidth, subImageHeight, null);

        g2d.dispose();

        return reconstructedImage;
    }
}
