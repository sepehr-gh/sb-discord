package com.github.sepehrgh.sbdiscord.registry;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CommandRegistry {
    @Getter
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public void register(Command command){
        if (this.commands.putIfAbsent(command.getName(), command) != null)
            throw new IllegalArgumentException(String.format("%s command is already registered", command.getName()));
    }

    public Optional<Command> findCommandByName(String name){
        return Optional.ofNullable(commands.get(name));
    }

}
