package net.opsucht.permission.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.opsucht.permission.api.Permission;
import net.opsucht.permission.api.PermissionProvider;
import net.opsucht.permission.bungee.manager.ProviderManager;

/*
 * BungeeCord Plugin zur Initialisierung der OPSucht PermissionsAPI.
 * Erkennt automatisch LuckPerms oder Fallback-Systeme und registriert den aktiven Provider.
 */
public final class Bungee extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getLogger().info("[OPSucht] Initialising PermissionsAPI...");

        PermissionProvider provider = ProviderManager.detectProvider();

        if (provider == null) {
            ProxyServer.getInstance().getLogger().warning("⚠️ Kein unterstütztes Permission-System gefunden!");
            ProxyServer.getInstance().getLogger().warning("Das Plugin bleibt inaktiv.");
            return;
        }

        Permission.set(provider);
        ProxyServer.getInstance().getLogger().info("✅ Permission-System erkannt: " + provider.getProviderName());
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getLogger().info("OpsuchtPermissions disabled.");
    }
}