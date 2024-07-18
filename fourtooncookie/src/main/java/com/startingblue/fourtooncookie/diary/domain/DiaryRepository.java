package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findAllByMember(Member member, Pageable pageable);
}
