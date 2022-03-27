package io.github.sepgh.sbdiscord.config;

import io.github.sepgh.sbdiscord.command.CommandEntity;
import io.github.sepgh.sbdiscord.command.CommandRegistry;
import io.github.sepgh.sbdiscord.command.PermissionValidator;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import io.github.sepgh.sbdiscord.command.context.DefaultContextImpl;
import io.github.sepgh.sbdiscord.config.properties.SBDiscordProperties;
import io.github.sepgh.sbdiscord.exceptions.CommandParseException;
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
    private final PermissionValidator permissionValidator;

    public DiscordCommandListener(CommandRegistry commandRegistry, SBDiscordProperties sbDiscordProperties, PermissionValidator permissionValidator) {
        this.commandRegistry = commandRegistry;
        this.sbDiscordProperties = sbDiscordProperties;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        CommandContextHolder.setContext(DefaultContextImpl.builder()
                .guildMessageReceivedEvent(Optional.of(event))
                .build());

        String message = event.getMessage().getContentStripped();
        if (message.startsWith("" + sbDiscordProperties.getBasicCommandSignature())) {
            String[] commandAndParameters = getCommandAndParameters(message);
            Optional<CommandEntity> optionalCommand = this.commandRegistry.findCommandByName(commandAndParameters[0]);
            if (!optionalCommand.isPresent()) {
                return; // ignoring: command doesnt exist
            }
            CommandEntity commandEntity = optionalCommand.get();
            if (!this.permissionValidator.hasPermission(event.getMember(), commandEntity.getName())) {
                return;
            }
            try {
                commandEntity.call(commandAndParameters[1]);
            } catch (CommandParseException e) {
                e.printStackTrace(); //todo: reply with parse exception explained
            } catch (Exception e) {
                log.error(String.format("Failed to handle message: %s", message), e);
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CommandContextHolder.setContext(DefaultContextImpl.builder()
                .slashCommandEvent(Optional.of(event))
                .build());
        Optional<CommandEntity> optionalCommand = this.commandRegistry.findCommandByName(event.getName());
        if (optionalCommand.isPresent()){
            CommandEntity commandEntity = optionalCommand.get();
            if (commandEntity.isSlashDiffer())
                event.deferReply().queue();
            try {
                commandEntity.call(event.getOptions());
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
