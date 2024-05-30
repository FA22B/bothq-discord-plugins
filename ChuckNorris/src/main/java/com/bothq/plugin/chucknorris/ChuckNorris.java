package com.bothq.plugin.chucknorris;

import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@Slf4j
public abstract class ChuckNorris extends PluginBase {

    private final String name = "ChuckNorris";
    private final String description = "A plugin that tells Chuck Norris jokes.";

    @Override
    public void createConfig(IConfig config) {
        log.info("{} config created!", name);
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

    if (event.getChannelType() != ChannelType.TEXT) {
        return;
    }

    if (event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong()
            || event.getAuthor().isBot()
            || event.getAuthor().isSystem()) {
        return;
    }

    var messageContent = event.getMessage().getContentRaw();

    if (messageContent.equalsIgnoreCase("!chucknorris")) {
        try {
            URL url = new URL("https://api.chucknorris.io/jokes/random");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " +responseCode);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(url.openStream());
                String joke = json.get("value").asText();

                event.getChannel().sendMessage(joke).queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
}