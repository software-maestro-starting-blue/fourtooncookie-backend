package com.startingblue.fourtooncookie.discord.service.midjourney;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.discord.model.midjourney.MidjourneyDiscordQueueEntity;
import com.startingblue.fourtooncookie.discord.service.DiscordService;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MidjourneyDiscordService extends ListenerAdapter {

    @Value("${discord.midjourney.guildid}")
    private Long guildId;

    private final DiscordService discordService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private HashMap<Long, HashMap<Long, MidjourneyDiscordQueueEntity>> pendingQueue = new HashMap<>();

    @PostConstruct
    public void init() {
        discordService.addListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild().getIdLong() != guildId) {
            return;
        }

        if (! pendingQueue.containsKey(event.getChannel().getIdLong())) {
            return;
        }

        Long channelId = event.getChannel().getIdLong();
        Message receivedMessage = event.getMessage();
        Message referencedMessage = receivedMessage.getReferencedMessage();

        if (referencedMessage == null || ! pendingQueue.get(channelId).containsKey(referencedMessage.getIdLong())) {
            return;
        }

        MidjourneyDiscordQueueEntity entity = pendingQueue.get(channelId).get(referencedMessage.getIdLong());

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

    public void pushPendingQueue(Long diaryId, String prompt, Integer gridPosition, Character character) {
        Long channelId = getChannelIdByCharacter(character);
        String message = "/imagine " + prompt;
        MidjourneyDiscordQueueEntity entity = new MidjourneyDiscordQueueEntity(diaryId, message, gridPosition);

        if (! pendingQueue.containsKey(channelId)) {
            pendingQueue.put(channelId, new HashMap<>());
        }

        Message sentMessage = discordService.sendMessage(guildId, channelId, entity.message()).join();

        pendingQueue.get(channelId).put(sentMessage.getIdLong(), entity);
    }

    private Long getChannelIdByCharacter(Character character) {
        return null; // TODO: 테이블의 정보를 활용하여 알아내거나, 채널 이름을 통해서 알아내기
    }
}
