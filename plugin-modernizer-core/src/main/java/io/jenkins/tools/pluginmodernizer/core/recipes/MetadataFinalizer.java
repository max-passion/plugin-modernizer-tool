package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.MetadataFinalizerVisitor;
import org.openrewrite.*;

/**
 * Metadata finalizer. Executed after FetchMetadata to finalize the metadata and store it.
 */
public class MetadataFinalizer extends Recipe {

    @Override
    public String getDisplayName() {
        return "Metadata finalizer";
    }

    @Override
    public String getDescription() {
        return "Metadata finalizer";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MetadataFinalizerVisitor();
    }
}
