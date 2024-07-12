package com.startingblue.fourtooncookie.image.paintingimage.dto;

import java.util.List;

public record PaintingImageSaveRequest(Long diaryId, List<String> paintingImageUrls) {
}
