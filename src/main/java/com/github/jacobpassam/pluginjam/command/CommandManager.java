package com.github.jacobpassam.pluginjam.command;

import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.permission.PermissionManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandManager extends ListenerAdapter {

    private static final String PREFIX = "-";

    private final Set<Command> commands;
    private final PermissionManager permissionManager;

    public CommandManager(PermissionManager permissionManager) {
        this.commands = new HashSet<>();

        this.permissionManager = permissionManager;
    }

    public void register(Command command) {
        commands.add(command);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        String content = event.getMessage().getContentRaw();
        if (content.startsWith(PREFIX)) {
            String[] args = content.split(" ");

            // Strip prefix
            if (args[0].length() == 1) return;
            args[0] = args[0].substring(1);

            String commandName = args[0];

            Command command = null;
            for (Command loop : commands) {
                if (loop.getName().equals(commandName)) {
                    command = loop;
                }
            }

            if (command == null) return;

            if (!permissionManager.hasPermission(event.getMember(), command.getPermission())) {
                new JamEmbed()
                        .withTitle("No Permission")
                        .withContent("You do not have permission to execute that command.")
                        .send(event.getMessage().getChannel()).queue();
                return;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
            command.execute(event.getMember(), event.getMessage(), args);
        }

    }
}
