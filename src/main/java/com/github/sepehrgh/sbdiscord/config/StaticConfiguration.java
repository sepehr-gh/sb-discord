package com.github.sepehrgh.sbdiscord.config;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class StaticConfiguration {

    public static List<Type> getAcceptableParameterTypes(){
        return Arrays.asList(Role.class, GuildChannel.class, Member.class, IMentionable.class, Long.class, Integer.class, Boolean.class, String.class);
    }

}
