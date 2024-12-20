package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.PluginMetadata;
import java.util.Optional;
import org.openrewrite.*;
import org.openrewrite.marker.Markers;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

/**
 * Determines if this project is using a BOM in it's bom file
 */
public class IsUsingBom extends Recipe {

    @Override
    public String getDisplayName() {
        return "Is the project a using Jenkins bom?";
    }

    @Override
    public String getDescription() {
        return "Checks if the project is a using a Jenkins bom by the presence of io.jenkins.tools.bom group ID as managed dependency.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new XmlVisitor<ExecutionContext>() {
            @Override
            public Xml visitDocument(Xml.Document document, ExecutionContext ctx) {

                // Ensure PluginMetadata is present
                Markers markers = document.getMarkers();
                Optional<PluginMetadata> pluginMetadata = markers.findFirst(PluginMetadata.class);
                if (pluginMetadata.isEmpty()) {
                    return document;
                }

                if (pluginMetadata.get().getBomVersion() != null) {
                    return SearchResult.found(document, "Project is using Jenkins bom");
                }

                return document;
            }
        };
    }
}
