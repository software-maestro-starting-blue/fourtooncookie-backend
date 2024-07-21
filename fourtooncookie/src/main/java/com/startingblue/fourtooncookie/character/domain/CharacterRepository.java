package com.startingblue.fourtooncookie.character.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    List<Character> findAllByModelType(ModelType modelType);
}
