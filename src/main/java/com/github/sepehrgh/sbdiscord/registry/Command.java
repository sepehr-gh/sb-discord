package com.github.sepehrgh.sbdiscord.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.var;

import java.lang.reflect.Method;

@Getter
@Builder
@AllArgsConstructor
public class Command {
    private String name;
    private String description;
    private Class<?> clazz;
    private Method method;

    //TODO
    protected boolean isValid(String... params){
        return false;
    }

    //TODO
    public void call(String... params){
        var methodParams = this.method.getParameters();

    }
}
