package io.github.sepgh.sbdiscord.controllers;

import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordController
public class TestController {
    @DiscordCommand(name = "test", type = DiscordCommand.Type.BASIC)
    public boolean test(){
        return true;
    }

    @DiscordCommand(name = "parametercommand", type = DiscordCommand.Type.BASIC)
    public boolean parameterCommand(
            @DiscordParameter(name="p1") String parameter1,
            @DiscordParameter(name = "parameter2") String parameter2,
            @DiscordParameter(name="parameter3", required = false, value = "4") String parameter3
    ){
        return true;
    }

    @DiscordCommand(name = "noparam", type = DiscordCommand.Type.BASIC)
    public boolean noparam(){
        return true;
    }


    @DiscordCommand(name = "failingParameterType", type = DiscordCommand.Type.BASIC)
    public boolean failingParameterType(
            @DiscordParameter(name = "parameter1") Exception parameter1
    ){
        return true;
    }

    @DiscordCommand(name = "failing Alpha Numeric", type = DiscordCommand.Type.BASIC)
    public boolean failingAlphaNumeric(
            @DiscordParameter(name = "in") Integer in
    ){
        return true;
    }

    @DiscordCommand(name="context", type = DiscordCommand.Type.BASIC)
    public boolean testContext(GuildMessageReceivedEvent event){
        return true;
    }
}