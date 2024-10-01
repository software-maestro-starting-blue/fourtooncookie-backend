package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.cloudfront.CloudFrontService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryCloudFrontService {

    private final CloudFrontService cloudFrontService;

    public Map<String, String> generateSignedCookies(UUID memberId) {
        try {
            String path = memberId.toString();
            return cloudFrontService.generateCloudFrontSignedCookies(path);
        } catch (Exception e) {
            log.error("Failed to generate signed cookies for memberId: {}", memberId, e);
            return null;
        }
    }

    public URL generateSignedUrl(Long diaryId, int imageGridPosition) {
        String path = diaryId + "/" + imageGridPosition  + ".png";
        return cloudFrontService.generateSignedUrl(path);
    }
}
