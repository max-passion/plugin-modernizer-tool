package io.jenkins.tools.pluginmodernizer.core.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

/**
 * Ensure the parent pom has a relativePath set to disable local resolution.
 */
public class EnsureRelativePath extends Recipe {
    @Override
    public String getDisplayName() {
        return "Ensure the parent pom has a relativePath set to disable local resolution";
    }

    @Override
    public String getDescription() {
        return "Ensure the parent pom has a relativePath set to disable local resolution.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                if (isParentTag()) {
                    Xml.Tag relativePathTag = Xml.Tag.build("<relativePath />");
                    if (tag.getContent() == null) {
                        return tag;
                    }
                    List<Content> contents = new ArrayList<>(tag.getContent());
                    // Skip if relativePath is already present
                    if (contents.stream()
                            .anyMatch(content -> content instanceof Xml.Tag
                                    && ((Xml.Tag) content).getName().equals("relativePath"))) {
                        return tag;
                    }
                    Optional<Xml.Tag> maybeChild = tag.getChild("artifactId");
                    if (maybeChild.isEmpty()) {
                        return tag;
                    }
                    relativePathTag =
                            relativePathTag.withPrefix(maybeChild.get().getPrefix());

                    contents.add(relativePathTag);
                    return tag.withContent(contents);
                }
                return super.visitTag(tag, ctx);
            }
        };
    }
}
