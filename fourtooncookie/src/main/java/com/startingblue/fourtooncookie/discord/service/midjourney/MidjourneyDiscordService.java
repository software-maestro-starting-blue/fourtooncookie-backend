package com.startingblue.fourtooncookie.discord.service.midjourney;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.discord.model.midjourney.MidjourneyDiscordQueueEntity;
import com.startingblue.fourtooncookie.discord.service.DiscordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;

@RequiredArgsConstructor
@Service
public class MidjourneyDiscordService extends ListenerAdapter {

    private final DiscordService discordService;

    private HashMap<Long, LinkedList<MidjourneyDiscordQueueEntity>> readyQueue = new HashMap<>();

    private HashMap<Long, MidjourneyDiscordQueueEntity> processingEntities = new HashMap<>();

    @PostConstruct
    public void init() {
        discordService.addListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // TODO processingEntities에 존재하는 entity에 대한 답장일 경우 이에 대해 handling
        // 1. 만약 isImageSelectionProcessed가 false일 경우 이에 대해 선택을 시킨 후, true로 바꾸기
        // 2. 그렇지 않을 경우, processingEntities에서 해당 부분을 지우고 event publish하기
    }

    public void pushReadyQueue(Long diaryId, String prompt, Integer gridPosition, Character character) {
        Long channelId = getChannelIdByCharacter(character);
        String message = "/imagine " + prompt;
        MidjourneyDiscordQueueEntity entity = new MidjourneyDiscordQueueEntity(diaryId, message, gridPosition, false);

        if (! readyQueue.containsKey(channelId)) {
            readyQueue.put(channelId, new LinkedList<>());
        }

        readyQueue.get(channelId).add(entity);
    }

    private Long getChannelIdByCharacter(Character character) {
        return null; // TODO: 테이블의 정보를 활용하여 알아내거나, 채널 이름을 통해서 알아내기
    }

    @Scheduled(fixedDelay = 1000)
    private void processReadyQueue() {
        // TODO processingEntities에 존재하지 않는 channel에 대한 readyQueue가 존재할 경우 이를 실행
    }
}
