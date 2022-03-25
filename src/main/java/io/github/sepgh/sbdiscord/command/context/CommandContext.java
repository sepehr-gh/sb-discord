package io.github.sepgh.sbdiscord.command.context;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Optional;


public interface CommandContext {
    boolean isSlashCommand();
    Optional<GuildMessageReceivedEvent> getGuildMessageReceivedEvent();
    Optional<SlashCommandEvent> getSlashCommandEvent();
}
