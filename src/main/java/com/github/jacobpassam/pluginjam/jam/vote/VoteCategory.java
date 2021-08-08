package com.github.jacobpassam.pluginjam.jam.vote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VoteCategory {

    CREATIVITY("Creativity"),
    FUNCTIONALITY("Functionality"),
    AESTHETICS("Aesthetics");

    @Getter
    private final String name;

}
