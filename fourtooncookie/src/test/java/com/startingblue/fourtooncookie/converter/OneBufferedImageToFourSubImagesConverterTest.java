package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class OneBufferedImageToFourSubImagesConverterTest {

    private final OneBufferedImageToFourSubImagesConverter converter = new OneBufferedImageToFourSubImagesConverter();

    @Test
    @DisplayName("BufferedImage를 4개의 서브 이미지로 변환 (정상 케이스)")
    void testSplitImageToSubImages() throws Exception {
        String imageUrl = "https://cdn.pixabay.com/photo/2016/07/11/17/45/abstract-1510190_1280.png";
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        List<BufferedImage> subImages = converter.splitImageToSubImages(image);

        assertThat(subImages).hasSize(4);
        assertThat(subImages.get(0).getWidth()).isEqualTo(image.getWidth() / 2);
        assertThat(subImages.get(0).getHeight()).isEqualTo(image.getHeight() / 2);
        assertThat(subImages.get(1).getWidth()).isEqualTo(image.getWidth() / 2);
        assertThat(subImages.get(1).getHeight()).isEqualTo(image.getHeight() / 2);
        assertThat(subImages.get(2).getWidth()).isEqualTo(image.getWidth() / 2);
        assertThat(subImages.get(2).getHeight()).isEqualTo(image.getHeight() / 2);
        assertThat(subImages.get(3).getWidth()).isEqualTo(image.getWidth() / 2);
        assertThat(subImages.get(3).getHeight()).isEqualTo(image.getHeight() / 2);
    }

    @Test
    @DisplayName("4개의 서브 이미지를 하나의 BufferedImage로 변환 (정상 케이스)")
    void testCombineSubImagesToImage() throws Exception {
        String imageUrl = "https://cdn.pixabay.com/photo/2016/07/11/17/45/abstract-1510190_1280.png";
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        List<BufferedImage> subImages = converter.splitImageToSubImages(image);
        BufferedImage combinedImage = converter.combineSubImagesToImage(subImages);

        assertThat(combinedImage.getWidth()).isEqualTo(image.getWidth());
        assertThat(combinedImage.getHeight()).isEqualTo(image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                assertThat(combinedImage.getRGB(x, y)).isEqualTo(image.getRGB(x, y));
            }
        }
    }

    @Test
    @DisplayName("BufferedImage가 null일 때 예외 발생")
    void testSplitImageToSubImagesWithNull() {
        assertThatThrownBy(() -> {
            converter.splitImageToSubImages(null);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("BufferedImage attribute is null");
    }

    @Test
    @DisplayName("List<BufferedImage>가 null일 때 예외 발생")
    void testCombineSubImagesToImageWithNull() {
        assertThatThrownBy(() -> {
            converter.combineSubImagesToImage(null);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("List<BufferedImage> dbData is null or does not contain exactly 4 subimages");
    }

    @Test
    @DisplayName("List<BufferedImage>의 크기가 4가 아닐 때 예외 발생")
    void testCombineSubImagesToImageWithInvalidSize() {
        List<BufferedImage> invalidList = new ArrayList<>();
        invalidList.add(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB));

        assertThatThrownBy(() -> {
            converter.combineSubImagesToImage(invalidList);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("List<BufferedImage> dbData is null or does not contain exactly 4 subimages");
    }

    @Test
    @DisplayName("BufferedImage를 4개의 서브 이미지로 분할 시 예외 발생")
    void testSplitImageToSubImagesWithException() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB) {
            @Override
            public BufferedImage getSubimage(int x, int y, int w, int h) {
                throw new RuntimeException("Mock exception");
            }
        };

        assertThatThrownBy(() -> {
            converter.splitImageToSubImages(image);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("Error splitting BufferedImage into subimages");
    }

    /**
     * 아래 주석은 실제로 보기 위한 테스트, 원할한 테스트를 위해 우선 주석
     * 추 후 컨버터 적용 내용을 눈으로 확인하는 용도로 사용 예정
     */
//
//    @Test
//    @DisplayName("하나의 이미지가 4개로 잘 나뉘는 가를 확인")
//    void testConvertToDatabaseColumn() throws Exception {
//        System.setProperty("java.awt.headless", "false");
//        String imageUrl = "https://cdn.pixabay.com/photo/2016/07/11/17/45/abstract-1510190_1280.png";
//
//        BufferedImage image = ImageIO.read(new URL(imageUrl));
//
//        List<BufferedImage> subImages = oneBufferedImageToFourSubImagesConverter.convertToDatabaseColumn(image);
//
//        subImages.forEach(subImage ->
//                SwingUtilities.invokeLater(() -> {
//                    JFrame frame = new JFrame();
//                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    frame.setSize(new Dimension(subImage.getWidth(), subImage.getHeight()));
//
//                    // ImageIcon을 사용하여 이미지를 JLabel에 설정
//                    ImageIcon icon = new ImageIcon(subImage);
//                    JLabel label = new JLabel(icon);
//
//                    // JLabel을 JFrame에 추가
//                    frame.add(label);
//
//                    // JFrame을 화면에 표시
//                    frame.pack();
//                    frame.setLocationRelativeTo(null); // 화면 중앙에 표시
//                    frame.setVisible(true);
//                })
//        );
//
//        sleep(5 * 1000);
//
//    }
//
//    @Test
//    @DisplayName("4장의 이미지가 하나로 잘 합쳐지는 가를 확인")
//    void testConvertToEntityAttribute() throws Exception {
//        System.setProperty("java.awt.headless", "false");
//        String imageUrl = "https://cdn.pixabay.com/photo/2016/07/11/17/45/abstract-1510190_1280.png";
//
//        BufferedImage image = ImageIO.read(new URL(imageUrl));
//
//        List<BufferedImage> images = List.of(image, image, image, image);
//
//        BufferedImage combinedImage = oneBufferedImageToFourSubImagesConverter.convertToEntityAttribute(images);
//
//
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame();
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(new Dimension(combinedImage.getWidth(), combinedImage.getHeight()));
//
//            // ImageIcon을 사용하여 이미지를 JLabel에 설정
//            ImageIcon icon = new ImageIcon(combinedImage);
//            JLabel label = new JLabel(icon);
//
//            // JLabel을 JFrame에 추가
//            frame.add(label);
//
//            // JFrame을 화면에 표시
//            frame.pack();
//            frame.setLocationRelativeTo(null); // 화면 중앙에 표시
//            frame.setVisible(true);
//        });
//
//        sleep(5 * 1000);
//    }

}