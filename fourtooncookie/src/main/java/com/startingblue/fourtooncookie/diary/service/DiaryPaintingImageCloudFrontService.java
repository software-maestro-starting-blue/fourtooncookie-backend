package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.cloudfront.CloudFrontService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryPaintingImageCloudFrontService {

    private static final String IMAGE_PNG_FORMAT = "png";

    @Value("${aws.cloudfront.domain.name}")
    private String cloudFrontDomainName;

    @Value("${aws.cloudfront.keyPairId}")
    private String keyPairId;

    @Value("${aws.cloudfront.privateKeyPath}")
    private String privateKeyPath;

    private final CloudFrontService cloudFrontService;

    public URL generateSignedUrl(Long diaryId, int imageGridPosition) {
        String path = getPath(diaryId, imageGridPosition, IMAGE_PNG_FORMAT);
        return cloudFrontService.generateSignedUrl(path, cloudFrontDomainName, keyPairId, privateKeyPath);
    }

    private String getPath(Long diaryId, int imageGridPosition, String imageFormat) {
        return String.format("%s/%s.%s", diaryId, imageGridPosition, imageFormat);
    }
}
