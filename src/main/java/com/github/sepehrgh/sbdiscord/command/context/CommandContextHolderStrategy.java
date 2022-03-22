package com.github.sepehrgh.sbdiscord.command.context;

public interface CommandContextHolderStrategy {
    void clearContext();
    CommandContext getContext();
    void setContext(CommandContext rowContext);
    CommandContext createEmptyContext();
}
