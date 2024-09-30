package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.exception.S3PathNotFoundException;
import com.startingblue.fourtooncookie.aws.s3.service.S3Service;
import com.startingblue.fourtooncookie.global.converter.image.ImageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiaryS3ServiceTest {

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private DiaryS3Service diaryS3Service;

    @Mock
    private ImageConverter imageConverter;

    static long diaryId = 1;
    static int gridPosition = 1;
    static String path = diaryId + "/" + gridPosition;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Presigned URL 생성 성공 테스트")
    void testGeneratePresignedUrlSuccess() throws Exception {
        // given
        URL expectedUrl = new URL("http://example.com");
        when(s3Service.generatePresignedUrl(any(String.class))).thenReturn(expectedUrl);

        // when
        URL actualUrl = diaryS3Service.generatePresignedUrl(diaryId, gridPosition);

        // then
        assertEquals(expectedUrl, actualUrl);
        verify(s3Service).generatePresignedUrl(any(String.class));
    }

    @Test
    @DisplayName("다이어리 ID로 4개의 이미지를 성공적으로 가져오고 병합")
    void testGetFullImageByDiaryIdSuccess() throws IOException {
        // given
        byte[] mockImageData = new byte[]{1, 2, 3};
        byte[] mergedImage = new byte[]{10, 11, 12}; // Merged image result

        when(s3Service.getImageFromS3(any(String.class))).thenReturn(mockImageData);
        when(imageConverter.mergeImagesIntoGrid(any(List.class))).thenReturn(mergedImage);

        // when
        byte[] result = diaryS3Service.getFullImageByDiaryId(1L);

        // then
        assertNotNull(result);
        assertArrayEquals(mergedImage, result); // Verify merged image
        verify(s3Service, times(4)).getImageFromS3(any(String.class)); // Verify 4 image retrievals
        verify(imageConverter).mergeImagesIntoGrid(any(List.class)); // Verify image merging
    }

    @Test
    @DisplayName("다이어리 다운로드 중 이미지가 존재하지 않는 경우 예외 발생")
    void testGetFullImageByDiaryIdImageNotFound() {
        // given
        when(s3Service.getImageFromS3(any(String.class)))
                .thenThrow(new S3PathNotFoundException("Image not found"));

        // when & then
        assertThrows(S3PathNotFoundException.class, () ->
                diaryS3Service.getFullImageByDiaryId(diaryId)
        );
    }
}
