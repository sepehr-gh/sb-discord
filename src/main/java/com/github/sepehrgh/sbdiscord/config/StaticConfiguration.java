package com.github.sepehrgh.sbdiscord.config;

import net.dv8tion.jda.api.entities.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class StaticConfiguration {

    public static List<Type> getAcceptableParameterTypes(){
        return Arrays.asList(Role.class, GuildChannel.class, Member.class, User.class, IMentionable.class, Long.class, Integer.class, Boolean.class, String.class);
    }

}
