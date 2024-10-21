package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.diary.DiaryService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.DiaryRepository;
import com.startingblue.fourtooncookie.diary.dto.DiarySaveRequest;
import com.startingblue.fourtooncookie.diary.exception.DiaryNotFoundException;
import com.startingblue.fourtooncookie.character.domain.PaymentType;
import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.MemberRepository;
import com.startingblue.fourtooncookie.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

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

    @MockBean
    private DiaryS3Service diaryS3Service;

    private Member member;
    private Character character;
    private Artwork artwork;

    @BeforeEach
    void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);

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
    void removeDiaryByIdTest() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);
        doNothing().when(diaryS3Service).removeImagesByDiaryId(anyLong());

        // when
        diaryService.removeDiaryById(diary.getId());

        // then
        boolean exists = diaryRepository.existsById(diary.getId());
        assertThat(exists).isFalse();
    }

    @DisplayName("저장된 일기를 ID로 가져온다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        // when
        Diary foundDiary = diaryService.getById(diary.getId());

        // then
        assertThat(foundDiary).isEqualTo(diary);
    }

    @DisplayName("존재하지 않는 일기는 찾을 수 없다.")
    @Test
    void NotFoundDiary() throws MalformedURLException {
        // given
        Long unsavedDiaryId = -1L;

        // when & then
        assertThatThrownBy(() -> diaryService.getById(unsavedDiaryId))
                .isInstanceOf(DiaryNotFoundException.class);
    }

    @DisplayName("존재하지 않는 일기는 삭제하지 못한다.")
    @Test
    void 존재하지_않는_일기는_삭제하지_못한다() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        Long notExistingId = -1L;

        // when & then
        assertThatThrownBy(() -> diaryService.removeDiaryById(notExistingId));

        boolean exists = diaryRepository.existsById(diary.getId());
        assertThat(exists).isTrue();
    }

    @DisplayName("일기를 생성하면 저장된 ID를 반환한다.")
    @Test
    void addDiaryTest() throws MalformedURLException {
        // given
        DiarySaveRequest request = new DiarySaveRequest("오늘의 일기", LocalDate.now(), character.getId());

        // when
        Long diaryId = diaryService.addDiary(request, member.getId());

        // then
        Diary savedDiary = diaryRepository.findById(diaryId).orElseThrow(DiaryNotFoundException::new);
        assertThat(savedDiary.getId()).isEqualTo(diaryId);
        assertThat(savedDiary.getContent()).isEqualTo(request.content());
        assertThat(savedDiary.getDiaryDate()).isEqualTo(request.diaryDate());
    }

    @DisplayName("일기를 즐겨찾기로 설정한다.")
    @Test
    void modifyDiaryFavoriteTest() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.of(2024, 7, 21), character, member);
        diaryRepository.save(diary);

        // when
        diaryService.modifyDiaryFavorite(diary.getId(), true);

        // then
        Diary updatedDiary = diaryService.getById(diary.getId());
        assertThat(updatedDiary.isFavorite()).isTrue();
    }

    @DisplayName("일기의 소유자가 맞는지 확인한다.")
    @Test
    void isDiaryOwnerTest() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.now(), character, member);
        diaryRepository.save(diary);

        // when
        boolean isOwner = diaryService.isDiaryOwner(member.getId(), diary.getId());

        // then
        assertThat(isOwner).isTrue();
    }

    @DisplayName("다른 사용자는 일기의 소유자가 아니므로 false를 반환한다.")
    @Test
    void isDiaryOwnerFalseTest() throws MalformedURLException {
        // given
        Diary diary = addDiary(LocalDate.now(), character, member);
        diaryRepository.save(diary);

        Member otherMember = createMember("다른 사람", LocalDate.of(1995, 1, 1), Gender.FEMALE);
        memberRepository.save(otherMember);

        // when
        boolean isOwner = diaryService.isDiaryOwner(otherMember.getId(), diary.getId());

        // then
        assertThat(isOwner).isFalse();
    }


    private Diary addDiary(LocalDate diaryDate, Character character, Member member) throws MalformedURLException {
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
