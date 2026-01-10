package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.json.Assertions.json;
import static org.openrewrite.test.SourceSpecs.text;

import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openrewrite.test.RewriteTest;

/**
 * Test for {@link SetupRenovate}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class SetupRenovateTest implements RewriteTest {

    @Test
    void shouldAddRenovate() {
        rewriteRun(
                spec -> spec.recipe(new SetupRenovate()),
                // language=yaml
                text(""), // Need one minimum file to trigger the recipe
                json(null, """
                    {
                      "$schema": "https://docs.renovatebot.com/renovate-schema.json",
                      "extends": [
                        "github>jenkinsci/renovate-config"
                      ]
                    }
                    """, sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.RENOVATE.getPath());
                }));
    }

    @Test
    void shouldNotAddRenovateIfDependabotConfigured() {
        rewriteRun(
                spec -> spec.recipe(new SetupRenovate()),
                text(""), // Need one minimum file to trigger the recipe
                text("{}", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.DEPENDABOT.getPath());
                }));
    }

    @Test
    void shouldNotChangeRenovateIfAlreadyExists() {
        rewriteRun(
                spec -> spec.recipe(new SetupRenovate()),
                text(""), // Need one minimum file to trigger the recipe
                // language=yaml
                json("""
                    {}
                    """, sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.RENOVATE.getPath());
                }));
    }
}
