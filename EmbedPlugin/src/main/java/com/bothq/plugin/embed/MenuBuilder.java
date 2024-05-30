package com.bothq.plugin.embed;

import com.bothq.plugin.embed.config.ConfigSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MenuBuilder<TConfig, TComponent> extends ListenerAdapter implements AutoCloseable {
    protected final Function<String, String> nameBuilder;
    protected ConfigSupplier<TConfig> configSupplier;
    private final Consumer<InteractionHook> onChangeCallback;
    private final Consumer<InteractionHook> onSuccessCallback;

    private final JDA jda;

    private boolean closed;


    public MenuBuilder(String name,
                       ConfigSupplier<TConfig> configSupplier,
                       Consumer<InteractionHook> onChangeCallback,
                       Consumer<InteractionHook> onSuccessCallback,
                       JDA jda) {
        nameBuilder = s -> makeName(name, s);
        this.configSupplier = configSupplier;
        this.onChangeCallback = onChangeCallback;
        this.onSuccessCallback = onSuccessCallback;
        this.jda = jda;

        jda.addEventListener(this);
    }


    protected String makeName(String parentName, String... componentName){
        StringBuilder name = new StringBuilder(parentName);

        for (String s : componentName) {
            name.append(".").append(s);
        }


        return name.toString();
    }

    abstract public TComponent build(TConfig config);

    protected void onChange(InteractionHook hook){
        onChangeCallback.accept(hook);
    }

    protected void onSuccess(InteractionHook hook){
        this.onSuccessCallback.accept(hook);
    }


    @Override
    public void close() {
        if (this.closed) return;

        jda.removeEventListener(this);
        this.closed = true;
    }
}
