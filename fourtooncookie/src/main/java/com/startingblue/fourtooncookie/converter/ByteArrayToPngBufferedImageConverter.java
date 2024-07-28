package com.startingblue.fourtooncookie.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Converter
@Component
public class ByteArrayToPngBufferedImageConverter implements AttributeConverter<byte[], BufferedImage> {


    @Override
    public BufferedImage convertToDatabaseColumn(byte[] attribute) {
        ByteArrayInputStream bais = new ByteArrayInputStream(attribute);
        try {
            return ImageIO.read(bais);
        } catch (Exception e) {
            throw new RuntimeException("Error converting byte array to BufferedImage", e);
        }
    }

    @Override
    public byte[] convertToEntityAttribute(BufferedImage dbData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(dbData, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error converting BufferedImage to byte array", e);
        }
    }
}
