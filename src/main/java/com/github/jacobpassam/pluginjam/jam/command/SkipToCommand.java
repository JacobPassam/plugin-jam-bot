package com.github.jacobpassam.pluginjam.jam.command;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class SkipToCommand implements Command {

    private PluginJam pluginJam;

    public SkipToCommand(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public String getName() {
        return "skipto";
    }

    @Override
    public Permission getPermission() {
        return Permissions.MANAGE_JAM_POSITION;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        if (!pluginJam.isActive()) {
            new JamEmbed()
                    .withTitle("Plugin Jam is not active.")
                    .withContent("You cannot skip before the Plugin Jam has been started.")
                    .send(message.getChannel()).queue();

            return;
        }

        if (args.length == 0) {
            new JamEmbed()
                    .withTitle("Incorrect arguments")
                    .withContent("Your usage is: `-skipto <number>`")
                    .send(message.getChannel()).queue();

            return;
        }

        try {
            int i = Integer.parseInt(args[0]);

            pluginJam.skipTo(i, member.getGuild(), message.getChannel());
        } catch (NumberFormatException exception) {
            new JamEmbed()
                    .withTitle("Incorrect usage")
                    .withContent("You must supply an entry number to skip to.")
                    .send(message.getChannel()).queue();
        }


    }
}
