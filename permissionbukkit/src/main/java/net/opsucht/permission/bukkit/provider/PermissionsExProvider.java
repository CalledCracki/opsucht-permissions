package net.opsucht.permission.bukkit.provider;

import io.papermc.paper.plugin.PermissionManager;
import net.opsucht.permission.api.PermissionProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
 * PermissionProvider-Implementierung für PermissionsEx mit Fallbacks für unterschiedliche API-Versionen.
 * Bevorzugt UUID-basierte Methoden; fällt auf namensbasierte Varianten zurück und toleriert getGroupNames()/getGroupList()-Abweichungen.
 */
public final class PermissionsExProvider implements PermissionProvider {

    private final @Nullable PermissionManager manager;

    public PermissionsExProvider() {
        PermissionManager pm = null;
        try {
            pm = Bukkit.getServicesManager().load(PermissionManager.class);
            if (pm == null) {
                pm = (PermissionManager) PermissionsEx.getPermissionManager();
            }
        } catch (Throwable ignored) {}
        this.manager = pm;
    }

    @Override
    public @NotNull String getProviderName() {
        return "PermissionsEx";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        if (manager == null) return false;
        String world = worldOf(uuid);
        try {
            Method m = manager.getClass().getMethod("has", UUID.class, String.class, String.class);
            Object r = m.invoke(manager, uuid, permission, world);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable ignored) {}
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        String name = op.getName();
        if (name == null) return false;
        try {
            Method m = manager.getClass().getMethod("has", String.class, String.class, String.class);
            Object r = m.invoke(manager, name, permission, world);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable ignored) {}
        PermissionUser user = user(uuid);
        if (user == null) return false;
        try {
            Method m = user.getClass().getMethod("has", String.class, String.class);
            Object r = m.invoke(user, permission, world);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable ignored) {}
        try {
            Method m = user.getClass().getMethod("has", String.class);
            Object r = m.invoke(user, permission);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable ignored) {}
        return false;
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        if (manager == null) return;
        PermissionUser user = user(uuid);
        if (user == null) return;
        if (!invokeBool(user, "addPermission", new Class[]{String.class}, permission)) {
            String world = worldOf(uuid);
            invokeVoid(user, "addPermission", new Class[]{String.class, String.class}, permission, world);
        }
        invokeVoid(user, "save", new Class[]{});
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        if (manager == null) return;
        PermissionUser user = user(uuid);
        if (user == null) return;
        if (!invokeBool(user, "removePermission", new Class[]{String.class}, permission)) {
            String world = worldOf(uuid);
            invokeBool(user, "removePermission", new Class[]{String.class, String.class}, permission, world);
        }
        invokeVoid(user, "save", new Class[]{});
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        if (manager == null) return Set.of();
        PermissionUser user = user(uuid);
        if (user == null) return Set.of();
        LinkedHashSet<String> out = new LinkedHashSet<>();
        Object arr = invoke(user, "getGroupsNames", new Class[]{});
        if (arr instanceof String[]) {
            for (String g : (String[]) arr) out.add(g);
            return out;
        }
        String world = worldOf(uuid);
        Object list = invoke(user, "getParentIdentifiers", new Class[]{String.class}, world);
        if (list instanceof List) {
            for (Object g : (List<?>) list) if (g != null) out.add(String.valueOf(g));
        }
        return out;
    }

    @Override
    public @NotNull Set<String> getGroups() {
        if (manager == null) return Set.of();
        LinkedHashSet<String> out = new LinkedHashSet<>();
        Object names = invoke(manager, "getGroupNames", new Class[]{});
        if (names instanceof Iterable) {
            for (Object n : (Iterable<?>) names) if (n != null) out.add(String.valueOf(n));
            if (!out.isEmpty()) return out;
        }
        Object list = invoke(manager, "getGroupList", new Class[]{});
        if (list instanceof Iterable) {
            for (Object g : (Iterable<?>) list) {
                if (g instanceof PermissionGroup) {
                    out.add(((PermissionGroup) g).getName());
                } else if (g != null) {
                    try {
                        Method m = g.getClass().getMethod("getName");
                        Object n = m.invoke(g);
                        if (n != null) out.add(String.valueOf(n));
                    } catch (Throwable ignored) {}
                }
            }
        }
        return out;
    }

    private @Nullable PermissionUser user(UUID uuid) {
        if (manager == null) return null;
        try {
            Method m = manager.getClass().getMethod("getUser", UUID.class);
            Object r = m.invoke(manager, uuid);
            if (r instanceof PermissionUser) return (PermissionUser) r;
        } catch (Throwable ignored) {}
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        String name = op.getName();
        if (name == null) return null;
        try {
            Method m = manager.getClass().getMethod("getUser", String.class);
            Object r = m.invoke(manager, name);
            if (r instanceof PermissionUser) return (PermissionUser) r;
        } catch (Throwable ignored) {}
        return null;
    }

    private @Nullable String worldOf(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return p != null ? p.getWorld().getName() : null;
    }

    private Object invoke(Object target, String name, Class<?>[] types, Object... args) {
        try {
            Method m = target.getClass().getMethod(name, types);
            return m.invoke(target, args);
        } catch (Throwable ignored) {}
        return null;
    }

    private boolean invokeBool(Object target, String name, Class<?>[] types, Object... args) {
        Object r = invoke(target, name, types, args);
        return r instanceof Boolean && (Boolean) r;
    }

    private void invokeVoid(Object target, String name, Class<?>[] types, Object... args) {
        try {
            Method m = target.getClass().getMethod(name, types);
            m.invoke(target, args);
        } catch (Throwable ignored) {}
    }
}