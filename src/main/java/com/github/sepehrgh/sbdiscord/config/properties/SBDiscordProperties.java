package com.github.sepehrgh.sbdiscord.config.properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.discord")
@Getter
@Setter
public class SBDiscordProperties {
    public Character basicCommandSignature = '!';
    public boolean enabled;
    public String token;
}
