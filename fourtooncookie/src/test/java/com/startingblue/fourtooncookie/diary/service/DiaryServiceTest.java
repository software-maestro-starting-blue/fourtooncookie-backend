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

//    @DisplayName("작성한 일기를 저장한다.")
//    @Test
//    void createDiaryTest() {
//        // given
//        DiarySaveRequest request = new DiarySaveRequest("Initial content", LocalDate.of(2024, 7, 21), character.getId());
//
//        // when
//        diaryService.createDiary(request, member.getId());
//
//        // then
//        List<Diary> diaries = diaryRepository.findAll();
//        assertThat(diaries).hasSize(1);
//
//        Diary savedDiary = diaries.get(0);
//        assertThat(savedDiary.getContent()).isEqualTo("Initial content");
//        assertThat(savedDiary.getDiaryDate()).isEqualTo(LocalDate.of(2024, 7, 21));
//        assertThat(savedDiary.getCharacter()).isEqualTo(character);
//        assertThat(savedDiary.getMemberId()).isEqualTo(member.getId());
//    }

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

//    @DisplayName("회원의 일기 목록을 페이지 단위로 읽어온다.")
//    @Test
//    void readDiariesByMemberIdTest() {
//        // given
//        for (int i = 1; i <= 9; i++) {
//            DiarySaveRequest request = new DiarySaveRequest("Content " + i, LocalDate.of(2024, 7, 21).plusDays(i), character.getId());
//            diaryService.createDiary(request, member.getId());
//        }
//
//        // when
//        DiarySavedResponses diariesPages1 = DiarySavedResponses.of(diaryService.readDiariesByMemberId(member.getId(), 0, 5));
//        DiarySavedResponses diariesPages2 = DiarySavedResponses.of(diaryService.readDiariesByMemberId(member.getId(), 1, 5));
//
//        List<DiarySavedResponse> diariesPage1 = diariesPages1.diarySavedResponses();
//        List<DiarySavedResponse> diariesPage2 = diariesPages2.diarySavedResponses();
//        // then
//        assertThat(diariesPage1).hasSize(5);
//        assertThat(diariesPage1.get(0).content()).isEqualTo("Content 9");
//        assertThat(diariesPage1.get(4).content()).isEqualTo("Content 5");
//
//        assertThat(diariesPage2).hasSize(4);
//        assertThat(diariesPage2.get(0).content()).isEqualTo("Content 4");
//        assertThat(diariesPage2.get(3).content()).isEqualTo("Content 1");
//    }

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

//    @DisplayName("일기 내용, 해시태그, 캐릭터를 업데이트 한다.")
//    @Test
//    void updateDiaryTest() throws MalformedURLException {
//        // given
//
//        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
//        diaryRepository.save(diary);
//
//        Character newCharacter = Character.builder()
//                .characterVisionType(CharacterVisionType.DALL_E_3)
//                .paymentType(PaymentType.FREE)
//                .name("오동이")
//                .artwork(artwork)
//                .selectionThumbnailUrl(new URL("http://오동이.png"))
//                .basePrompt("base prompt")
//                .build();
//
//        characterRepository.save(newCharacter);
//
//        DiaryUpdateRequest request = new DiaryUpdateRequest("새로운 일기 내용", newCharacter.getId());
//
//        // when
//        diaryService.updateDiary(diary.getId(), request);
//
//        // then
//        Diary updatedDiary = diaryRepository.findById(diary.getId()).get();
//        assertThat(updatedDiary.getContent()).isEqualTo("새로운 일기 내용");
//        assertThat(updatedDiary.getCharacter()).isEqualTo(newCharacter);
//    }

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
