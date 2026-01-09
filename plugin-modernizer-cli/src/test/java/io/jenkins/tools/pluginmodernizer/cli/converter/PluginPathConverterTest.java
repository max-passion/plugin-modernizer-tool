package io.jenkins.tools.pluginmodernizer.cli.converter;

import static org.junit.jupiter.api.Assertions.*;

import io.jenkins.tools.pluginmodernizer.core.model.Plugin;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PluginPathConverterTest {

    @Test
    void testSingleModulePlugin(@TempDir Path tempDir) throws Exception {
        // Create a single-module plugin structure
        Path pomFile = tempDir.resolve("pom.xml");
        Files.writeString(pomFile, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>my-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>hpi</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(tempDir.toString());

        assertNotNull(plugin);
        assertEquals("my-plugin", plugin.getName());
        assertEquals(tempDir, plugin.getLocalRepository());
    }

    @Test
    void testMultiModulePlugin(@TempDir Path tempDir) throws Exception {
        // Create a multi-module plugin structure

        // Root pom.xml
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>parent</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>openstack-cloud</module>
                </modules>
            </project>
            """);

        // Plugin module pom.xml
        Path pluginDir = tempDir.resolve("openstack-cloud");
        Files.createDirectories(pluginDir);
        Path pluginPom = pluginDir.resolve("pom.xml");
        Files.writeString(pluginPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <parent>
                    <groupId>io.jenkins.plugins</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0.0-SNAPSHOT</version>
                </parent>
                <artifactId>openstack-cloud</artifactId>
                <packaging>hpi</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(tempDir.toString());

        assertNotNull(plugin);
        assertEquals("openstack-cloud", plugin.getName());
        assertEquals(pluginDir, plugin.getLocalRepository());
    }

    @Test
    void testInvalidPath() {
        PluginPathConverter converter = new PluginPathConverter();

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("/non/existent/path");
        });
    }

    @Test
    void testDirectoryWithoutPom(@TempDir Path tempDir) {
        PluginPathConverter converter = new PluginPathConverter();

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(tempDir.toString());
        });
    }

    @Test
    void testMultiModuleWithoutHpiModule(@TempDir Path tempDir) throws Exception {
        // Create a multi-module project without HPI module
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>parent</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>pom</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(tempDir.toString());
        });

        assertTrue(exception
                .getMessage()
                .contains(
                        "Multi-module project detected but no module with packaging 'hpi' or 'jenkins-plugin' found"));
    }

    @Test
    void testMultiModuleWithJenkinsPluginPackaging(@TempDir Path tempDir) throws Exception {
        // Test with jenkins-plugin packaging (older format)
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>parent</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>pom</packaging>
            </project>
            """);

        Path pluginDir = tempDir.resolve("legacy-plugin");
        Files.createDirectories(pluginDir);
        Path pluginPom = pluginDir.resolve("pom.xml");
        Files.writeString(pluginPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <artifactId>legacy-plugin</artifactId>
                <packaging>jenkins-plugin</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(tempDir.toString());

        assertNotNull(plugin);
        assertEquals("legacy-plugin", plugin.getName());
        assertEquals(pluginDir, plugin.getLocalRepository());
    }

    @Test
    void testMultiModuleWithNestedStructure(@TempDir Path tempDir) throws Exception {
        // Test multi-module with plugin in nested subdirectory
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>workflow-cps-parent</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>pom</packaging>
            </project>
            """);

        Path pluginDir = tempDir.resolve("plugin");
        Files.createDirectories(pluginDir);
        Path pluginPom = pluginDir.resolve("pom.xml");
        Files.writeString(pluginPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <artifactId>workflow-cps</artifactId>
                <packaging>hpi</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(tempDir.toString());

        assertNotNull(plugin);
        assertEquals("workflow-cps", plugin.getName());
        assertEquals(pluginDir, plugin.getLocalRepository());
        assertTrue(plugin.isLocal());
    }

    @Test
    void testMultiModuleWithMultipleModules(@TempDir Path tempDir) throws Exception {
        // Test with multiple modules but only one is HPI
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>parent</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>pom</packaging>
            </project>
            """);

        // Create a JAR module
        Path jarModule = tempDir.resolve("lib");
        Files.createDirectories(jarModule);
        Files.writeString(jarModule.resolve("pom.xml"), """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <artifactId>lib</artifactId>
                <packaging>jar</packaging>
            </project>
            """);

        // Create the HPI module
        Path pluginDir = tempDir.resolve("plugin");
        Files.createDirectories(pluginDir);
        Files.writeString(pluginDir.resolve("pom.xml"), """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <artifactId>my-plugin</artifactId>
                <packaging>hpi</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(tempDir.toString());

        assertNotNull(plugin);
        assertEquals("my-plugin", plugin.getName());
        assertEquals(pluginDir, plugin.getLocalRepository());
    }

    @Test
    void testNormalPomPackaging(@TempDir Path tempDir) throws Exception {
        // Test that non-plugin pom packaging throws error
        Path pomFile = tempDir.resolve("pom.xml");
        Files.writeString(pomFile, """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>not-a-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>jar</packaging>
            </project>
            """);

        PluginPathConverter converter = new PluginPathConverter();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(tempDir.toString());
        });

        assertTrue(exception.getMessage().contains("Path does not contain a Jenkins plugin"));
    }

    @Test
    void testRelativePath(@TempDir Path tempDir) throws Exception {
        // Test with relative path
        Path pluginDir = tempDir.resolve("my-plugin");
        Files.createDirectories(pluginDir);
        Files.writeString(pluginDir.resolve("pom.xml"), """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>
                <groupId>io.jenkins.plugins</groupId>
                <artifactId>my-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>hpi</packaging>
            </project>
            """);

        // PluginPathConverter always converts to absolute path
        PluginPathConverter converter = new PluginPathConverter();
        Plugin plugin = converter.convert(pluginDir.toString());

        assertNotNull(plugin);
        assertEquals("my-plugin", plugin.getName());
        assertTrue(plugin.isLocal());
    }
}
