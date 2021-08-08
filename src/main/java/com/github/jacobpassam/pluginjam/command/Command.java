package com.github.jacobpassam.pluginjam.command;

import com.github.jacobpassam.pluginjam.permission.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public interface Command {

    String getName();
    Permission getPermission();

    void execute(Member member, Message message, String[] args);

}
