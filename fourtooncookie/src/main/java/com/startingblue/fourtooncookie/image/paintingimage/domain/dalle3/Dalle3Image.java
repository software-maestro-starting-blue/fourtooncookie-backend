package com.startingblue.fourtooncookie.image.paintingimage.domain.dalle3;

import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;

public class Dalle3Image extends PaintingImage {

    public Dalle3Image(String path, Diary diary, int gridPosition) {
        super(path, ModelType.DALL_E_3, diary, gridPosition);
    }
}
