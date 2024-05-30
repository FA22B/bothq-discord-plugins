package com.bothq.plugin.memberleave;

import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import com.bothq.lib.plugin.config.component.ITextBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MemberLeavePlugin extends PluginBase {

    @Getter
    private final String name = "MemberLeavePlugin";

    @Getter
    private final String description = "A plugin that sends a notification when a member leaves the server.";

    private ITextBox channelIdTextBox;

    private ITextBox leaveMessageTextBox;

    @Override
    public void createConfig(IConfig config) {
        channelIdTextBox = config.addTextBox("channelId", "Channel ID", "");
        leaveMessageTextBox = config.addTextBox("leaveMessage", "Leave Message", "Member %s has left the server.");
    }

    @Override
    public void pluginLoad() {
        log.info("{} loaded!", name);
    }

    @Override
    public void pluginUnload() {
        log.info("{} unloaded!", name);
    }

    @DiscordEventListener(GuildMemberRemoveEvent.class)
    public void onMemberLeave(@NotNull GuildMemberRemoveEvent event) {
        Guild guild = event.getGuild();
        log.debug("Member {} with Id {}", guild.getName(), guild.getId() );

        String channelId = channelIdTextBox.get(guild.getIdLong()).getValue();
        TextChannel channel = guild.getTextChannelById(channelId);
        if (channel != null) {
            log.debug("leave Channel is {} with Id {}", channel.getName(), channel.getId() );

            String leaveMessage = String.format(leaveMessageTextBox.get(guild.getIdLong()).getValue(), event.getUser().getEffectiveName());
            channel.sendMessage(leaveMessage).queue();
        }
    }
}