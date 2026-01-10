package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remove dependabot configuration files.
 */
public class RemoveDependabot extends ScanningRecipe<AtomicBoolean> {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RemoveDependabot.class);

    @Override
    public String getDisplayName() {
        return "Remove dependabot";
    }

    @Override
    public String getDescription() {
        return "Remove dependabot configuration file.";
    }

    @Override
    public AtomicBoolean getInitialValue(ExecutionContext ctx) {
        return new AtomicBoolean(false);
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(AtomicBoolean shouldRemove) {
        return new TreeVisitor<>() {

            @Override
            public Tree visit(Tree tree, ExecutionContext ctx) {
                SourceFile sourceFile = (SourceFile) tree;
                if (ArchetypeCommonFile.DEPENDABOT.same(sourceFile.getSourcePath())) {
                    shouldRemove.set(true);
                }
                return tree;
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(AtomicBoolean shouldRemove) {
        return new TreeVisitor<>() {
            @Override
            public Tree visit(Tree tree, ExecutionContext ctx) {
                LOG.info("Checking if Dependabot should be removed");
                if (shouldRemove.get() && tree instanceof SourceFile sourceFile) {
                    if (ArchetypeCommonFile.DEPENDABOT.same(sourceFile.getSourcePath())) {
                        LOG.info("Deleting Dependabot file: {}", sourceFile.getSourcePath());
                        return null;
                    }
                }
                return tree;
            }
        };
    }
}
