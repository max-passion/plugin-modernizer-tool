package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.MetadataVisitor;
import io.jenkins.tools.pluginmodernizer.core.extractor.PluginMetadata;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.RecipeList;
import org.openrewrite.TreeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recipe to fetch metadata from source files and store it in the target directory.
 * See {@link MetadataVisitor} for more details.
 * See {@link MetadataFinalizer} for the finalization of the metadata and merging of metadata from different sources.
 * See {@link PluginMetadata} for the metadata structure.
 */
public class FetchMetadata extends Recipe {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FetchMetadata.class);

    @Override
    public String getDisplayName() {
        return "Fetch metadata";
    }

    @Override
    public String getDescription() {
        return "Fetch metadata from source files.";
    }

    @Override
    public void buildRecipeList(RecipeList list) {
        super.buildRecipeList(list);
        list.recipe(new MetadataFinalizer());
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MetadataVisitor(new PluginMetadata());
    }
}
