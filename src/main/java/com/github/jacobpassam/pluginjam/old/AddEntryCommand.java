//package com.github.jacobpassam.pluginjam.old;
//
//import com.github.jacobpassam.pluginjam.command.Command;
//import com.github.jacobpassam.pluginjam.embed.Field;
//import com.github.jacobpassam.pluginjam.embed.JamEmbed;
//import com.github.jacobpassam.pluginjam.jam.PluginJam;
//import com.github.jacobpassam.pluginjam.permission.Permission;
//import com.github.jacobpassam.pluginjam.permission.Permissions;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.User;
//
//import java.util.List;
//
//public class AddEntryCommand implements Command {
//
//    private final PluginJam pluginJam;
//
//    public AddEntryCommand(PluginJam pluginJam) {
//        this.pluginJam = pluginJam;
//    }
//
//    @Override
//    public String getName() {
//        return "addentry";
//    }
//
//    @Override
//    public Permission getPermission() {
//        return Permissions.ADD_JAM_ENTRY;
//    }
//
//    @Override
//    public void execute(Member member, Message message, String[] args) {
//
//        if (args.length < 2) {
//            new JamEmbed()
//                    .withTitle("Incorrect arguments")
//                    .withContent("Your usage is: `-addentry <mention> <title>`")
//                    .addField(new Field("Tip", "Mention a user that isn't in the current chat using `<@id>.`", false))
//                    .send(message.getChannel()).queue();
//
//            return;
//        }
//
//        List<User> mentionedUsers = message.getMentionedUsers();
//
//        if (mentionedUsers.size() == 0) {
//            new JamEmbed()
//                    .withTitle("Incorrect usage")
//                    .withContent("You didn't mention the author.")
//                    .send(message.getChannel()).queue();
//
//            return;
//        }
//
//        User author = mentionedUsers.get(0);
//
//        long id = author.getIdLong();
//
//        StringBuilder description = new StringBuilder();
//        for (int i = 1; i < args.length; i++) {
//            description.append(args[i]).append(" ");
//        }
//
//        description.deleteCharAt(description.length() - 1);
//
//        int pos = pluginJam.addEntry(id, description.toString());
//
//        new JamEmbed()
//                .withTitle("Success for Entry #" + (pos + 1))
//                .withContent("You added the entry.")
//                .addField(new Field("Author", "<@" + id + ">", false))
//                .addField(new Field("Title", description.toString(), false))
//                .send(message.getChannel()).queue();
//    }
//}
