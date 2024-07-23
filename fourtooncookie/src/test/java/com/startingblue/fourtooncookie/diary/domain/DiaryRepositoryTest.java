package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("특정 멤버의 페이징된 일기를 작성날짜 내림차순으로 가져온다.")
    @Test
    void findAllByMemberOrderByDiaryDateDesc() {
        // given
        Member member1 = new Member();
        memberRepository.save(member1);

        Diary diary1 = createDiary(member1, "첫번째 일기", false, LocalDateTime.of(2024, 7, 20, 14, 30));
        Diary diary2 = createDiary(member1, "두번째 일기", false, LocalDateTime.of(2024, 7, 20, 15, 0));
        Diary diary3 = createDiary(member1, "마지막 일기", true, LocalDateTime.of(2024, 7, 20, 15, 30));
        diaryRepository.saveAll(List.of(diary1, diary2, diary3));

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "diaryDate"));

        // when
        Page<Diary> diaries = diaryRepository.findAllByMemberOrderByDiaryDateDesc(member1, pageable);

        // then
        assertThat(diaries).isNotNull();
        assertThat(diaries.getContent()).hasSize(3);
        assertThat(diaries.getContent().get(0).getContent()).isEqualTo("마지막 일기"); // 최신 일기 내용 확인
        assertThat(diaries.getContent().get(2).getContent()).isEqualTo("첫번째 일기"); // 오래된 일기 내용 확인

        // 페이지 관련 정보 검증
        assertThat(diaries.getNumber()).isEqualTo(pageNumber);
        assertThat(diaries.getSize()).isEqualTo(pageSize);
        assertThat(diaries.getSort().getOrderFor("diaryDate").getDirection()).isEqualTo(Sort.Direction.DESC);

        // 일기 날짜가 내림차순으로 정렬되었는지 검증
        assertThat(diaries.getContent().get(0).getDiaryDate()).isAfter(diaries.getContent().get(2).getDiaryDate());
    }

    private Diary createDiary(Member member, String content, boolean isFavorite, LocalDateTime current) {
        return Diary.builder()
                .content(content)
                .isFavorite(isFavorite)
                .member(member)
                .character(null)
                .diaryDate(current)
                .createdAt(current)
                .modifiedAt(current)
                .build();
    }
}