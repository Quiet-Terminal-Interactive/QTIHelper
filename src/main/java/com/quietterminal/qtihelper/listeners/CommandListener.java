package com.quietterminal.qtihelper.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.quietterminal.qtihelper.commands.*;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        
        String content = event.getMessage().getContentRaw();
        
        if (content.contains("&!")) {
            return;
        }
        
        if (content.startsWith("&&")) {
            handleCommand(event, content);
            return;
        }
    }
    
    private void handleCommand(MessageReceivedEvent event, String content) {
        String raw = content.substring(2).trim();
        int newline = raw.indexOf('\n');

        String command;
        String args;

        if (newline == -1) {
            String[] split = raw.split(" ", 2);
            command = split[0].toLowerCase();
            args = split.length > 1 ? split[1] : "";
        } else {
            command = raw.substring(0, newline).trim().toLowerCase();
            args = raw.substring(newline + 1);
        }
        
        switch (command) {
            case "clonecat":
                new CloneCatCommand().execute(event, args);
                break;
            case "motd":
                new MotdCommand().execute(event, args);
                break;
            default:
                new DefaultCommand().execute(event);
                break;
        }
    }
}