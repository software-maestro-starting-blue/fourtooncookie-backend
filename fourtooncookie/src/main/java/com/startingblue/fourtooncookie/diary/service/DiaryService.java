package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private static final URL DIARY_DEFAULT_IMAGE_URL; // TODO s3 기본 이미지로 수정
    private static final List<URL> DIARY_DEFAULT_IMAGE_URLS;

    static {
        try {
            DIARY_DEFAULT_IMAGE_URL = new URL("http://s3/defaultImage.png");
            DIARY_DEFAULT_IMAGE_URLS = List.of(DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL, DIARY_DEFAULT_IMAGE_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL");
        }
    }

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
        Diary diary = Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .hashtagsIds(request.hashtagIds())
                .paintingImageUrls(new ArrayList<>())
                .character(null) // todo : character 로 변경
                .member(member)
                .build();
        diaryRepository.save(diary);
    }

    public List<DiarySavedResponse> readDiariesByMember(final Long memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.findById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMember(foundMember, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate")));
        return diaries.stream()
                .map(DiarySavedResponse::of)
                .toList();
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = findById(diaryId);

//        Character character = characterServer.findById(request.characterId()); //TODO 주석 제거
        existedDiary.update(request.content(), DIARY_DEFAULT_IMAGE_URLS,request.hashtagIds(), null); // todo: null 을 character 로 변경
        diaryRepository.save(existedDiary);
    }

    public void deleteDiary(Long diaryId) {
        Diary foundDiary = findById(diaryId);
        diaryRepository.delete(foundDiary);
    }
}
