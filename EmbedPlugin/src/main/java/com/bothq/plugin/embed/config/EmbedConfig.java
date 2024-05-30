package com.bothq.plugin.embed.config;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EmbedConfig {

    @Getter
    private final SlashCommandInteractionEvent slashCommandInteractionEvent;

    @Getter
    private String title;
    @Getter
    private String description;

    /**
     * The channel to send the embed to.
     */
    @Setter
    private GuildChannel channel;

    private final List<EmbedField> fields = new ArrayList<>();
    private EmbedFieldMaker embedFieldMaker;

    public EmbedConfig(SlashCommandInteractionEvent event) {
        this.slashCommandInteractionEvent = event;
    }


    public String getId(){
        return slashCommandInteractionEvent.getHook().getId();
    }


    public EmbedField getField(int index) {
        return fields.get(index);
    }

    public List<EmbedField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Contract(pure = true)
    public EmbedFieldMaker addField(){
        if (embedFieldMaker == null)
            embedFieldMaker = new EmbedFieldMaker(this);

        return embedFieldMaker;
    }

    private void addField(EmbedField embedField){
        fields.add(embedField);
    }


    public MessageEmbed build() throws IllegalStateException {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(getTitle())
                .setDescription(getDescription())
                .setFooter(
                        "Sent with " + slashCommandInteractionEvent
                                .getJDA()
                                .getSelfUser()
                                .getEffectiveName(),
                        slashCommandInteractionEvent
                                .getJDA()
                                .getSelfUser()
                                .getEffectiveAvatarUrl()
                )
                .setAuthor(
                        slashCommandInteractionEvent
                                .getMember()
                                .getEffectiveName(),
                        null,
                        slashCommandInteractionEvent
                                .getMember()
                                .getEffectiveAvatarUrl()
                );

        for (EmbedField field : fields) {
            embedBuilder.addField(field.getTitle(), field.getValue(), field.isInline());
        }

        return embedBuilder.build();
    }

    public void setTitle(String title) {
        if (title != null && title.isBlank())
            title = null;

        this.title = title;
    }

    public void setDescription(String description) {
        if (description != null && description.isBlank())
            description = null;

        this.description = description;
    }

    public GuildChannel getChannel() {
        return channel == null
                ? getSlashCommandInteractionEvent().getChannel().asGuildMessageChannel()
                : channel;
    }

    public static class EmbedFieldMaker {
        @Getter
        @Setter
        String title;

        @Getter
        @Setter
        String value;

        @Getter
        @Setter
        boolean inline = true;

        final EmbedConfig config;

        private EmbedFieldMaker(EmbedConfig config) {
            this.config = config;
        }

        public void make(){
            config.addField(new EmbedField(
                    title,
                    value,
                    inline));
            config.embedFieldMaker = null;
        }

        public boolean toggleInline(){
            return inline = !inline;
        }


    }

}
