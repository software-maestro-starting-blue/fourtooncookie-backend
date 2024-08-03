package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.service.CharacterService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryPaintingImagesUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private static final URL DIARY_DEFAULT_IMAGE_URL; // TODO s3 기본 이미지로 수정
    private static final List<URL> DIARY_DEFAULT_IMAGE_URLS;

    static {
        try {
            DIARY_DEFAULT_IMAGE_URL = new URL("http://s3/defaultImage.png"); // todo, URL 수정
            DIARY_DEFAULT_IMAGE_URLS = List.of(DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL");
        }
    }

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final CharacterService characterService;

    public Diary findById(final Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(DiaryNoSuchElementException::new);
    }

    public void createDiary(final DiarySaveRequest request, final UUID memberId) {
        Character character = characterService.findById(request.characterId());
        Member member = memberService.readById(memberId);
        Diary diary = Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .hashtagsIds(request.hashtagIds())
                .paintingImageUrls(DIARY_DEFAULT_IMAGE_URLS)
                .character(character)
                .memberId(member.getId())
                .build();
        diaryRepository.save(diary);
        // todo vision
    }

    public List<DiarySavedResponse> readDiariesByMember(final UUID memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.readById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(foundMember.getId(), PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate")));
        return diaries.stream()
                .map(DiarySavedResponse::of)
                .toList();
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = findById(diaryId);
        Character character = characterService.findById(request.characterId());
        existedDiary.update(request.content(), request.hashtagIds(), character);
        diaryRepository.save(existedDiary);
        // todo vision
    }

    // TODO listener 로 코드 이동 예정
    public void updateDiary(Long diaryId, DiaryPaintingImagesUpdateRequest request) {
        Diary existedDiary = findById(diaryId);
        existedDiary.updatePaintingImageUrls(request.paintingImageUrls());
        diaryRepository.save(existedDiary);
    }

    public void deleteDiary(Long diaryId) {
        Diary foundDiary = findById(diaryId);
        diaryRepository.delete(foundDiary);
    }
}
