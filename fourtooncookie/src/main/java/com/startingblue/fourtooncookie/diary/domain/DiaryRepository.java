package com.startingblue.fourtooncookie.diary.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findAllByMemberId(Long memberId, Pageable pageable);
}
