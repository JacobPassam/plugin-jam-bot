package com.github.jacobpassam.pluginjam.command.impl;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.Field;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PingCommand implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Permission getPermission() {
        return Permissions.PING_COMMAND;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        new JamEmbed()
                .withTitle("Ping")
                .withContent("The bot is online and working!")
                .addField(new Field("Gateway Ping", member.getJDA().getGatewayPing() + "ms", false))
                .send(message.getChannel()).queue();
    }
}
