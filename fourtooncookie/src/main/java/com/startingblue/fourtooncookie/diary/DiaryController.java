package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.diary.dto.request.DiaryPageRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
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

    @PostMapping
    public ResponseEntity<Void> createDiary(@RequestBody final DiarySaveRequest request) {
        diaryService.createDiary(request, 0L); // TODO : 우선 디폴트 값 넣어 놓음.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timeline/{memberId}")
    public ResponseEntity<List<DiarySavedResponse>> readDiaries(
            @PathVariable final Long memberId,
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "10") final Integer pageSize) {
        DiaryPageRequest diaryPageRequest = new DiaryPageRequest(pageNumber, pageSize);
        List<DiarySavedResponse> responses = diaryService.readDiaries(diaryPageRequest, memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(@PathVariable final Long diaryId, @RequestBody final DiaryUpdateRequest request) {
        diaryService.updateDiary(diaryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable final Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }
}
