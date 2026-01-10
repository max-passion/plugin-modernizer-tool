package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.yaml.Assertions.yaml;

import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openrewrite.test.RewriteTest;

/**
 * Test for {@link RemoveDependabot}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class RemoveDependabotTest implements RewriteTest {

    @Test
    void shouldRemoveDependabot() {
        rewriteRun(
                spec -> spec.recipe(new RemoveDependabot()),
                // language=yaml
                yaml("{}", null, sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.DEPENDABOT.getPath());
                }));
    }
}
