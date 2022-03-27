package io.github.sepgh.sbdiscord.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import io.github.sepgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching
@ComponentScan("io.github.sepgh.sbdiscord")
@EnableConfigurationProperties(SBDiscordProperties.class)
@Slf4j
public class SpringbootDiscordAutoConfiguration {
    private final SBDiscordProperties sbDiscordProperties;

    @Autowired
    public SpringbootDiscordAutoConfiguration(SBDiscordProperties sbDiscordProperties) {
        this.sbDiscordProperties = sbDiscordProperties;
    }

    @ConditionalOnMissingBean(JDA.class)
    @Bean
    public JDA jda(DiscordCommandListener discordCommandListener, DiscordReadyListener discordReadyListener) throws LoginException, InterruptedException {
        log.info("Creating JDA bean ...");
        net.dv8tion.jda.api.JDABuilder builder = net.dv8tion.jda.api.JDABuilder.createDefault(sbDiscordProperties.getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.addEventListeners(discordCommandListener);
        builder.addEventListeners(discordReadyListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }

    @Bean("sbDiscordCacheManager")
    @ConditionalOnMissingBean(value = CacheManager.class, name = "sbDiscordCacheManager")
    public CacheManager sbDiscordCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(100).build().asMap(), false);
            }
        };

        return cacheManager;
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        if (this.sbDiscordProperties.getCommandPrivilegesFile() == null){
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<SBDiscordProperties.CommandPrivileges> commandPrivileges = objectMapper.readValue(new File(this.sbDiscordProperties.getCommandPrivilegesFile()),
                new TypeReference<List<SBDiscordProperties.CommandPrivileges>>() {
                });
        this.sbDiscordProperties.getCommandPrivileges().addAll(commandPrivileges);
    }

}
