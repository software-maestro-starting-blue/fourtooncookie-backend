package com.startingblue.fourtooncookie.member.service;

import com.startingblue.fourtooncookie.diary.DiaryRepository;
import com.startingblue.fourtooncookie.diary.DiaryService;
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
    private final DiaryService diaryService;

    public void removeDiariesByMemberId(UUID memberId) {
        List<Long> diaryIds = diaryRepository.findDiaryIdsByMemberId(memberId);
        for (Long diaryId : diaryIds) {
            diaryService.removeDiaryById(diaryId);
        }
    }
}
