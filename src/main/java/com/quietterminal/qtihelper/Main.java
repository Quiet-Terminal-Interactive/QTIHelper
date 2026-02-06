package com.quietterminal.qtihelper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import com.quietterminal.qtihelper.config.Config;
import com.quietterminal.qtihelper.listeners.CommandListener;

public class Main {
    private static final String TOKEN = Config.DISCORD_TOKEN;
    
    public static void main(String[] args) {
        try {
            @SuppressWarnings("unused")
            JDA jda = JDABuilder.createDefault(TOKEN)
                    .enableIntents(
                            GatewayIntent.GUILD_EXPRESSIONS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_POLLS,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MODERATION,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .addEventListeners(new CommandListener())
                    .disableCache(CacheFlag.FORUM_TAGS)
                    .enableCache(CacheFlag.ACTIVITY)
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build()
                    .awaitReady();

            System.out.println("Finished Loading!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}