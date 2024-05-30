package com.bothq.plugin.embed;

import com.bothq.plugin.embed.config.EmbedConfig;
import com.bothq.plugin.embed.config.EmbedConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Slf4j
public class EmbedActionRowBuilder extends MenuBuilder<EmbedConfig, ActionRow[]> {
    public static final String SET_TITLE_BUTTON_ID = "setTitle";
    public static final String ADD_FIELD_BUTTON_ID = "addField";
    public static final String ADD_INLINE_FIELD_BUTTON_ID = "addInlineField";
    public static final String SUBMIT_BUTTON_ID = "submit";
    public static final String CHANNEL_SELECTOR_ID = "channel";
    private final EmbedAddFieldModalBuilder addFieldModalBuilder;
    private final EmbedAddFieldModalBuilder addInlineFieldModalBuilder;
    private final EmbedSetTitleModalBuilder embedSetTitleModalBuilder;

    public EmbedActionRowBuilder(
            String name,
            EmbedConfigSupplier configSupplier,
            Consumer<InteractionHook> onChangeCallback,
            Consumer<InteractionHook> onSuccessCallback,
            JDA jda) {
        super(name,
                configSupplier,
                onChangeCallback,
                onSuccessCallback,
                jda);

        addFieldModalBuilder = new EmbedAddFieldModalBuilder(
                false,
                makeName(name, ADD_FIELD_BUTTON_ID, "modal"),
                configSupplier,
                onChangeCallback,
                onSuccessCallback,
                jda
        );

        addInlineFieldModalBuilder = new EmbedAddFieldModalBuilder(
                true,
                makeName(name, ADD_INLINE_FIELD_BUTTON_ID, "modal"),
                configSupplier,
                onChangeCallback,
                onSuccessCallback,
                jda
        );

        embedSetTitleModalBuilder = new EmbedSetTitleModalBuilder(
                makeName(name, SET_TITLE_BUTTON_ID, "modal"),
                configSupplier,
                onChangeCallback,
                onSuccessCallback,
                jda
        );
    }

    @Override
    public ActionRow[] build(EmbedConfig config) {
        return new ActionRow[]{
                ActionRow.of(
                        Button.primary(nameBuilder.apply(SET_TITLE_BUTTON_ID), "Change Description/Title")
                        ),
                ActionRow.of(
                        Button.primary(nameBuilder.apply(ADD_FIELD_BUTTON_ID), "Add a field"),
                        Button.primary(nameBuilder.apply(ADD_INLINE_FIELD_BUTTON_ID), "Add an inline field")
//                        ,
//                        Button.primary(nameBuilder.apply(REMOVE_FIELD_BUTTON_ID), "Remove a field")
//                                .withDisabled(config.getFields().size() == 0)
                ),
                ActionRow.of(
                        EntitySelectMenu
                                .create(
                                        nameBuilder.apply(CHANNEL_SELECTOR_ID),
                                        EntitySelectMenu.SelectTarget.CHANNEL
                                )
                                .setChannelTypes(
                                        ChannelType.TEXT,
                                        ChannelType.GUILD_PRIVATE_THREAD,
                                        ChannelType.GUILD_PUBLIC_THREAD
                                )
                                .setDefaultValues(
                                        EntitySelectMenu.DefaultValue.channel(
                                                config.getSlashCommandInteractionEvent().getChannelId()
                                        )
                                )
                                .build()
                ),
                ActionRow.of(
                        Button.success(nameBuilder.apply(SUBMIT_BUTTON_ID), "Send the embed in the channel above.")
                )
        };
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);

        log.debug("Got {} in {}", event, this);
        log.debug("Button pressed: {}", event.getButton().getId());

        Predicate<String> matchesName = s -> Objects.equals(event.getButton().getId(), nameBuilder.apply(s));

        if (matchesName.test(ADD_FIELD_BUTTON_ID)) {
            addField(event, false);
        } else if (matchesName.test(ADD_INLINE_FIELD_BUTTON_ID)) {
            addField(event, true);
        } else if (matchesName.test(SET_TITLE_BUTTON_ID)) {
            setTitle(event);
        } else if (matchesName.test(SUBMIT_BUTTON_ID)) {
            submit(event);
        }
    }

    private void submit(ButtonInteractionEvent event) {
        onSuccess(event.getHook());
        event.deferEdit().queue();
    }


    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        super.onEntitySelectInteraction(event);


        Predicate<String> matchesName = s -> Objects.equals(event.getSelectMenu().getId(), nameBuilder.apply(s));

        if (matchesName.test(CHANNEL_SELECTOR_ID)){
            changeChannel(event);
        }

    }

    private void changeChannel(EntitySelectInteractionEvent event) {
        event.deferEdit().queue();

        GuildChannel selected = event.getMentions().getChannels().get(0);
        EmbedConfig config = configSupplier.getConfig(event.getHook().getId());

        // Checks if member can post in the selected channel. If so, updates selection.
        if (config.getSlashCommandInteractionEvent()
                .getMember()
                .hasPermission(selected, selected.getType().isThread()
                        ? Permission.MESSAGE_SEND
                        : Permission.MESSAGE_SEND_IN_THREADS)){

            config.setChannel(selected);
        }

        onChange(event.getHook());
    }

    private void setTitle(ButtonInteractionEvent event) {
        event.replyModal(
                embedSetTitleModalBuilder.build(configSupplier.getConfig(event.getHook().getId()))
        ).queue();
    }

    private void addField(ButtonInteractionEvent event, boolean inline){
        EmbedAddFieldModalBuilder modalBuilder = inline
                ? addInlineFieldModalBuilder
                : addFieldModalBuilder;

        event.replyModal(
                modalBuilder.build(configSupplier.getConfig(event.getHook().getId()))
        ).queue();
    }

    @Override
    public void close()  {
        addInlineFieldModalBuilder.close();
        addFieldModalBuilder.close();
        embedSetTitleModalBuilder.close();

        super.close();
    }
}
