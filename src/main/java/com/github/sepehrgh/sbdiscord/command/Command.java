package com.github.sepehrgh.sbdiscord.command;

import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import com.github.sepehrgh.sbdiscord.parser.CommandParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@Builder
@AllArgsConstructor
public class Command {
    private final String name;
    private final String description;
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

    private void callMethod(Object... params) throws InvocationTargetException, IllegalAccessException {
        this.getMethod().invoke(this.getObject(), params);
    }
}
