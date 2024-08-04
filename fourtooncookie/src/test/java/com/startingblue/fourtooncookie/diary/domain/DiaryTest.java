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
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiaryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CharacterRepository characterRepository;

    private Validator validator;
    private Artwork artwork;
    private Character character;
    private Member member;
    private UUID memberUID;
    @Autowired
    private ArtworkRepository artworkRepository;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        member = Member.builder()
                .name("John Doe")
                .birth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
        member = memberRepository.save(member);
        memberUID = member.getId();

        try {
            artwork = new Artwork("Test Artwork", new URL("https://test.png"));
            artworkRepository.save(artwork);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name("CharacterName")
                .selectionThumbnailUrl(createURL("https://character.png"))
                .basePrompt("basePrompt")
                .build();
        character = characterRepository.save(character);
    }

    @DisplayName("유효한 일기를 생성한다.")
    @Test
    void testDiaryCreationValid() {
        // given
        String validContent = "This is a valid content.";
        URL imageUrl1 = createURL("http://example.com/image1");
        URL imageUrl2 = createURL("http://example.com/image2");
        List<Long> hashtagsIds = List.of(1L, 2L);

        Diary diary = Diary.builder()
                .content(validContent)
                .diaryDate(LocalDate.now())
                .character(character)
                .memberId(member.getId())
                .paintingImageUrls(List.of(imageUrl1, imageUrl2))
                .hashtagsIds(hashtagsIds)
                .isFavorite(true)
                .build();

        // when
        Set<ConstraintViolation<Diary>> violations = validator.validate(diary);

        // then
        assertTrue(violations.isEmpty());
    }

    @DisplayName("일기 내용이 빈 경우 일기를 생성하지 못한다.")
    @Test
    void testDiaryCreationInvalidContent() {
        // given
        String emptyContent = "";

        Diary diary = Diary.builder()
                .content(emptyContent)
                .diaryDate(LocalDate.now())
                .character(character)
                .memberId(member.getId())
                .build();

        // when
        Set<ConstraintViolation<Diary>> violations = validator.validate(diary);

        // then
        assertFalse(violations.isEmpty());
    }

    @DisplayName("일기 날짜가 없는 경우 일기를 생성하지 못한다.")
    @Test
    void testDiaryCreationNullDate() {
        // given
        String validContent = "Valid content";

        Diary diary = Diary.builder()
                .content(validContent)
                .diaryDate(null)
                .character(character)
                .memberId(member.getId())
                .build();

        // when
        Set<ConstraintViolation<Diary>> violations = validator.validate(diary);

        // then
        assertFalse(violations.isEmpty());
    }

    @DisplayName("일기 캐릭터가 없는 경우 일기를 생성하지 못한다.")
    @Test
    void testDiaryCreationNullCharacter() {
        // given
        String validContent = "Valid content";

        Diary diary = Diary.builder()
                .content(validContent)
                .diaryDate(LocalDate.now())
                .character(null)
                .memberId(member.getId())
                .build();

        // when
        Set<ConstraintViolation<Diary>> violations = validator.validate(diary);

        // then
        assertFalse(violations.isEmpty());
    }

    @DisplayName("일기 멤버가 없는 경우 일기를 생성하지 못한다.")
    @Test
    void testDiaryCreationNullMember() {
        // given
        String validContent = "Valid content";

        Diary diary = Diary.builder()
                .content(validContent)
                .diaryDate(LocalDate.now())
                .character(character)
                .memberId(null)
                .build();

        // when
        Set<ConstraintViolation<Diary>> violations = validator.validate(diary);

        // then
        assertFalse(violations.isEmpty());
    }

    @DisplayName("일기 소유자를 확인 한다.")
    @Test
    void testIsOwner() {
        // given
        String validContent = "Content";

        Diary diary = Diary.builder()
                .content(validContent)
                .diaryDate(LocalDate.now())
                .character(character)
                .memberId(member.getId())
                .build();

        // then
        assertTrue(diary.isOwner(memberUID));
        UUID notExistUID = UUID.nameUUIDFromBytes("notExistUID".getBytes());
        assertFalse(diary.isOwner(notExistUID));
    }

    @DisplayName("일기 내용, 해시태그, 캐릭터 업데이트")
    @Test
    void update() {
        // given
        String initialCharacterName = "멍멍이";
        String initialCharacterUrl = "http://멍멍이.png";
        Character initialCharacter = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name(initialCharacterName)
                .selectionThumbnailUrl(createURL(initialCharacterUrl))
                .basePrompt("basePrompt")
                .build();
        characterRepository.save(initialCharacter);

        String otherMemberName = "민서";
        LocalDate otherMemberBirthDate = LocalDate.of(2000, 5, 31);
        Member otherMember = Member.builder()
                .name(otherMemberName)
                .birth(otherMemberBirthDate)
                .email("minseo@example.com")
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
        memberRepository.save(otherMember);

        LocalDate diaryDate = LocalDate.of(2024, 7, 21);
        Diary saveDiary = createDiary(diaryDate, initialCharacter, otherMember);
        diaryRepository.save(saveDiary);

        Diary savedDiary = diaryRepository.findById(saveDiary.getId()).get();

        String newCharacterName = "오동이";
        String newCharacterUrl = "http://오동이.png";
        Character newCharacter = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name(newCharacterName)
                .selectionThumbnailUrl(createURL(newCharacterUrl))
                .basePrompt("new Base Prompt")
                .build();
        characterRepository.save(newCharacter);

        // when
        String updatedContent = "새로운 일기 내용";
        List<Long> updatedHashtags = List.of(1L);
        savedDiary.update(updatedContent, updatedHashtags, newCharacter);

        // then
        assertThat(savedDiary.getContent()).isEqualTo(updatedContent);
        assertThat(savedDiary.getHashtagsIds()).isEqualTo(updatedHashtags);
        assertThat(savedDiary.getCharacter()).isEqualTo(newCharacter);
        assertThat(savedDiary.getCharacter().getName()).isEqualTo(newCharacterName);
        assertThat(savedDiary.getCharacter().getSelectionThumbnailUrl()).isEqualTo(createURL(newCharacterUrl));
        assertThat(savedDiary.getCharacter().getBasePrompt()).isEqualTo("new Base Prompt");
    }

    @DisplayName("일기 그림 이미지 URL 업데이트")
    @Test
    void updatePaintingImageUrls() {
        // given
        characterRepository.save(character);

        String otherMemberName = "민서";
        LocalDate otherMemberBirthDate = LocalDate.of(2000, 5, 31);
        Member otherMember = Member.builder()
                .name(otherMemberName)
                .birth(otherMemberBirthDate)
                .email("minseo@example.com")
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
        memberRepository.save(otherMember);

        LocalDate diaryDate = LocalDate.of(2024, 7, 21);
        Diary saveDiary = createDiary(diaryDate, character, otherMember);
        diaryRepository.save(saveDiary);

        String url1 = "http://new1.png";
        String url2 = "http://new2.png";
        String url3 = "http://new3.png";
        String url4 = "http://new4.png";
        List<URL> updatePaintingImageUrls = List.of(
                createURL(url1),
                createURL(url2),
                createURL(url3),
                createURL(url4)
        );

        // when
        saveDiary.updatePaintingImageUrls(updatePaintingImageUrls);

        // then
        assertThat(saveDiary.getPaintingImageUrls())
                .extracting(URL::toString)
                .containsExactlyInAnyOrder(
                        url1,
                        url2,
                        url3,
                        url4
                );
    }

    private Diary createDiary(LocalDate diaryDate, Character character, Member member) {
        return Diary.builder()
                .content("Initial content")
                .diaryDate(diaryDate)
                .isFavorite(false)
                .paintingImageUrls(List.of(createURL("http://defaultImage.png")))
                .hashtagsIds(List.of(1L, 2L))
                .character(character)
                .memberId(member.getId())
                .build();
    }

    private URL createURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
