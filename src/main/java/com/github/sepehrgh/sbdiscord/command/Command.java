package com.github.sepehrgh.sbdiscord.command;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import com.github.sepehrgh.sbdiscord.parser.CommandParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class Command {
    private final String name;
    private final String description;
    private final DiscordCommand.Scope scope;
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
}
