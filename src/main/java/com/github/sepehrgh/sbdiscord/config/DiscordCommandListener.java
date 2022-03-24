package com.github.sepehrgh.sbdiscord.config;

import com.github.sepehrgh.sbdiscord.command.Command;
import com.github.sepehrgh.sbdiscord.command.CommandRegistry;
import com.github.sepehrgh.sbdiscord.command.context.CommandContextHolder;
import com.github.sepehrgh.sbdiscord.command.context.DefaultContextImpl;
import com.github.sepehrgh.sbdiscord.config.properties.SBDiscordProperties;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class DiscordCommandListener extends ListenerAdapter {
    private final CommandRegistry commandRegistry;
    private final SBDiscordProperties sbDiscordProperties;

    public DiscordCommandListener(CommandRegistry commandRegistry, SBDiscordProperties sbDiscordProperties) {
        this.commandRegistry = commandRegistry;
        this.sbDiscordProperties = sbDiscordProperties;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        CommandContextHolder.setContext(DefaultContextImpl.builder()
                .guildMessageReceivedEvent(Optional.of(event))
                .build());

        String message = event.getMessage().getContentStripped();
        if (message.startsWith("" + sbDiscordProperties.getBasicCommandSignature())) {
            String[] commandAndParameters = getCommandAndParameters(message);
            Optional<Command> optionalCommand = this.commandRegistry.findCommandByName(commandAndParameters[0]);
            if (optionalCommand.isPresent()) {
                try {
                    optionalCommand.get().call(commandAndParameters[1]);
                } catch (CommandParseException e) {
                    e.printStackTrace(); //todo: reply with parse exception explained
                } catch (Exception e) {
                    log.error(String.format("Failed to handle message: %s", message), e);
                }
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CommandContextHolder.setContext(DefaultContextImpl.builder()
                .slashCommandEvent(Optional.of(event))
                .build());
        event.deferReply().queue();
        Optional<Command> optionalCommand = this.commandRegistry.findCommandByName(event.getName());
        if (optionalCommand.isPresent()){
            try {
                optionalCommand.get().call(event.getOptions());
            } catch (CommandParseException e) {
                e.printStackTrace(); //todo: reply with parse exception explained
            } catch (Exception e) {
                log.error(String.format("Failed to handle command: %s", event.getName()), e);
            }
        }else {
            //todo: return command is missing
        }
    }


    private String[] getCommandAndParameters(String message){
        message = message.replace("" + sbDiscordProperties.getBasicCommandSignature(), "");
        String command = message.split(" ")[0];
        return new String[]{command, message.replace(command, "").trim()};
    }

}
