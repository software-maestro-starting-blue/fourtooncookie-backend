package com.startingblue.fourtooncookie.hashtag.domain.repository;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface HashtagRepository {

    Optional<Hashtag> findById(Long id);

    Optional<Hashtag> findByNameAndHashtagType(String hashtagName, HashtagType hashtagType);

    <S extends Hashtag> S save(S entity);

    void delete(Hashtag entity);

    List<Hashtag> findAllByHashtagType(HashtagType hashtagType);
}
