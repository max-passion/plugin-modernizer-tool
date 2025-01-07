package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.test.SourceSpecs.text;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public class MergeGitIgnoreRecipeTest implements RewriteTest {
    @Test
    void shouldMergeGitIgnoreEntries() {
        rewriteRun(
                spec -> spec.recipe(new MergeGitIgnoreRecipe(Path.of("archetypes/common-files/.gitignore"))),
                text(
                        """
                # Existing user-defined entries
                *.log
                build/
                .idea/

                # Custom section
                custom/*.tmp
                """,
                        sourceSpecs -> sourceSpecs.path(".gitignore")),
                text(
                        """
                target
                work

                # mvn hpi:run
                # IntelliJ IDEA project files
                *.iml
                *.iws
                *.ipr
                .idea

                # Eclipse project files
                .settings
                .classpath
                .project
                """,
                        sourceSpecs -> sourceSpecs.path("archetypes/common-files/.gitignore")),
                text(
                        """
                # Existing user-defined entries
                *.log
                build/
                .idea/

                # Custom section
                custom/*.tmp

                # Added from archetype
                target
                work
                *.iml
                *.iws
                *.ipr
                .settings
                .classpath
                .project
                """,
                        sourceSpecs -> sourceSpecs.path(".gitignore")));
    }
}
