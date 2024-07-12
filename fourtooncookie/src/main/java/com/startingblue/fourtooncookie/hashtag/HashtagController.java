package com.startingblue.fourtooncookie.hashtag;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagDeleteRequest;
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
    public ResponseEntity<HashtagSaveRequest> createHashtag(@RequestBody HashtagSaveRequest request) {
        hashtagService.createHashtag(request);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHashtag(@RequestBody HashtagDeleteRequest request) {
        hashtagService.deleteHashtag(request);
        return ResponseEntity.noContent().build();
    }
}
