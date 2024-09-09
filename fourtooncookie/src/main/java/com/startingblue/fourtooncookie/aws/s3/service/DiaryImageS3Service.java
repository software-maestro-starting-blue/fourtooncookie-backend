package com.startingblue.fourtooncookie.aws.s3.service;

import com.startingblue.fourtooncookie.aws.s3.exception.S3ImageExistenceCheckException;
import com.startingblue.fourtooncookie.aws.s3.exception.S3ImageNotFoundException;
import com.startingblue.fourtooncookie.aws.s3.exception.S3PreSignUrlException;
import com.startingblue.fourtooncookie.aws.s3.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class DiaryImageS3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    @Value("${aws.diaryimage.presignedurl.duration}")
    private Integer preSignedUrlDurationInMinutes;

    private static final String IMAGE_FORMAT = ".png";
    private static final String CONTENT_TYPE = "image/png";

    public void uploadImage(Long diaryId, byte[] image, Integer gridPosition) {
        String keyName = getKeyName(diaryId, gridPosition);
        try {
            PutObjectRequest putObjectRequest = createPutObjectRequest(keyName);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image));
        } catch (Exception e) {
            throw new S3UploadException(String.format("S3에 이미지 업로드 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private String getKeyName(Long diaryId, Integer gridPosition) {
        return String.format("%d/%d%s", diaryId, gridPosition, IMAGE_FORMAT);
    }

    private PutObjectRequest createPutObjectRequest(String keyName) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(CONTENT_TYPE)
                .key(keyName)
                .build();
    }

    public URL generatePreSignedImageUrl(Long diaryId, Integer gridPosition) {
        if (!isImageExist(diaryId, gridPosition)) {
            throw new S3ImageNotFoundException(String.format("S3에 이미지가 존재하지 않습니다. Key: %s", getKeyName(diaryId, gridPosition)));
        }

        String keyName = getKeyName(diaryId, gridPosition);

        try {
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(keyName);
            PresignedGetObjectRequest preSignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            return preSignedRequest.url();
        } catch (Exception e) {
            throw new S3PreSignUrlException(String.format("S3에서 프리사인 URL 생성 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(String keyName) {
        return GetObjectPresignRequest.builder()
                .getObjectRequest(r -> r.bucket(bucketName).key(keyName))
                .signatureDuration(Duration.ofMinutes(preSignedUrlDurationInMinutes))
                .build();
    }

    public boolean isImageExist(Long diaryId, Integer gridPosition) {
        String keyName = getKeyName(diaryId, gridPosition);
        try {
            HeadObjectRequest headObjectRequest = createHeadObjectRequest(keyName);
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new S3ImageExistenceCheckException(String.format("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private HeadObjectRequest createHeadObjectRequest(String keyName) {
        return HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
    }
}
