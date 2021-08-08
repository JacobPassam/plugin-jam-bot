package com.github.jacobpassam.pluginjam.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {

    CREATOR(397531629267976204L, 873582392823136367L),
    SPECIALIST(749450748244394094L, 873582497848504340L),
    STUDENT(465961874777833473L, 873699442199502909L);

    private final long roleId;
    private final long testServerRoleId;

}
