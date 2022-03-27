package io.github.sepgh.sbdiscord.config.properties;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@ConfigurationProperties(prefix = "spring.discord")
@Getter
@Setter
public class SBDiscordProperties {
    private Character basicCommandSignature = '!';
    private boolean enabled;
    private String token;
    private String commandPrivilegesFile;
    private Long guildId;
    private List<CommandPrivileges> commandPrivileges = new ArrayList<>();

    public boolean isPublicCommand(String name){
        Optional<CommandPrivileges> commandPrivileges = this.commandPrivileges.stream().filter(cp -> cp.commandName.equals(name)).findFirst();
        return commandPrivileges.map(CommandPrivileges::isPublic).orElse(true);
    }

    public List<CommandPrivilege> getPrivilegesOfCommand(String name){
        List<CommandPrivilege> privileges = new ArrayList<>();
        if (this.commandPrivileges == null){
            return privileges;
        }

        this.commandPrivileges.stream().filter(cp -> cp.commandName.equals(name)).forEach(cp -> {
            cp.getPrivileges().forEach(privilege -> {
                privileges.add(new CommandPrivilege(privilege.getType(), privilege.isEnabled(), privilege.getId()));
            });
        });

        return privileges;
    }

    @Getter
    @Setter
    public static class CommandPrivileges {
        private String commandName;
        private List<Privilege> privileges = new ArrayList<>();
        private boolean isPublic;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommandPrivileges that = (CommandPrivileges) o;
            return Objects.equals(commandName, that.commandName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(commandName);
        }
    }

    @Getter
    @Setter
    public static class Privilege {
        private CommandPrivilege.Type type;
        private long id;
        private boolean enabled = true;
    }
}
