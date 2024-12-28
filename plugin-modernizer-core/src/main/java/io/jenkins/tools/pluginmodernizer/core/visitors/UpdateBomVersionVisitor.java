package io.jenkins.tools.pluginmodernizer.core.visitors;

import io.jenkins.tools.pluginmodernizer.core.config.RecipesConsts;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.*;
import org.openrewrite.maven.table.MavenMetadataFailures;
import org.openrewrite.maven.trait.MavenDependency;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.VersionComparator;
import org.openrewrite.xml.ChangeTagValueVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A visitor that updates the BOM version in a maven pom file.
 */
public class UpdateBomVersionVisitor extends MavenIsoVisitor<ExecutionContext> {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UpdateBomVersionVisitor.class);

    private transient MavenMetadataFailures metadataFailures;

    /**
     * Contructor
     */
    public UpdateBomVersionVisitor(MavenMetadataFailures metadataFailures) {
        this.metadataFailures = metadataFailures;
    }

    @Override
    public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
        document = super.visitDocument(document, ctx);

        // Resolve artifact id
        Xml.Tag bomTag = getBomTag(document);
        Xml.Tag versionTag = bomTag.getChild("version").orElseThrow();
        Xml.Tag getProperties = getProperties(document);

        String artifactId =
                bomTag.getChild("artifactId").orElseThrow().getValue().orElseThrow();
        String version = bomTag.getChild("version").orElseThrow().getValue().orElseThrow();
        LOG.debug("Updating bom version from {} to latest.release", version);
        if (artifactId.equals("bom-${jenkins.baseline}.x")) {
            artifactId =
                    "bom-" + getProperties.getChildValue("jenkins.baseline").orElseThrow() + ".x";
        }

        String newBomVersionAvailable = findNewerBomVersion(artifactId, version, ctx);
        if (newBomVersionAvailable == null) {
            LOG.debug("No newer version available for {}", artifactId);
            return document;
        }

        return (Xml.Document)
                new ChangeTagValueVisitor<>(versionTag, newBomVersionAvailable).visitNonNull(document, ctx);
    }

    /**
     * Find the newer bom version
     * @param artifactId The artifact id
     * @param currentVersion The current version
     * @param ctx The execution context
     * @return The newer bom version
     */
    private String findNewerBomVersion(String artifactId, String currentVersion, ExecutionContext ctx) {
        VersionComparator latestRelease = new LatestRelease(RecipesConsts.VERSION_METADATA_PATTERN);
        try {
            return MavenDependency.findNewerVersion(
                    RecipesConsts.PLUGINS_BOM_GROUP_ID,
                    artifactId,
                    currentVersion,
                    getResolutionResult(),
                    metadataFailures,
                    latestRelease,
                    ctx);
        } catch (MavenDownloadingException e) {
            LOG.warn("Failed to download metadata for {}", artifactId, e);
            return null;
        }
    }

    /**
     * Get the bom tag from the document
     * @param document The document
     * @return The bom tag
     */
    private Xml.Tag getBomTag(Xml.Document document) {
        return document.getRoot()
                .getChild("dependencyManagement")
                .flatMap(dm -> dm.getChild("dependencies"))
                .flatMap(deps -> deps.getChild("dependency"))
                .filter(dep -> dep.getChildValue("groupId")
                        .map("io.jenkins.tools.bom"::equals)
                        .orElse(false))
                .orElseThrow();
    }

    /**
     * Get the properties tag from the document
     * @param document The document
     * @return The properties tag
     */
    private Xml.Tag getProperties(Xml.Document document) {
        return document.getRoot().getChild("properties").orElseThrow();
    }
}
