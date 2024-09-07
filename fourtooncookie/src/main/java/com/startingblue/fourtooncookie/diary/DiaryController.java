package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.diary.dto.request.DiaryFavoriteRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponses;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<HttpStatus> createDiary(UUID memberId,
                                                  @RequestBody final DiarySaveRequest request) {
        diaryService.createDiary(request, memberId);
        return status(HttpStatus.CREATED).build();
    }

    @GetMapping("/timeline")
    public ResponseEntity<DiarySavedResponses> readDiariesByMember (
            UUID memberId,
            @RequestParam(defaultValue = "0") @Min(0) @Max(200) final int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) final int pageSize) {
        DiarySavedResponses responses = DiarySavedResponses.of(diaryService.readDiariesByMemberId(memberId, pageNumber, pageSize));

        if (responses.diarySavedResponses().isEmpty()) {
            return noContent().build();
        }
        return ok(responses);
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity<HttpStatus> updateDiary(@PathVariable final Long diaryId,
                                            @RequestBody final DiaryUpdateRequest request) {
        diaryService.updateDiary(diaryId, request);
        return ok().build();
    }

    @PatchMapping("/{diaryId}/favorite")
    public ResponseEntity<HttpStatus> updateDiaryFavorite(@PathVariable final Long diaryId,
                                                    @RequestBody final DiaryFavoriteRequest diaryFavoriteRequest) {
        diaryService.updateDiaryFavorite(diaryId, diaryFavoriteRequest.isFavorite());
        return ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<HttpStatus> deleteDiary(@PathVariable final Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return noContent().build();
    }
}
