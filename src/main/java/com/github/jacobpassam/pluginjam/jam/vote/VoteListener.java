package com.github.jacobpassam.pluginjam.jam.vote;

import com.github.jacobpassam.pluginjam.guild.JamGuilds;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.jam.vote.UserVote;
import com.github.jacobpassam.pluginjam.permission.UserRole;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoteListener extends ListenerAdapter {

    private final PluginJam pluginJam;

    public VoteListener(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        JamGuilds guild = JamGuilds.MAIN;
        if (event.getGuild().getIdLong() == JamGuilds.TEST.getId()) guild = JamGuilds.TEST;

        if (!(event.getMessage().getChannel().getIdLong() == guild.getVotingChannelId())) {
            return;
        }

        if (event.getMessage().getAuthor().isBot() || event.getMessage().isWebhookMessage()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        event.getMessage().delete().queue();

        try {
            double d = Double.parseDouble(message);

            if (d < 0 || d > 5) {
                return;
            }

            d = Math.round(d * 10.0) / 10.0;

            Role specialist = event.getJDA().getRoleById(UserRole.SPECIALIST.getRoleId());
            if (guild == JamGuilds.TEST) specialist = event.getJDA().getRoleById(UserRole.SPECIALIST.getTestServerRoleId());

            pluginJam.recordVote(new UserVote(event.getMember().getIdLong(), d, event.getMember().getRoles().contains(specialist)));
        } catch (NumberFormatException ignored) { }

    }
}
