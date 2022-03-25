package io.github.sepgh.sbdiscord.config;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterTypes {
    public static List<Type> ACCEPTABLE;
    public static final Map<Class<?>, OptionType> MAP = new HashMap<>();

    static {
        ACCEPTABLE = Arrays.asList(Role.class, GuildChannel.class, Member.class, User.class, IMentionable.class, Long.class, Integer.class, Boolean.class, String.class);
    }

    static {
        MAP.put(Role.class, OptionType.ROLE);
        MAP.put(GuildChannel.class, OptionType.CHANNEL);
        MAP.put(Member.class, OptionType.USER);
        MAP.put(User.class, OptionType.USER);
        MAP.put(IMentionable.class, OptionType.MENTIONABLE);
        MAP.put(Long.class, OptionType.NUMBER);
        MAP.put(Integer.class, OptionType.INTEGER);
        MAP.put(Double.class, OptionType.INTEGER);
        MAP.put(Boolean.class, OptionType.BOOLEAN);
        MAP.put(String.class, OptionType.STRING);
    }

}
