package com.startingblue.fourtooncookie.hashtag.service;

import com.startingblue.fourtooncookie.hashtag.domain.*;
import com.startingblue.fourtooncookie.hashtag.domain.repository.HashtagRepository;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagSaveRequest;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagExistsException;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private static final Logger log = LoggerFactory.getLogger(HashtagService.class);
    private final HashtagRepository hashtagJpaRepository;

    private final HashtagRepository hashtagInMemoryRepository;

    public void createHashtag(HashtagSaveRequest hashtagSaveRequest) {
        Optional<Hashtag> foundHashtag = hashtagJpaRepository.findByNameAndHashtagType(
                hashtagSaveRequest.hashtagName(), HashtagType.findFromString(hashtagSaveRequest.hashtagType()));
        if (foundHashtag.isPresent()) {
            throw new HashtagExistsException("Hashtag with ID " + hashtagSaveRequest.hashtagName() + " already exists");
        }
        Hashtag createdHashtag = HashtagFactory.create(
                hashtagSaveRequest.hashtagName(),
                HashtagType.findFromString(hashtagSaveRequest.hashtagType())
        );
        hashtagJpaRepository.save(createdHashtag);
        hashtagInMemoryRepository.save(createdHashtag);
    }

    private Optional<Hashtag> findById(Long id) {
        Optional<Hashtag> foundHashtags = hashtagInMemoryRepository.findById(id);
        if (foundHashtags.isEmpty()) {
            log.info("hashtag cache fail");
            foundHashtags = hashtagJpaRepository.findById(id);
            if (foundHashtags.isEmpty()) {
                throw new HashtagNoSuchElementException("No hashtags found for type: " + id);
            }
            hashtagInMemoryRepository.save(foundHashtags.get());
        }
        return foundHashtags;
    }

    public List<Hashtag> findAllByHashtagIds(List<Long> hashtagIds) {
        Set<Hashtag> foundHashtags = new LinkedHashSet<>();
        for (Long hashtagId : hashtagIds) {
            Optional<Hashtag> foundHashtag = findById(hashtagId);
            if (foundHashtag.isEmpty()) {
                foundHashtag = hashtagJpaRepository.findById(hashtagId);
                if (foundHashtag.isEmpty()) {
                    throw new HashtagNoSuchElementException("No hashtags found for type: " + hashtagId);
                }
                hashtagInMemoryRepository.save(foundHashtag.get());
            }
            foundHashtags.add(foundHashtag.get());
        }
        return foundHashtags.stream().toList();
    }

    public void deleteHashtag(final Long hashtagId) {
        Hashtag foundHashtag = hashtagJpaRepository.findById(hashtagId)
                .orElseThrow(() -> new HashtagNoSuchElementException("Hashtag not found: " + hashtagId));
        hashtagJpaRepository.delete(foundHashtag);
        hashtagInMemoryRepository.delete(foundHashtag);
    }
}