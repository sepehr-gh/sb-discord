package io.github.sepgh.sbdiscord.config;

import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.Command;
import io.github.sepgh.sbdiscord.command.CommandRegistry;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

@Component
@Slf4j
public class DiscordReadyListener extends ListenerAdapter {
    private final ApplicationContext applicationContext;
    private final CommandRegistry commandRegistry;
    private static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";

    @Autowired
    public DiscordReadyListener(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
        this.applicationContext = applicationContext;
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
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
                        Command command = Command.builder()
                                .scope(annotation.scope())
                                .type(annotation.type())
                                .slashDiffer(annotation.slashDiffer())
                                .object(discordControllerBean)
                                .name(annotation.name())
                                .description(annotation.description())
                                .method(method)
                                .build();
                        this.commandRegistry.register(command);
                        if (command.getType().equals(DiscordCommand.Type.SLASH)){
                            command.register(event.getJDA());
                        }
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
            if (!ParameterTypes.ACCEPTABLE.contains(parameter.getType())){
                log.error(String.format("Command %s can't be registered because the parameter types are not accepted", commandMethod.getName()));
                return false;
            }
        }
        return true;
    }

}
