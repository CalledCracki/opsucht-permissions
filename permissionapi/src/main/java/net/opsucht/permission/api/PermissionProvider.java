package net.opsucht.permission.api;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Generic interface for permission systems.
 * 
 * <p>
 * This interface is implemented by providers for LuckPerms, GroupManager,
 * PermissionsEx, and other permission systems. It provides a unified API for
 * checking and managing permissions across different backends.
 * </p>
 * 
 * <p>
 * <b>Thread Safety:</b> Implementations should be thread-safe. The synchronous
 * methods ({@link #has}, {@link #add}, {@link #remove}) may be called from any
 * thread,
 * but I/O operations should not block the caller. Consider using the async
 * variants
 * for operations that involve disk or network I/O.
 * </p>
 * 
 * <p>
 * <b>Async Support:</b> The async methods ({@link #addAsync},
 * {@link #removeAsync})
 * have default implementations that execute synchronously. Implementations
 * should
 * override these methods to provide truly asynchronous behavior when possible.
 * </p>
 * 
 * @since 1.0.0
 */
public interface PermissionProvider {

    /**
     * Returns the name of the active provider (e.g., "LuckPerms").
     *
     * @return the provider name
     * @since 1.0.0
     */
    @NotNull
    String getProviderName();

    /**
     * Checks if the player with the given UUID has a specific permission.
     * 
     * <p>
     * This method performs a cached lookup when possible and should not
     * perform I/O operations. If the user is not loaded, this typically
     * returns false.
     * </p>
     *
     * @param uuid       the player's unique identifier
     * @param permission the permission node to check (e.g., "example.permission")
     * @return true if the player has the permission, false otherwise
     * @since 1.0.0
     */
    boolean has(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Adds a permission to a player.
     * 
     * <p>
     * This is a synchronous operation that may involve I/O. For non-blocking
     * operations, use {@link #addAsync} instead.
     * </p>
     * 
     * <p>
     * If the user is not loaded, implementations may silently fail or
     * queue the operation.
     * </p>
     *
     * @param uuid       the player's unique identifier
     * @param permission the permission node to add
     * @since 1.0.0
     * @see #addAsync(UUID, String)
     */
    void add(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Adds a permission to a player asynchronously.
     * 
     * <p>
     * This method returns a CompletableFuture that completes when the
     * permission has been added and saved. The future completes exceptionally
     * if the operation fails.
     * </p>
     * 
     * <p>
     * <b>Default Implementation:</b> The default implementation wraps the
     * synchronous {@link #add} method. Implementations should override this
     * to provide truly asynchronous behavior.
     * </p>
     * 
     * @param uuid       the player's unique identifier
     * @param permission the permission node to add
     * @return a future that completes when the operation is done
     * @since 1.0.0
     */
    default CompletableFuture<Void> addAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> add(uuid, permission));
    }

    /**
     * Removes a permission from a player.
     * 
     * <p>
     * This is a synchronous operation that may involve I/O. For non-blocking
     * operations, use {@link #removeAsync} instead.
     * </p>
     * 
     * <p>
     * If the user is not loaded, implementations may silently fail or
     * queue the operation.
     * </p>
     *
     * @param uuid       the player's unique identifier
     * @param permission the permission node to remove
     * @since 1.0.0
     * @see #removeAsync(UUID, String)
     */
    void remove(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Removes a permission from a player asynchronously.
     * 
     * <p>
     * This method returns a CompletableFuture that completes when the
     * permission has been removed and saved. The future completes exceptionally
     * if the operation fails.
     * </p>
     * 
     * <p>
     * <b>Default Implementation:</b> The default implementation wraps the
     * synchronous {@link #remove} method. Implementations should override this
     * to provide truly asynchronous behavior.
     * </p>
     * 
     * @param uuid       the player's unique identifier
     * @param permission the permission node to remove
     * @return a future that completes when the operation is done
     * @since 1.0.0
     */
    default CompletableFuture<Void> removeAsync(@NotNull UUID uuid, @NotNull String permission) {
        return CompletableFuture.runAsync(() -> remove(uuid, permission));
    }

    /**
     * Returns all groups a player belongs to.
     * 
     * <p>
     * The returned set contains the names of all groups the player
     * is a member of. The set may be empty if the player has no groups
     * or if the user is not loaded.
     * </p>
     *
     * @param uuid the player's unique identifier
     * @return an immutable set of group names
     * @since 1.0.0
     */
    @NotNull
    Set<String> getGroups(@NotNull UUID uuid);

    /**
     * Returns all known groups in the permission system.
     * 
     * <p>
     * This returns all groups that the system knows about, not just
     * those assigned to a specific player.
     * </p>
     *
     * @return an immutable set of all group names
     * @since 1.0.0
     */
    @NotNull
    Set<String> getGroups();
}