package com.startingblue.fourtooncookie.diary.dto.request;

import java.net.URL;
import java.util.List;

public record DiaryUpdateRequest(String content, List<URL> paintingImageUrls, List<Long> hashtagIds, Long characterId) {
}
