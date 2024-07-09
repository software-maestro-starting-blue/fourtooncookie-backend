package com.startingblue.fourtooncookie.hashtag.domain.repository;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long>, HashtagRepository {

    List<Hashtag> findAllByHashtagType(HashtagType hashtagType);

}
