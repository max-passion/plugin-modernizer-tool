package io.jenkins.tools.pluginmodernizer.core.visitors;

import java.util.ArrayList;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

/**
 * A visitor that add a maven property before another property.
 * If the property already exists, it will update its value.
 * If the previous property does not exist, the new property will not be added.
 */
public class AddBeforePropertyVisitor extends MavenIsoVisitor<ExecutionContext> {

    /**
     * The previous property name to add the new property before.
     */
    private final String previousPropertyName;

    /**
     * The property name to add.
     */
    private final String propertyName;

    /**
     * The property value to add.
     */
    private final String propertyValue;

    /**
     * Constructor of the visitor.
     * @param previousPropertyName The previous property name to add the new property before.
     * @param propertyName The property name to add.
     * @param propertyValue The property value to add.
     */
    public AddBeforePropertyVisitor(String previousPropertyName, String propertyName, String propertyValue) {
        this.previousPropertyName = previousPropertyName;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
        tag = super.visitTag(tag, executionContext);
        if (tag.getName().equals("properties")) {

            // Ensure previous
            Xml.Tag previousPropertyTag = tag.getChild(previousPropertyName).orElse(null);
            if (previousPropertyTag == null) {
                return tag;
            }

            // Ensure value if exists
            Xml.Tag existingPropertyTag = tag.getChild(propertyName).orElse(null);
            if (existingPropertyTag != null && existingPropertyTag.getValue().isPresent()) {
                // doAfterVisit(new ChangeTagValueVisitor<>(existingPropertyTag, propertyValue));
                return tag;
            }

            if (tag.getContent() == null || tag.getContent().isEmpty()) {
                return tag;
            }

            List<Content> contents = new ArrayList<>(tag.getContent());
            int propertyIndex = contents.indexOf(previousPropertyTag);

            // If there are comments leave them as they are
            while (propertyIndex > 0 && contents.get(propertyIndex - 1) instanceof Xml.Comment) {
                propertyIndex--;
            }

            // Place the tag just before the property or on first element of the sequence
            Xml.Tag propertyTag = Xml.Tag.build("<" + propertyName + ">" + propertyValue + "</" + propertyName + ">");
            propertyTag = propertyTag.withPrefix(previousPropertyTag.getPrefix());
            contents.add(propertyIndex, propertyTag);

            tag = tag.withContent(contents);
        }

        return tag;
    }
}
