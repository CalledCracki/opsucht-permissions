package net.opsucht.permission.bukkit;

import net.opsucht.permission.api.Permission;
import net.opsucht.permission.api.PermissionProvider;
import net.opsucht.permission.bukkit.manager.ProviderManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("[OPSucht] Initialising PermissionsAPI...");

        PermissionProvider provider = ProviderManager.detectProvider();

        if (provider == null) {
            getLogger().warning("⚠️ Kein unterstütztes Permission-System gefunden!");
            getLogger().warning("Das Plugin bleibt inaktiv.");
            return;
        }

        Permission.set(provider);
        getLogger().info("✅ Permission-System erkannt: " + provider.getProviderName());
    }

    @Override
    public void onDisable() {
        getLogger().info("[OPSucht] PermissionsAPI disabled.");
    }
}