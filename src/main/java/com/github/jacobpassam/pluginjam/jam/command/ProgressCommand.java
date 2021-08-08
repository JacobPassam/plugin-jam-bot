package com.github.jacobpassam.pluginjam.jam.command;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.permission.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class ProgressCommand implements Command {

    private final PluginJam pluginJam;

    public ProgressCommand(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public String getName() {
        return "progress";
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {

        new JamEmbed()
                .withTitle("Plugin Jam Progess")
                .withContent(pluginJam.generateStageString())
                .send(message.getChannel()).queue();

    }
}
