package com.startingblue.fourtooncookie.aws.s3.service;

import com.startingblue.fourtooncookie.aws.s3.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3Service s3Service;

    PresignedGetObjectRequest presignedGetObjectRequest;

    private final String bucketName = "test-bucket";
    private final int preSignedUrlDurationInMinutes = 60;

    static long diaryId = 1;
    static int gridPosition = 1;
    static String path = diaryId + "/" + gridPosition;
    static String keyName = path + ".png";

    @BeforeEach
    void setUp() {
        HeadObjectResponse headObjectResponse = mock(HeadObjectResponse.class);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headObjectResponse);
        presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
        ReflectionTestUtils.setField(s3Service, "preSignedUrlDurationInMinutes", preSignedUrlDurationInMinutes);
    }

    @Test
    @DisplayName("프리사인 URL 생성 성공 테스트")
    void testGeneratePresignedUrlSuccess() throws Exception {
        // given
        URL expectedUrl = URI.create("http://example.com").toURL();
        when(presignedGetObjectRequest.url()).thenReturn(expectedUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGetObjectRequest);

        // when
        URL actualUrl = s3Service.generatePresignedUrl(path);

        // then
        assertEquals(expectedUrl, actualUrl);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    @DisplayName("프리사인 URL 생성 중 오류 발생 시 S3PreSignUrlException 발생 테스트")
    void testGeneratePresignedUrlPreSignError() {
        // given
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Presign error"));

        // when & then
        S3PreSignUrlException exception = assertThrows(S3PreSignUrlException.class, () ->
                s3Service.generatePresignedUrl(path)
        );

        assertTrue(exception.getMessage().contains("S3에서 프리사인 URL 생성 중 오류가 발생했습니다. Key: " + keyName));
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

}
