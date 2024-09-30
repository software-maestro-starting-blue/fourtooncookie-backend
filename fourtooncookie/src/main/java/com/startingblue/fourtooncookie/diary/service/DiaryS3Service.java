package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.service.S3Service;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.global.converter.image.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryS3Service {

    private final S3Service s3Service;
    private final ImageConverter imageConverter;

    public URL generatePresignedUrl(Long diaryId, int gridPosition) {
        String path = diaryId + "/" + gridPosition;
        return s3Service.generatePresignedUrl(path);
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
        String keyName = diaryId + "/" + gridPosition;
        return s3Service.getImageFromS3(keyName);
    }

}
