package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;

    public Diary findById(final Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(DiaryNoSuchElementException::new);
    }

    public void createDiary(final DiarySaveRequest request, final Long memberId) {
        // TODO
//        Character character = characterService.findById(request.characterId())
        Member member = memberService.findById(memberId);
        Diary diary = buildDiary(request, member);
        diary.updateHashtags(Hashtag.findHashtagsByIds(request.hashtagIds()));
        diaryRepository.save(diary);
    }

    private static Diary buildDiary(DiarySaveRequest request, Member member) {
        return Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .hashtagsIds(new ArrayList<>())
                .paintingImages(new ArrayList<>())
                .character(null)
                .member(member)
                .build();
    }

    public List<DiarySavedResponse> readDiariesByMember(final Long memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.findById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMemberOrderByDiaryDateDesc(foundMember, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate")));
        return diaries.stream()
                .map(DiarySavedResponse::of)
                .toList();
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = diaryRepository.findById(diaryId)
                        .orElseThrow(DiaryNoSuchElementException::new);

        LocalDateTime modifiedAt = LocalDateTime.now();
        List<Hashtag> foundHashtags = Hashtag.findHashtagsByIds(request.hashtagIds());
//        Character character = characterServer.findById(request.characterId()); //TODO
        existedDiary.update(request.content(), modifiedAt, foundHashtags, null);
        diaryRepository.save(existedDiary);
    }

    public void deleteDiary(Long diaryId) {
        Diary foundDiary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNoSuchElementException("diary not found: " + diaryId));
        diaryRepository.delete(foundDiary);
    }
}
