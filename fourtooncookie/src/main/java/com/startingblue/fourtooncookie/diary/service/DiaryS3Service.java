package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.service.S3Service;
import com.startingblue.fourtooncookie.global.converter.image.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryS3Service {

    private static final String IMAGE_PNG_FORMAT = "png";

    private final S3Service s3Service;
    private final ImageConverter imageConverter;

    @Value("${aws.diaryimage.bucket.name}")
    private String bucketName;

    public URL generatePresignedUrl(Long diaryId, int gridPosition) {
        return s3Service.generatePresignedUrl(bucketName, getKeyName(diaryId, gridPosition, IMAGE_PNG_FORMAT));
    }

    public byte[] getFullImageByDiaryId(Long diaryId) throws IOException {
        List<byte[]> downloadedImages = new ArrayList<>();

        for (int gridPosition = 0; gridPosition < 4; gridPosition++) {
            byte[] imageData = getImageByDiaryIdAndGridPosition(diaryId, gridPosition);
            downloadedImages.add(imageData);
        }

        return imageConverter.mergeImagesIntoGrid(downloadedImages);
    }

    private byte[] getImageByDiaryIdAndGridPosition(Long diaryId, int gridPosition) {
        return s3Service.getImageFromS3(bucketName, getKeyName(diaryId, gridPosition, IMAGE_PNG_FORMAT));
    }

    private String getKeyName(Long diaryId, int gridPosition, String imageFormat) {
        return String.format("%s/%s.%s", diaryId, gridPosition, imageFormat);
    }

    public void deleteImagesByDiaryId(Long diaryId) {
        s3Service.deleteObjectsInFolder(bucketName, String.valueOf(diaryId));
    }
}
