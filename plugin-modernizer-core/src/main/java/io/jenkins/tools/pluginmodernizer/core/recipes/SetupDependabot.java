package io.jenkins.tools.pluginmodernizer.core.recipes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.intellij.lang.annotations.Language;
import org.openrewrite.*;
import org.openrewrite.yaml.YamlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setup Dependabot.
 */
@SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "No user input")
public class SetupDependabot extends ScanningRecipe<AtomicBoolean> {

    /**
     * The dependabot file.
     */
    @Language("yml")
    public static final String DEPENDABOT_FILE =
            """
        version: 2
        updates:
        - package-ecosystem: maven
          directory: /
          schedule:
            interval: monthly
        - package-ecosystem: github-actions
          directory: /
          schedule:
            interval: monthly
        """;

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SetupDependabot.class);

    @Override
    public String getDisplayName() {
        return "Setup dependabot";
    }

    @Override
    public String getDescription() {
        return "Setup dependabot for the project. If not already setup. Ignore also if Renovate is already setup.";
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
                if (sourceFile.getSourcePath().equals(Path.of(ArchetypeCommonFile.RENOVATE.getPath()))) {
                    LOG.info("Project is using Renovate. Doing nothing.");
                    shouldCreate.set(false);
                }
                if (sourceFile.getSourcePath().equals(Path.of(ArchetypeCommonFile.DEPENDABOT.getPath()))) {
                    LOG.info("Project is using Dependabot already. Doing nothing.");
                    shouldCreate.set(false);
                }
                return tree;
            }
        };
    }

    @Override
    public Collection<SourceFile> generate(AtomicBoolean shouldCreate, ExecutionContext ctx) {
        if (shouldCreate.get()) {
            return YamlParser.builder()
                    .build()
                    .parse(DEPENDABOT_FILE)
                    .map(brandNewFile -> (SourceFile)
                            brandNewFile.withSourcePath(Paths.get(ArchetypeCommonFile.DEPENDABOT.getPath())))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
