package com.startingblue.fourtooncookie.image.paintingimage.dto;

public record PaintingImageSaveRequest(String path, String modelType, Long diaryId, Integer gridPosition) {
}
