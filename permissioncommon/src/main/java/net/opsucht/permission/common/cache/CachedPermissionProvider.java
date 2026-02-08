package net.opsucht.permission.common.cache;

import net.opsucht.permission.api.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A caching wrapper for PermissionProvider implementations.
 * 
 * <p>This class caches permission check results for improved performance.
 * It's particularly useful when permissions are checked frequently for
 * the same player and permission node.</p>
 * 
 * <p><b>Note:</b> This is a simple time-based cache. For production use,
 * consider integrating with the underlying permission system's cache
 * invalidation mechanisms.</p>
 * 
 * @since 1.0.0
 */
public class CachedPermissionProvider implements PermissionProvider {

    private static final long DEFAULT_CACHE_DURATION_MS = TimeUnit.SECONDS.toMillis(30);

    private final PermissionProvider delegate;
    private final Map<CacheKey, CachedValue> cache;
    private final long cacheDurationMs;

    /**
     * Creates a new cached provider with the default cache duration (30 seconds).
     * 
     * @param delegate the underlying provider to cache
     */
    public CachedPermissionProvider(@NotNull PermissionProvider delegate) {
        this(delegate, DEFAULT_CACHE_DURATION_MS);
    }

    /**
     * Creates a new cached provider with a custom cache duration.
     * 
     * @param delegate the underlying provider to cache
     * @param cacheDurationMs cache duration in milliseconds
     */
    public CachedPermissionProvider(@NotNull PermissionProvider delegate, long cacheDurationMs) {
        this.delegate = delegate;
        this.cache = new ConcurrentHashMap<>();
        this.cacheDurationMs = cacheDurationMs;
    }

    @Override
    public @NotNull String getProviderName() {
        return delegate.getProviderName() + " (Cached)";
    }

    @Override
    public boolean has(@NotNull UUID uuid, @NotNull String permission) {
        CacheKey key = new CacheKey(uuid, permission);
        CachedValue cached = cache.get(key);

        if (cached != null && !cached.isExpired()) {
            return cached.value;
        }

        boolean result = delegate.has(uuid, permission);
        cache.put(key, new CachedValue(result, System.currentTimeMillis() + cacheDurationMs));
        return result;
    }

    @Override
    public void add(@NotNull UUID uuid, @NotNull String permission) {
        delegate.add(uuid, permission);
        invalidateCache(uuid, permission);
    }

    @Override
    public CompletableFuture<Void> addAsync(@NotNull UUID uuid, @NotNull String permission) {
        return delegate.addAsync(uuid, permission)
                .thenRun(() -> invalidateCache(uuid, permission));
    }

    @Override
    public void remove(@NotNull UUID uuid, @NotNull String permission) {
        delegate.remove(uuid, permission);
        invalidateCache(uuid, permission);
    }

    @Override
    public CompletableFuture<Void> removeAsync(@NotNull UUID uuid, @NotNull String permission) {
        return delegate.removeAsync(uuid, permission)
                .thenRun(() -> invalidateCache(uuid, permission));
    }

    @Override
    public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
        return delegate.getGroups(uuid);
    }

    @Override
    public @NotNull Set<String> getGroups() {
        return delegate.getGroups();
    }

    /**
     * Invalidates the cache entry for the given player and permission.
     * 
     * @param uuid the player's UUID
     * @param permission the permission node
     */
    private void invalidateCache(@NotNull UUID uuid, @NotNull String permission) {
        cache.remove(new CacheKey(uuid, permission));
    }

    /**
     * Clears all cached entries.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Cache key combining UUID and permission.
     */
    private static class CacheKey {
        private final UUID uuid;
        private final String permission;

        CacheKey(UUID uuid, String permission) {
            this.uuid = uuid;
            this.permission = permission;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;
            CacheKey cacheKey = (CacheKey) o;
            return uuid.equals(cacheKey.uuid) && permission.equals(cacheKey.permission);
        }

        @Override
        public int hashCode() {
            return 31 * uuid.hashCode() + permission.hashCode();
        }
    }

    /**
     * Cached value with expiration time.
     */
    private static class CachedValue {
        private final boolean value;
        private final long expiresAt;

        CachedValue(boolean value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
