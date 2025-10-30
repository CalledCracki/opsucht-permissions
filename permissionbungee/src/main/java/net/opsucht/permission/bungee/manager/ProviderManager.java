package net.opsucht.permission.bungee.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.opsucht.permission.api.PermissionProvider;
import net.opsucht.permission.bungee.provider.LPProvider;
import org.jetbrains.annotations.Nullable;

/*
 * Erkennt verfügbare Permission-Systeme für BungeeCord.
 * Aktuell unterstützt: LuckPerms.
 */
public final class ProviderManager {

    private ProviderManager() {}

    public static @Nullable PermissionProvider detectProvider() {
        try {
            LuckPerms lp = LuckPermsProvider.get();
            return new LPProvider(lp);
        } catch (IllegalStateException ignored) {}
        return null;
    }
}