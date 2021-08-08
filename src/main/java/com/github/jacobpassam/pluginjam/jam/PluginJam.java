package com.github.jacobpassam.pluginjam.jam;

import com.github.jacobpassam.pluginjam.embed.Field;
import com.github.jacobpassam.pluginjam.embed.JamEmbed;
import com.github.jacobpassam.pluginjam.guild.JamGuilds;
import com.github.jacobpassam.pluginjam.jam.vote.UserVote;
import com.github.jacobpassam.pluginjam.jam.vote.VoteCategory;
import com.github.jacobpassam.pluginjam.jam.vote.VoteTotals;
import com.github.jacobpassam.pluginjam.permission.UserRole;
import com.github.jacobpassam.pluginjam.sheet.SpreadsheetManager;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

public class PluginJam {

    private static final VoteCategory[] CATEGORY_ORDER = {VoteCategory.CREATIVITY, VoteCategory.FUNCTIONALITY, VoteCategory.AESTHETICS};

    @Getter
    private boolean active;

    private final SpreadsheetManager spreadsheetManager;

    private List<JamEntry> entries;

    private final Stage stage;

    private Message ourVotingChannelMessage;

    @Getter
    private long awaitingReactionMessageId;

    private Map<VoteCategory, VoteTotals> voteTotals;

    private final Map<VoteCategory, Set<UserVote>> currentEntryVotes;

    public PluginJam() {
        this.spreadsheetManager = new SpreadsheetManager();
        spreadsheetManager.load();

        this.entries = spreadsheetManager.getEntries();

        this.stage = new Stage();
        this.currentEntryVotes = new HashMap<>();
    }

    public void start(Guild jdaGuild, MessageChannel initiationChannel) {
        if (entries.size() == 0) {
            new JamEmbed()
                    .withTitle("Cannot start the Plugin Jam")
                    .withContent("We cannot begin the Plugin Jam as no entries could be found.")
                    .send(initiationChannel).queue();

            return;
        }

        this.active = true;

        StringBuilder sb = new StringBuilder();

        for (JamEntry entry : entries) {
            sb.append(entries.indexOf(entry) + 1).append(". <@").append(entry.getAuthorId()).append("> - ").append(entry.getTitle()).append("\n");
        }

        stage.setEntry(entries.get(0));
        stage.setSection(Stage.Section.REVIEW);

        new JamEmbed()
                .withTitle("Started the Plugin Jam!")
                .withContent("We've started the Plugin Jam.")
                .addField(new Field("Entries", sb.toString(), false))
                .addField(new Field("Progress", generateStageString(), false))
                .addField(new Field("How to?", "Execute `-next` to move to the next stage (review -> vote categories, or to next entry).", false))
                .addField(new Field("Make a mistake?", "Execute `-skipto <number>` and you can skip to a different entry.", false))
                .addField(new Field("Announce", "Want to announce that we've begun? Execute `-announcestart` and we'll send an embed to your announcement channel.", false))
                .send(initiationChannel).queue();

        JamGuilds guild = JamGuilds.MAIN;
        if (jdaGuild.getIdLong() == JamGuilds.TEST.getId()) guild = JamGuilds.TEST;

        startReview(guild, initiationChannel.getJDA());
    }

    public String generateStageString() {
        if (!active) return "The Plugin Jam has not yet been started.";

        int currentEntryNumber = getCurrentEntryNumber();
        if (currentEntryNumber == -1) return "No entry is currently being evaluated.";

        JamEntry entry = stage.getEntry();

        return "We are in the `" + stage.getSection().getName() + "` stage of entry **#" + (currentEntryNumber + 1) + "** submitted by <@" + entry.getAuthorId() + "> : " + entry.getTitle() + ".";
    }

    public void recordVote(UserVote userVote) {
        if (!active) return;
        if (stage.getVoteCategory() == null) return;

        currentEntryVotes.computeIfAbsent(stage.getVoteCategory(), k -> new HashSet<>());

        // Disallow multiple votes
        for (UserVote vote : currentEntryVotes.get(stage.getVoteCategory())) {
            if (vote.getUserId() == userVote.getUserId()) return;
        }

        currentEntryVotes.get(stage.getVoteCategory()).add(userVote);
    }

