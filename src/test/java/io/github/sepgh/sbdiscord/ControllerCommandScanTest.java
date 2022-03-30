package io.github.sepgh.sbdiscord;

import io.github.sepgh.sbdiscord.command.CommandRegistry;
import io.github.sepgh.sbdiscord.config.SpringbootDiscordAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringbootDiscordAutoConfiguration.class, SpringConfiguration.class})
class ControllerCommandScanTest {
	private final CommandRegistry commandRegistry;

	public ControllerCommandScanTest(@Autowired CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Test
	public void testCommandRegistration() {
		String commandName = "test";
		Assert.isTrue(commandRegistry.findCommandByName(commandName).isPresent(), String.format(
				"Command %s is not registered", commandName
		));


		// unknown parameter type should fail
		commandName = "failingParameterType";
		Assert.isTrue(!commandRegistry.findCommandByName(commandName).isPresent(), String.format(
				"Command %s is registered", commandName
		));

		// none alphanumeric name should fail
		commandName = "failing Alpha Numeric";
		Assert.isTrue(!commandRegistry.findCommandByName(commandName).isPresent(), String.format(
				"Command %s is registered", commandName
		));
	}

}
