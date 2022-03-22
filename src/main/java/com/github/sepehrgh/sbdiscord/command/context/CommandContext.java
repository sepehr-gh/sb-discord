package com.github.sepehrgh.sbdiscord.command.context;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Optional;


public interface CommandContext {
    default Optional<JDA> getJda(){
        return Optional.empty();
    }
    default boolean isSlashCommand(){
        return false;
    }
    default Optional<GuildMessageReceivedEvent> getEvent(){
        return Optional.empty();
    }
    default Optional<SlashCommandEvent> getSlashCommandEvent(){
        return Optional.empty();
    }
}
