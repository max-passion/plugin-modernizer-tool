package io.jenkins.tools.pluginmodernizer.core.extractor;

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

public class PomVisitor extends MavenIsoVisitor<PluginMetadata> {

    private static final Logger LOG = LoggerFactory.getLogger(PomVisitor.class);

    @Override
    public Xml.Document visitDocument(Xml.Document document, PluginMetadata pluginMetadata) {

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
                .filter(dependency -> "io.jenkins.tools.bom".equals(dependency.getGroupId()))
                .findFirst()
                .ifPresent(dependency -> pluginMetadata.setBomVersion(dependency.getVersion()));
        pluginMetadata.setProperties(properties);
        pluginMetadata.setJenkinsVersion(
                resolvedPom.getManagedVersion("org.jenkins-ci.main", "jenkins-core", null, null));

        return document;
    }
}
