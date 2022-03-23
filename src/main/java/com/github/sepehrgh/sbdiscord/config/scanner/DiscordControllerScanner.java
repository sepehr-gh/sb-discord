package com.github.sepehrgh.sbdiscord.config.scanner;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.annotations.DiscordController;
import com.github.sepehrgh.sbdiscord.command.Command;
import com.github.sepehrgh.sbdiscord.command.CommandRegistry;
import com.github.sepehrgh.sbdiscord.config.StaticConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;


@Slf4j
@Component
public class DiscordControllerScanner {
    private final CommandRegistry commandRegistry;
    private static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";

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
                    DiscordCommand annotation = method.getAnnotation(DiscordCommand.class);
                    if (this.isValid(method, annotation)){
                        this.commandRegistry.register(Command.builder()
                                .scope(annotation.scope())
                                .object(discordControllerBean)
                                .name(annotation.name())
                                .description(annotation.description())
                                .method(method)
                                .build());
                    }
                }
            }
        });

        log.info("Finished registering Discord commands ...");
    }

    private boolean isValid(Method commandMethod, DiscordCommand annotation){
        if ((commandMethod.getModifiers() & Modifier.PUBLIC) == 0){
            log.error(String.format("Command %s can't be registered because method modifier is not public", commandMethod.getName()));
            return false;
        }
        if (!annotation.name().matches(ALPHANUMERIC_PATTERN)){
            log.error(String.format("Command %s can't be registered because the DiscordCommand name is not alphanumeric", commandMethod.getName()));
            return false;
        }
        for (Parameter parameter : commandMethod.getParameters()) {
            if (!StaticConfiguration.getAcceptableParameterTypes().contains(parameter.getType())){
                log.error(String.format("Command %s can't be registered because the parameter types are not accepted", commandMethod.getName()));
                return false;
            }
        }
        return true;
    }

}
