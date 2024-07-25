package com.startingblue.fourtooncookie.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OneBufferedImageToFourSubImagesConverterTest {

    @Autowired
    OneBufferedImageToFourSubImagesConverter oneBufferedImageToFourSubImagesConverter;

    @Test
    @DisplayName("하나의 이미지가 4개로 잘 나뉘는 가를 확인")
    void testConvertToDatabaseColumn() throws Exception {
        System.setProperty("java.awt.headless", "false");
        String imageUrl = "https://cdn.pixabay.com/photo/2016/07/11/17/45/abstract-1510190_1280.png";

        BufferedImage image = ImageIO.read(new URL(imageUrl));

        List<BufferedImage> subImages = oneBufferedImageToFourSubImagesConverter.convertToDatabaseColumn(image);

        subImages.forEach(subImage ->
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(new Dimension(subImage.getWidth(), subImage.getHeight()));

                // ImageIcon을 사용하여 이미지를 JLabel에 설정
                ImageIcon icon = new ImageIcon(subImage);
                JLabel label = new JLabel(icon);

                // JLabel을 JFrame에 추가
                frame.add(label);

                // JFrame을 화면에 표시
                frame.pack();
                frame.setLocationRelativeTo(null); // 화면 중앙에 표시
                frame.setVisible(true);
            })
        );

        sleep(60 * 1000);

    }

    void testConvertToEntityAttribute() {
    }


}