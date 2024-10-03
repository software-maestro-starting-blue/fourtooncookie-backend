package com.startingblue.fourtooncookie.diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.diary.listener.DiarySQSMessageListener;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
import com.startingblue.fourtooncookie.member.domain.Gender;
import com.startingblue.fourtooncookie.member.domain.Member;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import com.startingblue.fourtooncookie.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DiarySQSMessageListenerTest {

    @Mock
    private DiaryService diaryService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private DiarySQSMessageListener diarySQSMessageListener;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    ArtworkRepository artworkRepository;

    private Diary diary;
    private Member member;
    private Character character;
    private Artwork artwork;

    @BeforeEach
    void setUp() throws MalformedURLException {
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

        diary = createDiary(LocalDate.now(), character, member);
    }

    @DisplayName("SQS 메시지가 성공적인 이미지 생성 요청을 처리하는지 확인한다.")
    @Test
    void handleSQSMessage_SuccessfulImageGeneration() throws Exception {
        // given
        String sqsMessage = "{\"diaryId\": 0, \"gridPosition\": 2, \"isSuccess\": true}";

        DiarySQSMessageListener.DiaryImageResponseMessage responseMessage =
                new DiarySQSMessageListener.DiaryImageResponseMessage(1L, 2, true);
        when(objectMapper.readValue(sqsMessage, DiarySQSMessageListener.DiaryImageResponseMessage.class))
                .thenReturn(responseMessage);
        when(diaryService.readById(1L)).thenReturn(diary);

        // when
        diarySQSMessageListener.handleSQSMessage(sqsMessage);

        // then
        verify(diaryService, times(1)).readById(1L);
        verify(diaryRepository, times(1)).save(diary);
        assertThat(diary.getPaintingImageGenerationStatuses().get(2)).isEqualTo(DiaryPaintingImageGenerationStatus.SUCCESS);
    }

    @DisplayName("SQS 메시지가 실패한 이미지 생성 요청을 처리하는지 확인한다.")
    @Test
    void handleSQSMessage_FailedImageGeneration() throws Exception {
        // given
        String sqsMessage = "{\"diaryId\": 1, \"gridPosition\": 2, \"isSuccess\": false}";

        DiarySQSMessageListener.DiaryImageResponseMessage responseMessage =
                new DiarySQSMessageListener.DiaryImageResponseMessage(1L, 2, false);
        System.out.println(responseMessage);
        when(objectMapper.readValue(sqsMessage, DiarySQSMessageListener.DiaryImageResponseMessage.class))
                .thenReturn(responseMessage);
        when(diaryService.readById(1L)).thenReturn(diary);

        // when
        diarySQSMessageListener.handleSQSMessage(sqsMessage);

        // then
        verify(diaryService, times(1)).readById(1L);
        verify(diaryRepository, times(1)).save(diary);
        assertThat(diary.getPaintingImageGenerationStatuses().get(2)).isEqualTo(DiaryPaintingImageGenerationStatus.FAILURE);
    }

    @DisplayName("잘못된 메시지 처리시 예외가 발생한다.")
    @Test
    void handleSQSMessage_NullDiaryId() throws Exception {
        // given
        String validMessage = "{\"\": , \"gridPosition\": 2, \"isSuccess\": true}";

        DiarySQSMessageListener.DiaryImageResponseMessage responseWithNullId = new DiarySQSMessageListener.DiaryImageResponseMessage(null, 2, true);
        when(objectMapper.readValue(validMessage, DiarySQSMessageListener.DiaryImageResponseMessage.class))
                .thenReturn(responseWithNullId);

        // when & then
        assertThatThrownBy(() -> diarySQSMessageListener.handleSQSMessage(validMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Diary ID is missing in the message");

        verifyNoInteractions(diaryService, diaryRepository);
    }

    private Diary createDiary(LocalDate diaryDate, Character character, Member member) throws MalformedURLException {
        return Diary.builder()
                .content("Initial content")
                .diaryDate(diaryDate)
                .isFavorite(false)
                .paintingImageUrls(List.of(new URL("http://defaultImage.png")))
                .paintingImageGenerationStatuses(new ArrayList<>(Collections.nCopies(4, DiaryPaintingImageGenerationStatus.GENERATING)))
                .status(DiaryStatus.IN_PROGRESS)
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
