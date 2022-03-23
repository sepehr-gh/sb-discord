package com.github.sepehrgh.sbdiscord;

import com.github.sepehrgh.sbdiscord.controllers.TestController;
import net.dv8tion.jda.api.JDA;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringConfiguration {
    @Bean
    @Primary
    public JDA jda(){
        return Mockito.mock(JDA.class);
    }
}
