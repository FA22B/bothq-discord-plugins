package com.bothq.plugin.embed.config;

import org.jetbrains.annotations.Contract;

public interface ConfigSupplier<TConfig> {
    void setConfig(String configId, TConfig config);

    @Contract(pure = true)
    TConfig getConfig(String configId);
}
