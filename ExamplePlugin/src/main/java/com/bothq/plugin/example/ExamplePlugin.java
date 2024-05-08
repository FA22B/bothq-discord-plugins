package com.bothq.plugin.example;

import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import com.bothq.lib.plugin.config.component.ICheckBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ExamplePlugin extends PluginBase {

    @Getter
    private final String name = "ExamplePlugin";

    @Getter
    private final String description = "An example plugin.";

    private ICheckBox testCheckBox;

    @Override
    public void createConfig(IConfig config) {

        // Create a test check box
        testCheckBox = config.addCheckBox("test", "Test Checkbox", false);
    }

    @Override
    public void pluginLoad() {
        log.info("{} loaded!", name);
    }

    @Override
    public void pluginUnload() {
        log.info("{} unloaded!", name);
    }

    @DiscordEventListener(MessageReceivedEvent.class)
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        // Echo back received message only in direct messages
        if (event.getChannelType() != ChannelType.PRIVATE) {
            return;
        }

        // Skip if the message author is the bot instance
        if (event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong()) {
            return;
        }

        // Get the raw message content
        var messageContent = event.getMessage().getContentRaw();

        // Send the echo message
        event.getChannel().sendMessage("Echo: " + messageContent).queue();

    }
}
