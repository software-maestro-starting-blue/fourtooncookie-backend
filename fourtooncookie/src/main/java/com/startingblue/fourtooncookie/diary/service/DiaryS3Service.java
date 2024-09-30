package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.s3.service.S3Service;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.global.converter.image.ImageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class DiaryS3Service {

    private final S3Service s3Service;
    private final ImageConverter imageConverter;

    public URL generatePresignedUrl(Long diaryId, int gridPosition) {
        String path = diaryId + "/" + gridPosition;
        System.out.println(1);
        return s3Service.generatePresignedUrl(path);
    }

    public byte[] getFullImageByDiaryId (Long diaryId) throws IOException {
        return imageConverter.mergeImagesIntoGrid(s3Service.getFullImageByDiaryId(diaryId));
    }

}
