package net.opsucht.permission.bungee.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.opsucht.permission.api.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * Implementation des PermissionProvider-Interfaces für LuckPerms (BungeeCord).
 */
public final class LPProvider implements PermissionProvider {

    private final LuckPerms api;

    public LPProvider(LuckPerms api) {
        this.api = api;
    }

    @Override
    public @NotNull String getProviderName() {
        return "LuckPerms";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) return false;
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
        return api.getGroupManager().getLoadedGroups().stream()
                .map(g -> g.getName())
                .collect(Collectors.toSet());
    }
}