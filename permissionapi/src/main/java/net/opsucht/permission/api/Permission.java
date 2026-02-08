package net.opsucht.permission.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Static access class for the currently active PermissionProvider.
 * 
 * <p>
 * This class provides a singleton pattern for accessing the permission system.
 * The provider must be initialized before use via
 * {@link #set(PermissionProvider)}.
 * </p>
 * 
 * <p>
 * <b>Thread Safety:</b> This class is thread-safe. The singleton instance uses
 * volatile memory semantics and synchronized initialization to prevent race
 * conditions.
 * </p>
 * 
 * @since 1.0.0
 */
public final class Permission {

    private static volatile @Nullable PermissionProvider instance = null;

    @ApiStatus.Internal
    private Permission() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns the currently configured PermissionProvider.
     * 
     * <p>
     * This method will throw an exception if the provider has not been initialized.
     * Check {@link #isInitialized()} first if you need to verify provider
     * availability.
     * </p>
     *
     * @return the active PermissionProvider instance
     * @throws IllegalStateException if no provider has been set
     * @see #isInitialized()
     * @since 1.0.0
     */
    public static @NotNull PermissionProvider get() {
        PermissionProvider current = instance;
        if (current == null) {
            throw new IllegalStateException("Permission has not been initialized");
        }
        return current;
    }

    /**
     * Checks whether a provider has been initialized.
     * 
     * @return true if a provider has been set, false otherwise
     * @since 1.0.0
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Sets the global PermissionProvider.
     * 
     * <p>
     * This method can only be called once. Subsequent calls will throw an
     * IllegalStateException to prevent accidental re-initialization.
     * </p>
     * 
     * <p>
     * <b>Thread Safety:</b> This method is synchronized to prevent race conditions
     * during initialization.
     * </p>
     *
     * @param provider the provider to use (must not be null)
     * @throws IllegalStateException if a provider has already been set
     * @since 1.0.0
     */
    public static synchronized void set(@NotNull PermissionProvider provider) {
        if (instance != null) {
            throw new IllegalStateException("Permission provider has already been initialized");
        }
        instance = provider;
    }
}
