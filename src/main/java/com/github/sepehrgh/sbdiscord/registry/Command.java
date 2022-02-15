package com.github.sepehrgh.sbdiscord.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
    protected String getParamAtIndex(int i){
        return "";
    }

    //TODO
    protected boolean isValid(String... params){
        return false;
    }

    //TODO
    public void call(String... params){

    }
}
