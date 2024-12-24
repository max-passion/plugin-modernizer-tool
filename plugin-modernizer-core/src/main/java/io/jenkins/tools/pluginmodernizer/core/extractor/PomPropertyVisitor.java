package io.jenkins.tools.pluginmodernizer.core.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pom property visitor.
 */
public class PomPropertyVisitor extends MavenIsoVisitor<PluginMetadata> {

    private static final Logger LOG = LoggerFactory.getLogger(PomPropertyVisitor.class);

    @Override
    public Xml.Tag visitTag(Xml.Tag tag, PluginMetadata pluginMetadata) {
        Xml.Tag t = super.visitTag(tag, pluginMetadata);
        MetadataXmlTag metadataXmlTag = convertToMetadataXmlTag(tag);
        List<MetadataFlag> newFlags = Arrays.stream(MetadataFlag.values())
                .filter(flag -> flag.isApplicable(metadataXmlTag))
                .toList();
        if (!newFlags.isEmpty()) {
            LOG.debug(
                    "Flags detected for tag {} {}",
                    tag,
                    newFlags.stream().map(Enum::name).collect(Collectors.joining(", ")));
            pluginMetadata.addFlags(newFlags);
        }
        return t;
    }

    /**
     * Convert an OpenRewrite XML tag to a metadata XML tag.
     * @param tag OpenRewrite XML tag
     * @return metadata XML tag
     */
    private MetadataXmlTag convertToMetadataXmlTag(Xml.Tag tag) {
        MetadataXmlTag metadataXmlTag = new MetadataXmlTag();
        metadataXmlTag.setName(tag.getName());
        metadataXmlTag.setValue(tag.getValue());
        metadataXmlTag.setChildren(
                tag.getChildren().stream().map(this::convertToMetadataXmlTag).collect(Collectors.toList()));
        return metadataXmlTag;
    }
}
