package com.startingblue.fourtooncookie.diary;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.CharacterService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.diary.dto.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.exception.DiaryDuplicateException;
import com.startingblue.fourtooncookie.diary.exception.DiaryNotFoundException;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.startingblue.fourtooncookie.diary.listener.DiarySQSMessageListener.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private static final int MIN_PAINTING_IMAGE_POSITION = 0;
    private static final int MAX_PAINTING_IMAGE_POSITION = 3;
    private static final int MAX_PAINTING_IMAGE_SIZE = 4;

    private final DiaryRepository diaryRepository;
    private final CharacterService characterService;
    private final DiaryS3Service diaryS3Service;
    private final DiaryPaintingImageCloudFrontService diaryPaintingImageCloudFrontService;
    private final DiaryLambdaService diaryImageGenerationLambdaInvoker;

    public Long createDiary(final DiarySaveRequest request, final UUID memberId) {
        Character character = characterService.readById(request.characterId());

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
    public Diary readDiaryById(final Long diaryId) {
        Optional<Diary> foundDiary = diaryRepository.findById(diaryId);
        if (foundDiary.get().isCompleted()) {
            List<URL> signedUrls = generateSignedUrls(foundDiary.get().getId());
            foundDiary.get().updatePaintingImageUrls(signedUrls);
        }
        return foundDiary.get();
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiariesByMemberId(final UUID memberId, final int pageNumber, final int pageSize) {
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
    public byte[] readDiaryFullImage(final Long diaryId) throws IOException {
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

    public void updateDiaryFavorite(Long diaryId, boolean isFavorite) {
        Diary foundDiary = readById(diaryId);
        foundDiary.updateFavorite(isFavorite);
    }

    public void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary existedDiary = readById(diaryId);
        Character character = characterService.readById(request.characterId());
        existedDiary.update(request.content(), character);
        diaryImageGenerationLambdaInvoker.invokeDiaryImageGenerationLambda(existedDiary, character);
        diaryPaintingImageCloudFrontService.invalidateCache(diaryId);
    }

    public void deleteDiaryById(Long diaryId) {
        Diary foundDiary = readById(diaryId);
        diaryS3Service.deleteImagesByDiaryId(diaryId);
        diaryRepository.delete(foundDiary);
    }

    @Transactional(readOnly = true)
    public Diary readById(final Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(DiaryNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public boolean verifyDiaryOwner(UUID memberId, Long diaryId) {
        Diary foundDiary = readById(diaryId);
        return foundDiary.isOwner(memberId);
    }

    @Transactional
    public boolean existsById(Long diaryId) {
        if (diaryId == null) return false;
        return diaryRepository.existsById(diaryId);
    }

    @Transactional
    public void processImageGenerationResponse(DiaryImageResponseMessage response) {
        Diary diary = diaryRepository.findById(response.diaryId()).get();
        diary.updateImageGenerationStatusAtIndex(response.gridPosition(), response.isSuccess());
    }

}
