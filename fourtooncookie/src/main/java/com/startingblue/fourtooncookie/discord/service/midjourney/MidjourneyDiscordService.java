package com.startingblue.fourtooncookie.discord.service.midjourney;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.discord.model.midjourney.MidjourneyDiscordQueueEntity;
import com.startingblue.fourtooncookie.discord.service.DiscordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class MidjourneyDiscordService extends ListenerAdapter {

    @Value("${discord.midjourney.guildid}")
    private Long guildId;

    private final DiscordService discordService;

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

        if (!entity.isImageSelectionProcessed()) {
            // 1. 만약 isImageSelectionProcessed가 false일 경우 이에 대해 선택을 시킨 후, true로 바꾸기
        } else {
            // 2. 그렇지 않을 경우, processingEntities에서 해당 부분을 지우고 event publish하기
        }

    }

    public void pusPendingQueue(Long diaryId, String prompt, Integer gridPosition, Character character) {
        Long channelId = getChannelIdByCharacter(character);
        String message = "/imagine " + prompt;
        MidjourneyDiscordQueueEntity entity = new MidjourneyDiscordQueueEntity(diaryId, message, gridPosition, false);

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
