package com.startingblue.fourtooncookie.image.paintingimage.domain.stablediffusion;

import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;

public class StableDiffusionImage extends PaintingImage {

    public StableDiffusionImage(String path, Diary diary, int gridPosition) {
        super(path, ModelType.STABLE_DIFFUSION, diary, gridPosition);
    }
}
