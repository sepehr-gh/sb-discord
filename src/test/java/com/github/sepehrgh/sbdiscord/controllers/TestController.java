package com.github.sepehrgh.sbdiscord.controllers;

import com.github.sepehrgh.sbdiscord.annotations.DiscordCommand;
import com.github.sepehrgh.sbdiscord.annotations.DiscordController;

@DiscordController
public class TestController {
    @DiscordCommand(name = "test")
    public boolean test(){
        return true;
    }
}