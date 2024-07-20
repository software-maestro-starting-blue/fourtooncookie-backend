package com.startingblue.fourtooncookie.discord.service;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscordService {

    private JDA jda;

    @Value("${discord.token}")
    private String DISCORD_API_KEY;

    @PostConstruct
    private void init() throws InterruptedException {
        jda = JDABuilder.createDefault(DISCORD_API_KEY)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .build();
        jda.awaitReady();
    }

    public void sendMessage(Integer guildId, Integer channelId, String message) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new IllegalStateException("Guild not found");
        }

        TextChannel textChannel = guild.getTextChannelById(channelId);

        if (textChannel == null) {
            throw new IllegalStateException("Channel not found");
        }

        textChannel.sendMessage(message).queue();
    }

    public void addListener(ListenerAdapter listener) {
        jda.addEventListener(listener);
    }

    public TextChannel getChannelByName(Integer guildId, String channelName) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new IllegalStateException("Guild not found");
        }

        return guild.getTextChannels().stream()
                .filter(channel -> channel.getName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Channel not found"));
    }

}
