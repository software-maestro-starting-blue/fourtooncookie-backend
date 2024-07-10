package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.DiaryHashtag;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryDeleteRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryPageRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<Void> createDiary(@RequestBody DiarySaveRequest diarySaveRequest) {
        diaryService.createDiary(diarySaveRequest);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/timeline")
    public ResponseEntity<List<DiarySavedResponse>> readDiaries(@ModelAttribute DiaryPageRequest pageRequest) {
        Page<Diary> diaryPage = diaryService.getDiaries(pageRequest.page(), pageRequest.size());

        if (diaryPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<DiarySavedResponse> responses = diaryPage.getContent().stream()
                .map(diary -> new DiarySavedResponse(
                        diary.getCharacter() != null ? diary.getCharacter().getName() : null,
                        diary.getContent(),
                        diary.getThumbnailUrl(),
                        diary.getHashtags() != null ? diary.getHashtags().stream()
                                .map(DiaryHashtag::getHashtag)
                                .filter(Objects::nonNull) // Filter out any null hashtags
                                .map(Hashtag::getName)
                                .collect(Collectors.toList()) : null
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDiary(@RequestBody DiaryDeleteRequest diaryDeleteRequest) {
        diaryService.deleteDiary(diaryDeleteRequest);
        return ResponseEntity.noContent().build();
    }
}
