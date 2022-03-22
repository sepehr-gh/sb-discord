package com.github.sepehrgh.sbdiscord;

import com.github.sepehrgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import com.github.sepehrgh.sbdiscord.parser.CommandParser;
import com.github.sepehrgh.sbdiscord.command.Command;
import com.github.sepehrgh.sbdiscord.command.CommandRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringbootDiscordAutoConfiguration.class})
public class CommandParserTest {
    private final CommandParser parser;

    public CommandParserTest(@Autowired CommandRegistry commandRegistry) {
        Optional<Command> optionalCommand = commandRegistry.findCommandByName("parameterCommand");
        Assertions.assertTrue(optionalCommand.isPresent(), String.format("%s command is not present", "parameterCommand"));

        Command command = optionalCommand.get();
        this.parser = command.getParser();
    }

    @Test
    public void testParse() throws CommandParseException {
        // Test simple assigning
        String[] output = parser.parse("1 2 3");
        Assertions.assertEquals(3, output.length);
        Assertions.assertEquals("1", output[0]);
        Assertions.assertEquals("2", output[1]);
        Assertions.assertEquals("3", output[2]);

        // Test missing none required parameter
        output = parser.parse("1 2");
        Assertions.assertEquals(3, output.length, "Size of output is not valid");
        Assertions.assertEquals("1", output[0]);
        Assertions.assertEquals("2", output[1]);
        Assertions.assertEquals("4", output[2]);

        output = parser.parse("1 p2=2");
        Assertions.assertEquals(3, output.length, "Size of output is not valid");
        Assertions.assertEquals("1", output[0]);
        Assertions.assertEquals("2", output[1]);
        Assertions.assertEquals("4", output[2]);

        output = parser.parse("1 p2=2 parameter3=5");
        Assertions.assertEquals(3, output.length, "Size of output is not valid");
        Assertions.assertEquals("1", output[0]);
        Assertions.assertEquals("2", output[1]);
        Assertions.assertEquals("5", output[2]);

    }

    @Test
    public void testParseErrors() throws CommandParseException {
        Assertions.assertThrows(CommandParseException.class, () -> {
            parser.parse("1 p2=2 unknown=5");
        });

        String[] output = parser.parse("1 p2 unknown=5");
        Assertions.assertEquals("unknown=5", output[2]);

        Assertions.assertThrows(CommandParseException.class, () -> {
            parser.parse("1");
        });

    }

    @Test
    public void testFillTexts() throws CommandParseException {
        String[] output = parser.parse("1 p2=\"hello sir\"");
        Assertions.assertEquals("hello sir", output[1]);
    }
}
