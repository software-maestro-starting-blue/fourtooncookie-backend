package com.startingblue.fourtooncookie.discord.service.midjourney;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.discord.model.midjourney.MidjourneyDiscordPendingEntity;
import com.startingblue.fourtooncookie.discord.service.DiscordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class MidjourneyDiscordService extends ListenerAdapter {

    @Value("${discord.midjourney.guildid}")
    private Long guildId;

    private final DiscordService discordService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private HashMap<Long, HashMap<Long, MidjourneyDiscordPendingEntity>> pendingEntities = new HashMap<>();

    @PostConstruct
    public void init() {
        discordService.addListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild().getIdLong() != guildId) {
            return;
        }

        if (! pendingEntities.containsKey(event.getChannel().getIdLong())) {
            return;
        }

        Long channelId = event.getChannel().getIdLong();
        Message receivedMessage = event.getMessage();
        Message referencedMessage = receivedMessage.getReferencedMessage();

        if (referencedMessage == null || ! pendingEntities.get(channelId).containsKey(referencedMessage.getIdLong())) {
            return;
        }

        MidjourneyDiscordPendingEntity entity = pendingEntities.get(channelId).get(referencedMessage.getIdLong());

        Optional<Attachment> imageAttachment = receivedMessage.getAttachments().stream()
                .filter(Attachment::isImage)
                .findFirst();

        if (imageAttachment.isEmpty()) {
            throw new IllegalStateException("Image not found");
        }

        String imageUrl = imageAttachment.get().getProxyUrl();

        Integer bestImageGrid = getBestImageGrid(imageUrl);

        byte[] imageBytes =getGridPositionImageByteArray(imageUrl, bestImageGrid);

        // applicationEventPublisher.publishEvent(new VisionReplyEvent(entity.diaryId(), imageBytes, entity.gridPosition())); TODO TSK-210에서 수정됨
    }

    private byte[] getGridPositionImageByteArray(String imageUrl, Integer gridPosition) {
        return null; // TODO 컨버터를 활용하여 이미지를 바이트 배열로 변환하기
    }

    private Integer getBestImageGrid(String imageUrl) {
        return 0; // TODO: 이미지를 llmService로 분석하여 가장 적합한 이미지를 선택하기
    }

    public void pushPendingEntities(Long diaryId, String prompt, Integer gridPosition, Character character) {
        Long channelId = getChannelIdByCharacter(character);
        String message = "/imagine " + prompt;
        MidjourneyDiscordPendingEntity entity = new MidjourneyDiscordPendingEntity(diaryId, message, gridPosition);

        if (! pendingEntities.containsKey(channelId)) {
            pendingEntities.put(channelId, new HashMap<>());
        }

        Message sentMessage = discordService.sendMessage(guildId, channelId, entity.message()).join();

        pendingEntities.get(channelId).put(sentMessage.getIdLong(), entity);
    }

    private Long getChannelIdByCharacter(Character character) {
        return null; // TODO: 테이블의 정보를 활용하여 알아내거나, 채널 이름을 통해서 알아내기
    }
}
