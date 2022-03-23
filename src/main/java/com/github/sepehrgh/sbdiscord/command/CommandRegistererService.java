package com.github.sepehrgh.sbdiscord.command;

import com.github.sepehrgh.sbdiscord.annotations.DiscordParameter;
import com.github.sepehrgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class CommandRegistererService {
    private final JDA jda;
    private final CommandRegistry commandRegistry;
    private final SBDiscordProperties sbDiscordProperties;
    private final Map<Class<?>, OptionType> typeMap;

    @Autowired
    public CommandRegistererService(JDA jda, CommandRegistry commandRegistry, SBDiscordProperties sbDiscordProperties) {
        this.jda = jda;
        this.commandRegistry = commandRegistry;
        this.sbDiscordProperties = sbDiscordProperties;
        this.typeMap = new HashMap<>();
        if (sbDiscordProperties.isSlashCommandEnabled())
            init();
    }

    private void init() {
        this.typeMap.put(Role.class, OptionType.ROLE);
        this.typeMap.put(GuildChannel.class, OptionType.CHANNEL);
        this.typeMap.put(Member.class, OptionType.USER);
        this.typeMap.put(User.class, OptionType.USER);
        this.typeMap.put(IMentionable.class, OptionType.MENTIONABLE);
        this.typeMap.put(Long.class, OptionType.NUMBER);
        this.typeMap.put(Integer.class, OptionType.INTEGER);
        this.typeMap.put(Double.class, OptionType.INTEGER);
        this.typeMap.put(Boolean.class, OptionType.BOOLEAN);
        this.typeMap.put(String.class, OptionType.STRING);
    }

    public void registerCommands(){
        if (!sbDiscordProperties.isSlashCommandEnabled())
            return;

        this.commandRegistry.getCommands().forEach((name, command) -> {
            log.info(String.format("registering slash command with name %s", name));
            CommandData commandData = new CommandData(name, command.getDescription());
            for (Parameter parameter : command.getMethod().getParameters()) {
                DiscordParameter discordParameter = parameter.getAnnotation(DiscordParameter.class);
                commandData.addOption(this.typeMap.get(parameter.getType()), discordParameter.name(), discordParameter.description(), discordParameter.required());
            }
            this.jda.upsertCommand(commandData).complete();
        });
    }

}
