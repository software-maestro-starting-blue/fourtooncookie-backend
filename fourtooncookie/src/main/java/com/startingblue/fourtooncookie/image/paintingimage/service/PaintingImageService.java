package com.startingblue.fourtooncookie.image.paintingimage.service;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImageRepository;
import com.startingblue.fourtooncookie.image.paintingimage.dto.PaintingImageSaveRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaintingImageService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.painting-image-folder}")
    private String paintingImageFolder;

    private final S3Client s3Client;

    private final DiaryService diaryService;

    private final PaintingImageRepository paintingImageRepository;

    public void createPaintingImages(final PaintingImageSaveRequest paintingImageSaveRequest) {
        Diary foundDiary = diaryService.findById(paintingImageSaveRequest.diaryId());
        updateDiaryPaintingImage(foundDiary, paintingImageSaveRequest.paintingImageUrls());
        uploadPaintingImageS3(foundDiary, paintingImageSaveRequest.paintingImageUrls());
    }

    private void updateDiaryPaintingImage(final Diary diary, final List<String> imageUrls) {
        for (int imageCount = 0; imageCount < imageUrls.size(); imageCount++) {
            String imageUrl = imageUrls.get(imageCount);
            byte[] imageBytes = downloadPaintingImageFromUrl(imageUrl);
            final String uploadPath = paintingImageFolder + diary.toString() + ".jpg";
            PaintingImage paintingImage = new PaintingImage(imageUrl, diary, imageCount);
            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .key(uploadPath)
                    .contentType("image/*")
                    .bucket(bucketName)
                    .build();
            paintingImageRepository.save(paintingImage);
        }
    }

    private void uploadPaintingImageS3(final Diary diary, final List<String> imageUrls) {
        for (int imageCount = 0; imageCount < imageUrls.size(); imageCount++) {
            String imageUrl = imageUrls.get(imageCount);
            byte[] imageBytes = downloadPaintingImageFromUrl(imageUrl);
            final String uploadPath = paintingImageFolder + diary.getId() + "[" + imageCount + "].jpg";
            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .key(uploadPath)
                    .contentType("image/*")
                    .bucket(bucketName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
        }
    }

    private byte[] downloadPaintingImageFromUrl(final String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            log.error("Failed to download image from URL: {}", imageUrl, e);
        }
        return new byte[0];
    }
}
