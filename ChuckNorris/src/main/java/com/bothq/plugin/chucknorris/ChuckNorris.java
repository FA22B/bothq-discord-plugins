package com.bothq.plugin.chucknorris;

import com.bothq.lib.annotation.DiscordEventListener;
import com.bothq.lib.plugin.PluginBase;
import com.bothq.lib.plugin.config.IConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

@Getter
@Slf4j
public abstract class ChuckNorris extends PluginBase {

    // The name of the plugin
    private final String name = "ChuckNorris";

    // The description of the plugin
    private final String description = "A plugin that tells Chuck Norris jokes.";

    // Create the plugin configuration
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

        // Only handle guild text channel messages here
        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        // Check for message skip conditions
        if (event.getAuthor().getIdLong() == jda.getSelfUser().getIdLong() // Self user ID check
                || event.getAuthor().isBot() // Bot check (this includes the self user, but we keep it in here for example purposes)
                || event.getAuthor().isSystem()) { // System message check (eg. user join)
            return;
        }

        // Get the raw message content
        var messageContent = event.getMessage().getContentRaw();

        // Check if the message is a request for a Chuck Norris joke
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
                    String inline = "";
                    Scanner scanner = new Scanner(url.openStream());

                    while(scanner.hasNext()) {
                        inline += scanner.nextLine();
                    }

                    scanner.close();

                    JSONObject json = new JSONObject(inline);
                    String joke = json.getString("value");

                    // Send the joke
                    event.getChannel().sendMessage(joke).queue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}