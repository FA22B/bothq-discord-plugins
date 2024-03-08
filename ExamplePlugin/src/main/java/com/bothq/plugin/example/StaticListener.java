package com.bothq.plugin.example;

import com.bothq.lib.annotations.DiscordEventListener;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class StaticListener {

    @DiscordEventListener(MessageReceivedEvent.class)
    public static void onMessageReceived(@NotNull MessageReceivedEvent event) {

        // Echo back received message only in direct messages
        if (event.getChannelType() != ChannelType.PRIVATE) {
            return;
        }

        // Skip if the message author is the bot instance
        if (event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        // Send the echo message
        event.getChannel().sendMessage("Handled in static method!").queue();

    }
}
