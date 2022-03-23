package com.github.sepehrgh.sbdiscord.config;

import com.github.sepehrgh.sbdiscord.command.CommandRegistererService;
import com.github.sepehrgh.sbdiscord.config.scanner.DiscordControllerScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SBDiscordApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final DiscordControllerScanner discordControllerScanner;
    private final CommandRegistererService commandRegistererService;

    public SBDiscordApplicationListener(DiscordControllerScanner discordControllerScanner, CommandRegistererService commandRegistererService) {
        this.discordControllerScanner = discordControllerScanner;
        this.commandRegistererService = commandRegistererService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        discordControllerScanner.scan(contextRefreshedEvent.getApplicationContext());
        commandRegistererService.registerCommands();
    }
}