    public void startReview(JamGuilds guild, JDA jda) {
        if (this.ourVotingChannelMessage != null) {
            ourVotingChannelMessage.delete().queue();
            ourVotingChannelMessage = null;
        }

        TextChannel votingChannel = jda.getTextChannelById(guild.getVotingChannelId());
        assert votingChannel != null;

        this.ourVotingChannelMessage = new JamEmbed()
                .withTitle("Reviewal")
                .withContent("We are currently reviewing entry **#" + (getCurrentEntryNumber() + 1) + "** on stream. Check back later for voting!")
                .addField(new Field("Plugin Author", "<@" + stage.getEntry().getAuthorId() + ">", false))
                .addField(new Field("Title", stage.getEntry().getTitle(), false))
                .send(votingChannel).complete();

        long id = guild == JamGuilds.MAIN ? UserRole.STUDENT.getRoleId() : UserRole.STUDENT.getTestServerRoleId();
        ;

        votingChannel.getManager().putPermissionOverride(
                jda.getRoleById(id),
                Sets.newHashSet(),
                Sets.newHashSet(Permission.MESSAGE_WRITE)
        ).queue();
    }

    public void next(Guild jdaGuild, MessageChannel channel) {

        JamGuilds guild = JamGuilds.MAIN;
        if (jdaGuild.getIdLong() == JamGuilds.TEST.getId()) guild = JamGuilds.TEST;

        if (stage.getSection() == Stage.Section.REVIEW) {
            stage.setSection(Stage.Section.VOTING);
            stage.setVoteCategory(VoteCategory.CREATIVITY);

            new JamEmbed()
                    .withTitle("Voting Started")
                    .withContent("You have moved to the `Voting` stage of entry **#" + (getCurrentEntryNumber() + 1) + "**. Voting will now commence in the correct channel.")
                    .addField(new Field("Vote Category", stage.getVoteCategory().getName(), false))
                    .send(channel).queue();

            startVoting(guild, channel.getJDA());
        } else {
            if (getCurrentEntryNumber() + 1 == entries.size() && stage.getVoteCategory() == VoteCategory.AESTHETICS) {
                end(channel);
            } else {

                if (stage.getVoteCategory() == VoteCategory.AESTHETICS) {
                    moveToEntry(channel, guild, entries.get(getCurrentEntryNumber() + 1), true);

                    return;
                }

                if (stage.getVoteCategory() == VoteCategory.CREATIVITY) {
                    stage.setVoteCategory(VoteCategory.FUNCTIONALITY);

                    new JamEmbed()
                            .withTitle("Voting Moved")
                            .withContent("You have moved on to the next Vote Category: `" + stage.getVoteCategory().getName() + "`.")
                            .send(channel).queue();

                    startVoting(guild, channel.getJDA());
                } else if (stage.getVoteCategory() == VoteCategory.FUNCTIONALITY) {
                    stage.setVoteCategory(VoteCategory.AESTHETICS);

                    new JamEmbed()
                            .withTitle("Voting Moved")
                            .withContent("You have moved on to the next Vote Category: `" + stage.getVoteCategory().getName() + "`.")
                            .send(channel).queue();

                    startVoting(guild, channel.getJDA());
                }


            }

        }

    }

    public void moveToEntry(MessageChannel channel, JamGuilds guild, JamEntry entry, boolean gracefullyEndVoting) {
        new JamEmbed()
                .withTitle("Next entry!")
                .withContent("You have moved to the `Review` stage of entry **#" + (entries.indexOf(entry) + 1) + "**. Voting for the last entry has ended.")
                .send(channel).queue();

        if (gracefullyEndVoting) endVoting(channel);
        currentEntryVotes.clear();

        stage.setSection(Stage.Section.REVIEW);
        stage.setEntry(entry);

        startReview(guild, channel.getJDA());
    }

    public void startVoting(JamGuilds guild, JDA jda) {
        if (ourVotingChannelMessage != null) {
            ourVotingChannelMessage.delete().queue();
            ourVotingChannelMessage = null;
        }

        TextChannel votingChannel = jda.getTextChannelById(guild.getVotingChannelId());
        assert votingChannel != null;

        MessageEmbed embed = new JamEmbed()
                .withTitle("Voting | " + stage.getVoteCategory().getName())
                .withContent("Please enter your votes for this entry in the channel below. The bot will register your votes and delete your message.\n\nYou should state **one** number, either whole or precise to **1 decimal place**, between 0 and 5 inclusive. You may not vote twice.\n\n**Any votes in the invalid format will be deleted, but not counted.**")
                .addField(new Field("Vote Category", stage.getVoteCategory().getName(), true))
                .addField(new Field("Plugin Author", "<@" + stage.getEntry().getAuthorId() + ">", false))
                .addField(new Field("Entry Number", "#" + (getCurrentEntryNumber() + 1), true))
                .addField(new Field("Title", stage.getEntry().getTitle(), false))
                .build();

        this.ourVotingChannelMessage = votingChannel.sendMessageEmbeds(embed).complete();

        long id = guild == JamGuilds.MAIN ? UserRole.STUDENT.getRoleId() : UserRole.STUDENT.getTestServerRoleId();

        votingChannel.getManager().removePermissionOverride(jda.getRoleById(id)).queue();
    }

