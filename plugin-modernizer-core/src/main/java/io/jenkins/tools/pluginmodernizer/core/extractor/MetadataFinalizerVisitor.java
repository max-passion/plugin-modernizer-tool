package io.jenkins.tools.pluginmodernizer.core.extractor;

import io.jenkins.tools.pluginmodernizer.core.utils.JsonUtils;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor to finalize metadata from source files
 */
public class MetadataFinalizerVisitor extends TreeVisitor<Tree, ExecutionContext> {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetadataFinalizerVisitor.class);

    @Override
    public Tree visit(Tree tree, ExecutionContext ctx) {

        PluginMetadata mergedMetadata = ctx.getMessage("mergedMetadata", new PluginMetadata());
        PluginMetadata commonMetadata = ctx.getMessage("commonMetadata", new PluginMetadata());
        PluginMetadata pluginMetadata = ctx.getMessage("pomMetadata", new PluginMetadata());
        PluginMetadata jenkinsFileMetadata = ctx.getMessage("jenkinsFileMetadata", new PluginMetadata());

        // Merge the metadata
        PluginMetadata merged = JsonUtils.fromJson(
                JsonUtils.merge(pluginMetadata.toJson(), jenkinsFileMetadata.toJson()), PluginMetadata.class);
        merged = JsonUtils.fromJson(JsonUtils.merge(commonMetadata.toJson(), merged.toJson()), PluginMetadata.class);
        merged = JsonUtils.fromJson(JsonUtils.merge(mergedMetadata.toJson(), merged.toJson()), PluginMetadata.class);

        LOG.debug("Merged metadata: {}", JsonUtils.toJson(merged));

        // Write the metadata to a file for later use by the plugin modernizer.
        merged.save();
        LOG.debug("Plugin metadata written to {}", merged.getRelativePath());
        ctx.putMessage("mergedMetadata", merged);
        LOG.debug(JsonUtils.toJson(merged));

        return tree;
    }
}
