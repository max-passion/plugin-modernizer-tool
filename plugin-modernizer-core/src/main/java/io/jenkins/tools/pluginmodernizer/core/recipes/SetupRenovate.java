package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.intellij.lang.annotations.Language;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.json.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setup Renovate.
 */
public class SetupRenovate extends ScanningRecipe<AtomicBoolean> {

    /**
     * The renovate file.
     */
    @Language("json")
    public static final String RENOVATE_FILE = """
        {
          "$schema": "https://docs.renovatebot.com/renovate-schema.json",
          "extends": [
            "github>jenkinsci/renovate-config"
          ]
        }
        """;

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SetupRenovate.class);

    /**
     * Force adding Renovate even if Dependabot is already setup.
     */
    @Option(
            displayName = "Force",
            description = "Force adding Renovate even if Dependabot exists",
            example = "true",
            required = false)
    Boolean force;

    public SetupRenovate() {
        this.force = null;
    }

    public SetupRenovate(Boolean force) {
        this.force = force;
    }

    @Override
    public String getDisplayName() {
        return "Setup Renovate";
    }

    @Override
    public String getDescription() {
        return "Setup Renovate for the project. If not already setup. Ignore also if Dependabot is already setup.";
    }

    @Override
    public AtomicBoolean getInitialValue(ExecutionContext ctx) {
        return new AtomicBoolean(true);
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(AtomicBoolean shouldCreate) {
        return new TreeVisitor<>() {

            @Override
            public Tree visit(Tree tree, ExecutionContext ctx) {
                SourceFile sourceFile = (SourceFile) tree;
                LOG.info("Force value: {}", force);
                if (sourceFile.getSourcePath().equals(ArchetypeCommonFile.DEPENDABOT.getPath())
                        && (force == null || !force)) {
                    LOG.info("Project is using Dependabot. Doing nothing.");
                    shouldCreate.set(false);
                }
                if (sourceFile.getSourcePath().equals(ArchetypeCommonFile.RENOVATE.getPath())) {
                    LOG.info("Project is using Renovate already. Doing nothing.");
                    shouldCreate.set(false);
                }
                return tree;
            }
        };
    }

    @Override
    public Collection<SourceFile> generate(AtomicBoolean shouldCreate, ExecutionContext ctx) {
        if (shouldCreate.get()) {
            return JsonParser.builder()
                    .build()
                    .parse(RENOVATE_FILE)
                    .map(brandNewFile ->
                            (SourceFile) brandNewFile.withSourcePath(ArchetypeCommonFile.RENOVATE.getPath()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
