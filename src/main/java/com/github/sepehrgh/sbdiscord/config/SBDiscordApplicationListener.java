package com.github.sepehrgh.sbdiscord.config;

import com.github.sepehrgh.sbdiscord.command.SlashCommandRegistererService;
import com.github.sepehrgh.sbdiscord.config.scanner.DiscordControllerScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SBDiscordApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final DiscordControllerScanner discordControllerScanner;

    public SBDiscordApplicationListener(DiscordControllerScanner discordControllerScanner) {
        this.discordControllerScanner = discordControllerScanner;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        discordControllerScanner.scan(contextRefreshedEvent.getApplicationContext());
    }
}
