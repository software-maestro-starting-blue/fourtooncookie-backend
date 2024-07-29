package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.character.domain.PaymentType;
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
        String memberName = "testUser";
        String memberEmail = "test@email.com";
        LocalDate memberBirthDate = LocalDate.of(2024, 7, 23);
        Gender memberGender = Gender.OTHER;
        Role memberRole = Role.MEMBER;

        Member member = Member.builder()
                .name(memberName)
                .email(memberEmail)
                .birth(memberBirthDate)
                .gender(memberGender)
                .role(memberRole)
                .build();
        memberRepository.save(member);

        String characterName = "Test Character";
        URL characterUrl = new URL("https://testImagePng.com");
        String basePrompt = "Test base prompt";

        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name(characterName)
                .selectionThumbnailUrl(characterUrl)
                .basePrompt(basePrompt)
                .build();
        characterRepository.save(character);

        String diaryContent = "Test Content";
        LocalDate diaryDate = LocalDate.now();
        URL paintingImageUrl = new URL("https://example.com/image.png");
        List<Long> hashtagsIds = List.of(1L, 2L, 3L);

        Diary diary = Diary.builder()
                .content(diaryContent)
                .isFavorite(false)
                .diaryDate(diaryDate)
                .paintingImageUrls(List.of(paintingImageUrl))
                .hashtagsIds(hashtagsIds)
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
        assertThat(savedDiary.getHashtagsIds()).containsExactlyElementsOf(hashtagsIds);
        assertThat(savedDiary.getCharacter()).isEqualTo(character);
        assertThat(savedDiary.getMemberId()).isEqualTo(member.getId());
    }

    @DisplayName("멤버 별 일기를 일기 날짜 내림차순으로 조회한다.")
    @Test
    void findAllByMemberWithPagination() throws MalformedURLException {
        // given
        String memberName = "testUser";
        String memberEmail = "test@email.com";
        LocalDate memberBirthDate = LocalDate.of(2024, 7, 23);
        Gender memberGender = Gender.OTHER;
        Role memberRole = Role.MEMBER;

        Member member = Member.builder()
                .name(memberName)
                .email(memberEmail)
                .birth(memberBirthDate)
                .gender(memberGender)
                .role(memberRole)
                .build();
        memberRepository.save(member);

        String characterName = "Test Character";
        URL characterUrl = new URL("https://testImagePng.com");
        String basePrompt = "Test base prompt";

        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name(characterName)
                .selectionThumbnailUrl(characterUrl)
                .basePrompt(basePrompt)
                .build();
        characterRepository.save(character);

        String content1 = "Test Content 1";
        LocalDate date1 = LocalDate.of(2024, 7, 23);
        URL imageUrl1 = new URL("https://example.com/image1.png");
        List<Long> hashtagsIds1 = List.of(1L);

        String content2 = "Test Content 2";
        LocalDate date2 = LocalDate.of(2024, 7, 24);
        URL imageUrl2 = new URL("https://example.com/image2.png");
        List<Long> hashtagsIds2 = List.of(2L);

        String content3 = "Test Content 3";
        LocalDate date3 = LocalDate.of(2024, 7, 25);
        URL imageUrl3 = new URL("https://example.com/image3.png");
        List<Long> hashtagsIds3 = List.of(3L);

        Diary diary1 = Diary.builder()
                .content(content1)
                .isFavorite(false)
                .diaryDate(date1)
                .paintingImageUrls(List.of(imageUrl1))
                .hashtagsIds(hashtagsIds1)
                .character(character)
                .memberId(member.getId())
                .build();

        Diary diary2 = Diary.builder()
                .content(content2)
                .isFavorite(false)
                .diaryDate(date2)
                .paintingImageUrls(List.of(imageUrl2))
                .hashtagsIds(hashtagsIds2)
                .character(character)
                .memberId(member.getId())
                .build();

        Diary diary3 = Diary.builder()
                .content(content3)
                .isFavorite(false)
                .diaryDate(date3)
                .paintingImageUrls(List.of(imageUrl3))
                .hashtagsIds(hashtagsIds3)
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
                .containsExactly(content3, content2);
    }
}
