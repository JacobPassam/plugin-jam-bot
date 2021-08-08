package com.github.jacobpassam.pluginjam.guild;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JamGuilds {

    MAIN(397526357191557121L, 872918158342782976L, 873683236352573490L),
    TEST(873255720601129020L, 873677270122889216L, 873683280149499914L);

    private final long id;
    private final long announceChannelId;
    private final long votingChannelId;

}
