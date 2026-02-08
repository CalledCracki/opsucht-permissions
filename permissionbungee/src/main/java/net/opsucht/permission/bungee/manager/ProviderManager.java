package net.opsucht.permission.bungee.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.opsucht.permission.api.PermissionProvider;
import net.opsucht.permission.bungee.provider.LPProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Detects available permission systems for BungeeCord.
 * Currently supported: LuckPerms.
 * 
 * @since 1.0.0
 */
public final class ProviderManager {

    private ProviderManager() {
    }

    /**
     * Detects and returns the available permission provider.
     * 
     * @return the detected provider, or null if none found
     */
    public static @Nullable PermissionProvider detectProvider() {
        try {
            LuckPerms lp = LuckPermsProvider.get();
            return new LPProvider(lp, ProxyServer.getInstance().getLogger());
        } catch (IllegalStateException e) {
            ProxyServer.getInstance().getLogger().fine("LuckPerms not available: " + e.getMessage());
        }
        return null;
    }
}