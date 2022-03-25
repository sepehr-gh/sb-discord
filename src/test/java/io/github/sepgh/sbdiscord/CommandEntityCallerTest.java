package io.github.sepgh.sbdiscord;


import io.github.sepgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import io.github.sepgh.sbdiscord.controllers.TestController;
import io.github.sepgh.sbdiscord.exceptions.CommandParseException;
import io.github.sepgh.sbdiscord.command.CommandEntity;
import io.github.sepgh.sbdiscord.command.CommandRegistry;
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
public class CommandEntityCallerTest {
    private final TestController mockTestController;
    private final CommandRegistry commandRegistry;

    @Autowired
    public CommandEntityCallerTest(CommandRegistry commandRegistry) {
        this.commandRegistry = Mockito.mock(CommandRegistry.class);
        this.mockTestController = Mockito.mock(TestController.class);

        CommandEntity parameterCommandEntity = commandRegistry.findCommandByName("parametercommand").get();
        parameterCommandEntity.setObject(this.mockTestController);

        CommandEntity noParameterCommandEntity = commandRegistry.findCommandByName("noparam").get();
        noParameterCommandEntity.setObject(this.mockTestController);


        Mockito.when(this.commandRegistry.findCommandByName("parametercommand")).thenReturn(Optional.of(parameterCommandEntity));
        Mockito.when(this.commandRegistry.findCommandByName("noparam")).thenReturn(Optional.of(noParameterCommandEntity));
    }

    @Test
    public void testMethodCall() throws CommandParseException, InvocationTargetException, IllegalAccessException {
        Optional<CommandEntity> optionalCommand = this.commandRegistry.findCommandByName("parametercommand");
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
        Optional<CommandEntity> optionalCommand = this.commandRegistry.findCommandByName("noparam");
        assert optionalCommand.isPresent();
        optionalCommand.get().call(" ");
        Mockito.verify(this.mockTestController).noparam();
    }
}
