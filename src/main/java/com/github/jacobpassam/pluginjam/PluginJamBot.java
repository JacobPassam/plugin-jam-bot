package com.github.jacobpassam.pluginjam;

import com.github.jacobpassam.pluginjam.command.CommandManager;
import com.github.jacobpassam.pluginjam.command.impl.DevModeCommand;
import com.github.jacobpassam.pluginjam.command.impl.PingCommand;
import com.github.jacobpassam.pluginjam.file.JsonConfigurationFile;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.jam.command.*;
import com.github.jacobpassam.pluginjam.jam.vote.VoteListener;
import com.github.jacobpassam.pluginjam.permission.PermissionManager;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import com.github.jacobpassam.pluginjam.permission.UserRole;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class PluginJamBot {

    private static final String DEFAULT_STATUS = "Plugin Jam #2";

    @SneakyThrows
    public static void main(String[] args) {
        JsonConfigurationFile config = new JsonConfigurationFile("config.json");
        config.load();

        if (!config.getData().has("token")) {
            System.out.println("Cannot proceed without token.");
            return;
        }

        JDA jda = JDABuilder.createDefault(config.getData().get("token").getAsString())
                .setActivity(Activity.watching(DEFAULT_STATUS))
                .build();

        PermissionManager permissionManager = new PermissionManager();
        permissionManager.addPermission(UserRole.SPECIALIST, Permissions.PING_COMMAND);
        permissionManager.addPermission(UserRole.SPECIALIST, Permissions.REFRESH_ENTRIES);
        permissionManager.addPermission(UserRole.SPECIALIST, Permissions.MANAGE_JAM_POSITION);
        permissionManager.addPermission(UserRole.CREATOR, Permissions.START_STOP_JAM);

        CommandManager commandManager = new CommandManager(permissionManager);
        jda.addEventListener(commandManager);

        commandManager.register(new PingCommand());
        commandManager.register(new DevModeCommand(permissionManager));

        PluginJam pluginJam = new PluginJam(jda);
        commandManager.register(new StartCommand(pluginJam));
        commandManager.register(new RefreshEntriesCommand(pluginJam));
        commandManager.register(new AnnounceStartCommand(pluginJam));
        commandManager.register(new ProgressCommand(pluginJam));
        commandManager.register(new StopCommand(pluginJam));
        commandManager.register(new NextCommand(pluginJam));
        commandManager.register(new SkipToCommand(pluginJam));

        jda.addEventListener(new VoteListener(pluginJam, permissionManager));
    }

}
