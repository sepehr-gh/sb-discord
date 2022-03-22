package com.github.sepehrgh.sbdiscord.command.context;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

public class CommandContextHolder {
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    public static final String SYSTEM_PROPERTY = "spring.discord.strategy";
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
    private static CommandContextHolderStrategy strategy;
    private static int initializeCount = 0;

    public CommandContextHolder() {
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static CommandContext getContext() {
        return strategy.getContext();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    private static void initialize() {
        if (!StringUtils.hasText(strategyName)) {
            strategyName = MODE_INHERITABLETHREADLOCAL;
        }

        if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalCommandContextHolderStrategy();
        }else if(strategyName.equals(MODE_INHERITABLETHREADLOCAL)){
            strategy = new InheritableThreadLocalCommandContextHolderStrategy();
        } else {
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (CommandContextHolderStrategy) customStrategy.newInstance();
            } catch (Exception var2) {
                ReflectionUtils.handleReflectionException(var2);
            }
        }

        ++initializeCount;
    }

    public static void setContext(CommandContext context) {
        strategy.setContext(context);
    }

    public static void setStrategyName(String strategyName) {
        CommandContextHolder.strategyName = strategyName;
        initialize();
    }

    public static CommandContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public static CommandContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public String toString() {
        return "CommandContextHolder[strategy='" + strategyName + "'; initializeCount=" + initializeCount + "]";
    }

    static {
        initialize();
    }
}
