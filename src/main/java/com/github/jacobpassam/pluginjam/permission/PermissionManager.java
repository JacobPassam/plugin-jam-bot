package com.github.jacobpassam.pluginjam.permission;

import com.github.jacobpassam.pluginjam.guild.JamGuilds;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionManager {

    @Getter @Setter
    private boolean devMode;

    public static final Set<Long> BOT_DEVELOPERS = ImmutableSet.of(361444999037976578L);

    private final Map<UserRole, Set<Permission>> permissionMap;

    public PermissionManager() {
        this.permissionMap = new HashMap<>();
    }

    public void addPermission(UserRole role, Permission permission) {
        if (permissionMap.containsKey(role)) {
            permissionMap.get(role).add(permission);
        } else {
            permissionMap.put(role, new HashSet<>());
            addPermission(role, permission);
        }
    }

    public boolean hasPermission(UserRole role, Permission permission) {
        if (permissionMap.get(role) == null) return false;

        return permissionMap.get(role).contains(permission);
    }

    public boolean hasPermission(Member member, Permission permission) {
        if (permission == null) return true;
        if (devMode && BOT_DEVELOPERS.contains(member.getIdLong())) return true;

        Set<UserRole> roles = getRolesForMember(member);

        for (UserRole role : roles) {
            if (hasPermission(role, permission)) return true;
        }

        return false;
    }

    public Set<UserRole> getRolesForMember(Member member) {

        Set<UserRole> roles = new HashSet<>();

        for (UserRole value : UserRole.values()) {

            Role role = null;

            if (member.getGuild().getIdLong() == JamGuilds.MAIN.getId()) role = member.getJDA().getRoleById(value.getRoleId());
            else if (member.getGuild().getIdLong() == JamGuilds.TEST.getId()) role = member.getJDA().getRoleById(value.getTestServerRoleId());

            if (role == null) continue;

            if (member.getRoles().contains(role)) {
                roles.add(value);
            }

        }

        return roles;

    }


}
