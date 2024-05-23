package com.bothq.plugin.greet;


import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import com.bothq.lib.plugin.config.component.ITextBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class GreetPlugin extends PluginBase {
    @Getter
    private final String name = "GreetPlugin";

    @Getter
    private final String description = "A plugin that greets a user on join.";

    private ITextBox message;
    private ITextBox channel;

    @Override
    public void createConfig(IConfig config) {

        // Create a test check box
        message = config.addTextBox("message", "Greet Message", "Greetings {user.mention}");
        channel = config.addTextBox("channel", "Greeting Channel", "");
    }

    @Override
    public void pluginLoad() {
        log.info("{} loaded!", name);
    }

    @Override
    public void pluginUnload() {
        log.info("{} unloaded!", name);
    }

    @DiscordEventListener(GuildMemberJoinEvent.class)
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        log.debug("User joined on guild " + guild.getId());

        String channelId = channel.get(guild.getIdLong()).getValue();
        TextChannel greetingChannel = guild.getTextChannelById(channelId);
        if (greetingChannel == null) return;

        log.debug("Greeting channel is \"" + greetingChannel.getName() + "\"");


        String messageValue = message.get(guild.getIdLong()).getValue();

        // It would be nice to have a proper service which allows for proper usage of objects,
        // allowing things like {user.<name,mention>} etc.
        messageValue = messageValue.replace("{user.name}", event.getUser().getEffectiveName());
        messageValue = messageValue.replace("{user.mention}", event.getUser().getAsMention());

        greetingChannel.sendMessage(messageValue).queue();
    }
}
