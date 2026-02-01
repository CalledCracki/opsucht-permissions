package net.opsucht.permission.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Statische Zugriffsklasse auf den aktuell aktiven PermissionProvider.
 */
public final class Permission {

    private static @Nullable PermissionProvider instance = null;

    @ApiStatus.Internal
    private Permission() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gibt den aktuell gesetzten PermissionProvider zurück.
     *
     * @return PermissionProvider
     * @throws IllegalStateException wenn kein Provider gesetzt wurde
     */
    public static @NotNull PermissionProvider get() {
        if (instance == null) {
            throw new IllegalStateException("Permission has not been initialized");
        }
        return instance;
    }

    /**
     * Prüft, ob ein Provider initialisiert wurde.
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Setzt den globalen PermissionProvider.
     *
     * @param provider der zu verwendende Provider
     */
    public static void set(@NotNull PermissionProvider provider) {
        instance = provider;
    }
}
