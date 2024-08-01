package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
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
        if (attribute == null) {
            throw new ConversionException("Byte array attribute is null", null);
        }
        return convertByteArrayToBufferedImage(attribute);
    }

    @Override
    public byte[] convertToEntityAttribute(BufferedImage dbData) {
        if (dbData == null) {
            throw new ConversionException("BufferedImage attribute is null", null);
        }
        return convertBufferedImageToByteArray(dbData);
    }

    private BufferedImage convertByteArrayToBufferedImage(byte[] attribute) {
        ByteArrayInputStream bais = new ByteArrayInputStream(attribute);
        try {
            return ImageIO.read(bais);
        } catch (Exception e) {
            throw new ConversionException("Error converting byte array to BufferedImage", e);
        }
    }

    private byte[] convertBufferedImageToByteArray(BufferedImage dbData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(dbData, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ConversionException("Error converting BufferedImage to byte array", e);
        }
    }
}
