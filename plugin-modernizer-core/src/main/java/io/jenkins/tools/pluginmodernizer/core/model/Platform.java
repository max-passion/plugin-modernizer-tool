package io.jenkins.tools.pluginmodernizer.core.model;

/**
 * Platform from Jenkinsfile
 */
public enum Platform {
    LINUX,
    WINDOWS,
    UNKNOWN;

    /**
     * Return a platform from a string.
     * @param platform The platform
     * @return The platform
     */
    public static Platform fromPlatform(String platform) {
        return switch (platform) {
            case "linux" -> LINUX;
            case "windows" -> WINDOWS;
            default -> UNKNOWN;
        };
    }
}
