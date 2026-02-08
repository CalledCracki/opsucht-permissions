package net.opsucht.permission.api;

/**
 * Main API class providing version information and metadata for the Permission
 * API.
 * 
 * <p>
 * This class cannot be instantiated. It serves as a holder for constants
 * and utility methods related to the API version.
 * </p>
 * 
 * @since 1.0.0
 */
public final class API {

    /**
     * The current version of the Permission API.
     */
    public static final String VERSION = "1.0.0-SNAPSHOT";

    /**
     * The API version number used for compatibility checking.
     * This is incremented when breaking changes are made to the API.
     */
    public static final int API_VERSION = 1;

    /**
     * The minimum API version this implementation is compatible with.
     */
    public static final int MIN_API_VERSION = 1;

    private API() {
        throw new UnsupportedOperationException("API class cannot be instantiated");
    }

    /**
     * Checks if the given API version is compatible with this implementation.
     * 
     * @param version the API version to check
     * @return true if the version is supported, false otherwise
     * @since 1.0.0
     */
    public static boolean isCompatibleWith(int version) {
        return version >= MIN_API_VERSION && version <= API_VERSION;
    }
}