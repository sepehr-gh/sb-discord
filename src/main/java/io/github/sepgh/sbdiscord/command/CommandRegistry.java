package io.github.sepgh.sbdiscord.command;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CommandRegistry {
    @Getter
    private final Map<String, CommandEntity> commands = new ConcurrentHashMap<>();

    public void register(CommandEntity commandEntity){
        if (this.commands.putIfAbsent(commandEntity.getName(), commandEntity) != null)
            throw new IllegalArgumentException(String.format("%s command is already registered", commandEntity.getName()));
    }

    public Optional<CommandEntity> findCommandByName(String name){
        return Optional.ofNullable(commands.get(name));
    }

}
