package com.startingblue.fourtooncookie.hashtag.service;

import com.startingblue.fourtooncookie.hashtag.domain.*;
import com.startingblue.fourtooncookie.hashtag.domain.repository.HashtagRepository;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagDeleteRequest;
import com.startingblue.fourtooncookie.hashtag.dto.request.HashtagSaveRequest;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagExistsException;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional()
public class HashtagService {

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

    public List<Hashtag> findHashtagsFromHashtagType(String hashtagTypeName) {
        HashtagType foundHashtagType = HashtagType.findFromString(hashtagTypeName);

        List<Hashtag> foundHashtags = hashtagInMemoryRepository.findAllByHashtagType(foundHashtagType);
        if (foundHashtags.isEmpty()) {
            foundHashtags = hashtagJpaRepository.findAllByHashtagType(foundHashtagType);

            if (foundHashtags == null || foundHashtags.isEmpty()) {
                throw new HashtagNotFoundException("No hashtags found for type: " + hashtagTypeName);
            }

            foundHashtags.forEach(hashtagInMemoryRepository::save);
        }

        return foundHashtags;
    }

    public void deleteHashtag(HashtagDeleteRequest hashtagDeleteRequest) {
        Long deleteHashtagId = hashtagDeleteRequest.hashtagId();
        Hashtag foundHashtag = hashtagJpaRepository.findById(deleteHashtagId)
                .orElseThrow(() -> new HashtagNotFoundException("Hashtag not found: " + deleteHashtagId));

        hashtagJpaRepository.delete(foundHashtag);
        hashtagInMemoryRepository.delete(foundHashtag);
    }
}
