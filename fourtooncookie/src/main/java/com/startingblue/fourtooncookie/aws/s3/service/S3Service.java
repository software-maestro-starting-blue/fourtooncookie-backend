package com.startingblue.fourtooncookie.aws.s3.service;

import com.startingblue.fourtooncookie.aws.s3.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    @Value("${aws.diaryimage.presignedurl.duration}")
    private Integer preSignedUrlDurationInMinutes;

    private static final String IMAGE_FORMAT = ".png";

    public URL generatePresignedUrl(String path) {
        String keyName = getKeyName(path);
        verifyPathExists(keyName);

        return createPresignedUrl(keyName);
    }

    private String getKeyName(String path) {
        return String.format("%s%s", path, IMAGE_FORMAT);
    }

    private void verifyPathExists(String keyName) {
        if (!isPathExists(keyName)) {
            throw new S3PathNotFoundException(String.format("S3에 이미지가 존재하지 않습니다. Key: %s", keyName));
        }
    }

    private URL createPresignedUrl(String keyName) {
        try {
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(keyName);
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url();
        } catch (Exception e) {
            throw new S3PreSignUrlException(String.format("S3에서 프리사인 URL 생성 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private boolean isPathExists(String keyName) {
        try {
            HeadObjectRequest headObjectRequest = createHeadObjectRequest(keyName);
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return handleNoSuchKey(e);
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private HeadObjectRequest createHeadObjectRequest(String keyName) {
        return HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
    }

    private boolean handleNoSuchKey(NoSuchKeyException e) {
        if (e.statusCode() == 404) {
            return false;
        }
        throw new S3Exception("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다.", e);
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(String keyName) {
        return GetObjectPresignRequest.builder()
                .getObjectRequest(r -> r.bucket(bucketName).key(keyName))
                .signatureDuration(Duration.ofMinutes(preSignedUrlDurationInMinutes))
                .build();
    }

    public byte[] getImageFromS3(String keyName) {
        try {
            return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build()).asByteArray();
        } catch (NoSuchKeyException e) {
            throw new S3PathNotFoundException(String.format("S3에 이미지가 존재하지 않습니다. Key: %s", keyName));
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에서 이미지 다운로드 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

}
