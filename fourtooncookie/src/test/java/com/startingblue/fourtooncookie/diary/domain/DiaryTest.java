package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiaryTest {

    private Diary diary;
    private Character character;
    private Member member;
    private List<Hashtag> hashtags;

    @BeforeEach
    void setUp() {
        character = mock(Character.class);
        member = mock(Member.class);
        hashtags = new ArrayList<>();
        diary = Diary.builder()
                .content("Initial content")
                .isFavorite(false)
                .diaryDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .character(character)
                .member(member)
                .paintingImages(new ArrayList<>())
                .hashtagsIds(new ArrayList<>())
                .build();
    }

    @Test
    void 다이어리_생성() {
        String newContent = "Updated content";
        LocalDateTime newModifiedAt = LocalDateTime.now();
        Character newCharacter = mock(Character.class);

        Hashtag hashtag1 = mock(Hashtag.class);
        Hashtag hashtag2 = mock(Hashtag.class);
        when(hashtag1.getId()).thenReturn(1L);
        when(hashtag2.getId()).thenReturn(2L);
        List<Hashtag> newHashtags = List.of(hashtag1, hashtag2);

        diary.update(newContent, newModifiedAt, newHashtags, newCharacter);

        assertEquals(newContent, diary.getContent());
        assertEquals(newModifiedAt, diary.getModifiedAt());
        assertEquals(newCharacter, diary.getCharacter());
        assertEquals(List.of(1L, 2L), diary.getHashtagsIds());
    }

    @Test
    void 업데이트_그림일기() {
        PaintingImage paintingImage1 = mock(PaintingImage.class);
        PaintingImage paintingImage2 = mock(PaintingImage.class);
        PaintingImage paintingImage3 = mock(PaintingImage.class);
        PaintingImage paintingImage4 = mock(PaintingImage.class);
        List<PaintingImage> newPaintingImages = List.of(paintingImage1, paintingImage2, paintingImage3, paintingImage4);

        diary.updatePaintingImages(newPaintingImages);

        assertTrue(diary.getPaintingImages().contains(paintingImage1));
        assertTrue(diary.getPaintingImages().contains(paintingImage2));
        assertTrue(diary.getPaintingImages().contains(paintingImage3));
        assertTrue(diary.getPaintingImages().contains(paintingImage4));
        verify(paintingImage1).assignDiary(diary);
        verify(paintingImage2).assignDiary(diary);
        verify(paintingImage3).assignDiary(diary);
        verify(paintingImage4).assignDiary(diary);
    }

    @Test
    void 해시_태그_업데이트() {
        Hashtag hashtag1 = mock(Hashtag.class);
        Hashtag hashtag2 = mock(Hashtag.class);
        when(hashtag1.getId()).thenReturn(1L);
        when(hashtag2.getId()).thenReturn(2L);
        List<Hashtag> newHashtags = List.of(hashtag1, hashtag2);

        diary.updateHashtags(newHashtags);

        assertEquals(List.of(1L, 2L), diary.getHashtagsIds());
    }
}
