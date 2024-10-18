package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.S3Service;
import com.startingblue.fourtooncookie.aws.s3.exception.S3PathNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiaryS3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("S3에서 이미지 가져오기 성공 테스트")
    void testGetImageFromS3Success() {
        // given
        String bucketName = "test-bucket";
        String keyName = "test-key.png";
        byte[] imageData = new byte[]{1, 2, 3, 4};

        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), imageData);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        // when
        byte[] result = s3Service.getImageFromS3(bucketName, keyName);

        // then
        assertNotNull(result);
        assertArrayEquals(imageData, result);
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("S3에서 이미지 가져오기 실패: NoSuchBucketException 테스트")
    void testGetImageFromS3NoSuchBucketException() {
        // given
        String bucketName = "test-bucket";
        String keyName = "test-key.png";

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(NoSuchBucketException.builder().build());

        // when & then
        assertThrows(S3PathNotFoundException.class, () -> s3Service.getImageFromS3(bucketName, keyName));
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("S3에서 폴더 내 모든 객체 삭제 성공 테스트")
    void testDeleteObjectsInFolderSuccess() {
        // given
        String bucketName = "test-bucket";
        String folderKey = "test-folder/";

        S3Object s3Object1 = S3Object.builder().key("test-folder/file1.png").build();
        S3Object s3Object2 = S3Object.builder().key("test-folder/file2.png").build();

        ListObjectsV2Response listObjectsResponse = ListObjectsV2Response.builder()
                .contents(Arrays.asList(s3Object1, s3Object2))
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsResponse);

        // when
        s3Service.deleteObjectsInFolder(bucketName, folderKey);

        // then
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3Client).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("S3에서 폴더 내 객체 삭제 실패: NoSuchBucketException 테스트")
    void testDeleteObjectsInFolderNoSuchBucketException() {
        // given
        String bucketName = "test-bucket";
        String folderKey = "test-folder/";

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(NoSuchBucketException.builder().build());

        // when & then
        assertThrows(S3PathNotFoundException.class, () -> s3Service.deleteObjectsInFolder(bucketName, folderKey));
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
    }
}
