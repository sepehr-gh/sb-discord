package io.github.sepgh.sbdiscord.annotations;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordParameter {
    boolean required() default true;
    String value() default "";
    String description() default "description not provided";
    String name();
}
