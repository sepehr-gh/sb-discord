package com.github.sepehrgh.sbdiscord;


import com.github.sepehrgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import com.github.sepehrgh.sbdiscord.controllers.TestController;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import com.github.sepehrgh.sbdiscord.command.Command;
import com.github.sepehrgh.sbdiscord.command.CommandRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringbootDiscordAutoConfiguration.class})
public class CommandCallerTest {
    private final TestController mockTestController;
    private final CommandRegistry commandRegistry;

    @Autowired
    public CommandCallerTest(CommandRegistry commandRegistry) {
        this.commandRegistry = Mockito.mock(CommandRegistry.class);
        this.mockTestController = Mockito.mock(TestController.class);

        Command parameterCommand = commandRegistry.findCommandByName("parameterCommand").get();
        parameterCommand.setObject(this.mockTestController);

        Command noParameterCommand = commandRegistry.findCommandByName("noparam").get();
        noParameterCommand.setObject(this.mockTestController);


        Mockito.when(this.commandRegistry.findCommandByName("parameterCommand")).thenReturn(Optional.of(parameterCommand));
        Mockito.when(this.commandRegistry.findCommandByName("noparam")).thenReturn(Optional.of(noParameterCommand));
    }

    @Test
    public void testMethodCall() throws CommandParseException, InvocationTargetException, IllegalAccessException {
        Optional<Command> optionalCommand = this.commandRegistry.findCommandByName("parameterCommand");
        assert optionalCommand.isPresent();
        optionalCommand.get().call("A B");
        Mockito.verify(this.mockTestController).parameterCommand("A", "B", "4");

        optionalCommand.get().call("A B C");
        Mockito.verify(this.mockTestController).parameterCommand("A", "B", "C");

        optionalCommand.get().call("A B \"Hello World\"");
        Mockito.verify(this.mockTestController).parameterCommand("A", "B", "Hello World");

        optionalCommand.get().call("A B parameter3=\"Hello There\"");
        Mockito.verify(this.mockTestController).parameterCommand("A", "B", "Hello There");
    }

    @Test
    public void noParamMethodCall() throws CommandParseException, InvocationTargetException, IllegalAccessException {
        Optional<Command> optionalCommand = this.commandRegistry.findCommandByName("noparam");
        assert optionalCommand.isPresent();
        optionalCommand.get().call(" ");
        Mockito.verify(this.mockTestController).noparam();
    }
}
