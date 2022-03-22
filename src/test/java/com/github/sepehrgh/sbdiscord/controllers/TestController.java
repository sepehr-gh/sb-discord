package com.github.sepehrgh.sbdiscord.controllers;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.annotations.DiscordController;
import com.github.sepehrgh.sbdiscord.annotations.DiscordParameter;

@DiscordController
public class TestController {
    @DiscordCommand(name = "test")
    public boolean test(){
        return true;
    }

    @DiscordCommand(name = "parameterCommand")
    public boolean parameterCommand(
            @DiscordParameter String parameter1,
            @DiscordParameter(name = "p2") String parameter2,
            @DiscordParameter(required = false, value = "4") String parameter3
    ){
        return true;
    }

    @DiscordCommand(name = "noparam")
    public boolean noparam(){
        return true;
    }


    @DiscordCommand(name = "failingParameterType")
    public boolean failingParameterType(
            @DiscordParameter Exception parameter1
    ){
        return true;
    }

    @DiscordCommand(name = "failing Alpha Numeric")
    public boolean failingAlphaNumeric(
            @DiscordParameter Integer in
    ){
        return true;
    }
}