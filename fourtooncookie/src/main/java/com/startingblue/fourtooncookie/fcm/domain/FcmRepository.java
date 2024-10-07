package com.startingblue.fourtooncookie.fcm.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {

    @Query("SELECT f FROM FcmToken f JOIN f.diaries d WHERE d.id = :diaryId")
    Optional<FcmToken> findByDiaryId(Long diaryId);

    Optional<FcmToken> findByFcmToken(String token);
}
