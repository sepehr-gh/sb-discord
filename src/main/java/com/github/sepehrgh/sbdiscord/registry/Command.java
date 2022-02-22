package com.github.sepehrgh.sbdiscord.registry;

import com.github.sepehrgh.sbdiscord.parser.CommandParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.var;

import java.lang.reflect.Method;

@Getter
@Builder
@AllArgsConstructor
public class Command {
    private final String name;
    private final String description;
    private final Class<?> clazz;
    private final Method method;
    private CommandParser parser;


    //TODO
    protected boolean isValid(String... params){
        return false;
    }

    public CommandParser getParser(){
        synchronized (this){
            if (this.parser == null){
                this.parser = new CommandParser(this.method);
            }
        }
        return this.parser;
    }

    //TODO
    public void call(String... params){
        var methodParams = this.method.getParameters();

    }
}
