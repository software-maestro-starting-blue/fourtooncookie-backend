package com.startingblue.fourtooncookie.vision.reply.controller;

import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyRequest;
import com.startingblue.fourtooncookie.vision.reply.service.VisionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vision")
public class VisionReplyController {

    private final VisionReplyService visionReplyService;

    @PostMapping("/reply")
    public void replyVision(@RequestBody VisionReplyRequest request) {
        visionReplyService.processVisionReply(request.diaryId(), request.images());
    }
}
