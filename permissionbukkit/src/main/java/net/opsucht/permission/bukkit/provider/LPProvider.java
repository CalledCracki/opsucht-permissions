package net.opsucht.permission.bukkit.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.opsucht.permission.api.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation des PermissionProvider-Interfaces für LuckPerms.
 */
public final class LPProvider implements PermissionProvider {

    private final LuckPerms api;

    public LPProvider() {
        this.api = LuckPermsProvider.get();
    }

    @Override
    public @NotNull String getProviderName() {
        return "LuckPerms";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) return false; // Spieler noch nicht geladen

        return user.getCachedData()
                .getPermissionData()
                .checkPermission(permission)
                .asBoolean();
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) return;

        Node node = Node.builder(permission).build();
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) return;

        Node node = Node.builder(permission).build();
        user.data().remove(node);
        api.getUserManager().saveUser(user);
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) return Set.of();

        return user.getNodes().stream()
                .filter(node -> node.getType().name().equalsIgnoreCase("inheritance"))
                .map(node -> node.getKey().replace("group.", ""))
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<String> getGroups() {
        return api.getGroupManager()
                .getLoadedGroups()
                .stream()
                .map(Group::getName)
                .collect(Collectors.toSet());
    }
}