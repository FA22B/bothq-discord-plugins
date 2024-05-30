package com.bothq.plugin.embed.config;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class EmbedConfigSupplier implements ConfigSupplier<EmbedConfig> {
    private static EmbedConfigSupplier instance;
    Map<String, EmbedConfig> configs = new HashMap<>();

    @Override
    public void setConfig(String configId, EmbedConfig config){
        configs.put(configId, config);
    }

    @Override
    @Contract(pure = true)
    public EmbedConfig getConfig(String configId){
        return configs.get(configId);
    }


    public EmbedConfig getOrCreateConfig(SlashCommandInteractionEvent event) {
        EmbedConfig config = new EmbedConfig(event);
        setConfig(event.getHook().getId(), config);

        return config;
    }

    private EmbedConfigSupplier() {}

    public static EmbedConfigSupplier getInstance() {
        if (instance == null) instance = new EmbedConfigSupplier();
        return instance;
    }
}
