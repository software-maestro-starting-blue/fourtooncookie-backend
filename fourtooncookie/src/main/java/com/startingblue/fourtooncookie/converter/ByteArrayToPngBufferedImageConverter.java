package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class ByteArrayToPngBufferedImageConverter {

    public BufferedImage convertByteArrayToBufferedImage(byte[] attribute) {
        if (attribute == null) {
            throw new ConversionException("Byte array attribute is null", null);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(attribute);
        try {
            return ImageIO.read(bais);
        } catch (Exception e) {
            throw new ConversionException("Error converting byte array to BufferedImage", e);
        }
    }

    public byte[] convertBufferedImageToByteArray(BufferedImage dbData) {
        if (dbData == null) {
            throw new ConversionException("BufferedImage attribute is null", null);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(dbData, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ConversionException("Error converting BufferedImage to byte array", e);
        }
    }
}
