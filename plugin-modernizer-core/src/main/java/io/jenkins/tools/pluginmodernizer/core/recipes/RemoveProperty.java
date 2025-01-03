package io.jenkins.tools.pluginmodernizer.core.recipes;

import java.util.HashMap;
import java.util.Map;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;

/**
 * Remove a Maven property if it's not referenced on current sources.
 * It allow to cleanup version overrides that are not used and still reference old version
 */
public class RemoveProperty extends ScanningRecipe<Map<String, String>> {

    @Option(
            displayName = "Property name",
            description = "Key name of the property to remove.",
            example = "configuration-as-code.version")
    String propertyName;

    public RemoveProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String getDisplayName() {
        return "Remove a Maven property if it's not referenced on current sources";
    }

    @Override
    public String getDescription() {
        return "Remove a Maven property if it's not referenced on current sources.";
    }

    @Override
    public Map<String, String> getInitialValue(ExecutionContext ctx) {
        return new HashMap<>();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Map<String, String> acc) {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                tag = super.visitTag(tag, ctx);
                return tag;
            }
        };
    }
}
