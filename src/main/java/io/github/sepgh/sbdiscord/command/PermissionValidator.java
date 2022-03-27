package io.github.sepgh.sbdiscord.command;
import io.github.sepgh.sbdiscord.config.properties.SBDiscordProperties;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PermissionValidator {
    private final SBDiscordProperties sbDiscordProperties;

    public PermissionValidator(SBDiscordProperties sbDiscordProperties) {
        this.sbDiscordProperties = sbDiscordProperties;
    }

    public boolean hasPermission(Member member, String command){
        if (sbDiscordProperties.isPublicCommand(command)) {
            return true;
        }

        Long userId = member.getUser().getIdLong();
        List<Long> roleIds = new ArrayList<>();
        member.getRoles().forEach(role -> {
            roleIds.add(role.getIdLong());
        });

        return this.hasPermission(userId, roleIds, command);
    }

    private boolean hasPermission(Long userId, List<Long> roleIds, String command){
        for (CommandPrivilege commandPrivilege : sbDiscordProperties.getPrivilegesOfCommand(command)) {
            if (commandPrivilege.getType().equals(CommandPrivilege.Type.ROLE) && roleIds.contains(commandPrivilege.getIdLong())) {
                return true;
            }

            if (commandPrivilege.getType().equals(CommandPrivilege.Type.USER) && userId.equals(commandPrivilege.getIdLong())) {
                return true;
            }
        }
        return false;
    }

}
