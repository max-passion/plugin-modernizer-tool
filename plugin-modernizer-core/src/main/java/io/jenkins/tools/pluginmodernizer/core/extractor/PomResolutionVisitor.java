package io.jenkins.tools.pluginmodernizer.core.extractor;

import io.jenkins.tools.pluginmodernizer.core.config.RecipesConsts;
import java.util.Map;
import java.util.Optional;
import org.openrewrite.marker.Markers;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.MavenResolutionResult;
import org.openrewrite.maven.tree.Parent;
import org.openrewrite.maven.tree.Pom;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pom visitor that accumulate PluginMetadata using maven resolution result.
 * Maven resolution might not get updated if the tree is modified by other visitor
 * So it's best used in preconditons recipes to avoid side effect
 * An other implementation of this visitor could be extraction from the tree instead of maven resolution result
 */
public class PomResolutionVisitor extends MavenIsoVisitor<PluginMetadata> {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PomResolutionVisitor.class);

    @Override
    public Xml.Document visitDocument(Xml.Document document, PluginMetadata pluginMetadata) {

        document = super.visitDocument(document, pluginMetadata);

        Markers markers = getCursor().firstEnclosingOrThrow(Xml.Document.class).getMarkers();

        // Ensure maven resolution result is present
        Optional<MavenResolutionResult> mavenResolutionResult = markers.findFirst(MavenResolutionResult.class);
        if (mavenResolutionResult.isEmpty()) {
            return document;
        }

        // Get the pom
        MavenResolutionResult resolutionResult = mavenResolutionResult.get();
        ResolvedPom resolvedPom = resolutionResult.getPom();
        Pom pom = resolvedPom.getRequested();

        // Extract tags
        new PomPropertyVisitor().reduce(document, pluginMetadata);

        // Remove the properties that are not needed and specific to the build environment
        Map<String, String> properties = pom.getProperties();
        properties.remove("project.basedir");
        properties.remove("basedir");

        // Construct the plugin metadata
        pluginMetadata.setPluginName(pom.getName());
        Parent parent = pom.getParent();
        if (parent != null) {
            pluginMetadata.setParentVersion(parent.getVersion());
        }
        // Lookup by group ID to set the BOM version if any
        pom.getDependencyManagement().stream()
                .peek(dependency -> LOG.debug("Dependency: {}", dependency))
                .filter(dependency -> RecipesConsts.PLUGINS_BOM_GROUP_ID.equals(dependency.getGroupId()))
                .findFirst()
                .ifPresent(dependency -> {
                    pluginMetadata.setBomArtifactId(dependency.getArtifactId());
                    pluginMetadata.setBomVersion(dependency.getVersion());
                });
        pluginMetadata.setProperties(properties);
        pluginMetadata.setJenkinsVersion(
                resolvedPom.getManagedVersion("org.jenkins-ci.main", "jenkins-core", null, null));

        return document;
    }
}
