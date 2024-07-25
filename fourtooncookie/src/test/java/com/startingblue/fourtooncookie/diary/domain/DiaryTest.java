package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        artwork = mock(Artwork.class);
        character = mock(Character.class);
        member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
    }

    @DisplayName("유효한 일기를 생성한다.")
    @Test
    void testDiaryCreationValid() throws MalformedURLException {
        // given
        Diary diary = Diary.builder()
                .content("This is a valid content.")
                .diaryDate(LocalDate.now())
                .character(character)
                .member(member)
                .paintingImageUrls(Arrays.asList(new URL("http://example.com/image1"), new URL("http://example.com/image2")))
                .hashtagsIds(Arrays.asList(1L, 2L))
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
        Diary diary = Diary.builder()
                .content("")
                .diaryDate(LocalDate.now())
                .character(character)
                .member(member)
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
        Diary diary = Diary.builder()
                .content("Valid content")
                .diaryDate(null)
                .character(character)
                .member(member)
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
        Diary diary = Diary.builder()
                .content("Valid content")
                .diaryDate(LocalDate.now())
                .character(null)
                .member(member)
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
        Diary diary = Diary.builder()
                .content("Valid content")
                .diaryDate(LocalDate.now())
                .character(character)
                .member(null)
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
        Diary diary = Diary.builder()
                .content("Content")
                .diaryDate(LocalDate.now())
                .character(character)
                .member(member)
                .build();

        // then
        assertTrue(diary.isOwner(1L));
        assertFalse(diary.isOwner(2L));
    }

    @DisplayName("일기 내용, 해시태그, 캐릭터 업데이트")
    @Test
    void update() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "멍멍이", new URL("http://멍멍이.png"), "base Prompt");
        characterRepository.save(character);

        Member member = createMember("민서", LocalDate.of(2000, 5, 31), Gender.MALE);
        memberRepository.save(member);

        Diary saveDiary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(saveDiary);

        Diary savedDiary = diaryRepository.findById(saveDiary.getId()).get();

        String newCharacterName = "오동이";
        URL newCharacterUrl = new URL("http://오동이.png");
        String newBasePrompt = "new Base Prompt";
        Character newCharacter = new Character(CharacterVisionType.STABLE_DIFFUSION, artwork, newCharacterName, newCharacterUrl, newBasePrompt);
        characterRepository.save(newCharacter);

        // when
        savedDiary.update("새로운 일기 내용", List.of(1L), newCharacter);

        // then
        assertThat(savedDiary.getContent()).isEqualTo("새로운 일기 내용");
        assertThat(savedDiary.getHashtagsIds()).isEqualTo(List.of(1L));
        assertThat(savedDiary.getCharacter()).isEqualTo(newCharacter);
        assertThat(savedDiary.getCharacter().getName()).isEqualTo(newCharacterName);
        assertThat(savedDiary.getCharacter().getSelectionThumbnailUrl()).isEqualTo(newCharacterUrl);
        assertThat(savedDiary.getCharacter().getBasePrompt()).isEqualTo(newBasePrompt);
    }

    @DisplayName("일기 그림 이미지 URL 업데이트")
    @Test
    void updatePaintingImageUrls() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "멍멍이", new URL("http://멍멍이.png"), "base Prompt");
        characterRepository.save(character);

        Member member = createMember("민서", LocalDate.of(2000, 5, 31), Gender.MALE);
        memberRepository.save(member);

        Diary saveDiary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(saveDiary);

        String url1 = "http://new1.png";
        String url2 = "http://new2.png";
        String url3 = "http://new3.png";
        String url4 = "http://new4.png";
        List<URL> updatePaintingImageUrls = List.of(new URL(url1), new URL(url2), new URL(url3), new URL(url4));

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

    private Diary createDiary(LocalDate diaryDate, Character character, Member member) throws MalformedURLException {
        return Diary.builder()
                .content("Initial content")
                .diaryDate(diaryDate)
                .isFavorite(false)
                .paintingImageUrls(List.of(new URL("http://defaultImage.png")))
                .hashtagsIds(List.of(1L, 2L))
                .character(character)
                .member(member)
                .build();
    }

    private Member createMember(String name, LocalDate birthDate, Gender gender) {
        return Member.builder()
                .name(name)
                .birth(birthDate)
                .email("temp@gmail.com")
                .gender(gender)
                .role(Role.MEMBER)
                .build();
    }
}
