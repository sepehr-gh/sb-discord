package io.github.sepgh.sbdiscord;

import io.github.sepgh.sbdiscord.config.DiscordReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringConfiguration {
    @Bean
    @Primary
    public JDA jda(DiscordReadyListener discordReadyListener){
        JDA jda = Mockito.mock(JDA.class);
        ReadyEvent readyEvent = Mockito.mock(ReadyEvent.class);
        discordReadyListener.onReady(readyEvent);
        return jda;
    }
}
