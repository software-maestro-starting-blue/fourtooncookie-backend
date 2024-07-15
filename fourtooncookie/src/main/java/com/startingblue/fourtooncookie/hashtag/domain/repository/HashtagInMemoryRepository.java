package com.startingblue.fourtooncookie.hashtag.domain.repository;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagType;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Transactional
@EqualsAndHashCode
public class HashtagInMemoryRepository implements HashtagRepository {

    private final Map<Long, Hashtag> hashtags = new ConcurrentHashMap<>();

    @Override
    public Optional<Hashtag> findById(Long id) {
        return Optional.ofNullable(hashtags.get(id));
    }

    @Override
    public Optional<Hashtag> findByName(String hashtagName) {
        return hashtags.values().stream()
                .filter(hashtag -> hashtag.getName().equals(hashtagName))
                .findFirst();
    }

    @Override
    public Optional<Hashtag> findByNameAndHashtagType(String hashtagName, HashtagType hashtagType) {
        return hashtags.values().stream()
                .filter(hashtag -> hashtag.getName().equals(hashtagName) && hashtag.getHashtagType().equals(hashtagType))
                .findFirst();
    }

    @Override
    public <S extends Hashtag> S save(S entity) {
        hashtags.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void delete(Hashtag hashtag) {
        hashtags.remove(hashtag.getId());
    }

    @Override
    public List<Hashtag> findAllByHashtagType(HashtagType type) {
        List<Hashtag> result = new ArrayList<>();
        for (Hashtag hashtag : hashtags.values()) {
            if (hashtag.getHashtagType() == type) {
                result.add(hashtag);
            }
        }
        return result;
    }
}
