package com.startingblue.fourtooncookie.vision.reply.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class VisionReplyEvent extends ApplicationEvent {

    private final Long diaryId;

    private final List<byte[]> images;

    public VisionReplyEvent(Object source, Long diaryId, List<byte[]> images) {
        super(source);
        this.diaryId = diaryId;
        this.images = images;
    }


}
