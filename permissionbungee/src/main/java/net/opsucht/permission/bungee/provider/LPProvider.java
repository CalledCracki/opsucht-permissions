package net.opsucht.permission.bungee.provider;

import net.luckperms.api.LuckPerms;
import net.opsucht.permission.common.provider.AbstractLPProvider;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * LuckPerms provider implementation for BungeeCord/Waterfall proxies.
 * 
 * <p>
 * This implementation extends the shared AbstractLPProvider and provides
 * BungeeCord-specific initialization and logging.
 * </p>
 * 
 * @since 1.0.0
 */
public final class LPProvider extends AbstractLPProvider {

    private final Logger logger;

    /**
     * Constructs a new LPProvider for BungeeCord.
     * 
     * @param api    the LuckPerms API instance
     * @param logger the logger for this provider
     */
    public LPProvider(@NotNull LuckPerms api, @NotNull Logger logger) {
        super(api);
        this.logger = logger;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
