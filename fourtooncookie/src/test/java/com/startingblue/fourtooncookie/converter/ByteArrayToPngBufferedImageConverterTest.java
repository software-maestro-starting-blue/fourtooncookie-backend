package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ByteArrayToPngBufferedImageConverterTest {

    private final ByteArrayToPngBufferedImageConverter converter = new ByteArrayToPngBufferedImageConverter();

    @Test
    @DisplayName("byte[]를 BufferedImage로 변환 (정상 케이스)")
    void testConvertToDatabaseColumn() throws Exception {
        BufferedImage inputImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        byte[] byteArray = convertBufferedImageToByteArray(inputImage);

        BufferedImage outputImage = converter.convertByteArrayToBufferedImage(byteArray);

        assertThat(outputImage).isNotNull();
        assertThat(outputImage.getWidth()).isEqualTo(inputImage.getWidth());
        assertThat(outputImage.getHeight()).isEqualTo(inputImage.getHeight());
    }

    @Test
    @DisplayName("BufferedImage를 byte[]로 변환 (정상 케이스)")
    void testConvertToEntityAttribute() throws Exception {
        BufferedImage inputImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        byte[] byteArray = converter.convertBufferedImageToByteArray(inputImage);

        assertThat(byteArray).isNotEmpty();

        BufferedImage outputImage = convertByteArrayToBufferedImage(byteArray);

        assertThat(outputImage).isNotNull();
        assertThat(outputImage.getWidth()).isEqualTo(inputImage.getWidth());
        assertThat(outputImage.getHeight()).isEqualTo(inputImage.getHeight());
    }

    @Test
    @DisplayName("byte[]가 null일 때 예외 발생")
    void testConvertByteArrayToBufferedImageNull() {
        assertThatThrownBy(() -> {
            converter.convertByteArrayToBufferedImage(null);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("Byte array attribute is null");
    }

    @Test
    @DisplayName("BufferedImage가 null일 때 예외 발생")
    void testConvertByteArrayToBufferedImageWithNull() {
        assertThatThrownBy(() -> {
            converter.convertBufferedImageToByteArray(null);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("BufferedImage attribute is null");
    }

    private byte[] convertBufferedImageToByteArray(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private BufferedImage convertByteArrayToBufferedImage(byte[] byteArray) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        return ImageIO.read(bais);
    }
}
