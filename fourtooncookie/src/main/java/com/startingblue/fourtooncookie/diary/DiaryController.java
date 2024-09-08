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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private static final int MIN_PAINTING_IMAGE_POSITION = 1;
    private static final int MAX_PAINTING_IMAGE_POSITION = 4;

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

        DiarySavedResponses responses = DiarySavedResponses.of(diaryService.readDiariesByMemberId(memberId, pageNumber, pageSize));

        if (responses.diarySavedResponses().isEmpty()) {
            return noContent().build();
        }

        List<DiarySavedResponse> diaryResponsesWithPreSignedUrls = responses.diarySavedResponses().stream().map(savedDiary -> {
            List<String> preSignedUrls = IntStream.rangeClosed(MIN_PAINTING_IMAGE_POSITION, MAX_PAINTING_IMAGE_POSITION)
                    .mapToObj(imageGridPosition -> {
                        try {
                            return diaryImageS3Service.generatePreSignedImageUrl(savedDiary.diaryId(), imageGridPosition);
                        } catch (Exception e) {
                            log.error("Failed to generate pre-signed image url", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return new DiarySavedResponse(
                    savedDiary.diaryId(),
                    savedDiary.content(),
                    savedDiary.isFavorite(),
                    savedDiary.diaryDate(),
                    preSignedUrls,
                    savedDiary.characterId()
            );
        }).collect(Collectors.toList());

        return ok(new DiarySavedResponses(diaryResponsesWithPreSignedUrls));
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
