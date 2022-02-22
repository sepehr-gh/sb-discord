package com.github.sepehrgh.sbdiscord;

import com.github.sepehrgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import com.github.sepehrgh.sbdiscord.parser.CommandParser;
import com.github.sepehrgh.sbdiscord.registry.Command;
import com.github.sepehrgh.sbdiscord.registry.CommandRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringbootDiscordAutoConfiguration.class})
public class CommandParserTest {
    private final CommandRegistry commandRegistry;

    public CommandParserTest(@Autowired CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Test
    public void testParse() throws CommandParseException {
        Optional<Command> optionalCommand = commandRegistry.findCommandByName("parameterCommand");
        Assert.isTrue(optionalCommand.isPresent(), String.format("%s command is not present", "parameterCommand"));

        Command command = optionalCommand.get();
        CommandParser parser = command.getParser();


        // Test simple assigning
        String[] output = parser.parse("1 2 3");
        Assert.isTrue(output.length == 3, "Size of output is not valid");
        Assert.isTrue(output[0].equals("1"), "Invalid output");
        Assert.isTrue(output[1].equals("2"), "Invalid output");
        Assert.isTrue(output[2].equals("3"), "Invalid output");

        // Test missing none required parameter
        output = parser.parse("1 2");
        Assert.isTrue(output.length == 3, "Size of output is not valid");
        Assert.isTrue(output[0].equals("1"), "Invalid output");
        Assert.isTrue(output[1].equals("2"), "Invalid output");
        Assert.isTrue(output[2].equals("4"), "Invalid output");

        output = parser.parse("1 p2=2");
        Assert.isTrue(output.length == 3, "Size of output is not valid");
        Assert.isTrue(output[0].equals("1"), "Invalid output");
        Assert.isTrue(output[1].equals("2"), "Invalid output");
        Assert.isTrue(output[2].equals("4"), "Invalid output");

        output = parser.parse("1 p2=2 parameter3=5");
        Assert.isTrue(output.length == 3, "Size of output is not valid");
        Assert.isTrue(output[0].equals("1"), "Invalid output");
        Assert.isTrue(output[1].equals("2"), "Invalid output");
        Assert.isTrue(output[2].equals("5"), "Invalid output");

    }

}
