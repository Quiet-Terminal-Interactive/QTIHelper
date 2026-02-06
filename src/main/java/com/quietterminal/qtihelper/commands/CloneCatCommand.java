package com.quietterminal.qtihelper.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CloneCatCommand {

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

        String[] parts = args.split(" ", 3);
        if (parts.length < 3) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(new Color(237, 66, 69))
                    .setDescription("Missing arguments!\n\n**Usage:** `&&clonecat <category id> <prefix> <name>`")
                    .setFooter("Example: &&clonecat 123456789 sv Survival");
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String categoryId = parts[0];
        String newPrefix = parts[1];
        String newCategoryName = parts[2];

        Guild guild = event.getGuild();
        Category sourceCategory = guild.getCategoryById(categoryId);

        if (sourceCategory == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setColor(new Color(237, 66, 69))
                    .setDescription("Could not find a category with ID `" + categoryId + "`.");
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        Category newCategory = guild.createCategory(newCategoryName).complete();

        for (PermissionOverride override : sourceCategory.getPermissionOverrides()) {
            if (override.isRoleOverride()) {
                newCategory.upsertPermissionOverride(override.getRole())
                        .setAllowed(override.getAllowed())
                        .setDenied(override.getDenied())
                        .complete();
            } else if (override.isMemberOverride()) {
                newCategory.upsertPermissionOverride(override.getMember())
                        .setAllowed(override.getAllowed())
                        .setDenied(override.getDenied())
                        .complete();
            }
        }

        List<GuildChannel> sourceChannels = sourceCategory.getChannels();
        List<String> createdChannelNames = new ArrayList<>();

        for (GuildChannel channel : sourceChannels) {
            String originalName = channel.getName();
            String strippedName;
            int dashIndex = originalName.indexOf('-');
            if (dashIndex != -1) {
                strippedName = originalName.substring(dashIndex + 1);
            } else {
                strippedName = originalName;
            }
            String newChannelName = newPrefix + "-" + strippedName;

            if (channel instanceof NewsChannel) {
                NewsChannel newsChannel = (NewsChannel) channel;
                ChannelAction<NewsChannel> action = newCategory.createNewsChannel(newChannelName);
                if (newsChannel.getTopic() != null) {
                    action.setTopic(newsChannel.getTopic());
                }
                action.setNSFW(newsChannel.isNSFW());
                action.complete();
            } else if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                ChannelAction<TextChannel> action = newCategory.createTextChannel(newChannelName);
                if (textChannel.getTopic() != null) {
                    action.setTopic(textChannel.getTopic());
                }
                action.setSlowmode(textChannel.getSlowmode());
                action.setNSFW(textChannel.isNSFW());
                action.complete();
            } else if (channel instanceof ForumChannel) {
                ForumChannel forumChannel = (ForumChannel) channel;
                ChannelAction<ForumChannel> action = newCategory.createForumChannel(newChannelName);
                if (forumChannel.getTopic() != null) {
                    action.setTopic(forumChannel.getTopic());
                }
                action.setSlowmode(forumChannel.getSlowmode());
                action.setNSFW(forumChannel.isNSFW());
                action.complete();
            } else if (channel instanceof MediaChannel) {
                newCategory.createMediaChannel(newChannelName).complete();
            } else if (channel instanceof VoiceChannel) {
                VoiceChannel voiceChannel = (VoiceChannel) channel;
                ChannelAction<VoiceChannel> action = newCategory.createVoiceChannel(newChannelName);
                action.setBitrate(voiceChannel.getBitrate());
                action.setUserlimit(voiceChannel.getUserLimit());
                action.complete();
            } else if (channel instanceof StageChannel) {
                newCategory.createStageChannel(newChannelName).complete();
            } else {
                continue;
            }

            createdChannelNames.add(newChannelName);
        }

        StringBuilder channelList = new StringBuilder();
        for (String name : createdChannelNames) {
            channelList.append("- `").append(name).append("`\n");
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Category Cloned")
                .setColor(new Color(87, 242, 135))
                .setDescription(String.format(
                        "Cloned **%s** → **%s** with prefix `%s`",
                        sourceCategory.getName(), newCategoryName, newPrefix
                ))
                .addField("Created Channels (" + createdChannelNames.size() + ")", channelList.toString(), false);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
