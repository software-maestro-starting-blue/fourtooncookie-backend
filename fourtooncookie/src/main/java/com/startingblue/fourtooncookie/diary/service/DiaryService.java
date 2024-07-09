package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryDeleteRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;
import com.startingblue.fourtooncookie.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final HashtagService hashtagService;

    public void createDiary(DiarySaveRequest diarySaveRequest) {
//        Optional<Member> memberOptional = memberRepository.findById(diarySaveRequest.memberId());
//        if (memberOptional.isEmpty()) {
//            throw new RuntimeException("Member with ID " + diarySaveRequest.memberId() + " not found");
//        }
//
//        Member member = memberOptional.get();
        Diary diary = new Diary(null, null, diarySaveRequest.content(), diarySaveRequest.thumbnailUrl());

        for (Long hashtagId : diarySaveRequest.hashtagIds()) {
            Optional<Hashtag> hashtagOptional = hashtagService.findById(hashtagId);
            if (hashtagOptional.isPresent()) {
                Hashtag hashtag = hashtagOptional.get();
                diary.addHashtag(hashtag);
            } else {
                throw new RuntimeException("Hashtag with ID " + hashtagId + " not found");
            }
        }

        diaryRepository.save(diary);
    }

    public Page<Diary> getDiaries(int page, int size) {
        return diaryRepository.findAll(PageRequest.of(page, size));
    }

    public void deleteDiary(DiaryDeleteRequest diaryDeleteRequest) {
        Long deleteDiaryId = diaryDeleteRequest.diaryId();
        Diary foundHashtag = diaryRepository.findById(deleteDiaryId)
                .orElseThrow(() -> new DiaryNoSuchElementException("diary not found: " + deleteDiaryId));

        diaryRepository.delete(foundHashtag);
    }
}
