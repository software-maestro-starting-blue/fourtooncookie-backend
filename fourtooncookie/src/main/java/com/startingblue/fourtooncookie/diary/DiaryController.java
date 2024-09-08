package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.aws.s3.service.DiaryImageS3Service;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryFavoriteRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponses;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    private final DiaryImageS3Service diaryImageS3Service;

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

        List<Diary> diaries = diaryService.readDiariesByMemberId(memberId, pageNumber, pageSize);

        if (diaries.isEmpty()) {
            return noContent().build();
        }

        DiarySavedResponses responses = DiarySavedResponses.of(diaries);

        List<DiarySavedResponse> diaryResponsesWithPreSignedUrls = new ArrayList<>();
        for (DiarySavedResponse savedDiary : responses.diarySavedResponses()) {
            List<String> preSignedUrls = new ArrayList<>();

            for (int imageGridPosition = 1; imageGridPosition <= 4; imageGridPosition++) {
                try {
                    String preSignedUrl = diaryImageS3Service.generatePreSignedImageUrl(savedDiary.diaryId(), imageGridPosition);
                    preSignedUrls.add(preSignedUrl);
                } catch (Exception e) {
                    log.error("Failed to generate pre-signed image url", e);
                }
            }

            preSignedUrls.removeIf(Objects::isNull);

            DiarySavedResponse updatedDiary = new DiarySavedResponse(
                    savedDiary.diaryId(),
                    savedDiary.content(),
                    savedDiary.isFavorite(),
                    savedDiary.diaryDate(),
                    preSignedUrls,
                    savedDiary.characterId()
            );

            diaryResponsesWithPreSignedUrls.add(updatedDiary);
        }

        DiarySavedResponses updatedResponses = new DiarySavedResponses(diaryResponsesWithPreSignedUrls);
        return ok(updatedResponses);
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
