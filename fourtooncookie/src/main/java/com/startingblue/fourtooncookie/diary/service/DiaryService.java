package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.diary.dto.request.DiaryPaintingImagesUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
//        memberService.save(new Member()); // TODO : 삭제 해야함.
        diaryService.createDiary(request, 1L); // TODO : 우선 디폴트 값 넣어 놓음.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<DiarySavedResponse>> readDiariesByMember (
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "0") @Min(0) @Max(200) final int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) final int pageSize) {
        Long memberId = Long.parseLong(httpRequest.getHeader("memberId")); // TODO 현재는 헤더에 넣고, jwt 를 이용코드로 변경 예정
        List<DiarySavedResponse> responses = diaryService.readDiariesByMember(memberId, pageNumber, pageSize);
        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(@PathVariable final Long diaryId,
                                            @RequestBody final DiaryUpdateRequest request) {
        diaryService.updateDiary(diaryId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{diaryId}/painting-images")
    public ResponseEntity<Void> updateDiary(@PathVariable final Long diaryId,
                                            @RequestBody final DiaryPaintingImagesUpdateRequest request) {
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
