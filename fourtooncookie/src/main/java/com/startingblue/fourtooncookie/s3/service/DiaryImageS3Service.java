package com.startingblue.fourtooncookie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class DiaryImageS3Service {

    private final S3Client s3Client;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    private static final String IMAGE_FORMAT = ".png";

    public void uploadImage(Integer diaryId, byte[] image, Integer gridPosition) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(getKeyName(diaryId, gridPosition))
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image));
        } catch (Exception e) {
            throw new RuntimeException("S3에 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public byte[] downloadImageByByteArray(Integer diaryId, Integer gridPosition) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(getKeyName(diaryId, gridPosition))
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;

        try {
            while ((bytesRead = s3Object.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("S3에서 다운로드된 이미지 변환 중 오류가 발생했습니다.", e);
        }
    }

    private String getKeyName(Integer diaryId, Integer gridPosition) {
        return diaryId + "/" + gridPosition + IMAGE_FORMAT;
    }

}
