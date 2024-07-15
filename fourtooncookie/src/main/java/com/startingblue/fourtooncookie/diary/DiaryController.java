package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.diary.dto.request.DiaryPageRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> createDiary(@RequestBody final DiarySaveRequest request) {
        memberService.save(new Member());
        diaryService.createDiary(request, 1L); // TODO : 우선 디폴트 값 넣어 놓음.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<DiarySavedResponse>> readAllDiaries() {
        return ResponseEntity.ok(diaryService.readAllDiaries());
    }

    @GetMapping("/timeline/{memberId}")
    public ResponseEntity<List<DiarySavedResponse>> readDiariesByMember (
            @PathVariable final Long memberId,
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer pageSize) {
        DiaryPageRequest diaryPageRequest = new DiaryPageRequest(pageNumber, pageSize);
        List<DiarySavedResponse> responses = diaryService.readDiariesByMember(diaryPageRequest, memberId);
        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(@PathVariable final Long diaryId, @RequestBody final DiaryUpdateRequest request) {
        diaryService.updateDiary(diaryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable final Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/favorite/{diaryId}")
    public ResponseEntity<Void> favoriteDiary(@PathVariable final Long diaryId, @RequestBody final boolean isFavorite) {
        log.info("favorite: {} diary {}", isFavorite, diaryId);
        return ResponseEntity.ok().build();
    }
}
