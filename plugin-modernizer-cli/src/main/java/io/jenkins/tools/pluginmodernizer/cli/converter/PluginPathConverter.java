package io.jenkins.tools.pluginmodernizer.cli.converter;

import io.jenkins.tools.pluginmodernizer.core.model.Plugin;
import io.jenkins.tools.pluginmodernizer.core.utils.PomModifier;
import java.nio.file.Files;
import java.nio.file.Path;
import picocli.CommandLine;

/**
 * Custom converter to get a list of plugin from a local folder
 */
public class PluginPathConverter implements CommandLine.ITypeConverter<Plugin> {

    @Override
    public Plugin convert(String value) throws Exception {
        Path path = Path.of(value);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path is not a directory: " + path);
        }
        // We assume the plugin is at the pom, will not work for multi module projects
        Path pom = path.resolve("pom.xml");
        if (!Files.exists(pom)) {
            throw new IllegalArgumentException("Path does not contain a pom.xml: " + path);
        }
        PomModifier pomModifier = new PomModifier(pom.toString());
        String packaging = pomModifier.getPackaging();
        if (!"hpi".equals(packaging)) {
            throw new IllegalArgumentException("Path does not contain a Jenkins plugin: " + path);
        }
        String artifactId = pomModifier.getArtifactId();
        if (artifactId == null) {
            throw new IllegalArgumentException("Path does not contain a valid Jenkins plugin: " + path);
        }
        // Build a local plugin
        return Plugin.build(artifactId, path);
    }
}
