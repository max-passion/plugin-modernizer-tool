package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.PluginMetadata;
import io.jenkins.tools.pluginmodernizer.core.extractor.PomVisitor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Determines if this project is using a BOM in it's bom file
 */
public class IsMissingJenkinsBaselineProperty extends Recipe {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(IsMissingJenkinsBaselineProperty.class);

    @Override
    public String getDisplayName() {
        return "Is the project missing a jenkins.baseline property on pom";
    }

    @Override
    public String getDescription() {
        return "Is the project missing a jenkins.baseline property on pom.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {

                PluginMetadata pluginMetadata = new PomVisitor().reduce(document, new PluginMetadata());

                if (!pluginMetadata.getProperties().containsKey("jenkins.baseline")) {
                    LOG.info("Project is missing jenkins.baseline property");
                    return SearchResult.found(document, "Project is missing jenkins.baseline property");
                }

                return document;
            }
        };
    }
}
