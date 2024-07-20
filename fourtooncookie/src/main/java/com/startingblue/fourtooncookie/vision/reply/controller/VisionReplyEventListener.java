package com.startingblue.fourtooncookie.vision.reply.controller;

import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import com.startingblue.fourtooncookie.vision.reply.service.VisionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VisionReplyEventListener {

    private final VisionReplyService visionReplyService;

    @EventListener
    public void handleVisionReplyEvent(VisionReplyEvent event) {
        visionReplyService.processVisionReply(event.getDiaryId(), event.getImages());
    }

}
