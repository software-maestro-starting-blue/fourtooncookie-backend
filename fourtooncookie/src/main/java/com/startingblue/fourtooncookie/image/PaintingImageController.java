package com.startingblue.fourtooncookie.image;

import com.startingblue.fourtooncookie.image.paintingimage.dto.PaintingImageSaveRequest;
import com.startingblue.fourtooncookie.image.paintingimage.service.PaintingImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/painting-image")
@RequiredArgsConstructor
public class PaintingImageController {

    private final PaintingImageService paintingImageService;

    @PostMapping()
    public void as(@RequestBody PaintingImageSaveRequest request) {
        paintingImageService.createPaintingImages(request);
    }
}
