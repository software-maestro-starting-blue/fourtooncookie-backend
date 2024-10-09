package com.startingblue.fourtooncookie.diary.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findAllByMemberIdOrderByDiaryDateDesc(UUID memberId, Pageable pageable);
    boolean existsByMemberIdAndDiaryDate(UUID memberId, LocalDate diaryDate);
    @Query("SELECT d.id FROM Diary d WHERE d.memberId = :memberId")
    List<Long> findDiaryIdsByMemberId(@Param("memberId") UUID memberId);
}
