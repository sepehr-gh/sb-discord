package io.github.sepgh.sbdiscord.command.context;

import org.springframework.util.Assert;

public class InheritableThreadLocalCommandContextHolderStrategy implements CommandContextHolderStrategy {
    private static final InheritableThreadLocal<CommandContext> contextHolder = new InheritableThreadLocal<CommandContext>(){
        @Override
        protected CommandContext initialValue() {
            return new DefaultContextImpl();
        }
    };

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
    public void setContext(CommandContext rowContext) {
        Assert.notNull(rowContext, "Only non-null Command context instances are permitted");
        contextHolder.set(rowContext);
    }

    @Override
    public CommandContext createEmptyContext() {
        return new DefaultContextImpl();
    }
}
