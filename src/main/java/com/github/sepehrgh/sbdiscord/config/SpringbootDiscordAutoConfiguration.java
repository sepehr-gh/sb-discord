package com.github.sepehrgh.sbdiscord.config;
import com.github.sepehrgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.Builder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;


@Configuration
@ComponentScan("com.github.sepehrgh.sbdiscord")
@ConditionalOnProperty(value = "spring.discord.enabled", havingValue = "true")
@EnableConfigurationProperties(SBDiscordProperties.class)
public class SpringbootDiscordAutoConfiguration {

    @ConditionalOnMissingBean(JDA.class)
    @Builder
    public JDA jda(@Value("${spring.discord.token}") String token, DiscordCommandListener discordCommandListener) throws LoginException {
        net.dv8tion.jda.api.JDABuilder builder = net.dv8tion.jda.api.JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.addEventListeners(discordCommandListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }

}
