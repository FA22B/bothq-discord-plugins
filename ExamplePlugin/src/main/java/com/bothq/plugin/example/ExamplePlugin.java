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

        // Only handle guild text channel messages here
        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        // Check for message skip conditions
        if (event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong() // Self user ID check
                || event.getAuthor().isBot() // Bot check (this includes the self user, but we keep it in here for example purposes)
                || event.getMessage().getType().isSystem() // System message check (eg. user join)
                || event.isWebhookMessage()) {
            return;
        }

        log.info(event.getAuthor().getEffectiveName());


        // Get the channel
        var channel = event.getChannel().asGuildMessageChannel();

        // Get the raw message content
        var messageContent = event.getMessage().getContentRaw();

        // Send the echo message
        channel.sendMessage("Echo: " + messageContent).queue();

        // Get the guild ID
        var guildId = channel.getGuild().getIdLong();

        // Get the checkbox state
        var checkBoxState = testCheckBox.get(guildId).getValue();

        // Send test checkbox state
        channel.sendMessage("Checkbox state: " + checkBoxState).queue();


        // channel.sendMessage("```json\n" + event.getRawData().toPrettyString() + "```").queue();

    }
}
