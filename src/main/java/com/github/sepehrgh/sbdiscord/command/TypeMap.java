package com.github.sepehrgh.sbdiscord.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.HashMap;
import java.util.Map;

public class TypeMap {
    public static final Map<Class<?>, OptionType> MAP = new HashMap<>();
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
