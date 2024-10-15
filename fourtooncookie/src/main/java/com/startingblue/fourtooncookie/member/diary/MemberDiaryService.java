package com.startingblue.fourtooncookie.member.diary;

import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.service.DiaryS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberDiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryS3Service diaryS3Service;

    public void deleteDiariesByMemberId(UUID memberId) {
        List<Long> diaryIds = diaryRepository.findDiaryIdsByMemberId(memberId);
        for (Long diaryId : diaryIds) {
            diaryS3Service.deleteImagesByDiaryId(diaryId);
            diaryRepository.deleteById(diaryId);
        }
    }
}
