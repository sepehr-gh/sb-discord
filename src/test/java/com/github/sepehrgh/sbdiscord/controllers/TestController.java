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
}