package com.github.jacobpassam.pluginjam.jam;

import com.github.jacobpassam.pluginjam.jam.vote.VoteCategory;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class Stage {

    private Section section;
    private JamEntry entry;

    private VoteCategory voteCategory;

    @RequiredArgsConstructor
    public enum Section {
        REVIEW("Review"),
        VOTING("Voting");

        @Getter
        private final String name;
    }

}
