package io.jenkins.tools.pluginmodernizer.core.visitors;

import java.util.ArrayList;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

/**
 * A visitor that remove a comment from a property in a maven pom file.
 * Only a comment with exact match will be removed inside the properties block.
 */
public class RemovePropertyCommentVisitor extends MavenIsoVisitor<ExecutionContext> {

    /**
     * The comment to remove.
     */
    private final String comment;

    /**
     * Constructor of the visitor.
     * @param comment The comment to remove.
     */
    public RemovePropertyCommentVisitor(String comment) {
        this.comment = comment;
    }

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
        tag = super.visitTag(tag, executionContext);
        if (tag.getName().equals("properties")) {

            List<Content> contents = new ArrayList<>(tag.getContent() != null ? tag.getContent() : List.of());

            // Remove the comment if needed
            boolean containsComment = contents.stream()
                    .anyMatch(c -> c instanceof Xml.Comment && comment.equals(((Xml.Comment) c).getText()));

            // Add comment if not exists
            if (containsComment) {
                for (int i = 0; i < contents.size(); i++) {
                    if (contents.get(i) instanceof Xml.Comment
                            && comment.equals(((Xml.Comment) contents.get(i)).getText())) {
                        contents.remove(i);
                        break;
                    }
                }
                tag = tag.withContent(contents);
            }
        }
        return tag;
    }
}
