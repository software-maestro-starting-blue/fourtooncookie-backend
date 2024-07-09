package com.startingblue.fourtooncookie.hashtag;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagDeleteRequest;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagSaveRequest;
import com.startingblue.fourtooncookie.hashtag.dto.response.HashtagSavedResponse;
import com.startingblue.fourtooncookie.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/hashtag")
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping()
    public ResponseEntity<List<HashtagSavedResponse>> hashtags(@RequestParam String hashtagType) {
        List<Hashtag> allFromHashtagType = hashtagService.findHashtagsFromHashtagType(hashtagType);

        String iconUrl = ""; // 변경 예정
        List<HashtagSavedResponse> responseList = allFromHashtagType.stream()
                .map(hashtag -> new HashtagSavedResponse(hashtag.getHashtagType().toString(), hashtag.getName(), iconUrl))
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping()
    public ResponseEntity<HashtagSaveRequest> createHashtag(@RequestBody HashtagSaveRequest hashtagSaveRequest) {
        hashtagService.createHashtag(hashtagSaveRequest);
        return ResponseEntity.ok(hashtagSaveRequest);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteHashtag(@RequestBody HashtagDeleteRequest hashtagDeleteRequest) {
        hashtagService.deleteHashtag(hashtagDeleteRequest);
        return ResponseEntity.ok(hashtagDeleteRequest.hashtagId() + " hashtag deleted");
    }
}
