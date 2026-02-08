package net.opsucht.permission.bukkit.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.opsucht.permission.common.provider.AbstractLPProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * LuckPerms provider implementation for Bukkit/Paper servers.
 * 
 * <p>
 * This implementation extends the shared AbstractLPProvider and provides
 * Bukkit-specific initialization and logging.
 * </p>
 * 
 * @since 1.0.0
 */
public final class LPProvider extends AbstractLPProvider {

    private final Logger logger;

    /**
     * Constructs a new LPProvider for Bukkit.
     * Uses LuckPermsProvider.get() to obtain the LuckPerms API instance.
     */
    public LPProvider() {
        this(LuckPermsProvider.get());
    }

    /**
     * Constructs a new LPProvider with the given LuckPerms instance.
     * 
     * @param api the LuckPerms API instance
     */
    public LPProvider(@NotNull LuckPerms api) {
        super(api);
        this.logger = Bukkit.getLogger();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
