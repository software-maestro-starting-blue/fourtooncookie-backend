package com.startingblue.fourtooncookie.aws.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class DiaryImageS3Service {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    @Value("${aws.diaryimage.presignedurl.duration}")
    private Integer PresignedUrlDurationInMinutes;

    private static final String IMAGE_FORMAT = ".png";

    public void uploadImage(Long diaryId, byte[] image, Integer gridPosition) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType("image/png")
                    .key(getKeyName(diaryId, gridPosition))
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image));
        } catch (Exception e) {
            throw new RuntimeException("S3에 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public String generatePresignedImageUrl(Long diaryId, Integer gridPosition) {
        if (!isImageExist(diaryId, gridPosition)) {
            throw new RuntimeException("S3에 이미지가 존재하지 않습니다.");
        }

        String keyName = getKeyName(diaryId, gridPosition);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(r -> r.bucket(bucketName).key(keyName))
                .signatureDuration(Duration.ofMinutes(PresignedUrlDurationInMinutes))
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedRequest.url().toString();
    }

    public boolean isImageExist(Long diaryId, Integer gridPosition) {
        String keyName = getKeyName(diaryId, gridPosition);
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true; // 파일이 존재하면 true 반환
        } catch (NoSuchKeyException e) {
            return false; // 파일이 존재하지 않으면 false 반환
        } catch (Exception e) {
            throw new RuntimeException("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다.", e);
        }
    }

    private String getKeyName(Long diaryId, Integer gridPosition) {
        return diaryId + "/" + gridPosition + IMAGE_FORMAT;
    }

}
