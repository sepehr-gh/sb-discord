package com.github.sepehrgh.sbdiscord.config;

import com.github.sepehrgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;


@Configuration
@ComponentScan("com.github.sepehrgh.sbdiscord")
@EnableConfigurationProperties(SBDiscordProperties.class)
@Slf4j
public class SpringbootDiscordAutoConfiguration {

    @ConditionalOnMissingBean(JDA.class)
    @Bean
    public JDA jda(SBDiscordProperties sbDiscordProperties, DiscordCommandListener discordCommandListener) throws LoginException {
        log.info("Creating JDA bean ...");
        net.dv8tion.jda.api.JDABuilder builder = net.dv8tion.jda.api.JDABuilder.createDefault(sbDiscordProperties.getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.addEventListeners(discordCommandListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }

}
