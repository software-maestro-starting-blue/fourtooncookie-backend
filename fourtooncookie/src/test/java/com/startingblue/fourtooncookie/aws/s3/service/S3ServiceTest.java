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
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
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
    private S3Client s3Client;  // S3Client 모킹

    @Mock
    private S3Presigner s3Presigner;  // S3Presigner 모킹

    @InjectMocks
    private S3Service s3Service;  // S3Service에 모킹된 의존성 주입

    private final String bucketName = "test-bucket";
    private final int preSignedUrlDurationInMinutes = 60;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
        ReflectionTestUtils.setField(s3Service, "preSignedUrlDurationInMinutes", preSignedUrlDurationInMinutes);
    }
    @Test
    @DisplayName("이미지가 존재할 때 프리사인 URL 생성 성공 테스트")
    void testGeneratePresignedUrlSuccess() throws Exception {
        String path = "1/1";
        String keyName = path + ".png";

        // given: 이미지가 존재하는 S3 객체의 헤드 요청을 모킹
        HeadObjectResponse headObjectResponse = mock(HeadObjectResponse.class);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headObjectResponse);

        // given: presigned URL을 반환하는 presignGetObject 모킹
        PresignedGetObjectRequest presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);
        URL expectedUrl = URI.create("http://example.com").toURL();
        when(presignedGetObjectRequest.url()).thenReturn(expectedUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGetObjectRequest);

        // when: S3Service에서 generatePresignedUrl 메서드 호출
        URL actualUrl = s3Service.generatePresignedUrl(path);

        // then: 예상된 URL이 반환되었는지 검증
        assertEquals(expectedUrl, actualUrl);

        // then: s3Client.headObject()와 s3Presigner.presignGetObject()가 호출되었는지 검증
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    @DisplayName("이미지 경로가 존재하지 않을 때 S3Exception 발생 테스트")
    void testGeneratePresignedUrlImageNotFound() {
        // given
        long diaryId = 1;
        int gridPosition = 1;
        String path = diaryId + "/" + gridPosition;

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());

        // when & then
        S3Exception exception = assertThrows(NoSuchKeyException.class, () ->
                s3Service.generatePresignedUrl(path)
        );

        assertEquals("S3에 이미지가 존재하지 않습니다. Key: 1/1.png", exception.getMessage());

        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    @DisplayName("프리사인 URL 생성 중 오류 발생 시 S3PreSignUrlException 발생 테스트")
    void testGeneratePresignedUrlPreSignError() {
        // given
        long diaryId = 1;
        int gridPosition = 1;
        String path =  diaryId + "/" + gridPosition;
        String keyName = path + ".png";

        HeadObjectResponse headObjectResponse = mock(HeadObjectResponse.class);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headObjectResponse);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Presign error"));

        // when & then
        S3PreSignUrlException exception = assertThrows(S3PreSignUrlException.class, () ->
                s3Service.generatePresignedUrl(path)
        );

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("S3에서 프리사인 URL 생성 중 오류가 발생했습니다. Key: " + keyName));
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }
}