package net.opsucht.permission.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Permission singleton.
 */
class PermissionTest {

    @BeforeEach
    @AfterEach
    void resetSingleton() throws Exception {
        // Reset the singleton instance between tests using reflection
        Field instanceField = Permission.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void shouldThrowExceptionWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> Permission.get(),
                "Should throw IllegalStateException when not initialized");
    }

    @Test
    void shouldReturnFalseWhenNotInitialized() {
        assertFalse(Permission.isInitialized(),
                "isInitialized should return false when not initialized");
    }

    @Test
    void shouldReturnProviderAfterInitialization() {
        PermissionProvider mockProvider = new MockPermissionProvider();
        Permission.set(mockProvider);

        assertTrue(Permission.isInitialized(), "Should be initialized after set");
        assertEquals(mockProvider, Permission.get(), "Should return the set provider");
    }

    @Test
    void shouldThrowExceptionOnDuplicateInitialization() {
        PermissionProvider mockProvider = new MockPermissionProvider();
        Permission.set(mockProvider);

        assertThrows(IllegalStateException.class, () -> Permission.set(mockProvider),
                "Should throw on duplicate initialization");
    }

    /**
     * Mock implementation for testing.
     */
    private static class MockPermissionProvider implements PermissionProvider {
        @Override
        public @NotNull String getProviderName() {
            return "Mock";
        }

        @Override
        public boolean has(@NotNull UUID uuid, @NotNull String permission) {
            return false;
        }

        @Override
        public void add(@NotNull UUID uuid, @NotNull String permission) {
        }

        @Override
        public void remove(@NotNull UUID uuid, @NotNull String permission) {
        }

        @Override
        public @NotNull Set<String> getGroups(@NotNull UUID uuid) {
            return Set.of();
        }

        @Override
        public @NotNull Set<String> getGroups() {
            return Set.of();
        }
    }
}
