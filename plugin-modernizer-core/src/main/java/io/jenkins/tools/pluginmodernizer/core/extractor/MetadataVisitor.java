package io.jenkins.tools.pluginmodernizer.core.extractor;

import io.jenkins.tools.pluginmodernizer.core.utils.JsonUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.PathUtils;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor to extract metadata from source files.
 */
public class MetadataVisitor extends TreeVisitor<Tree, ExecutionContext> {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetadataVisitor.class);

    final PluginMetadata pluginMetadata;

    public MetadataVisitor(PluginMetadata pluginMetadata) {
        this.pluginMetadata = pluginMetadata;
    }

    @Override
    public @Nullable Tree postVisit(@NonNull Tree tree, ExecutionContext executionContext) {
        LOG.debug("Finalizing metadata");
        return super.postVisit(tree, executionContext);
    }

    @Override
    public Tree visit(Tree tree, ExecutionContext executionContext) {

        SourceFile sourceFile = (SourceFile) tree;

        ArchetypeCommonFile commonFile =
                ArchetypeCommonFile.fromFile(sourceFile.getSourcePath().toString());

        // Store common files into metadata
        if (commonFile != null) {
            if (pluginMetadata.hasCommonFile(commonFile)) {
                LOG.debug("File {} is already a common file", sourceFile.getSourcePath());
            } else {
                LOG.debug("File {} is a common file", sourceFile.getSourcePath());
                pluginMetadata.addCommonFile(commonFile);
            }
        } else {
            LOG.debug("File {} is not a common file", sourceFile.getSourcePath());
        }

        // Extract metadata from Jenkinsfile
        if (PathUtils.matchesGlob(sourceFile.getSourcePath(), "**/Jenkinsfile")) {
            LOG.debug("Visiting Jenkinsfile {}", sourceFile.getSourcePath());
            PluginMetadata jenkinsFileMetadata = new JenkinsfileVisitor().reduce(tree, pluginMetadata);
            LOG.debug("Jenkinsfile metadata: {}", JsonUtils.toJson(jenkinsFileMetadata));
            executionContext.putMessage(
                    "jenkinsFileMetadata", jenkinsFileMetadata); // Is there better than context messaging ?
            return tree;
        }

        // Extract metadata from POM
        else if (PathUtils.matchesGlob(sourceFile.getSourcePath(), "**/pom.xml")) {
            LOG.debug("Visiting POM {}", sourceFile.getSourcePath());
            PluginMetadata pomMetadata = new PomResolutionVisitor().reduce(tree, pluginMetadata);
            LOG.debug("POM metadata: {}", JsonUtils.toJson(pomMetadata));
            executionContext.putMessage("pomMetadata", pomMetadata); // Is there better than context messaging ?
            return tree;
        }

        return tree;
    }
}
