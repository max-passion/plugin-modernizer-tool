package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.extractor.PluginMetadata;
import io.jenkins.tools.pluginmodernizer.core.extractor.PomResolutionVisitor;
import io.jenkins.tools.pluginmodernizer.core.visitors.AddBeforePropertyVisitor;
import io.jenkins.tools.pluginmodernizer.core.visitors.AddPropertyCommentVisitor;
import org.jspecify.annotations.Nullable;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.AddPropertyVisitor;
import org.openrewrite.maven.ChangeManagedDependencyGroupIdAndArtifactId;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A recipe to migrate the pom.xml to use the jenkins.baseLine property.
 * Doesn't upgrade the jenkins version or bom version.
 * If the plugin is not using the bom or using the weekly baseline, it will skip the update.
 * This will also fix the bom version if it doesn't match the jenkins.version.
 */
public class MigrateToJenkinsBaseLineProperty extends Recipe {

    /**
     * Logger for the class.
     */
    public static final Logger LOG = LoggerFactory.getLogger(MigrateToJenkinsBaseLineProperty.class);

    @Override
    public String getDisplayName() {
        return "Update the pom.xml to use newly introduced jenkins.baseline property";
    }

    @Override
    public String getDescription() {
        return "Update the pom.xml to use newly introduced jenkins.baseline property.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MigrateToJenkinsBaseLinePropertyVisitor();
    }

    /**
     * Visitor to migrate to Jenkins baseline property.
     */
    private static class MigrateToJenkinsBaseLinePropertyVisitor extends MavenIsoVisitor<ExecutionContext> {

        @Override
        public @Nullable Xml visit(@Nullable Tree tree, ExecutionContext executionContext, Cursor parent) {
            return super.visit(tree, executionContext, parent);
        }

        @Override
        public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
            Xml.Document d = super.visitDocument(document, ctx);

            // Skip if not using bom or using weekly baseline
            PluginMetadata pluginMetadata = new PomResolutionVisitor().reduce(document, new PluginMetadata());
            if (pluginMetadata.getBomArtifactId() == null || "bom-weekly".equals(pluginMetadata.getBomArtifactId())) {
                LOG.debug("Project is using Jenkins weekly bom or not bom, skipping");
                return d;
            }

            performUpdate(document);
            return document;
        }

        /**
         * Perform the update.
         * @param document The document to update.
         */
        private void performUpdate(Xml.Document document) {

            Xml.Tag jenkinsVersionTag = document.getRoot()
                    .getChild("properties")
                    .flatMap(props -> props.getChild("jenkins.version"))
                    .orElse(null);

            if (jenkinsVersionTag == null) {
                return;
            }

            // Keep only major and minor and ignore patch version
            String jenkinsVersion = jenkinsVersionTag.getValue().get();
            String jenkinsBaseline = jenkinsVersion;
            String jenkinsPatch = null;

            // It's a LTS, extract patch
            if (jenkinsVersion.matches("\\d+\\.\\d+\\.\\d+")
                    || jenkinsVersion.matches("\\$\\{jenkins.baseline}\\.\\d+")) {
                jenkinsBaseline = jenkinsBaseline.substring(0, jenkinsBaseline.lastIndexOf('.'));
                jenkinsPatch = jenkinsVersion.substring(jenkinsVersion.lastIndexOf('.') + 1);
            }
            LOG.debug("Jenkins baseline version is {}", jenkinsBaseline);
            LOG.debug("Jenkins patch version is {}", jenkinsPatch);

            String expectedVersion =
                    jenkinsPatch != null ? "${jenkins.baseline}." + jenkinsPatch : "${jenkins.baseline}";

            // Add or changes properties
            doAfterVisit(new AddBeforePropertyVisitor("jenkins.version", "jenkins.baseline", jenkinsBaseline));
            doAfterVisit(new AddPropertyCommentVisitor(
                    "jenkins.baseline",
                    " https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ "));

            LOG.debug("Jenkins version is {}", jenkinsVersionTag.getValue().get());
            LOG.debug("Expected version is {}", expectedVersion);

            // Change the jenkins version
            if (!expectedVersion.equals(jenkinsVersionTag.getValue().get())) {
                doAfterVisit(new AddPropertyVisitor("jenkins.version", expectedVersion, false));
                maybeUpdateModel();
            }

            // Change the bom artifact ID
            Xml.Tag bom = document.getRoot()
                    .getChild("dependencyManagement")
                    .flatMap(dm -> dm.getChild("dependencies"))
                    .flatMap(deps -> deps.getChild("dependency"))
                    .filter(dep -> dep.getChildValue("groupId")
                            .map("io.jenkins.tools.bom"::equals)
                            .orElse(false))
                    .orElseThrow();

            Xml.Tag artifactIdTag = bom.getChild("artifactId").orElseThrow();
            Xml.Tag version = bom.getChild("version").orElseThrow();

            LOG.debug("Artifact ID is {}", artifactIdTag.getValue().get());
            LOG.debug("Version is {}", version.getValue().get());

            // Change the artifact and perform upgrade
            if (!artifactIdTag.getValue().get().equals("bom-${jenkins.baseline}.x")) {
                doAfterVisit(new ChangeManagedDependencyGroupIdAndArtifactId(
                                "io.jenkins.tools.bom",
                                artifactIdTag.getValue().get(),
                                "io.jenkins.tools.bom",
                                "bom-${jenkins.baseline}.x",
                                version.getValue().get(),
                                "\\.v[a-f0-9_]+")
                        .getVisitor());
            }
        }
    }
}
