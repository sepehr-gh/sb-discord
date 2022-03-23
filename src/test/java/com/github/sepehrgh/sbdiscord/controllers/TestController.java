package com.github.sepehrgh.sbdiscord.controllers;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.annotations.DiscordController;
import com.github.sepehrgh.sbdiscord.annotations.DiscordParameter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordController
public class TestController {
    @DiscordCommand(name = "test")
    public boolean test(){
        return true;
    }

    @DiscordCommand(name = "parameterCommand")
    public boolean parameterCommand(
            @DiscordParameter(name="p1") String parameter1,
            @DiscordParameter(name = "parameter2") String parameter2,
            @DiscordParameter(name="parameter3", required = false, value = "4") String parameter3
    ){
        return true;
    }

    @DiscordCommand(name = "noparam")
    public boolean noparam(){
        return true;
    }


    @DiscordCommand(name = "failingParameterType")
    public boolean failingParameterType(
            @DiscordParameter(name = "parameter1") Exception parameter1
    ){
        return true;
    }

    @DiscordCommand(name = "failing Alpha Numeric")
    public boolean failingAlphaNumeric(
            @DiscordParameter(name = "in") Integer in
    ){
        return true;
    }

    @DiscordCommand(name="context")
    public boolean testContext(GuildMessageReceivedEvent event){
        return true;
    }
}