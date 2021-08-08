package com.github.jacobpassam.pluginjam.jam.command;

import com.github.jacobpassam.pluginjam.command.Command;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.guild.JamGuilds;
import com.github.jacobpassam.pluginjam.jam.PluginJam;
import com.github.jacobpassam.pluginjam.permission.Permission;
import com.github.jacobpassam.pluginjam.permission.Permissions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class AnnounceStartCommand implements Command {

    private final PluginJam pluginJam;

    public AnnounceStartCommand(PluginJam pluginJam) {
        this.pluginJam = pluginJam;
    }

    @Override
    public String getName() {
        return "announcestart";
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
                    .withContent("You cannot announce the Plugin Jam when it has not yet started.")
                    .send(message.getChannel()).queue();

            return;
        }

        MessageEmbed embed = new JamEmbed()
                .withTitle("The Plugin Jam is starting!")
                .withContent("Come and watch our second Plugin Jam at https://twitch.tv/genericstephen")
                .withoutThumbnail()
                .withBigLogo()
                .build();

        if (member.getGuild().getIdLong() == JamGuilds.MAIN.getId()) {
            TextChannel channel = member.getJDA().getTextChannelById(JamGuilds.MAIN.getAnnounceChannelId());
            assert channel != null;

            channel.sendMessage("@here").queue();
            channel.sendMessageEmbeds(embed).queue();
        } else if (member.getGuild().getIdLong() == JamGuilds.TEST.getId()) {
            TextChannel channel = member.getJDA().getTextChannelById(JamGuilds.TEST.getAnnounceChannelId());
            assert channel != null;

            channel.sendMessage("@here").queue();
            channel.sendMessageEmbeds(embed).queue();
        } else {
            new JamEmbed()
                    .withTitle("Incorrect server")
                    .withContent("It seems you executed this in a server we don't know about.")
                    .send(message.getChannel()).queue();
        }
    }
}
