package net.opsucht.permission.bukkit.provider;

import net.opsucht.permission.api.PermissionProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Fallback provider using Bukkit's native permission system.
 * 
 * <p>
 * This provider is used when no other permission plugin is detected.
 * It provides basic permission checking through Bukkit's built-in system
 * but has limited functionality (no group support, no persistence).
 * </p>
 * 
 * <p>
 * <b>Limitations:</b>
 * </p>
 * <ul>
 * <li>Only works for online players</li>
 * <li>No group support (getGroups always returns empty)</li>
 * <li>add/remove operations are runtime-only (not persisted)</li>
 * </ul>
 * 
 * @since 1.0.0
 */
public final class BukkitNativeProvider implements PermissionProvider {

    private static final Logger LOGGER = Bukkit.getLogger();

    @Override
    public @NotNull String getProviderName() {
        return "Native Bukkit Permissions";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            LOGGER.fine("Player not online for permission check: " + uuid);
            return false;
        }
        return player.hasPermission(permission);
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            LOGGER.warning("Cannot add permission - player not online: " + uuid);
            return;
        }

        // Note: This is runtime-only and not persisted
        player.addAttachment(
                Bukkit.getPluginManager().getPlugin("opsucht-permission")).setPermission(permission, true);

        LOGGER.info("Added runtime permission '" + permission + "' to player " + player.getName());
    }

    @Override
    public CompletableFuture<Void> addAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> add(uuid, permission));
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            LOGGER.warning("Cannot remove permission - player not online: " + uuid);
            return;
        }

        // Note: This only works for runtime permissions, not those from permissions.yml
        player.addAttachment(
                Bukkit.getPluginManager().getPlugin("opsucht-permission")).setPermission(permission, false);

        LOGGER.info("Removed runtime permission '" + permission + "' from player " + player.getName());
    }

    @Override
    public CompletableFuture<Void> removeAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> remove(uuid, permission));
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        // Bukkit's native permission system doesn't have groups
        return Collections.emptySet();
    }

    @Override
    public @NotNull Set<String> getGroups() {
        // Bukkit's native permission system doesn't have groups
        return Collections.emptySet();
    }
}
