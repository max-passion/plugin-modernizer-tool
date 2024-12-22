package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.MetadataVisitor;
import io.jenkins.tools.pluginmodernizer.core.extractor.PluginMetadata;
import org.openrewrite.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
