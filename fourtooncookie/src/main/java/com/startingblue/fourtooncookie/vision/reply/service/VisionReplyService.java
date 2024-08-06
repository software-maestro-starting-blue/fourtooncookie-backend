package com.startingblue.fourtooncookie.vision.reply.service;

import com.startingblue.fourtooncookie.aws.s3.service.DiaryImageS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VisionReplyService {

    private final DiaryImageS3Service diaryImageS3Service;

    public void processVisionReply(Long diaryId, byte[] image, Integer gridPosition) {
        diaryImageS3Service.uploadImage(diaryId, image, gridPosition);
    }

}
