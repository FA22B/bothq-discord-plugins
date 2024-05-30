package com.bothq.plugin.embed;

import com.bothq.plugin.embed.config.EmbedConfig;
import com.bothq.plugin.embed.config.EmbedConfigSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EmbedSetTitleModalBuilder extends ModalMenuBuilder<EmbedConfig> {
    public static final String TITLE_ID = "title";
    public static final String DESCRIPTION_ID = "description";
    private final String name;

    public EmbedSetTitleModalBuilder(String name,
                                     EmbedConfigSupplier configSupplier,
                                     Consumer<InteractionHook> onChangeCallback,
                                     Consumer<InteractionHook> onSuccessCallback,
                                     JDA jda) {
        super(name, configSupplier, onChangeCallback, onSuccessCallback, jda);

        this.name = name;
    }

    @Override
    public Modal build(EmbedConfig config) {
        return Modal
                .create(name, "Change Embed Description and Title")
                .addActionRow(TextInput.create(nameBuilder
                                .apply(TITLE_ID), "Embed title", TextInputStyle.SHORT)
                        .setMaxLength(MessageEmbed.TITLE_MAX_LENGTH)
                        .setValue(config.getTitle())
                        .setRequired(false)
                        .build()
                )
                .addActionRow(
                        TextInput
                                .create(nameBuilder.apply(DESCRIPTION_ID), "Embed description", TextInputStyle.PARAGRAPH)
                                .setMaxLength(Math.min(
                                        TextInput.MAX_VALUE_LENGTH,
                                        MessageEmbed.DESCRIPTION_MAX_LENGTH))
                                .setValue(config.getDescription())
                                .setRequired(false)
                                .build())
                .build();
    }


    @Override
    public void onSubmit(@NotNull ModalInteractionEvent event) {
        var valueExtractor = getValueExtractor(event);

        EmbedConfig config = configSupplier.getConfig(event.getHook().getId());
        config.setTitle(valueExtractor.apply(TITLE_ID));
        config.setDescription(valueExtractor.apply(DESCRIPTION_ID));

    }
}
