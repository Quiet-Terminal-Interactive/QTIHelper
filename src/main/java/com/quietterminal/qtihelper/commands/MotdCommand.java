package com.quietterminal.qtihelper.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import com.google.gson.JsonObject;

public class MotdCommand {

    private static final String QTI_ROLE_ID = "1297669279461933116";

    public void execute(MessageReceivedEvent event, String args) {
        Member member = event.getMember();
        if (member == null || member.getRoles().stream().noneMatch(r -> r.getId().equals(QTI_ROLE_ID))) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Permission Denied")
                    .setColor(new Color(237, 66, 69))
                    .setDescription("You need the <@&" + QTI_ROLE_ID + "> role to use this command.");
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (args.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Missing Argument")
                    .setColor(new Color(237, 66, 69))
                    .setDescription("Usage: &&motd <motd>");
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String password = "NiceTrySourceControl";

        JsonObject obj = new JsonObject();
        obj.addProperty("motd", args);
        obj.addProperty("token", password);

        String json = obj.toString();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.quietterminal.co.uk/api/motd"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        try {
            client.send(request, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("MOTD Set")
                .setColor(new Color(87, 242, 135))
                .setDescription(String.format(
                        "MOTD set to `%s`",
                        args
                ));
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
