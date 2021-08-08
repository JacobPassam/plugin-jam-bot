package com.github.jacobpassam.pluginjam.jam.command;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class RefreshEntriesCommand implements Command {

    private final PluginJam pluginJam;

    public RefreshEntriesCommand(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public String getName() {
        return "refreshentries";
    }

    @Override
    public Permission getPermission() {
        return Permissions.REFRESH_ENTRIES;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        pluginJam.reloadEntries(member.getJDA());

        new JamEmbed()
                .withTitle("Refreshed entries")
                .withContent("You refreshed the entries from the spreadsheet.")
                .send(message.getChannel()).queue();
    }
}
