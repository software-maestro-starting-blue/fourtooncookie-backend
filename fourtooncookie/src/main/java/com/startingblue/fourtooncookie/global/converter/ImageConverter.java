package com.startingblue.fourtooncookie.global.converter;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ImageConverter {

    public byte[] mergeImagesIntoGrid(List<byte[]> images) throws IOException {
        validateImageCount(images);

        BufferedImage[] bufferedImages = convertToBufferedImages(images);
        BufferedImage combinedImage = createCombinedImage(bufferedImages);

        return convertBufferedImageToByteArray(combinedImage);
    }

    private void validateImageCount(List<byte[]> images) {
        if (images.size() < 4) {
            throw new IllegalArgumentException("4개의 이미지가 필요합니다.");
        }
    }

    private BufferedImage[] convertToBufferedImages(List<byte[]> images) throws IOException {
        BufferedImage[] bufferedImages = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(images.get(i));
            bufferedImages[i] = ImageIO.read(bais);
        }
        return bufferedImages;
    }

    private BufferedImage createCombinedImage(BufferedImage[] images) {
        int width = images[0].getWidth();
        int height = images[0].getHeight();

        BufferedImage combinedImage = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        g.drawImage(images[0], 0, 0, null);
        g.drawImage(images[1], width, 0, null);
        g.drawImage(images[2], 0, height, null);
        g.drawImage(images[3], width, height, null);
        g.dispose();

        return combinedImage;
    }

    private byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
