package com.startingblue.fourtooncookie.hashtag;

import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagSaveRequest;
import com.startingblue.fourtooncookie.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/hashtag")
public class HashtagController {

    private final HashtagService hashtagService;

    @PostMapping()
    public ResponseEntity<HashtagSaveRequest> createHashtag(@RequestBody final HashtagSaveRequest request) {
        hashtagService.createHashtag(request);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{hashtagId}")
    public ResponseEntity<Void> deleteHashtag(@PathVariable final Long hashtagId) {
        hashtagService.deleteHashtag(hashtagId);
        return ResponseEntity.noContent().build();
    }
}
