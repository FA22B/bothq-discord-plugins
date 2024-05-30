package com.bothq.plugin.embed;

import com.bothq.plugin.embed.config.EmbedConfig;
import com.bothq.plugin.embed.config.EmbedConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Slf4j
public class EmbedAddFieldModalBuilder extends ModalMenuBuilder<EmbedConfig> {
    public static final String VALUE_ID = "value";
    public static final String TITLE_ID = "title";
    private final boolean inline;

    public EmbedAddFieldModalBuilder(boolean inline,
                                     String name,
                                     EmbedConfigSupplier configSupplier,
                                     Consumer<InteractionHook> onChangeCallback,
                                     Consumer<InteractionHook> onSuccessCallback,
                                     JDA jda) {
        super(name, configSupplier, onChangeCallback, onSuccessCallback, jda);

        this.inline = inline;
    }

    @Override
    public Modal build(EmbedConfig config) {
        config.addField().setInline(inline);

        return Modal
                .create(name, config.addField().isInline()
                        ? "Add an inline field to the embed message"
                        : "Add a field to the embed message")
                .addActionRow(TextInput.create(nameBuilder
                        .apply(TITLE_ID), "Embed Field Title", TextInputStyle.SHORT)
                        .setMaxLength(MessageEmbed.TITLE_MAX_LENGTH)
                        .setRequired(false)
                        .build()
                )
                .addActionRow(
                        TextInput
                                .create(nameBuilder.apply(VALUE_ID), "Embed Field Value", TextInputStyle.PARAGRAPH)
                                .setMaxLength(MessageEmbed.VALUE_MAX_LENGTH)
                                .setRequired(false)
                                .build())
                .build();
    }


    @Override
    public void onSubmit(@NotNull ModalInteractionEvent event) {
        var valueExtractor = getValueExtractor(event);

        EmbedConfig config = configSupplier.getConfig(event.getHook().getId());
        EmbedConfig.EmbedFieldMaker embedFieldMaker = config.addField();

        embedFieldMaker.setTitle(valueExtractor.apply(TITLE_ID));
        embedFieldMaker.setValue(valueExtractor.apply(VALUE_ID));
        embedFieldMaker.setInline(inline);

        embedFieldMaker.make();
    }

//    // Had to remove Button, because Modals don't allow them
//    @Override
//    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
//        super.onButtonInteraction(event);
//
//        Predicate<String> matchesName = s -> Objects.equals(event.getButton().getId(), nameBuilder.apply(s));
//
//        if (!matchesName.test("inline"))
//            return;
//
//
//        configSupplier
//                .getConfig(event.getMessageId())
//                .addField()
//                .toggleInline();
//
//
//    }


}
