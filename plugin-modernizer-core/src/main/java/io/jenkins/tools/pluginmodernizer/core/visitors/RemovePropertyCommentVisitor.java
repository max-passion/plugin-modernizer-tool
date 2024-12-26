package io.jenkins.tools.pluginmodernizer.core.visitors;

import java.util.ArrayList;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

public class RemovePropertyCommentVisitor extends MavenIsoVisitor<ExecutionContext> {

    private String comment;

    public RemovePropertyCommentVisitor(String comment) {
        this.comment = comment;
    }

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
        tag = super.visitTag(tag, executionContext);
        if (tag.getName().equals("properties")) {

            List<Content> contents = new ArrayList<>(tag.getContent());

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
