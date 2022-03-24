package io.github.sepehrgh.sbdiscord.command.context;

import lombok.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DefaultContextImpl implements CommandContext {
    @Builder.Default
    private Optional<GuildMessageReceivedEvent> guildMessageReceivedEvent = Optional.empty();
    @Builder.Default
    private Optional<SlashCommandEvent> slashCommandEvent = Optional.empty();


    @Override
    public boolean isSlashCommand() {
        return this.getSlashCommandEvent().isPresent();
    }
}
