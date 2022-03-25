package io.github.sepgh.sbdiscord.config;

import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.CommandEntity;
import io.github.sepgh.sbdiscord.command.CommandRegistry;
import io.github.sepgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class DiscordReadyListener extends ListenerAdapter {
    private final ApplicationContext applicationContext;
    private final CommandRegistry commandRegistry;
    private final SBDiscordProperties sbDiscordProperties;
    private static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";
    private Guild guild;

    @Autowired
    public DiscordReadyListener(ApplicationContext applicationContext, CommandRegistry commandRegistry, SBDiscordProperties sbDiscordProperties) {
        this.applicationContext = applicationContext;
        this.commandRegistry = commandRegistry;
        this.sbDiscordProperties = sbDiscordProperties;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.registerCommands(event);
    }

    protected void registerCommands(ReadyEvent event){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(
                DiscordController.class
        );

        log.info("Registering Discord commands ...");

        if (sbDiscordProperties.getGuildId() != null && sbDiscordProperties.getGuildId() != 0L){
            this.guild = event.getJDA().getGuildById(sbDiscordProperties.getGuildId());
        }

        beansWithAnnotation.values().forEach(discordControllerBean -> {
            if (AopUtils.isAopProxy(discordControllerBean)){
                return;
            }

            Class<?> aClass = AopUtils.getTargetClass(discordControllerBean);
            for (Method method : aClass.getMethods()) {
                if (!method.isAnnotationPresent(DiscordCommand.class)) {
                    return;
                }
                DiscordCommand annotation = method.getAnnotation(DiscordCommand.class);
                if (!this.isValid(method, annotation)) {
                    return;
                }
                CommandEntity commandEntity = CommandEntity.builder()
                        .scope(annotation.scope())
                        .isPublic(sbDiscordProperties.isPublicCommand(annotation.name()))
                        .type(annotation.type())
                        .slashDiffer(annotation.slashDiffer())
                        .object(discordControllerBean)
                        .name(annotation.name())
                        .description(annotation.description())
                        .method(method)
                        .build();
                this.commandRegistry.register(commandEntity);
                if (commandEntity.getType().equals(DiscordCommand.Type.SLASH)){
                    commandEntity.register(
                            event.getJDA(),
                            Optional.ofNullable(this.guild),
                            this::setPermissions
                    );
                }
            }
        });

        log.info("Finished registering Discord commands ...");
    }

    private void setPermissions(Command command){
        if (this.guild == null)
            return;
        if (!sbDiscordProperties.isPublicCommand(command.getName())){
            List<CommandPrivilege> privilegesOfCommand = sbDiscordProperties.getPrivilegesOfCommand(command.getName());
            command.updatePrivileges(this.guild, privilegesOfCommand).queue();
        }
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
