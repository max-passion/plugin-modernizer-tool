package io.jenkins.tools.pluginmodernizer.core.model;

import io.jenkins.tools.pluginmodernizer.core.impl.CacheManager;
import java.nio.file.Path;
import java.util.List;

/**
 * List of plugins that have opted out of receiving PRs from plugin-modernizer-tool
 */
public class OptOutPluginsData extends CacheEntry<OptOutPluginsData> {

    /**
     * List of plugin names that have opted out
     */
    private List<String> opted_out_plugins;

    public OptOutPluginsData(CacheManager cacheManager) {
        super(cacheManager, OptOutPluginsData.class, CacheManager.OPT_OUT_PLUGINS_CACHE_KEY, Path.of("."));
    }

    /**
     * Get the list of plugins that opted out
     * @return list of plugin names
     */
    public List<String> getOptedOutPlugins() {
        return opted_out_plugins;
    }
}
