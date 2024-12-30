package io.jenkins.tools.pluginmodernizer.core.recipes;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.jenkins.UpgradeVersionProperty;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A recipe to upgrade the Jenkins version in the pom.xml.
 * Take care of updating the bom or adding the bom if it's not present.
 * Not changing anything if the version is already higher than the minimum version.
 */
public class UpgradeJenkinsVersion extends Recipe {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UpgradeJenkinsVersion.class);

    /**
     * The minimum version.
     */
    @Option(displayName = "Version", description = "The version.", example = "2.452.4")
    String minimumVersion;

    /**
     * Constructor.
     * @param minimumVersion The minimum version.
     */
    public UpgradeJenkinsVersion(String minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    @Override
    public String getDisplayName() {
        return "Upgrade Jenkins version";
    }

    @Override
    public String getDescription() {
        return "Upgrade Jenkins version.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {

            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {

                // Return another tree with jenkins version updated
                document = (Xml.Document) new UpgradeVersionProperty("jenkins.version", minimumVersion)
                        .getVisitor()
                        .visitNonNull(document, ctx);
                return (Xml.Document) new UpdateBom().getVisitor().visitNonNull(document, ctx);
            }
        };
    }
}
