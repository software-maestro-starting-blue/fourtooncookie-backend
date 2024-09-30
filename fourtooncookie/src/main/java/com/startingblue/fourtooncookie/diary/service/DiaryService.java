package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.service.CharacterService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private static final int MIN_PAINTING_IMAGE_POSITION = 0;
    private static final int MAX_PAINTING_IMAGE_POSITION = 3;

    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final CharacterService characterService;
    private final DiaryS3Service diaryS3Service;
    private final DiaryCloudFrontService diaryCloudFrontService;
    private final DiaryLambdaService diaryImageGenerationLambdaInvoker;

    public Long createDiary(final DiarySaveRequest request, final UUID memberId) {
        Member member = memberService.readById(memberId);
        Character character = characterService.readById(request.characterId());
        verifyUniqueDiary(memberId, request.diaryDate());

        Diary diary = buildDiary(request, member, character);
        diaryRepository.save(diary);
        diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(diary, character);
        return diary.getId();
    }

    private Diary buildDiary(DiarySaveRequest request, Member member, Character character) {
        return Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .paintingImageUrls(Collections.emptyList())
                .status(DiaryStatus.IN_PROGRESS)
                .character(character)
                .memberId(member.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public Diary readDiaryById(final Long diaryId) {
        Optional<Diary> foundDiary = diaryRepository.findById(diaryId);
        List<URL> preSignedUrls = generatePreSignedUrls(foundDiary.get().getId());

        foundDiary.get().updatePaintingImageUrls(preSignedUrls);
        return foundDiary.get();
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiariesByMemberId(final UUID memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.readById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(
                foundMember.getId(),
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate"))
        );

        return diaries.getContent().stream().map(savedDiary -> {
            List<URL> preSignedUrls = generatePreSignedUrls(savedDiary.getId());
            savedDiary.updatePaintingImageUrls(preSignedUrls);
            return savedDiary;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] readDiaryFullImage(final Long diaryId) throws IOException {
        return diaryS3Service.getFullImageByDiaryId(diaryId);
    }

    private List<URL> generatePreSignedUrls(Long diaryId) {
        return IntStream.rangeClosed(MIN_PAINTING_IMAGE_POSITION, MAX_PAINTING_IMAGE_POSITION)
                .mapToObj(imageGridPosition -> {
                    try {
                        return diaryS3Service.generatePresignedUrl(diaryId, imageGridPosition);
                    } catch (Exception e) {
                        log.error("Failed to generate pre-signed image URL for diaryId: {}", diaryId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void updateDiaryFavorite(Long diaryId, boolean isFavorite) {
        Diary foundDiary = readById(diaryId);
        foundDiary.updateFavorite(isFavorite);
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = readById(diaryId);
        Character character = characterService.readById(request.characterId());
        existedDiary.update(request.content(), character, DiaryStatus.IN_PROGRESS);
        diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(existedDiary, character);
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

    public void deleteDiaryByMemberId(UUID memberId) {
        diaryRepository.deleteByMemberId(memberId);
    }

    public Map<String, String> generateSignedCookiesByMemberId(UUID memberId) {
        return diaryCloudFrontService.generateSignedCookies(memberId);
    }

}
