package com.github.jacobpassam.pluginjam.jam.vote;

import lombok.Data;

@Data
public class UserVote {

    private final long userId;
    private final double score;
    private final boolean isSpecialist;

}
