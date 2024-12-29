package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.yaml.Assertions.yaml;

import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public class RemoveReleaseDrafterTest implements RewriteTest {

    @Test
    void shouldNotRemoveReleaseDrafter() {
        rewriteRun(
                spec -> spec.recipe(new RemoveReleaseDrafter()),
                // language=yaml
                yaml("{}", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.RELEASE_DRAFTER.getPath());
                }));
    }

    @Test
    void shouldRemoveReleaseDrafterIfCD() {
        rewriteRun(
                spec -> spec.recipe(new RemoveReleaseDrafter()),
                // language=yaml
                yaml("{}", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.WORKFLOW_CD.getPath());
                }),
                yaml("{}", null, sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.RELEASE_DRAFTER.getPath());
                }));
    }
}
