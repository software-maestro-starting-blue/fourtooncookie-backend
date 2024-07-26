package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    ArtworkRepository artworkRepository;

    private Artwork artwork;

    @BeforeEach
    void setUp() throws MalformedURLException {
        diaryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        characterRepository.deleteAllInBatch();
        artworkRepository.deleteAllInBatch();

        artwork = new Artwork("Test Artwork", new URL("https://test.com/artwork.png"));
        artwork = artworkRepository.save(artwork);
    }

    @DisplayName("일기를 저장한다.")
    @Test
    void saveDiary() throws MalformedURLException {
        // given
        Member member = Member.builder()
                .name("testUser")
                .email("test@email.com")
                .birth(LocalDate.of(2024, 7, 23))
                .gender(Gender.OTHER)
                .role(Role.MEMBER)
                .build();
        memberRepository.save(member);

        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "Test Character", new URL("https://testImagePng.com"), "Test base prompt");
        characterRepository.save(character);

        Diary diary = Diary.builder()
                .content("Test Content")
                .isFavorite(false)
                .diaryDate(LocalDate.now())
                .paintingImageUrls(List.of(new URL("https://example.com/image.png")))
                .hashtagsIds(List.of(1L, 2L, 3L))
                .character(character)
                .memberId(member.getId())
                .build();

        // when
        Diary savedDiary = diaryRepository.save(diary);

        // then
        assertThat(savedDiary.getId()).isNotNull();
        assertThat(savedDiary.getContent()).isEqualTo("Test Content");
        assertThat(savedDiary.isFavorite()).isFalse();
        assertThat(savedDiary.getDiaryDate()).isEqualTo(LocalDate.now());
        assertThat(savedDiary.getPaintingImageUrls()).containsExactly(new URL("https://example.com/image.png"));
        assertThat(savedDiary.getHashtagsIds()).containsExactly(1L, 2L, 3L);
        assertThat(savedDiary.getCharacter()).isEqualTo(character);
        assertThat(savedDiary.getMemberId()).isEqualTo(member.getId());
    }

    @DisplayName("멤버 별 일기를 일기 날짜 내림차순으로 조회한다.")
    @Test
    void findAllByMemberWithPagination() throws MalformedURLException {
        // given
        Member member = Member.builder()
                .name("testUser")
                .email("test@email.com")
                .birth(LocalDate.of(2024, 7, 23))
                .gender(Gender.OTHER)
                .role(Role.MEMBER)
                .build();
        memberRepository.save(member);

        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "Test Character", new URL("https://testImagePng.com"), "Test base prompt");
        characterRepository.save(character);

        Diary diary1 = Diary.builder()
                .content("Test Content 1")
                .isFavorite(false)
                .diaryDate(LocalDate.of(2024, 7, 23))
                .paintingImageUrls(List.of(new URL("https://example.com/image1.png")))
                .hashtagsIds(List.of(1L))
                .character(character)
                .memberId(member.getId())
                .build();

        Diary diary2 = Diary.builder()
                .content("Test Content 2")
                .isFavorite(false)
                .diaryDate(LocalDate.of(2024, 7, 24))
                .paintingImageUrls(List.of(new URL("https://example.com/image2.png")))
                .hashtagsIds(List.of(2L))
                .character(character)
                .memberId(member.getId())
                .build();

        Diary diary3 = Diary.builder()
                .content("Test Content 3")
                .isFavorite(false)
                .diaryDate(LocalDate.of(2024, 7, 25))
                .paintingImageUrls(List.of(new URL("https://example.com/image3.png")))
                .hashtagsIds(List.of(3L))
                .character(character)
                .memberId(member.getId())
                .build();

        diaryRepository.saveAll(List.of(diary1, diary2, diary3));

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<Diary> diaryPage = diaryRepository.findAllByMemberIdOrderByDiaryDateDesc(member.getId(), pageable);

        // then
        assertThat(diaryPage.getTotalElements()).isEqualTo(3);
        assertThat(diaryPage.getContent()).hasSize(2);
        assertThat(diaryPage.getContent())
                .extracting(Diary::getContent)
                .containsExactly("Test Content 3", "Test Content 2");
    }
}
