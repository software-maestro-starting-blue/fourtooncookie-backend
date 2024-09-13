package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.character.domain.PaymentType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponses;
import com.startingblue.fourtooncookie.diary.exception.DiaryNotFoundException;
import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    ArtworkRepository artworkRepository;

    private Member member;
    private Character character;
    private Artwork artwork;

    @BeforeEach
    void setUp() throws MalformedURLException {
        diaryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        characterRepository.deleteAllInBatch();

        artwork = new Artwork("artwork title", new URL("https://artwork.png"));
        artworkRepository.save(artwork);

        character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .name("멍멍이")
                .artwork(artwork)
                .selectionThumbnailUrl(new URL("https://멍멍이.png"))
                .basePrompt("base prompt")
                .build();
        characterRepository.save(character);

        member = createMember("민서", LocalDate.of(2000, 5, 31), Gender.MALE);
        memberRepository.save(member);
    }

    @DisplayName("저장된 일기를 삭제한다.")
    @Test
    void deleteDiaryTest() throws MalformedURLException {
        // given
        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        // when
        diaryService.deleteDiary(diary.getId());

        // then
        boolean exists = diaryRepository.existsById(diary.getId());
        assertThat(exists).isFalse();
    }

    @DisplayName("저장된 일기를 ID로 가져온다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        // when
        Diary foundDiary = diaryService.readById(diary.getId());

        // then
        assertThat(foundDiary).isEqualTo(diary);
    }

    @DisplayName("존재하지 않는 일기는 찾을 수 없다.")
    @Test
    void NotFoundDiary() throws MalformedURLException {
        // given
        Long unsavedDiaryId = -1L;

        // when & then
        assertThatThrownBy(() -> diaryService.readById(unsavedDiaryId))
                .isInstanceOf(DiaryNotFoundException.class);
    }

    @DisplayName("존재하지 않는 일기는 삭제하지 못한다.")
    @Test
    void 존재하지_않는_일기는_삭제하지_못한다() throws MalformedURLException {
        // given
        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        Long notExistingId = -1L;

        // when & then
        assertThatThrownBy(() -> diaryService.deleteDiary(notExistingId));

        boolean exists = diaryRepository.existsById(diary.getId());
        assertThat(exists).isTrue();
    }

    private Diary createDiary(LocalDate diaryDate, Character character, Member member) throws MalformedURLException {
        return Diary.builder()
                .content("Initial content")
                .diaryDate(diaryDate)
                .isFavorite(false)
                .paintingImageUrls(List.of(new URL("http://defaultImage.png")))
                .character(character)
                .memberId(member.getId())
                .build();
    }

    private Member createMember(String name, LocalDate birthDate, Gender gender) {
        return Member.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birth(birthDate)
                .gender(gender)
                .role(Role.MEMBER)
                .build();
    }
}
