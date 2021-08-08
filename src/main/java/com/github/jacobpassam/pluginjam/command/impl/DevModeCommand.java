package com.github.jacobpassam.pluginjam.command.impl;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class DevModeCommand implements Command {

    private final PermissionManager permissionManager;

    public DevModeCommand(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public String getName() {
        return "devmode";
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {

        if (!PermissionManager.BOT_DEVELOPERS.contains(member.getIdLong())) {
            new JamEmbed()
                    .withTitle("Developer-Only Action")
                    .withContent("That action can only be executed by bot developers.")
                    .send(message.getChannel()).queue();

            return;
        }

        permissionManager.setDevMode(!permissionManager.isDevMode());

        String content = "You enabled developer mode. Bot developers now have access to all features, no matter their role.";
        if (!permissionManager.isDevMode()) content = "You disabled developer mode. Bot developers now retain usual role-based permission checks.";

        new JamEmbed()
                .withTitle("Developer Mode")
                .withContent(content)
                .withoutThumbnail()
                .send(message.getChannel()).queue();

        return;
    }
}
