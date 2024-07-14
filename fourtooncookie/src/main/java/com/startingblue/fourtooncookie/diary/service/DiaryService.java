package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.DiaryHashtag;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryPageRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.service.HashtagService;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final HashtagService hashtagService;
    private final MemberService memberService;

    public Diary findById(final Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(DiaryNoSuchElementException::new);
    }

    public void createDiary(final DiarySaveRequest request, final Long memberId) {
//        Character character = characterRepository.findById(request.characterId())
//                .orElseThrow(() -> new RuntimeException("Character with ID " + diarySaveRequest.characterId() + " not found"));
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member with ID " + memberId + " not found"));

        Diary diary = Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .hashtags(new ArrayList<>())
                .paintingImages(new ArrayList<>())
                .character(null)
                .member(member)
                .build();
        List<Hashtag> foundHashtags = hashtagService.findAllByHashtagIds(request.hashtagIds());
        diary.updateHashtags(foundHashtags);
        diaryRepository.save(diary);
        log.info("Create diary: {}", diary);
    }

    public List<DiarySavedResponse> readDiariesByMember(final DiaryPageRequest request, final Long memberId) {
        return diaryRepository.findAllByMemberId(memberId,
                        PageRequest.of(request.pageNumber(), request.pageSize(), Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(DiarySavedResponse::of)
                .toList();
    }

    public List<DiarySavedResponse> readAllDiaries() {
        return diaryRepository.findAll()
                .stream()
                .map(DiarySavedResponse::of)
                .toList();
    }


    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = diaryRepository.findById(diaryId)
                        .orElseThrow(DiaryNoSuchElementException::new);

        List<Hashtag> foundHashtags = hashtagService.findAllByHashtagIds(request.hashtagIds());
        LocalDateTime modifiedAt = LocalDateTime.now();
//        Character character = characterServer.findById(request.characterId());
        existedDiary.update(request.content(), request.isFavorite(), modifiedAt, foundHashtags, null);
        diaryRepository.save(existedDiary);
    }

    public void deleteDiary(Long diaryId) {
        Diary foundDiary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNoSuchElementException("diary not found: " + diaryId));
        diaryRepository.delete(foundDiary);
    }
}
