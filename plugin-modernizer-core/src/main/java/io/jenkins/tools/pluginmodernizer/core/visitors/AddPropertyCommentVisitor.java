package io.jenkins.tools.pluginmodernizer.core.visitors;

import java.util.ArrayList;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Tree;
import org.openrewrite.marker.Markers;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

/**
 * A visitor that add a comment before a maven property.
 * If a comment already exists, it's updated.
 */
public class AddPropertyCommentVisitor extends MavenIsoVisitor<ExecutionContext> {

    /**
     * The property name to add the comment before.
     */
    private final String propertyName;

    /**
     * The comment to add.
     */
    private final String comment;

    /**
     * Constructor of the visitor.
     * @param propertyName The property name to add the comment before.
     * @param comment The comment to add.
     */
    public AddPropertyCommentVisitor(String propertyName, String comment) {
        this.propertyName = propertyName;
        this.comment = comment;
    }

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
        tag = super.visitTag(tag, executionContext);
        if (tag.getName().equals("properties")) {

            // Ensure property exists
            Xml.Tag propertyTag = tag.getChild(propertyName).orElse(null);
            if (propertyTag == null) {
                return tag;
            }

            if (tag.getContent() == null) {
                return tag;
            }

            List<Content> contents = new ArrayList<>(tag.getContent());
            int propertyIndex = contents.indexOf(propertyTag);

            // Add or update comment
            if (propertyTag.getContent() != null) {
                boolean containsComment = contents.stream()
                        .anyMatch(c -> c instanceof Xml.Comment && comment.equals(((Xml.Comment) c).getText()));

                // Add comment if not exists
                if (!containsComment) {

                    // If there is a comment just before, remove it
                    if (propertyIndex > 0 && contents.get(propertyIndex - 1) instanceof Xml.Comment xmlComment) {
                        propertyIndex--;
                        contents.remove(propertyIndex);
                        doAfterVisit(new RemovePropertyCommentVisitor(xmlComment.getText()));
                    }

                    Xml.Comment customComment = new Xml.Comment(
                            Tree.randomId(), contents.get(propertyIndex).getPrefix(), Markers.EMPTY, comment);
                    contents.add(propertyIndex, customComment);
                    tag = tag.withContent(contents);
                }
            }
        }
        return tag;
    }
}
