package com.github.jacobpassam.pluginjam.jam.command;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class NextCommand implements Command {

    private final PluginJam pluginJam;

    public NextCommand(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public String getName() {
        return "next";
    }

    @Override
    public Permission getPermission() {
        return Permissions.START_STOP_JAM;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        if (!pluginJam.isActive()) {
            new JamEmbed()
                    .withTitle("Plugin Jam is not active.")
                    .withContent("You cannot move to the next stage before the Plugin Jam has been started.")
                    .send(message.getChannel()).queue();

            return;
        }

        pluginJam.next(member.getGuild(), message.getChannel());
    }
}
