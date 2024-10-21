package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.diary.dto.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryCreatedResponse> postDiary(UUID memberId,
                                                          @RequestBody final DiarySaveRequest request) {
        DiaryCreatedResponse createdDiaryId = new DiaryCreatedResponse(diaryService.addDiary(request, memberId));
        return ResponseEntity.ok(createdDiaryId);
    }

    @GetMapping("/timeline")
    public ResponseEntity<DiarySavedResponses> getDiaries(
            UUID memberId,
            @RequestParam(defaultValue = "0") @Min(0) @Max(200) final int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) final int pageSize) {

        DiarySavedResponses responses = DiarySavedResponses.of(diaryService.getDiariesByMemberId(memberId, pageNumber, pageSize));

        if (responses.diarySavedResponses().isEmpty()) {
            return noContent().build();
        }
        return ok(responses);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<DiarySavedResponse> getDiary(
            @PathVariable final Long diaryId) {
        DiarySavedResponse response = DiarySavedResponse.of(diaryService.getById(diaryId));
        return ok(response);
    }

    @GetMapping("/{diaryId}/image/full")
    public ResponseEntity<byte[]> getDiaryFullImage(@PathVariable final Long diaryId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(diaryService.getDiaryFullImage(diaryId));
    }

    @PatchMapping("/{diaryId}/favorite")
    public ResponseEntity<HttpStatus> patchDiaryFavorite(@PathVariable final Long diaryId,
                                                         @RequestBody final DiaryFavoriteRequest diaryFavoriteRequest) {
        diaryService.modifyDiaryFavorite(diaryId, diaryFavoriteRequest.isFavorite());
        return ok().build();
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity<HttpStatus> putDiary(@PathVariable final Long diaryId,
                                               @RequestBody final DiaryUpdateRequest request) {
        diaryService.modifyDiary(diaryId, request);
        return ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<HttpStatus> deleteDiary(@PathVariable final Long diaryId) {
        diaryService.removeDiaryById(diaryId);
        return noContent().build();
    }

}
