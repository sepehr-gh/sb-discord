package com.github.sepehrgh.sbdiscord.annotations;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordCommand {
    String name();
    String description() default "Description not available";
}