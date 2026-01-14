package io.jenkins.tools.pluginmodernizer.core.visitors;

import java.util.ArrayList;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

/**
 * A visitor that add a maven property at the end of the properties section
 */
public class AddLastPropertyVisitor extends MavenIsoVisitor<ExecutionContext> {

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
     * @param propertyName The property name to add.
     * @param propertyValue The property value to add.
     */
    public AddLastPropertyVisitor(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
        tag = super.visitTag(tag, executionContext);
        if (tag.getName().equals("properties")) {

            // Ensure value if exists
            Xml.Tag existingPropertyTag = tag.getChild(propertyName).orElse(null);
            if (existingPropertyTag != null && existingPropertyTag.getValue().isPresent()) {
                return tag;
            }

            // Ensure previous
            Xml.Tag previousPropertyTag = tag.getChildren().getFirst();

            // Add new property at the end
            List<Content> contents = new ArrayList<>();
            if (tag.getContent() != null) {
                contents.addAll(tag.getContent());
            }
            Xml.Tag newPropertyTag =
                    Xml.Tag.build("<" + propertyName + ">" + propertyValue + "</" + propertyName + ">");
            newPropertyTag = newPropertyTag.withPrefix(previousPropertyTag.getPrefix());
            contents.add(newPropertyTag);

            tag = tag.withContent(contents);
        }

        return tag;
    }
}
