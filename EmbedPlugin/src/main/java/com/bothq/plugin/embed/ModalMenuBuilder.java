package com.bothq.plugin.embed;

import com.bothq.plugin.embed.config.ConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class ModalMenuBuilder<TConfig> extends MenuBuilder<TConfig, Modal> {
    protected final String name;

    public ModalMenuBuilder(String name,
                            ConfigSupplier<TConfig> configSupplier,
                            Consumer<InteractionHook> onChangeCallback,
                            Consumer<InteractionHook> onSuccessCallback,
                            JDA jda) {
        super(name, configSupplier, onChangeCallback, onSuccessCallback, jda);
        this.name = name;
    }

    @Override
    public final void onModalInteraction(@NotNull ModalInteractionEvent event) {
        super.onModalInteraction(event);

        if (!event.getModalId().equals(name))
            return;

        log.debug("Handling {} in: {}", event, name);

        onSubmit(event);

        onChange(event.getHook());
        event.deferEdit().queue();
    }

    public abstract void onSubmit(ModalInteractionEvent event);

    public Function<String, String> getValueExtractor(ModalInteractionEvent event){
        return s -> {
            var mapping = event.getValue(nameBuilder.apply(s));
            if (mapping == null)
                return null;

            return mapping.getAsString();
        };
    }
}
