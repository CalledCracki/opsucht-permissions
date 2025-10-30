package net.opsucht.permission.bukkit.manager;

import net.opsucht.permission.api.PermissionProvider;
import net.opsucht.permission.bukkit.provider.GroupManagerProvider;
import net.opsucht.permission.bukkit.provider.LPProvider;
import net.opsucht.permission.bukkit.provider.PermissionsExProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Verwaltet die Erkennung und Bereitstellung des aktiven Permission-Providers.
 */
public final class ProviderManager {

    private ProviderManager() {}

    /**
     * Erkennt das aktive Permission-System anhand der geladenen Plugins.
     *
     * @return Eine Instanz des passenden PermissionProviders oder null, wenn keins gefunden wurde.
     */
    public static @Nullable PermissionProvider detectProvider() {
        Plugin luckPerms = Bukkit.getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null && luckPerms.isEnabled()) {
            return new LPProvider();
        }

        Plugin groupManager = Bukkit.getPluginManager().getPlugin("GroupManager");
        if (groupManager != null && groupManager.isEnabled()) {
            return new GroupManagerProvider();
        }

        Plugin pex = Bukkit.getPluginManager().getPlugin("PermissionsEx");
        if (pex != null && pex.isEnabled()) {
            return new PermissionsExProvider();
        }

        return null;
    }
}