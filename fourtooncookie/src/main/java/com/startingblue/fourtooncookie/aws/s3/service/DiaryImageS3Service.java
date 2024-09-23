package com.startingblue.fourtooncookie.aws.s3.service;

import com.startingblue.fourtooncookie.aws.s3.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<byte[]> downloadImages(Long diaryId) {
        List<byte[]> downloadedImages = new ArrayList<>();

        for (int gridPosition = 0; gridPosition < 4; gridPosition++) {
            byte[] imageData = downloadImage(diaryId, gridPosition);
            downloadedImages.add(imageData);
        }

        return downloadedImages;
    }

    private byte[] downloadImage(Long diaryId, Integer gridPosition) {
        String keyName = getKeyName(diaryId, gridPosition);

        try {
            return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build()).asByteArray();
        } catch (NoSuchKeyException e) {
            throw new S3ImageNotFoundException(String.format("S3에 이미지가 존재하지 않습니다. Key: %s", keyName));
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에서 이미지 다운로드 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    public byte[] mergeImagesTo2x2(List<byte[]> images) throws IOException {
        if (images.size() <= 4) {
            throw new IllegalArgumentException("4개의 이미지가 필요합니다.");
        }

        BufferedImage[] bufferedImages = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(images.get(i));
            bufferedImages[i] = ImageIO.read(bais);
        }

        int width = bufferedImages[0].getWidth();
        int height = bufferedImages[0].getHeight();

        BufferedImage combinedImage = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = combinedImage.createGraphics();
        g.drawImage(bufferedImages[0], 0, 0, null);           // 좌상단
        g.drawImage(bufferedImages[1], width, 0, null);       // 우상단
        g.drawImage(bufferedImages[2], 0, height, null);      // 좌하단
        g.drawImage(bufferedImages[3], width, height, null);  // 우하단
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, "png", baos);
        return baos.toByteArray();
    }
}
