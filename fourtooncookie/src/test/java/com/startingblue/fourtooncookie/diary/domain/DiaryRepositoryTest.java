package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
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
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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
        artwork = new Artwork("Test Artwork", new URL("https://test.com/artwork.png"));
        artwork = artworkRepository.save(artwork);
    }

    @DisplayName("일기를 저장한다.")
    @Test
    void saveDiary() throws MalformedURLException {
        // given
        Member member = createMember();
        Character character = createCharacter();

        String diaryContent = "Test Content";
        LocalDate diaryDate = LocalDate.now();
        URL paintingImageUrl = new URL("https://example.com/image.png");

        Diary diary = Diary.builder()
                .content(diaryContent)
                .isFavorite(false)
                .diaryDate(diaryDate)
                .paintingImageUrls(List.of(paintingImageUrl))
                .character(character)
                .memberId(member.getId())
                .build();

        // when
        Diary savedDiary = diaryRepository.save(diary);

        // then
        assertThat(savedDiary.getId()).isNotNull();
        assertThat(savedDiary.getContent()).isEqualTo(diaryContent);
        assertThat(savedDiary.isFavorite()).isFalse();
        assertThat(savedDiary.getDiaryDate()).isEqualTo(diaryDate);
        assertThat(savedDiary.getPaintingImageUrls()).containsExactly(paintingImageUrl);
        assertThat(savedDiary.getCharacter()).isEqualTo(character);
        assertThat(savedDiary.getMemberId()).isEqualTo(member.getId());
    }

    @DisplayName("멤버 별 일기를 일기 날짜 내림차순으로 조회한다.")
    @Test
    void findAllByMemberWithPagination() throws MalformedURLException {
        // given
        Member member = createMember();
        Character character = createCharacter();

        createDiary(member, character, "Test Content 1", LocalDate.of(2024, 7, 23), "https://example.com/image1.png");
        createDiary(member, character, "Test Content 2", LocalDate.of(2024, 7, 24), "https://example.com/image2.png");
        createDiary(member, character, "Test Content 3", LocalDate.of(2024, 7, 25), "https://example.com/image3.png");

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

    @DisplayName("멤버 ID와 일기 날짜로 일기 존재 여부 확인 테스트")
    @Test
    void testExistsByMemberIdAndDiaryDate() throws MalformedURLException {
        // given
        Member member = createMember();
        Character character = createCharacter();

        LocalDate diaryDate = LocalDate.now();
        createDiary(member, character, "Test Content", diaryDate, "https://example.com/image.png");

        // when
        boolean exists = diaryRepository.existsByMemberIdAndDiaryDate(member.getId(), diaryDate);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("멤버 ID와 일기 날짜로 일기 존재하지 않음 테스트")
    @Test
    void testNotExistsByMemberIdAndDiaryDate() {
        // given
        UUID memberId = UUID.randomUUID();
        LocalDate diaryDate = LocalDate.now();

        // when
        boolean exists = diaryRepository.existsByMemberIdAndDiaryDate(memberId, diaryDate);

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("일기 즐겨찾기 상태 업데이트 테스트")
    @Test
    void testUpdateFavorite() throws MalformedURLException {
        // given
        Member member = createMember();
        Character character = createCharacter();

        Diary diary = createDiary(member, character, "Test Content", LocalDate.now(), "https://example.com/image.png");

        // when
        diary.updateFavorite(true);
        diaryRepository.save(diary);

        // then
        Diary updatedDiary = diaryRepository.findById(diary.getId()).orElseThrow();
        assertThat(updatedDiary.isFavorite()).isTrue();

        // when
        diary.updateFavorite(false);
        diaryRepository.save(diary);

        // then
        updatedDiary = diaryRepository.findById(diary.getId()).orElseThrow();
        assertThat(updatedDiary.isFavorite()).isFalse();
    }

    private Member createMember() {
        Member member = Member.builder()
                .id(UUID.randomUUID())
                .name("testUser")
                .birth(LocalDate.of(2024, 7, 23))
                .gender(Gender.OTHER)
                .role(Role.MEMBER)
                .build();
        return memberRepository.save(member);
    }

    private Character createCharacter() throws MalformedURLException {
        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name("Test Character")
                .selectionThumbnailUrl(new URL("https://testImagePng.com"))
                .basePrompt("Test base prompt")
                .build();
        return characterRepository.save(character);
    }

    private Diary createDiary(Member member, Character character, String content, LocalDate date, String imageUrl) throws MalformedURLException {
        Diary diary = Diary.builder()
                .content(content)
                .isFavorite(false)
                .diaryDate(date)
                .paintingImageUrls(List.of(new URL(imageUrl)))
                .character(character)
                .memberId(member.getId())
                .build();
        return diaryRepository.save(diary);
    }
}
