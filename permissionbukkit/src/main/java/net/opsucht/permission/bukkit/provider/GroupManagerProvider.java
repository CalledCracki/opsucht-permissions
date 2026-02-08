package net.opsucht.permission.bukkit.provider;

import net.opsucht.permission.api.PermissionProvider;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * PermissionProvider implementation for GroupManager (world-based).
 * 
 * <p>
 * Uses AnjoPermissionsHandler for checks and OverloadedWorldHolder for
 * mutations.
 * Online-first strategy; offline fallback uses default world.
 * </p>
 * 
 * @since 1.0.0
 */
public final class GroupManagerProvider implements PermissionProvider {

    private static final String DEFAULT_WORLD = "world";
    private static final Logger LOGGER = Bukkit.getLogger();

    private final GroupManager gm;

    public GroupManagerProvider() {
        Plugin p = Bukkit.getPluginManager().getPlugin("GroupManager");
        if (!(p instanceof GroupManager) || !p.isEnabled()) {
            throw new IllegalStateException("GroupManager not available or disabled");
        }
        this.gm = (GroupManager) p;
    }

    @Override
    public @NotNull String getProviderName() {
        return "GroupManager";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return false;
        AnjoPermissionsHandler h = gm.getWorldsHolder().getWorldPermissions(player);
        return h != null && h.has(player, permission);
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        String name;
        OverloadedWorldHolder holder;
        if (player != null) {
            holder = gm.getWorldsHolder().getWorldData(player);
            name = player.getName();
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            name = op.getName();
            if (name == null)
                return;
            String worldName = getDefaultWorldName();
            holder = gm.getWorldsHolder().getWorldData(worldName);
        }
        holder.getUser(name).addPermission(permission);
        if (player != null)
            GroupManager.BukkitPermissions.updatePermissions(player);
    }

    @Override
    public CompletableFuture<Void> addAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> add(uuid, permission));
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        String name;
        OverloadedWorldHolder holder;
        if (player != null) {
            holder = gm.getWorldsHolder().getWorldData(player);
            name = player.getName();
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            name = op.getName();
            if (name == null)
                return;
            String worldName = Bukkit.getWorlds().isEmpty() ? "world" : Bukkit.getWorlds().get(0).getName();
            holder = gm.getWorldsHolder().getWorldData(worldName);
        }
        holder.getUser(name).removePermission(permission);
        if (player != null)
            GroupManager.BukkitPermissions.updatePermissions(player);
    }

    @Override
    public CompletableFuture<Void> removeAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> remove(uuid, permission));
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        String name;
        AnjoPermissionsHandler h;
        if (player != null) {
            h = gm.getWorldsHolder().getWorldPermissions(player);
            if (h == null)
                return Set.of();
            name = player.getName();
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            name = op.getName();
            if (name == null)
                return Set.of();
            String worldName = Bukkit.getWorlds().isEmpty() ? "world" : Bukkit.getWorlds().get(0).getName();
            h = gm.getWorldsHolder().getWorldData(worldName).getPermissionsHandler();
            if (h == null)
                return Set.of();
        }
        return new LinkedHashSet<>(Arrays.asList(h.getGroups(name)));
    }

    @Override
    public @NotNull Set<String> getGroups() {
        if (Bukkit.getWorlds().isEmpty())
            return Set.of();
        String worldName = Bukkit.getWorlds().get(0).getName();
        org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder data = gm.getWorldsHolder().getWorldData(worldName);
        if (data == null)
            return Set.of();
        java.util.Map<String, ?> groups = data.getGroups();
        if (groups == null || groups.isEmpty())
            return Set.of();
        return new java.util.LinkedHashSet<>(groups.keySet());
    }

    /**
     * Returns the default world name for offline players.
     * 
     * @return the default world name
     */
    private String getDefaultWorldName() {
        return Bukkit.getWorlds().isEmpty() ? DEFAULT_WORLD : Bukkit.getWorlds().get(0).getName();
    }
}