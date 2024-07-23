package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryPaintingImagesUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.dto.request.DiaryUpdateRequest;
import com.startingblue.fourtooncookie.diary.dto.response.DiarySavedResponse;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
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
import java.util.List;

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

    private Member member;
    private Character character;

    @BeforeEach
    void setUp() throws MalformedURLException {
        diaryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        characterRepository.deleteAllInBatch();

        character = new Character(ModelType.DALL_E_3, "멍멍이", new URL("http://멍멍이.png"));
        characterRepository.save(character);

        member = createMember("민서", LocalDate.of(2000, 5, 31), Gender.MALE);
        memberRepository.save(member);
    }

    @DisplayName("사용자가 작성한 일기를 저장한다.")
    @Test
    void createDiaryTest() {
        // given
        DiarySaveRequest request = new DiarySaveRequest("Initial content", LocalDate.of(2024, 7, 21), List.of(1L, 2L), character.getId());

        // when
        diaryService.createDiary(request, member.getId());

        // then
        List<Diary> diaries = diaryRepository.findAll();
        assertThat(diaries).hasSize(1);

        Diary savedDiary = diaries.get(0);
        assertThat(savedDiary.getContent()).isEqualTo("Initial content");
        assertThat(savedDiary.getDiaryDate()).isEqualTo(LocalDate.of(2024, 7, 21));
        assertThat(savedDiary.getHashtagsIds()).isEqualTo(List.of(1L, 2L));
        assertThat(savedDiary.getCharacter()).isEqualTo(character);
        assertThat(savedDiary.getMember()).isEqualTo(member);
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
        Diary foundDiary = diaryService.findById(diary.getId());

        // then
        assertThat(foundDiary).isEqualTo(diary);
    }

    @DisplayName("존재하지 않는 일기는 찾을 수 없다.")
    @Test
    void NotFoundDiary() throws MalformedURLException {
        // given
        Long unsavedDiaryId = -1L;

        // when & then
        assertThatThrownBy(() -> diaryService.findById(unsavedDiaryId))
                .isInstanceOf(DiaryNoSuchElementException.class);
    }

    @DisplayName("회원의 일기 목록을 페이지 단위로 읽어온다.")
    @Test
    void readDiariesByMemberTest() {
        // given
        for (int i = 1; i <= 10; i++) {
            DiarySaveRequest request = new DiarySaveRequest("Content " + i, LocalDate.of(2024, 7, 21).plusDays(i), List.of((long) i), character.getId());
            diaryService.createDiary(request, member.getId());
        }

        // when
        List<DiarySavedResponse> diariesPage1 = diaryService.readDiariesByMember(member.getId(), 0, 5);
        List<DiarySavedResponse> diariesPage2 = diaryService.readDiariesByMember(member.getId(), 1, 5);

        // then
        assertThat(diariesPage1).hasSize(5);
        assertThat(diariesPage1.get(0).content()).isEqualTo("Content 10");
        assertThat(diariesPage1.get(4).content()).isEqualTo("Content 6");

        assertThat(diariesPage2).hasSize(5);
        assertThat(diariesPage2.get(0).content()).isEqualTo("Content 5");
        assertThat(diariesPage2.get(4).content()).isEqualTo("Content 1");
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

    @DisplayName("일기 내용, 해시태그, 캐릭터를 업데이트 한다.")
    @Test
    void updateDiaryTest() throws MalformedURLException {
        // given
        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        Character newCharacter = new Character(ModelType.STABLE_DIFFUSION, "오동이", new URL("http://오동이.png"));
        characterRepository.save(newCharacter);

        DiaryUpdateRequest request = new DiaryUpdateRequest("새로운 일기 내용", List.of(1L), newCharacter.getId());

        // when
        diaryService.updateDiary(diary.getId(), request);

        // then
        Diary updatedDiary = diaryRepository.findById(diary.getId()).get();
        assertThat(updatedDiary.getContent()).isEqualTo("새로운 일기 내용");
        assertThat(updatedDiary.getHashtagsIds()).isEqualTo(List.of(1L));
        assertThat(updatedDiary.getCharacter()).isEqualTo(newCharacter);
    }

    @DisplayName("일기 그림 이미지 URL을 업데이트 한다.")
    @Test
    void updatePaintingImageUrlsTest() throws MalformedURLException {
        // given
        Diary diary = createDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        DiaryPaintingImagesUpdateRequest request = new DiaryPaintingImagesUpdateRequest(
                List.of(new URL("http://new1.png"), new URL("http://new2.png"), new URL("http://new3.png"), new URL("http://new4.png"))
        );

        // when
        diaryService.updateDiary(diary.getId(), request);

        // then
        Diary updatedDiary = diaryRepository.findById(diary.getId()).get();
        assertThat(updatedDiary.getPaintingImageUrls())
                .extracting(URL::toString)
                .containsExactlyInAnyOrder(
                        "http://new1.png",
                        "http://new2.png",
                        "http://new3.png",
                        "http://new4.png"
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
