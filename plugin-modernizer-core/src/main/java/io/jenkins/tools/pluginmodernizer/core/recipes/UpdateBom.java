package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.visitors.UpdateBomVersionVisitor;
import org.openrewrite.*;
import org.openrewrite.maven.table.MavenMetadataFailures;

/**
 * A recipe that update the bom version to latest available.
 */
public class UpdateBom extends Recipe {

    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Update bom recipe";
    }

    @Override
    public @NlsRewrite.Description String getDescription() {
        return "Update bom recipe.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new IsUsingBom(), new UpdateBomVersionVisitor(new MavenMetadataFailures(this)));
    }
}
