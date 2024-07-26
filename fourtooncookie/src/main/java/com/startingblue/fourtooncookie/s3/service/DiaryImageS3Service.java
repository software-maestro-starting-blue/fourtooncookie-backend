package com.startingblue.fourtooncookie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class DiaryImageS3Service {

    private final S3Client s3Client;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    private static final String IMAGE_FORMAT = ".png";

    public void uploadImage(Long diaryId, byte[] image, Integer gridPosition) {
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

    public byte[] downloadImageByByteArray(Long diaryId, Integer gridPosition) {
        if (!isImageExist(diaryId, gridPosition)) {
            throw new RuntimeException("S3에 이미지가 존재하지 않습니다.");
        }

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
