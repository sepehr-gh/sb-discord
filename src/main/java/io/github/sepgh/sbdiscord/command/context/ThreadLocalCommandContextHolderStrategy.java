package io.github.sepgh.sbdiscord.command.context;

import org.springframework.util.Assert;

public class ThreadLocalCommandContextHolderStrategy implements CommandContextHolderStrategy {
    private static final ThreadLocal<CommandContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public CommandContext getContext() {
        CommandContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = this.createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    @Override
    public void setContext(CommandContext commandContext) {
        Assert.notNull(commandContext, "Only non-null Command context instances are permitted");
        contextHolder.set(commandContext);
    }

    @Override
    public CommandContext createEmptyContext() {
        return new DefaultContextImpl();
    }
}
