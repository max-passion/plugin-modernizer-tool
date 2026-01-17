package io.jenkins.tools.pluginmodernizer.core.visitors;

import io.jenkins.tools.pluginmodernizer.core.recipes.AddProperty;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.ChangeTagValueVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor to modify POM for incrementals support.
 * Transforms version structure and adds required properties.
 */
public class AddIncrementalsVisitor extends MavenIsoVisitor<ExecutionContext> {

    private static final Logger LOG = LoggerFactory.getLogger(AddIncrementalsVisitor.class);
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+)*)(.*|$)");
    private static final Pattern GITHUB_PATTERN =
            Pattern.compile("github\\.com[:/]([^/]+/[^/]+?)(?:\\.git)?$", Pattern.CASE_INSENSITIVE);

    @Override
    public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
        document = super.visitDocument(document, ctx);

        Xml.Tag root = document.getRoot();

        // Check if properties section exists
        Optional<Xml.Tag> propertiesTag = root.getChild("properties");
        if (propertiesTag.isEmpty()) {
            LOG.warn(
                    "POM lacks a properties section. Cannot add incrementals properties. Skipping transformation.");
            return document;
        }

        // Check if already using incrementals format
        Optional<Xml.Tag> versionTag = root.getChild("version");
        if (versionTag.isPresent()) {
            String currentVersion = versionTag.get().getValue().orElse("");
            if (currentVersion.contains("${revision}") || currentVersion.contains("${changelist}")) {
                LOG.info("POM already uses incrementals version format. Skipping transformation.");
                return document;
            }

            // Extract revision from current version
            Matcher versionMatcher = VERSION_PATTERN.matcher(currentVersion);
            String revision = "1";
            if (versionMatcher.find()) {
                revision = versionMatcher.group(1);
            }

            // Update version to ${revision}.${changelist} format
            LOG.debug("Transforming version from {} to ${{revision}}.${{changelist}}", currentVersion);
            document = (Xml.Document) new ChangeTagValueVisitor<>(versionTag.get(), "${revision}.${changelist}")
                    .visitNonNull(document, ctx);

            // Add properties if they don't exist
            document = (Xml.Document)
                    new AddProperty("revision", revision).getVisitor().visitNonNull(document, ctx);
            document = (Xml.Document) new AddProperty("changelist", "999999-SNAPSHOT")
                    .getVisitor()
                    .visitNonNull(document, ctx);

            // Extract and add GitHub repo from SCM
            Optional<Xml.Tag> scmTag = root.getChild("scm");
            String gitHubRepo = null;
            if (scmTag.isPresent()) {
                gitHubRepo = extractGitHubRepo(scmTag.get());
                if (gitHubRepo != null) {
                    LOG.debug("Adding gitHubRepo property: {}", gitHubRepo);
                    document = (Xml.Document) new AddProperty("gitHubRepo", gitHubRepo)
                            .getVisitor()
                            .visitNonNull(document, ctx);

                    // Update SCM URLs to use ${gitHubRepo} property
                    document = updateScmUrls(document, gitHubRepo, ctx);
                }

                // Add scmTag property
                document = (Xml.Document)
                        new AddProperty("scmTag", "HEAD").getVisitor().visitNonNull(document, ctx);

                // Refresh root tag reference to avoid stale references after document transformations
                root = document.getRoot();
                scmTag = root.getChild("scm");

                // Update SCM tag to use ${scmTag}
                Optional<Xml.Tag> tagTag = scmTag.isPresent() ? scmTag.get().getChild("tag") : Optional.empty();
                if (tagTag.isPresent()) {
                    document = (Xml.Document)
                            new ChangeTagValueVisitor<>(tagTag.get(), "${scmTag}").visitNonNull(document, ctx);
                }
            }

            // Only rewrite the URL if the gitHubRepo property was successfully added
            if (gitHubRepo != null) {
                Optional<Xml.Tag> urlTag = root.getChild("url");
                if (urlTag.isPresent()) {
                    String url = urlTag.get().getValue().orElse("");
                    Matcher urlMatcher = GITHUB_PATTERN.matcher(url);
                    if (urlMatcher.find()) {
                        document = (Xml.Document) new ChangeTagValueVisitor<>(
                                        urlTag.get(), "https://github.com/${gitHubRepo}")
                                .visitNonNull(document, ctx);
                    }
                }
            }
        }

        return document;
    }

    /**
     * Extract GitHub repository from SCM tag
     */
    private String extractGitHubRepo(Xml.Tag scmTag) {
        // Try connection tag first
        Optional<Xml.Tag> connectionTag = scmTag.getChild("connection");
        if (connectionTag.isPresent()) {
            String connection = connectionTag.get().getValue().orElse("");
            Matcher matcher = GITHUB_PATTERN.matcher(connection);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        // Fall back to developerConnection tag
        Optional<Xml.Tag> developerConnectionTag = scmTag.getChild("developerConnection");
        if (developerConnectionTag.isPresent()) {
            String developerConnection = developerConnectionTag.get().getValue().orElse("");
            Matcher matcher = GITHUB_PATTERN.matcher(developerConnection);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        // Fall back to url tag
        Optional<Xml.Tag> urlTag = scmTag.getChild("url");
        if (urlTag.isPresent()) {
            String url = urlTag.get().getValue().orElse("");
            Matcher matcher = GITHUB_PATTERN.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    /**
     * Update SCM URLs to use ${gitHubRepo} property
     */
    private Xml.Document updateScmUrls(Xml.Document document, String gitHubRepo, ExecutionContext ctx) {
        Xml.Tag root = document.getRoot();
        Optional<Xml.Tag> scmTag = root.getChild("scm");
        if (scmTag.isEmpty()) {
            return document;
        }

        // Update connection
        Optional<Xml.Tag> connectionTag = scmTag.get().getChild("connection");
        if (connectionTag.isPresent()) {
            document = (Xml.Document)
                    new ChangeTagValueVisitor<>(connectionTag.get(), "scm:git:https://github.com/${gitHubRepo}.git")
                            .visitNonNull(document, ctx);
            // Refresh references after document transformation
            root = document.getRoot();
            scmTag = root.getChild("scm");
        }

        // Update developerConnection
        Optional<Xml.Tag> devConnectionTag = scmTag.isPresent() ? scmTag.get().getChild("developerConnection") : Optional.empty();
        if (devConnectionTag.isPresent()) {
            document = (Xml.Document)
                    new ChangeTagValueVisitor<>(devConnectionTag.get(), "scm:git:git@github.com:${gitHubRepo}.git")
                            .visitNonNull(document, ctx);
            // Refresh references after document transformation
            root = document.getRoot();
            scmTag = root.getChild("scm");
        }

        // Update url
        Optional<Xml.Tag> scmUrlTag = scmTag.isPresent() ? scmTag.get().getChild("url") : Optional.empty();
        if (scmUrlTag.isPresent()) {
            document = (Xml.Document) new ChangeTagValueVisitor<>(scmUrlTag.get(), "https://github.com/${gitHubRepo}")
                    .visitNonNull(document, ctx);
        }

        return document;
    }
}
