package io.github.sepgh.sbdiscord.command;

import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.config.ParameterTypes;
import io.github.sepgh.sbdiscord.exceptions.CommandParseException;
import io.github.sepgh.sbdiscord.parser.CommandParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Builder
@AllArgsConstructor
public class CommandEntity {
    private final String name;
    private final String description;
    private final DiscordCommand.Scope scope;
    private final DiscordCommand.Type type;
    private final boolean slashDiffer;
    private final boolean isPublic;
    @Setter
    private Object object;
    private final Method method;
    private CommandParser parser;

    public CommandParser getParser(){
        synchronized (this){
            if (this.parser == null){
                this.parser = new CommandParser(this.method);
            }
        }
        return this.parser;
    }

    public void call(String params) throws CommandParseException, InvocationTargetException, IllegalAccessException {
        this.callMethod((Object[]) this.getParser().parse(params));
    }

    public void call(String... params) throws CommandParseException, InvocationTargetException, IllegalAccessException {
        this.callMethod((Object[]) this.getParser().parse(params));
    }

    public void call(List<OptionMapping> options) throws CommandParseException, InvocationTargetException, IllegalAccessException {
        this.callMethod((Object[]) this.getParser().parse(options));
    }

    private void callMethod(Object... params) throws InvocationTargetException, IllegalAccessException {
        this.getMethod().invoke(this.getObject(), params);
    }

    public void register(JDA jda, Optional<Guild> optionalGuild){
        this.register(jda, optionalGuild);
    }

    public void register(JDA jda, Optional<Guild> optionalGuild, @Nullable Consumer<? super Command> success){
        CommandData commandData = new CommandData(name, this.getDescription());
        commandData.setDefaultEnabled(this.isPublic);
        for (Parameter parameter : this.getMethod().getParameters()) {
            DiscordParameter discordParameter = parameter.getAnnotation(DiscordParameter.class);
            commandData.addOption(ParameterTypes.MAP.get(parameter.getType()), discordParameter.name(), discordParameter.description(), discordParameter.required());
        }
        if (this.getScope().equals(DiscordCommand.Scope.SERVER)) {
            optionalGuild.ifPresent(guild -> {
                guild.upsertCommand(commandData).queue(success);
            });
        }else {
            jda.upsertCommand(commandData).complete();
        }
    }
}
