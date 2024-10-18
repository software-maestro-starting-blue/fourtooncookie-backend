package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.S3Service;
import com.startingblue.fourtooncookie.global.converter.ImageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiaryS3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private DiaryS3Service diaryS3Service;

    @Mock
    private ImageConverter imageConverter;

    static String bucketName = "bucketName";
    static long diaryId = 1;
    static int gridPosition = 1;
    static String keyName = diaryId + "/" + gridPosition + ".png";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(diaryS3Service, "bucketName", "test-bucket");
    }

    @Test
    @DisplayName("Presigned URL 생성 성공 테스트")
    void testGeneratePresignedUrlSuccess() throws Exception {
        // given
        URL expectedUrl = new URL("http://example.com");
        when(s3Service.generatePresignedUrl(any(String.class), any(String.class))).thenReturn(expectedUrl);

        // when
        URL actualUrl = diaryS3Service.generatePresignedUrl(diaryId, gridPosition);

        // then
        assertEquals(expectedUrl, actualUrl);
        verify(s3Service).generatePresignedUrl(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("다이어리 ID로 4개의 이미지를 성공적으로 가져오고 병합")
    void testGetFullImageByDiaryIdSuccess() throws IOException {
        // given
        byte[] mockImageData = new byte[]{1, 2, 3, 4};
        byte[] mergedImage = new byte[]{10, 11, 12, 5};

        when(s3Service.getImageFromS3(eq(bucketName), eq(keyName))).thenReturn(mockImageData);
        when(imageConverter.mergeImagesIntoGrid(any(List.class))).thenReturn(mergedImage);

        // when
        byte[] result = diaryS3Service.getFullImageByDiaryId(1L);

        // then
        assertNotNull(result);
        assertArrayEquals(mergedImage, result);
        verify(s3Service, times(4)).getImageFromS3(any(String.class), any(String.class));
        verify(imageConverter).mergeImagesIntoGrid(any(List.class));
    }

}
