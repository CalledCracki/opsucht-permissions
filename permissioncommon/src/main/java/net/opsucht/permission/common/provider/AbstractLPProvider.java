package net.opsucht.permission.common.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.opsucht.permission.api.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Abstract base class for LuckPerms provider implementations.
 * 
 * <p>This class contains the shared logic for both Bukkit and BungeeCord
 * LuckPerms providers, reducing code duplication.</p>
 * 
 * <p>Implementations should provide a logger via {@link #getLogger()}
 * for error reporting.</p>
 * 
 * @since 1.0.0
 */
public abstract class AbstractLPProvider implements PermissionProvider {

    private static final String INHERITANCE_TYPE = "inheritance";
    private static final String GROUP_PREFIX = "group.";

    protected final LuckPerms api;

    /**
     * Constructs a new AbstractLPProvider.
     * 
     * @param api the LuckPerms API instance
     */
    protected AbstractLPProvider(@NotNull LuckPerms api) {
        this.api = api;
    }

    /**
     * Returns the logger for this provider.
     * Implementations should provide their platform-specific logger.
     * 
     * @return the logger instance
     */
    protected abstract Logger getLogger();

    @Override
    public @NotNull String getProviderName() {
        return "LuckPerms";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) {
            getLogger().fine("User not loaded for permission check: " + uuid);
            return false;
        }

        return user.getCachedData()
                .getPermissionData()
                .checkPermission(permission)
                .asBoolean();
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) {
            getLogger().warning("Cannot add permission - user not loaded: " + uuid);
            return;
        }

        Node node = Node.builder(permission).build();
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    @Override
    public CompletableFuture<Void> addAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.supplyAsync(() -> api.getUserManager().getUser(uuid))
                .thenCompose(user -> {
                    if (user == null) {
                        getLogger().warning("Cannot add permission - user not loaded: " + uuid);
                        return CompletableFuture.completedFuture(null);
                    }
                    
                    Node node = Node.builder(permission).build();
                    user.data().add(node);
                    return api.getUserManager().saveUser(user);
                });
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) {
            getLogger().warning("Cannot remove permission - user not loaded: " + uuid);
            return;
        }

        Node node = Node.builder(permission).build();
        user.data().remove(node);
        api.getUserManager().saveUser(user);
    }

    @Override
    public CompletableFuture<Void> removeAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.supplyAsync(() -> api.getUserManager().getUser(uuid))
                .thenCompose(user -> {
                    if (user == null) {
                        getLogger().warning("Cannot remove permission - user not loaded: " + uuid);
                        return CompletableFuture.completedFuture(null);
                    }
                    
                    Node node = Node.builder(permission).build();
                    user.data().remove(node);
                    return api.getUserManager().saveUser(user);
                });
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        if (user == null) {
            getLogger().fine("User not loaded for groups lookup: " + uuid);
            return Set.of();
        }

        return user.getNodes().stream()
                .filter(node -> node.getType().name().equalsIgnoreCase(INHERITANCE_TYPE))
                .map(node -> node.getKey().replace(GROUP_PREFIX, ""))
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
