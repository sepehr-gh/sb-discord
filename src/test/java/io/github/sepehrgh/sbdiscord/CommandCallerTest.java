package io.github.sepehrgh.sbdiscord;


import io.github.sepehrgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import io.github.sepehrgh.sbdiscord.controllers.TestController;
import io.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import io.github.sepehrgh.sbdiscord.command.Command;
import io.github.sepehrgh.sbdiscord.command.CommandRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringbootDiscordAutoConfiguration.class, SpringConfiguration.class})
public class CommandCallerTest {
    private final TestController mockTestController;
    private final CommandRegistry commandRegistry;

    @Autowired
    public CommandCallerTest(CommandRegistry commandRegistry) {
        this.commandRegistry = Mockito.mock(CommandRegistry.class);
        this.mockTestController = Mockito.mock(TestController.class);

        Command parameterCommand = commandRegistry.findCommandByName("parametercommand").get();
        parameterCommand.setObject(this.mockTestController);

        Command noParameterCommand = commandRegistry.findCommandByName("noparam").get();
        noParameterCommand.setObject(this.mockTestController);


        Mockito.when(this.commandRegistry.findCommandByName("parametercommand")).thenReturn(Optional.of(parameterCommand));
        Mockito.when(this.commandRegistry.findCommandByName("noparam")).thenReturn(Optional.of(noParameterCommand));
    }

    @Test
    public void testMethodCall() throws CommandParseException, InvocationTargetException, IllegalAccessException {
        Optional<Command> optionalCommand = this.commandRegistry.findCommandByName("parametercommand");
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
