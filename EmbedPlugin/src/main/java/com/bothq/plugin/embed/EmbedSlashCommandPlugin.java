package com.bothq.plugin.embed;


import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import com.bothq.plugin.embed.config.EmbedConfig;
import com.bothq.plugin.embed.config.EmbedConfigSupplier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EmbedSlashCommandPlugin extends PluginBase {
    @Getter
    private final String name = "Embed Command Plugin";
    @Getter
    private final String description = "Allows the user to send an embed as the bot.";
    @Getter
    private String commandName = "Embed";

    @Getter
    private String commandId;

    EmbedConfigSupplier configSupplier = EmbedConfigSupplier.getInstance();
    EmbedActionRowBuilder embedActionRowBuilder;


    // private ITextBox requiredPerm;

    @Override
    public void createConfig(IConfig config) {

        // Sets required Permission to use command
        // requiredPerm = config.addTextBox("RequiredPermission", "Required Permission", Permission.ADMINISTRATOR.getName());
    }

    @Override
    public void pluginLoad() {
        log.info("{} loaded!", name);

        jda
                // .getGuildById("1204755028934533160")
                .upsertCommand("embed", "Open a menu to send an embed as the bot.")
                .queue(command -> {
                    commandId = command.getId();
                    commandName = command.getName();
                });

        log.debug("Upserted embed command.");

        embedActionRowBuilder = new EmbedActionRowBuilder(
                commandName + "." + "embedActions",
                configSupplier,
                this::updateMessage,
                this::sendMessage,
                jda
        );
    }

    @Override
    public void pluginUnload() {
        log.info("{} unloaded!", name);

        jda
                // .getGuildById("1204755028934533160")
                .deleteCommandById(commandId)
                .queue();

        embedActionRowBuilder.close();
    }

    @DiscordEventListener(SlashCommandInteractionEvent.class)
    public void onSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals(commandName)) return;

        log.debug("Event matched '{}'. Handling in '{}'", commandName, this.getClass());


        EmbedConfig config = configSupplier.getOrCreateConfig(event);

        event
                .reply(build(config))
                .setEphemeral(true)
                .queue(m -> log.debug("Sent initial response."));

    }


    private MessageCreateData build(EmbedConfig config){
        return new MessageCreateBuilder()
                .setEmbeds(
                        new EmbedBuilder()
                                .setTitle("Send an embed as the Bot.")
                                .setDescription(
                                        "Use the Buttons to configure your Embed. You can see the current layout below.\n " +
                                                "When you're ready, publish it to any channel you can write in!"
                                )
                                .build(),
                        config.build()
                )
                .setComponents(embedActionRowBuilder.build(config))
                .build();
    }

    private void updateMessage(InteractionHook interactionHook){
        EmbedConfig config = configSupplier.getConfig(interactionHook.getId());

        interactionHook.editOriginal(
                MessageEditData.fromCreateData(build(config))
        ).queue(
                m -> log.debug("Updated message with id: {}", m.getId())
        );
    }

    private void sendMessage(InteractionHook interactionHook){
        EmbedConfig config = configSupplier.getConfig(interactionHook.getId());

        if (!config.getChannel().getType().isMessage()) {
            // onError();

            config.setChannel(null);
        }

        GuildMessageChannel channel = (GuildMessageChannel) config.getChannel();
        log.debug("Attempting sending embed in channel '{}'.", config.getChannel().getId());

        if (!config.getSlashCommandInteractionEvent()
                .getMember()
                .hasPermission(channel, channel.getType().isThread()
                        ? Permission.MESSAGE_SEND
                        : Permission.MESSAGE_SEND_IN_THREADS)){

            config.setChannel(null);

            log.debug("Member does not have Permission '{}' in channel '{}'",
                    channel.getType().isThread()
                            ? Permission.MESSAGE_SEND
                            : Permission.MESSAGE_SEND_IN_THREADS,
                    channel.getId()
            );

            updateMessage(interactionHook);


            // onError("");
            return;
        }


        channel
                .sendMessage(
                        new MessageCreateBuilder()
                                .setEmbeds(config.build())
                                .build()
                )
                .queue(m -> {
                    log.debug("Sent embed.");
                    interactionHook.deleteOriginal().queue();
                });
    }

}
