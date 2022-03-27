package io.github.sepgh.sbdiscord.command;

import io.github.sepgh.sbdiscord.config.properties.SBDiscordProperties;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PermissionValidator {
    private final SBDiscordProperties sbDiscordProperties;

    @Autowired
    public PermissionValidator(SBDiscordProperties sbDiscordProperties) {
        this.sbDiscordProperties = sbDiscordProperties;
    }

    @Cacheable(cacheManager = "sbDiscordCacheManager", cacheNames = "PERMISSION_VALIDATOR", key = "#member.idLong + #command")
    public boolean hasPermission(Member member, String command){
        log.trace(String.format("Checking permission for member with id %s for command %s", member.getId(), command));
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
