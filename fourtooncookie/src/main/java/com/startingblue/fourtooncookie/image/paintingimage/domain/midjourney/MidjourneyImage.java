package com.startingblue.fourtooncookie.image.paintingimage.domain.midjourney;

import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;

public class MidjourneyImage extends PaintingImage {

    public MidjourneyImage(String path, Diary diary, int gridPosition) {
        super(path, ModelType.MIDJOURNEY, diary, gridPosition);
    }
}
