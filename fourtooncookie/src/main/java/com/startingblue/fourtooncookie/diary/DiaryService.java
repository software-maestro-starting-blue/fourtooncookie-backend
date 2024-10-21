package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.diary.dto.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.exception.DiaryNotFoundException;
import com.startingblue.fourtooncookie.diary.service.DiaryCharacterService;
import com.startingblue.fourtooncookie.diary.service.DiaryLambdaService;
import com.startingblue.fourtooncookie.diary.service.DiaryPaintingImageCloudFrontService;
import com.startingblue.fourtooncookie.diary.service.DiaryS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.startingblue.fourtooncookie.diary.listener.DiarySQSMessageListener.DiaryImageResponseMessage;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private static final int MIN_PAINTING_IMAGE_POSITION = 0;
    private static final int MAX_PAINTING_IMAGE_POSITION = 3;
    private static final int MAX_PAINTING_IMAGE_SIZE = 4;

    private final DiaryRepository diaryRepository;
    private final DiaryCharacterService diaryCharacterService;
    private final DiaryS3Service diaryS3Service;
    private final DiaryPaintingImageCloudFrontService diaryPaintingImageCloudFrontService;
    private final DiaryLambdaService diaryImageGenerationLambdaInvoker;

    public Long addDiary(final DiarySaveRequest request, final UUID memberId) {
        Character character = diaryCharacterService.readById(request.characterId());
        
        Diary diary = buildDiary(request, memberId, character);
        diaryRepository.save(diary);
        diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(diary, character);
        return diary.getId();
    }

    private Diary buildDiary(DiarySaveRequest request, UUID memberId, Character character) {
        return Diary.builder()
                .content(request.content())
                .isFavorite(false)
                .diaryDate(request.diaryDate())
                .paintingImageUrls(Collections.emptyList())
                .paintingImageGenerationStatuses(new ArrayList<>(Collections.nCopies(MAX_PAINTING_IMAGE_SIZE, DiaryPaintingImageGenerationStatus.GENERATING)))
                .status(DiaryStatus.IN_PROGRESS)
                .character(character)
                .memberId(memberId)
                .build();
    }

    @Transactional(readOnly = true)
    public Diary getById(final Long diaryId) {
        Diary foundDiary = diaryRepository.findById(diaryId).orElseThrow(DiaryNotFoundException::new);
        if (foundDiary.isCompleted()) {
            List<URL> signedUrls = generateSignedUrls(foundDiary.getId());
            foundDiary.updatePaintingImageUrls(signedUrls);
        }
        return foundDiary;
    }

    @Transactional(readOnly = true)
    public List<Diary> getDiariesByMemberId(final UUID memberId, final int pageNumber, final int pageSize) {
        Page<Diary> diaries = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(
                memberId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate"))
        );

        return diaries.getContent().stream().map(savedDiary -> {
            if (savedDiary.isCompleted()) {
                List<URL> signedUrls = generateSignedUrls(savedDiary.getId());
                savedDiary.updatePaintingImageUrls(signedUrls);
            }
            return savedDiary;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] getDiaryFullImage(final Long diaryId) throws IOException {
        return diaryS3Service.getFullImageByDiaryId(diaryId);
    }

    private List<URL> generateSignedUrls(Long diaryId) {
        return IntStream.rangeClosed(MIN_PAINTING_IMAGE_POSITION, MAX_PAINTING_IMAGE_POSITION)
                .mapToObj(imageGridPosition -> {
                    try {
                        return diaryPaintingImageCloudFrontService.generateSignedUrl(diaryId, imageGridPosition);
                    } catch (Exception e) {
                        log.error("Failed to generate signed image URL for diaryId: {}", diaryId, e);
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void modifyDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = getById(diaryId);
        Character character = diaryCharacterService.readById(request.characterId());
        existedDiary.update(request.content(), character);
        diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(existedDiary, character);
        diaryPaintingImageCloudFrontService.invalidateCache(diaryId);
    }

    public void modifyDiaryFavorite(Long diaryId, boolean isFavorite) {
        Diary foundDiary = getById(diaryId);
        foundDiary.updateFavorite(isFavorite);
    }

    public void removeDiaryById(Long diaryId) {
        Diary foundDiary = getById(diaryId);
        diaryS3Service.deleteImagesByDiaryId(diaryId);
        diaryRepository.delete(foundDiary);
    }

    @Transactional(readOnly = true)
    public boolean isDiaryOwner(UUID memberId, Long diaryId) {
        return getById(diaryId).isOwner(memberId);
    }

    @Transactional
    public boolean existsById(Long diaryId) {
        return diaryRepository.existsById(diaryId);
    }

    @Transactional
    public void processImageGenerationResponse(DiaryImageResponseMessage response) {
        Diary diary = diaryRepository.findById(response.diaryId()).get();
        diary.updateImageGenerationStatusAtIndex(response.gridPosition(), response.isSuccess());
    }

}
