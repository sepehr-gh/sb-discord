package com.github.sepehrgh.sbdiscord.config.scanner;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.annotations.DiscordController;
import com.github.sepehrgh.sbdiscord.registry.Command;
import com.github.sepehrgh.sbdiscord.registry.CommandRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;


@Slf4j
@Component
public class DiscordControllerScanner {
    private final CommandRegistry commandRegistry;

    @Autowired
    public DiscordControllerScanner(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void scan(ApplicationContext applicationContext){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(
                DiscordController.class
        );

        log.info("Registering Discord commands ...");

        beansWithAnnotation.values().forEach(discordControllerBean -> {
            if (AopUtils.isAopProxy(discordControllerBean)){
                return;
            }

            Class<?> aClass = AopUtils.getTargetClass(discordControllerBean);
            for (Method method : aClass.getMethods()) {
                if (method.isAnnotationPresent(DiscordCommand.class)){
                    assert method.isAccessible();
                    DiscordCommand annotation = method.getAnnotation(DiscordCommand.class);
                    this.commandRegistry.register(Command.builder()
                            .clazz(aClass)
                            .name(annotation.name())
                            .description(annotation.description())
                            .method(method)
                            .clazz(aClass)
                            .build());
                }
            }
        });

        log.info("Finished registering Discord commands ...");
    }

}
