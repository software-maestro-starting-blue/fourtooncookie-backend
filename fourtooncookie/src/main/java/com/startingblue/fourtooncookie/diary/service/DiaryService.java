package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload.DiaryImageGenerationLambdaInvoker;
import com.startingblue.fourtooncookie.aws.s3.service.DiaryImageS3Service;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    private final DiaryImageS3Service diaryImageS3Service;
    private final DiaryImageGenerationLambdaInvoker diaryImageGenerationLambdaInvoker;

    public Long createDiary(final DiarySaveRequest request, final UUID memberId) {
        Member member = memberService.readById(memberId);
        Character character = characterService.readById(request.characterId());
        verifyUniqueDiary(memberId, request.diaryDate());

        Diary diary = buildDiary(request, member, character);
        createDiaryAndInvokeLambda(diary, character);
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

    public void createDiaryAndInvokeLambda(Diary diary, Character character) {
        try {
            diaryRepository.save(diary);
            diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(diary, character);

            diary.updateDiaryStatus(DiaryStatus.COMPLETED);
        } catch (Exception e) {
            log.error("Lambda 호출 중 오류 발생: {}", e.getMessage());
            handleLambdaInvocationFailure(diary);
        }
    }

    private void handleLambdaInvocationFailure(Diary diary) {
        diary.update("일기 생성 중 오류가 발생했습니다. 일기를 삭제 후 다시 생성해 주세요.",
                diary.getCharacter(),
                DiaryStatus.FAILED);
        diaryRepository.save(diary);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiariesByMemberId(final UUID memberId, final int pageNumber, final int pageSize) {
        Member foundMember = memberService.readById(memberId);
        Page<Diary> diaries = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(
                foundMember.getId(),
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate"))
        );

        return diaries.getContent().stream().map(savedDiary -> {
            List<URL> preSignedUrls = IntStream.rangeClosed(MIN_PAINTING_IMAGE_POSITION, MAX_PAINTING_IMAGE_POSITION)
                    .mapToObj(imageGridPosition -> {
                        try {
                             return diaryImageS3Service.generatePreSignedImageUrl(savedDiary.getId(), imageGridPosition);
                        } catch (Exception e) {
                            log.error("Failed to generate pre-signed image url", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            savedDiary.updatePaintingImageUrls(preSignedUrls);
            return savedDiary;
        }).collect(Collectors.toList());
    }

    public void updateDiaryFavorite(Long diaryId, boolean isFavorite) {
        Diary foundDiary = readById(diaryId);
        foundDiary.updateFavorite(isFavorite);
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = readById(diaryId);
        Character character = characterService.readById(request.characterId());
        existedDiary.update(request.content(), character, DiaryStatus.IN_PROGRESS);
        createDiaryAndInvokeLambda(existedDiary, character);
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
}
