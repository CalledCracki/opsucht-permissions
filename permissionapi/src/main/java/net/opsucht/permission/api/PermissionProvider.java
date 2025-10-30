package net.opsucht.permission.api;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Generische Schnittstelle für Permission-Systeme.
 * Implementiert wird sie von LuckPerms-, GroupManager- oder PEX-Providern.
 */
public interface PermissionProvider {

    /**
     * Gibt den Namen des aktiven Providers zurück (z. B. "LuckPerms").
     *
     * @return Name des Providers
     */
    @NotNull String getProviderName();

    /**
     * Prüft, ob der Spieler mit der angegebenen UUID eine bestimmte Permission hat.
     *
     * @param uuid       UUID des Spielers
     * @param permission Permission-String
     * @return true, wenn der Spieler die Permission besitzt
     */
    boolean has(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Fügt einem Spieler eine Permission hinzu.
     *
     * @param uuid       UUID des Spielers
     * @param permission Permission-String
     */
    void add(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Entfernt eine Permission von einem Spieler.
     *
     * @param uuid       UUID des Spielers
     * @param permission Permission-String
     */
    void remove(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Gibt alle Gruppen eines Spielers zurück.
     *
     * @param uuid UUID des Spielers
     * @return Set von Gruppennamen
     */
    @NotNull Set<String> getGroups(@NotNull UUID uuid);

    /**
     * Gibt alle bekannten Gruppen zurück, die das System kennt.
     *
     * @return Set von Gruppennamen
     */
    @NotNull Set<String> getGroups();
}