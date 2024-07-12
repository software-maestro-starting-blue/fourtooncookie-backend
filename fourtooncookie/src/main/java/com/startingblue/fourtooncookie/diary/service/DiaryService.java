package com.startingblue.fourtooncookie.diary.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final HashtagService hashtagService;

    public Diary findById(Long id) {
        return diaryRepository.findById(id).orElseThrow(DiaryNoSuchElementException::new);
    }

    public void createDiary(DiarySaveRequest request, Long memberId) {
//        Character character = characterRepository.findById(diarySaveRequest.characterId())
//                .orElseThrow(() -> new RuntimeException("Character with ID " + diarySaveRequest.characterId() + " not found"));
//        Member member = memberRepository.findById(diarySaveRequest.memberId())
//                .orElseThrow(() -> new RuntimeException("Member with ID " + diarySaveRequest.memberId() + " not found"));
        List<Hashtag> foundHashtags = hashtagService.findAllByHashtagIds(request.hashtagIds());
        Diary diary = new Diary(request.content(), LocalDateTime.now(), foundHashtags,null, null);
        diaryRepository.save(diary);
    }

    public List<DiarySavedResponse> readDiaries(final DiaryPageRequest diaryPageRequest) {
        return diaryRepository.findAll(
                        PageRequest.of(diaryPageRequest.pageNumber(), diaryPageRequest.pageSize(), Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .stream()
                .map(diary -> new DiarySavedResponse(
                        diary.getContent(),
                        diary.getIsFavorite(),
                        diary.getCreatedAt().toString(),
                        diary.getModifiedAt().toString(),
                        diary.getPaintingImages().stream().map(PaintingImage::getPath).toList(),
                        diary.getHashtags().stream().map(diaryHashtag -> diaryHashtag.getHashtag().getId()).toList()
                ))
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