    public void endVoting(MessageChannel output) {
        Map<VoteCategory, VoteTotals> totals = new HashMap<>();

        for (VoteCategory value : VoteCategory.values()) {
            Set<UserVote> userVotes = currentEntryVotes.get(value);

            if (userVotes == null) continue;

            int specialists = 0;
            double specialistTotal = 0;

            double total = 0;

            for (UserVote userVote : userVotes) {
                total += userVote.getScore();

                if (userVote.isSpecialist()) {
                    specialists++;
                    specialistTotal += userVote.getScore();
                }
            }

            double average = total / userVotes.size();
            double specialistAverage = specialistTotal / specialists;

            totals.put(value, new VoteTotals(specialistAverage, average));
        }

        for (VoteCategory voteCategory : CATEGORY_ORDER) {
            totals.putIfAbsent(voteCategory, new VoteTotals(0, 0));
        }

        this.voteTotals = totals;

        JamEmbed jamEmbed = new JamEmbed()
                .withTitle("Vote Results | #" + (getCurrentEntryNumber() + 1))
                .withContent("The vote results given are mean averages.\n\nReact with :white_check_mark: below to approve the addition of these votes to the spreadsheet.");

        for (VoteCategory voteCategory : CATEGORY_ORDER) {
            jamEmbed.addField(new Field(voteCategory.getName() + " Score / 5 - Overall", String.valueOf(totals.get(voteCategory).getTotalAverage()), false));
            jamEmbed.addField(new Field(voteCategory.getName() + " Score / 5 - Specialist", String.valueOf(totals.get(voteCategory).getSpecialistAverage()), true));
        }

        Message message = jamEmbed.send(output).complete();
        message.addReaction("✅").queue();

        this.awaitingReactionMessageId = message.getIdLong();
    }

    public void end(MessageChannel channel) {
        if (this.ourVotingChannelMessage != null) {
            ourVotingChannelMessage.delete().queue();
            ourVotingChannelMessage = null;
        }

        new JamEmbed()
                .withTitle("Plugin Jam Ended")
                .withContent("All entries have been voted on and the Plugin Jam has ended.")
                .send(channel).queue();

        endVoting(channel);

        this.active = false;

        stage.setSection(null);
        stage.setVoteCategory(null);

        this.currentEntryVotes.clear();
    }

    public int getCurrentEntryNumber() {
        if (stage.getEntry() == null) return -1;

        return entries.indexOf(stage.getEntry());
    }

    public void stop(MessageChannel channel) {
        if (this.ourVotingChannelMessage != null) {
            ourVotingChannelMessage.delete().queue();
            ourVotingChannelMessage = null;
        }

        this.active = false;

        stage.setSection(null);
        stage.setEntry(null);
        stage.setVoteCategory(null);

        new JamEmbed()
                .withTitle("Stopped the Plugin Jam")
                .withContent("You forcefully stopped the Plugin Jam.")
                .send(channel).queue();
    }

    public void reloadEntries() {
        this.entries = spreadsheetManager.getEntries();
    }

    public void addVotesToSpreadsheet(MessageChannel messageChannel) {
        // i know it's bodgy don't shout at me :)
        this.awaitingReactionMessageId = -1;

        int ourIndex = getCurrentEntryNumber() - (active ? 1 : 0);

        spreadsheetManager.addVotes(ourIndex, voteTotals);

        new JamEmbed()
                .withTitle("Added votes")
                .withContent("Votes for entry **#" + (ourIndex + 1) + "** have been added to the spreadsheet.")
                .send(messageChannel).queue();
    }

    public void skipTo(int i, Guild jdaGuild, MessageChannel channel) {
        if (i > entries.size() || i < 1) {
            new JamEmbed()
                    .withTitle("Incorrect usage")
                    .withContent("That is not a valid entry number. Please double-check the spreadsheet or execute `-refreshentries`.")
                    .send(channel).queue();

            return;
        }

        if (this.ourVotingChannelMessage != null) {
            ourVotingChannelMessage.delete().queue();
            ourVotingChannelMessage = null;
        }

        JamGuilds guild = JamGuilds.MAIN;
        if (jdaGuild.getIdLong() == JamGuilds.TEST.getId()) guild = JamGuilds.TEST;

        this.awaitingReactionMessageId = -1;

        moveToEntry(channel, guild, entries.get(i-1), false);
    }
}
