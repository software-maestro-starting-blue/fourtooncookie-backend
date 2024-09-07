package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.service.CharacterService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.exception.DiaryDuplicateException;
import com.startingblue.fourtooncookie.diary.exception.DiaryNotFoundException;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final CharacterService characterService;

    public void createDiary(final DiarySaveRequest request, final UUID memberId) {
        Member member = memberService.readById(memberId);
        Character character = characterService.readById(request.characterId());
        verifyUniqueDiary(memberId, request.diaryDate());

        Diary diary = Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .paintingImageUrls(Collections.emptyList())
                .character(character)
                .memberId(member.getId())
                .build();
        diaryRepository.save(diary);
        // todo vision
    }


    @Transactional(readOnly = true)
    public List<Diary> readDiariesByMemberId(final UUID memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.readById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(
                foundMember.getId(),
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate"))
        );
        return diaries.getContent();
    }

    public void updateDiaryFavorite(Long diaryId, boolean isFavorite) {
        Diary foundDiary = readById(diaryId);
        foundDiary.updateFavorite(isFavorite);
        diaryRepository.save(foundDiary);
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = readById(diaryId);
        Character character = characterService.readById(request.characterId());

        existedDiary.update(request.content(), character);
        diaryRepository.save(existedDiary);
        // todo vision
    }

    public void deleteDiary(Long diaryId) {
        Diary foundDiary = readById(diaryId);
        diaryRepository.delete(foundDiary);
    }

    @Transactional(readOnly = true)
    public Diary readById(final Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(DiaryNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void verifyUniqueDiary(UUID memberId, LocalDate diaryDate) {
        if (diaryRepository.existsByMemberIdAndDiaryDate(memberId, diaryDate)) {
            throw new DiaryDuplicateException("이미 " + diaryDate + "에 일기를 작성하셨습니다.");
        }
    }

    @Transactional(readOnly = true)
    public boolean verifyDiaryOwner(UUID memberId, Long diaryId) {
        Diary foundDiary = readById(diaryId);
        return foundDiary.isOwner(memberId);
    }
}
